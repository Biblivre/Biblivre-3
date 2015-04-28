/**
 *  Este arquivo é parte do Biblivre3.
 *  
 *  Biblivre3 é um software livre; você pode redistribuí-lo e/ou 
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 *  Licença, ou (caso queira) qualquer versão posterior.
 *  
 *  Este programa é distribuído na esperança de que possa ser  útil, 
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *  
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 * 
 */

package biblivre3.cataloging.holding;

import biblivre3.cataloging.bibliographic.BiblioSearchBO;
import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.circulation.lending.LendingBO;
import biblivre3.enums.Availability;
import biblivre3.enums.Database;
import biblivre3.enums.HoldingSave;
import biblivre3.enums.MaterialType;
import biblivre3.enums.RecordStatus;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcReader;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.ApplicationConstants;
import biblivre3.utils.TextUtils;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.LoginDTO;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

public class JsonHoldingHandler extends RootJsonHandler {

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("open")) {
            dto = getHoldingRecord(request);
        } else if (submitButton.equals("switch")) {
            dto = switchRecord(request);
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("delete")) {
            dto = delete(request);
        } else if (submitButton.equals("generate_label")) {
            dto = generateLabel(request);
        } else if (submitButton.equals("create_automatic_holding")) {
            dto = createAutomaticHolding(request);
        } else if (submitButton.equals("get_next_location")) {
            dto = getNextLocation(request);
        }
        return dto.toJSONObject(properties);
    }

    private IFJson getHoldingRecord(HttpServletRequest request) {
        int holdingSerial = 0;

        try {
            holdingSerial = Integer.parseInt(request.getParameter("holding_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        String type = TextUtils.sanitize(request.getParameter("type"), "holding_form");


        final HoldingBO bo = new HoldingBO();
        final HoldingDTO dto = bo.getById(holdingSerial);

        final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());

        if (type.equals("holding_marc")) {
            dto.setMarc(MarcReader.iso2709ToMarc(dto.getIso2709()));
        } else if (type.equals("holding_form")) {
            dto.setJson(MarcUtils.recordToJson(record));
        }

        return dto;
    }

    private IFJson getNextLocation(HttpServletRequest request) {
        int recordSerial = 0;

        try {
            recordSerial = Integer.parseInt(request.getParameter("record_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        final BiblioSearchBO bo = new BiblioSearchBO();
        final RecordDTO rdto = bo.getById(recordSerial);
        
        final LocationDTO ldto = new LocationDTO();

        if (rdto != null) {
            final Record record = MarcUtils.iso2709ToRecord(rdto.getIso2709());
            final String[] location = Indexer.listLocation(record);

            final HoldingBO holdingBo = new HoldingBO();
        
            ldto.setLocationA(location[0]);
            ldto.setLocationB(location[1]);
            ldto.setLocationC(location[2]);
            ldto.setLocationD(holdingBo.getNextLocationD(recordSerial));
        }

        return ldto;
    }

    private IFJson switchRecord(HttpServletRequest request) {
        String from = request.getParameter("from");
        String to   = request.getParameter("to");
        String data = request.getParameter("data");
        String serial = request.getParameter("serial");
        RecordStatus status =  RecordStatus.NEW;
        if (StringUtils.isNotBlank(serial) && !serial.equals("0")) {
            status = RecordStatus.CORRECTED;
        }

        int availability = 0;

        try {
            availability = Integer.parseInt(request.getParameter("available"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        HoldingDTO dto = new HoldingDTO();
        dto.setAvailability(Availability.values()[availability]);

        Record record = null;

        try {
            if (from.equals("holding_marc")) {
                record = MarcReader.marcToRecord(data, MaterialType.HOLDINGS, status);
            } else if (from.equals("holding_form")) {
                record = MarcUtils.jsonToRecord(new JSONObject(data), MaterialType.HOLDINGS, status);
            }

            if (to.equals("holding_marc")) {
                String iso2709 = MarcUtils.recordToIso2709(record);
                dto.setMarc(MarcReader.iso2709ToMarc(iso2709));
            } else if (to.equals("holding_form")) {
                dto.setJson(MarcUtils.recordToJson(record));
            }
        } catch (Exception e) {
            return new ErrorDTO("ERROR_MARC_SWITCH", "warning");
        }

        return dto;
    }

    private IFJson save(final HttpServletRequest request) {
        String dataType = request.getParameter("type");
        String data = request.getParameter("data");

        if (StringUtils.isBlank(dataType)) {
            return new ErrorDTO("ERROR_INVALID_REQUIRED_PARAMETER", "warning");
        }

        int holdingSerial = 0;
        try {
            holdingSerial = Integer.parseInt(request.getParameter("holding_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        int recordSerial = 0;
        try {
            recordSerial = Integer.parseInt(request.getParameter("record_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        int availability = 0;
        try {
            availability = Integer.parseInt(request.getParameter("available"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        Database base = Database.valueOf(request.getParameter("base"));
        RecordStatus status =  RecordStatus.NEW;
        if (holdingSerial != 0) {
            status = RecordStatus.CORRECTED;
        }
        String marc = null;
        try {
            if (dataType.equals("holding_form")) {
                Record record = MarcUtils.jsonToRecord(new JSONObject(data), MaterialType.HOLDINGS, status);
                marc = MarcReader.iso2709ToMarc(MarcUtils.recordToIso2709(record));
            } else if (dataType.equals("holding_marc")) {
                marc = data;
            }
        } catch (Exception e) {
            return new ErrorDTO("ERROR_SAVE_HOLDING", "error");
        }

        LoginDTO ldto = (LoginDTO) request.getSession().getAttribute("LOGGED_USER");
        UserDTO user = new CirculationBO().searchByLoginId(ldto.getLoginId());

        HoldingBO bo = new HoldingBO(user);
        HoldingSave result;
        
        if (status.equals(RecordStatus.NEW)) {
            result = bo.insert(recordSerial, base, marc, Availability.values()[availability]);
        } else {
            result = bo.update(holdingSerial, recordSerial, marc, Availability.values()[availability]);
        }

        switch (result) {
            case SUCCESS:
                return new SuccessDTO(holdingSerial == 0 ? "SUCCESS_CREATE_HOLDING" : "SUCCESS_UPDATE_HOLDING");
            case SUCCESS_WITH_NEW_ASSET_HOLDING:
                return new SuccessDTO(holdingSerial == 0 ? "SUCCESS_CREATE_HOLDING_NEW_ASSET" : "SUCCESS_UPDATE_HOLDING_NEW_ASSET");
            case ASSET_HOLDING_ALREADY_IN_USE:
                return new ErrorDTO("ERROR_ASSET_HOLDING_ALREADY_IN_USE", "warning");
            default:
                return new ErrorDTO("ERROR_SAVE_HOLDING", "warning");
        }
    }

    private IFJson delete(final HttpServletRequest request) {
        int holdingSerial = 0;
        try {
            holdingSerial = Integer.parseInt(request.getParameter("holding_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        HoldingDTO dto = new HoldingDTO();
        dto.setSerial(holdingSerial);

        LendingBO lbo = new LendingBO();
        if (lbo.isLent(dto) || lbo.wasLent(dto)) {
            return new ErrorDTO("MESSAGE_DELETE_HOLDING_ERROR", "warning");
        }

        HoldingBO bo = new HoldingBO();
        if (bo.delete(dto)) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
    }

    private IFJson generateLabel(HttpServletRequest request) {
        int holdingSerial = 0;
        try {
            holdingSerial = Integer.parseInt(request.getParameter("holding_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        int recordSerial = 0;
        try {
            recordSerial = Integer.parseInt(request.getParameter("record_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        BiblioSearchBO bsbo = new BiblioSearchBO();
        HoldingBO bo = new HoldingBO();

        RecordDTO rdto = bsbo.getById(recordSerial);
        HoldingDTO hdto = bo.getById(holdingSerial);

        if (bo.generateLabel(holdingSerial, recordSerial, rdto, hdto)) {
            return new SuccessDTO("SUCCESS_CREATE_RECORD");
        } else {
            return new ErrorDTO("ERROR", "warning");
        }
    }

    private IFJson createAutomaticHolding(HttpServletRequest request) {
        int recordSerial = 0;
        try {
            recordSerial = Integer.parseInt(request.getParameter("record_id"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_NUMERIC_PARAMETER", "error");
        }

        Database base = Database.valueOf(request.getParameter("base"));

        final String[] ex_auto = new String[6];
        ex_auto[0] = request.getParameter("quant");
        ex_auto[1] = request.getParameter("nvol");
        ex_auto[2] = request.getParameter("nvol_obra");
        ex_auto[3] = request.getParameter("biblio_dep");
        ex_auto[4] = request.getParameter("aquis");
        ex_auto[5] = request.getParameter("dt_tomb");

        LoginDTO ldto = (LoginDTO)request.getSession().getAttribute("LOGGED_USER");
        UserDTO user = new CirculationBO().searchByLoginId(ldto.getLoginId());

        BiblioSearchBO bsbo = new BiblioSearchBO();
        HoldingBO bo = new HoldingBO(user);
        
        RecordDTO dto = bsbo.getById(recordSerial);
        Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());

        if (bo.createAutomaticHolding(record, base, recordSerial, Availability.AVAILABLE, ex_auto)) {
            return new SuccessDTO("SUCCESS_CREATE_RECORD");
        } else {
            return new ErrorDTO("ERROR", "warning");
        }
    }

}
