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

package biblivre3.cataloging.bibliographic;

import biblivre3.circulation.lending.LendingBO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.Database;
import biblivre3.enums.MaterialType;
import biblivre3.enums.RecordStatus;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.holding.HoldingDTO;
import biblivre3.circulation.reservation.ReservationBO;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcReader;
import biblivre3.marcutils.MarcUtils;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

public class JsonBiblioHandler extends RootJsonHandler {

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("open")) {
            dto = getBiblioRecord(request);
        } else if (submitButton.equals("switch")) {
            dto = switchRecord(request);
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("delete")) {
            dto = delete(request);
        } else if (submitButton.equals("item_count")) {
            dto = count(request);
        } else if (submitButton.equals("file_upload")) {
            dto = uploadFile(request);
        } else if (submitButton.equals("move_records")) {
            dto = moveRecords(request);
        } else if (submitButton.equals("move_all_records")) {
            dto = moveAllRecords(request);
        }
        return dto.toJSONObject(properties);
    }

    private IFJson getBiblioRecord(HttpServletRequest request) {
        String recordId = request.getParameter("record_id");
        if (recordId == null || recordId.isEmpty()) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }

        String type = request.getParameter("type");
        if (type == null) {
            type = "record";
        }

        final BiblioSearchBO biblioBo = new BiblioSearchBO();
        final RecordDTO dto = biblioBo.getById(recordId);
        final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
        // Here we have the old DTO complete, but we need some more info to send back to browser

        // The first info is the holdings availability
        final HoldingBO hbo = new HoldingBO();
        final LendingBO lbo = new LendingBO();
        final ReservationBO rbo = new ReservationBO();

        int totalHoldings = hbo.countHoldings(dto);
        int availableHoldings = hbo.countAvailableHoldings(dto);
        int lentCount = 0;
        int reservedCount = 0;

        if (availableHoldings > 0) {
            lentCount = lbo.countLentHoldings(dto);
            reservedCount = rbo.countReservedHoldings(dto);
        }

        dto.setTotalCount(totalHoldings);
        dto.setAvailableCount(availableHoldings - lentCount);
        dto.setLentCount(lentCount);
        dto.setReservedCount(reservedCount);

        // Then title and links
        dto.setTitle(Indexer.listOneTitle(record));
        dto.setLinks(MarcUtils.getLinks(record));

        //List the holdings for this biblio record
        List<HoldingDTO> holdingsList = hbo.list(dto);
        for (HoldingDTO hdto : holdingsList) {
            hdto.setLent(lbo.isLent(hdto));
        }
        Collections.sort(holdingsList);
        dto.setHoldings(holdingsList);

        // Finally, we need the field list for this record
        if (type.equals("record")) {
            dto.setFields(MarcUtils.createFieldsList(record));
        } else if (type.equals("marc")) {
            dto.setMarc(MarcReader.iso2709ToMarc(dto.getIso2709()));
        } else if (type.equals("form")) {
            dto.setJson(MarcUtils.recordToJson(record));
        }
        return dto;
    }

    private boolean checkSearchTerms(final String[] searchTerms) {
        if (searchTerms == null) {
            return false;
        }
        for (String term : searchTerms) {
            if (!term.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private IFJson search(final HttpServletRequest request) {
        String itemType = request.getParameter("ITEM_TYPE");
        if (itemType == null) {
            itemType = "ALL";
        }

        String[] boolOp = request.getParameterValues("BOOL_OP");
        String[] searchTerms = request.getParameterValues("SEARCH_TERM");
        String[] searchAttr = request.getParameterValues("SEARCH_ATTR");

        boolean listAll = !checkSearchTerms(searchTerms);
        int offset;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }

        String base = request.getParameter("base");
        if (base == null) {
            base = Database.MAIN.toString();
        }

        BiblioSearchBO bsbo = new BiblioSearchBO();
        BiblioSearchResultsDTO bdto;

        if (listAll) {
            // Busca Completa
            bdto = bsbo.list(base, itemType, offset);
        } else {
            bdto = bsbo.search(base, itemType, searchTerms, searchAttr, boolOp, offset);
        }

        if (bdto != null && bdto.al != null) {
            return bdto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson switchRecord(HttpServletRequest request) {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String data = request.getParameter("data");
        String materialType = request.getParameter("material_type");
        String serial = request.getParameter("serial");
        RecordStatus status =  RecordStatus.NEW;
        if (StringUtils.isNotBlank(serial) && !serial.equals("0")) {
            status = RecordStatus.CORRECTED;
        }

        RecordDTO dto = new RecordDTO();
        Record record = null;
        MaterialType type;
        if (StringUtils.isBlank(materialType)) {
            type = MaterialType.BOOK;
        } else {
            type = MaterialType.getByCode(materialType.toUpperCase());
        }
        dto.setMaterialType(type);
        try {
            if (from.equals("marc")) {
                record = MarcReader.marcToRecord(data, type, status);
            } else if (from.equals("form")) {
                record = MarcUtils.jsonToRecord(new JSONObject(data), type, status);
            }

            if (to.equals("marc")) {
                String iso2709 = MarcUtils.recordToIso2709(record);
                dto.setMarc(MarcReader.iso2709ToMarc(iso2709));
            } else if (to.equals("form")) {
                dto.setJson(MarcUtils.recordToJson(record));
            }
        } catch (Exception e) {
            return new ErrorDTO("ERROR_MARC_SWITCH", "warning");
        }

        return dto;
    }

    private IFJson save(final HttpServletRequest request) {
        String type = request.getParameter("type");
        String data = request.getParameter("data");
        String id = request.getParameter("record_id");
        String materialType = request.getParameter("material_type");

        if (materialType == null) {
            materialType = MaterialType.BOOK.toString();
        }

        boolean result = false;
        if (StringUtils.isBlank(type)) {
            return new ErrorDTO("ERROR", "warning");
        }

        RecordStatus status = (id == null || id.equals("0")) ? RecordStatus.NEW : RecordStatus.CORRECTED;
        Record record = null;
        MaterialType mt = MaterialType.getByCode(materialType);
        try {
            if (type.equals("form")) {
                record = MarcUtils.jsonToRecord(new JSONObject(data), mt, status);
            } else if (type.equals("marc")) {
                record = MarcReader.marcToRecord(data, mt, status);
            }
        } catch (Exception e) {
            return new ErrorDTO("ERROR", "warning");
        }

        BiblioBO bo = new BiblioBO();
        String base = request.getParameter("base");
        if (base == null) {
            base = Database.MAIN.toString();
        }

        RecordDTO dto = null;
        if (id == null || id.equals("0")) {
            dto = bo.insert(record, Database.valueOf(base), mt);
            result = dto != null;
        } else {
            result = bo.update(record, id, mt);
        }

        if (result) {
            if (id == null || id.equals("0")) {
                SuccessDTO success = new SuccessDTO("SUCCESS_CREATE_RECORD");
                success.setData(String.valueOf(dto.getRecordSerial()));
                return success;
            } else {
                return new SuccessDTO("SUCCESS_UPDATE_RECORD");
            }
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson delete(final HttpServletRequest request) {
        String serial = request.getParameter("record_id");
        String base = request.getParameter("base");

        if (StringUtils.isBlank(serial)) {
            return new ErrorDTO("ERROR", "warning");
        }

        if (base == null) {
            base = Database.MAIN.toString();
        }

        BiblioBO bo = new BiblioBO();
        boolean result = false;
        try {
            result = bo.delete(new String[]{serial});
        } catch (RuntimeException re) {
            if ("MESSAGE_DELETE_BIBLIO_ERROR".equals(re.getMessage())) {
                return new ErrorDTO(re.getMessage(), "warning");
            }
        }

        if (result) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson count(final HttpServletRequest request) {
        String base = request.getParameter("base");
        if (base == null) {
            base = Database.MAIN.toString();
        }
        BiblioBO bo = new BiblioBO();
        Integer total = bo.getTotalNroRecords(Database.valueOf(base), MaterialType.ALL);
        SuccessDTO dto = new SuccessDTO("OK");
        dto.setData(String.valueOf(total));
        return dto;
    }

    private IFJson uploadFile(HttpServletRequest request) {
        String targetUrl = Config.getConfigProperty(ConfigurationEnum.DIGITAL_MEDIA);
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = request.getContextPath();
        }
        targetUrl += "/DigitalMediaController";
        String fileId = request.getParameter("file_id");
        String link = targetUrl + "?id=" + fileId;
        String recordId = request.getParameter("record_id");
        String description = request.getParameter("description");
        BiblioBO bo = new BiblioBO();

        boolean success = bo.createLinkDatafield(recordId, link, description);
        if (!success) {
            return new ErrorDTO("ERROR", "warning");
        }

        RecordDTO dto = new BiblioSearchBO().getById(recordId);
        Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
        dto.setLinks(MarcUtils.getLinks(record));
        return dto;
    }

    private IFJson moveRecords(HttpServletRequest request) {
        String idParams = request.getParameter("serial_list");
        String[] ids = idParams.split(",");
        String from = request.getParameter("base");
        BiblioBO bo = new BiblioBO();
        if (bo.moveRecords(ids, from)) {
            return new SuccessDTO("SUCCESS_BIBLIO_MOVE");
        } else {
            return new ErrorDTO("ERROR_BIBLIO_MOVE", "warning");
        }
    }

    private IFJson moveAllRecords(HttpServletRequest request) {
        String materialType = request.getParameter("material_type");
        if (materialType == null) {
            materialType = MaterialType.BOOK.toString();
        }

        MaterialType mt = MaterialType.getByCode(materialType);
        String from = request.getParameter("base");

        BiblioBO bo = new BiblioBO();
        if (bo.moveAllRecords(mt, from)) {
            return new SuccessDTO("SUCCESS_BIBLIO_MOVE");
        } else {
            return new ErrorDTO("ERROR_BIBLIO_MOVE", "warning");
        }
    }
}
