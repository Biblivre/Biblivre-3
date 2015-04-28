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

package biblivre3.circulation.reservation;

import biblivre3.cataloging.bibliographic.BiblioSearchBO;
import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.circulation.lending.LendingBO;
import biblivre3.circulation.lending.LendingInfoDTO;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.DTOCollection;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.DTO;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

public class JsonReservationHandler extends RootJsonHandler {

    private ReservationBO bo;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        bo = new ReservationBO();
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;        
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("list_reservations")) {
            dto = listReservations(request);
        } else if (submitButton.equals("reserve_record")) {
            dto = reserveRecord(request);
        } else if (submitButton.equals("delete_reserve")) {
            dto = deleteReserve(request);
        } else if (submitButton.equals("list_all_reservations")) {
            dto = listAll();
        } else if (submitButton.equals("list_pending_circulations")) {
            dto = listPendingCirculations(request);
        }
        return dto.toJSONObject(properties);
    }
    
    public IFJson listReservations(final HttpServletRequest request) {
        final String userId = request.getParameter("user_id");
        DTOCollection<ReservationDTO> dtoList = new DTOCollection<ReservationDTO>();
        UserDTO udto = null;
        try {
            udto = (new CirculationBO()).searchByUserId(Integer.valueOf(userId));
        } catch (Exception e) {
        }
        List<ReservationDTO> results = bo.list(udto);
        dtoList.addAll(results);
        return dtoList;
    }

    public IFJson listAll() {
        DTOCollection<ReservationInfoDTO> dtoList = new DTOCollection<ReservationInfoDTO>();
        dtoList.addAll(bo.listAll());
        return dtoList;
    }

    private IFJson deleteReserve(HttpServletRequest request) {
        final String reservationId = request.getParameter("reservation_id");
        if (StringUtils.isBlank(reservationId)) {
            return new ErrorDTO("MESSAGE_ERROR_SELECT_RESERVE", "warning");
        }
        if (bo.delete(Integer.valueOf(reservationId))) {
            return new SuccessDTO("SUCCESS_DELETE_RESERVATION");
        } else {
            return new ErrorDTO("ERROR_DELETE_RESERVATION", "warning");
        }
    }

    private IFJson listPendingCirculations(HttpServletRequest request) {
        final String recordSerial = request.getParameter("record_serial");
        
        final DTOCollection<ReservationInfoDTO> reservationList = new DTOCollection<ReservationInfoDTO>();
        reservationList.addAll(new ReservationBO().listByRecordSerial(Integer.valueOf(recordSerial)));

        final DTOCollection<LendingInfoDTO> lendingList = new DTOCollection<LendingInfoDTO>();
        lendingList.addAll(new LendingBO().listByRecordSerial(Integer.valueOf(recordSerial)));
        
        return new DTO() {
            @Override
            public JSONObject toJSONObject(Properties properties) {
                JSONObject json = super.toJSONObject(properties);
                try {
                    json.put("reservations", reservationList.toJSONObject(properties));
                    json.put("lendings", lendingList.toJSONObject(properties));
                } catch (JSONException e) {
                }
                return json;
            }
        };
    }

    private IFJson reserveRecord(HttpServletRequest request) {
        final String recordSerial = request.getParameter("record_serial");
        final String userId = request.getParameter("user_id");

        RecordDTO rdto = null;
        try {
            rdto = (new BiblioSearchBO()).getById(Integer.valueOf(recordSerial));
        } catch (Exception e) {
        }

        if (rdto == null) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "warning");
        }

        UserDTO udto = null;
        try {
            udto = (new CirculationBO()).searchByUserId(Integer.valueOf(userId));
        } catch (Exception e) {
        }

        if (udto == null) {
            return new ErrorDTO("MESSAGE_USER_SERIAL_NOT_FOUND", "warning");
        }

        ReservationBO rbo = new ReservationBO();
        if (rbo.insert(udto.getUserid(), rdto.getRecordSerial())) {
            ReservationDTO rsdto = rbo.getLastById(udto.getUserid(), rdto.getRecordSerial());
            ReservationInfoDTO ridto = new ReservationInfoDTO();
            ridto.setReservationSerial(rsdto.getReservationSerial());
            ridto.setUserName(udto.getName());
            ridto.setUserSerial(udto.getUserid());
            ridto.setRecordSerial(rdto.getRecordSerial());
            ridto.setCreated(rsdto.getCreated());
            ridto.setExpires(rsdto.getExpires());
            Record record = MarcUtils.iso2709ToRecord(rdto.getIso2709());
            ridto.setTitle(Indexer.listOneTitle(record));
            ridto.setAuthor(Indexer.listAuthors(record));
            ridto.setMessage("SUCCESS_CREATE_RESERVATION");
            return ridto;
        } else {
            return new ErrorDTO("ERROR_CREATE_RESERVATION", "warning");
        }
    }

}
