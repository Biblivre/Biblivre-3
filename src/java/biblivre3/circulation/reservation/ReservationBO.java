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
import biblivre3.circulation.UserTypeDTO;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mercury.BaseBO;
import org.marc4j_2_3_1.marc.Record;

public class ReservationBO extends BaseBO {

    ReservationDAO dao;

    public ReservationBO() {
        dao = new ReservationDAO();
    }

    public final boolean deleteExpired() {
        return dao.deleteExpired();
    }

    public final ReservationDTO getById(final Integer reservationId) {
        return dao.getById(reservationId);
    }

    public final ReservationDTO getLastById(final Integer userId, final Integer recordSerial) {
        return dao.getLastById(userId, recordSerial);
    }

    public final List<ReservationDTO> getByUserId(final Integer userId) {
        return dao.getByUserId(userId);
    }

    public final List<ReservationDTO> getByRecordId(final Integer recordSerial) {
        return dao.getByRecordId(recordSerial);
    }

    public final int countReservedHoldings(final RecordDTO dto) {
        return this.countReservedHoldings(dto.getRecordSerial());
    }


    public final int countReservedHoldings(final int recordSerial) {
        return dao.countReservedHoldings(recordSerial);
    }

    public final List<ReservationDTO> list(final UserDTO user) {
        List<ReservationDTO> list = dao.getByUserId(user.getUserid());
        BiblioSearchBO bsbo = new BiblioSearchBO();
        for (ReservationDTO dto : list) {
            RecordDTO rdto = null;
            rdto = bsbo.getById(dto.getRecordSerial());
            Record record = MarcUtils.iso2709ToRecord(rdto.getIso2709());
            dto.setTitle(Indexer.listOneTitle(record));
            dto.setAuthor(Indexer.listPrimaryAuthor(record));
        }
        return list;
    }

    public final List<ReservationInfoDTO> listAll() {
        List<ReservationDTO> search = dao.listAll();
        List<ReservationInfoDTO> result = new ArrayList<ReservationInfoDTO>();
        BiblioSearchBO bsbo = new BiblioSearchBO();
        CirculationBO cbo = new CirculationBO();

        for (ReservationDTO dto : search) {
            ReservationInfoDTO ridto = new ReservationInfoDTO();
            ridto.setReservationSerial(dto.getReservationSerial());
            ridto.setCreated(dto.getCreated());
            ridto.setExpires(dto.getExpires());
            ridto.setUserSerial(dto.getUserid());
            ridto.setRecordSerial(dto.getRecordSerial());

            RecordDTO rdto = bsbo.getById(dto.getRecordSerial());
            Record record = MarcUtils.iso2709ToRecord(rdto.getIso2709());

            ridto.setTitle(Indexer.listOneTitle(record));
            ridto.setAuthor(Indexer.listPrimaryAuthor(record));

            if (dto.getUserid() != null) {
                UserDTO udto = cbo.searchByUserId(dto.getUserid());
                ridto.setUserName(udto.getName());
                ridto.setUserEmail(udto.getEmail());
                ridto.setUserPhoneNumber(udto.getTelRef1());
            }

            result.add(ridto);
        }
        return result;
    }

    public final List<ReservationInfoDTO> listByRecordSerial(Integer recordSerial) {
        List<ReservationDTO> search = dao.getByRecordId(recordSerial);
        List<ReservationInfoDTO> result = new ArrayList<ReservationInfoDTO>();
        BiblioSearchBO bsbo = new BiblioSearchBO();
        CirculationBO cbo = new CirculationBO();

        RecordDTO rdto = bsbo.getById(recordSerial);
        Record record = MarcUtils.iso2709ToRecord(rdto.getIso2709());
        String title = Indexer.listOneTitle(record);
        String author = Indexer.listPrimaryAuthor(record);

        for (ReservationDTO dto : search) {
            ReservationInfoDTO ridto = new ReservationInfoDTO();
            ridto.setReservationSerial(dto.getReservationSerial());
            ridto.setCreated(dto.getCreated());
            ridto.setExpires(dto.getExpires());
            ridto.setUserSerial(dto.getUserid());
            ridto.setRecordSerial(dto.getRecordSerial());

            ridto.setTitle(title);
            ridto.setAuthor(author);

            if (dto.getUserid() != null) {
                UserDTO udto = cbo.searchByUserId(dto.getUserid());
                ridto.setUserName(udto.getName());
                ridto.setUserEmail(udto.getEmail());
                ridto.setUserPhoneNumber(udto.getTelRef1());
            }
            result.add(ridto);
        }
        return result;
    }

    public final boolean insert(final int userid, final int recordSerial) {
        ReservationDTO dto = new ReservationDTO();
        dto.setUserid(userid);
        dto.setRecordSerial(recordSerial);

        Date now = new Date();
        dto.setCreated(now);

        CirculationBO bo = new CirculationBO();
        UserDTO udto = bo.searchByUserId(userid);
        UserTypeDTO utdto = bo.getUserTypeById(udto.getUserType());
        Integer maxDays = utdto.getMaxReservationDays();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, maxDays != 0 ? maxDays : 7);
        dto.setExpires(cal.getTime());

        return dao.insert(dto);
    }

    public final boolean delete(final int reservationId) {
        return dao.delete(reservationId);
    }

    public final boolean delete(final int userId, final int recordSerial) {
        return dao.delete(userId, recordSerial);
    }

}
