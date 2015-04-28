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

package biblivre3.circulation.lending;

import biblivre3.administration.AdminBO;
import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.circulation.*;
import biblivre3.enums.Availability;
import biblivre3.enums.LendingRules;
import biblivre3.enums.UserStatus;
import biblivre3.cataloging.holding.HoldingDTO;
import biblivre3.circulation.reservation.ReservationBO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import mercury.BaseBO;

public class LendingBO extends BaseBO {

    /**
     * Checks if it's possible to complete the lending operation for the holding
     * and user specified.  Runs the business rules for this checking.
     * 
     * @param holding
     * @param user
     * @return LendingRules - An enum with that represents the result of this checking
     */
    public final LendingRules checkLending(final HoldingDTO holding, final UserDTO user) {
        //The User can't be suspended, blocked or with debts
        //TODO check for debts
        if (!user.getUserStatus().equals(UserStatus.ACTIVE) && !user.getUserStatus().equals(UserStatus.PENDINGS)) {
            return LendingRules.USER_BLOCKED;
        }

        //The material must be available
        final boolean available = holding.getAvailability().equals(Availability.AVAILABLE);
        if (!available) {
            return LendingRules.MATERIAL_NOT_AVAILABLE;
        }

        //The material can't be already lent
        final boolean isLent = this.isLent(holding);
        if (isLent) {
            return LendingRules.MATERIAL_ALREADY_LENT;
        }

        //The lending limit (total number of materials that can be lent to a 
        //specific user) must be preserved
        if (!this.checkUserLendLimit(user, false)) {
            return LendingRules.LENDING_LIMIT_EXCEEDED;
        }

        return LendingRules.LENDING_POSSIBLE;
    }

    /**
     * Checks if it's possible to complete the renew operation for the holding
     * and user specified.  Runs the business rules for this checking.
     * 
     * @param holding
     * @param user
     * @return LendingRules - An enum with that represents the result of this checking
     */
    public final LendingRules checkRenew(final HoldingDTO holding, final UserDTO user) {
        //The User can't be suspended, blocked or with debts
        //TODO check for debts
        if (!user.getUserStatus().equals(UserStatus.ACTIVE) && !user.getUserStatus().equals(UserStatus.PENDINGS)) {
            return LendingRules.USER_BLOCKED;
        }

        //The material must be available
        final boolean available = holding.getAvailability().equals(Availability.AVAILABLE);
        if (!available) {
            return LendingRules.MATERIAL_NOT_AVAILABLE;
        }

        //The lending limit (total number of materials that can be lent to a 
        //specific user) must be preserved
        if (!this.checkUserLendLimit(user, true)) {
            return LendingRules.LENDING_LIMIT_EXCEEDED;
        }

        return LendingRules.LENDING_POSSIBLE;
    }

    public final boolean isLent(final HoldingDTO holding) {
        return this.getByHolding(holding) != null;
    }

    public final boolean wasLent(final HoldingDTO holding) {
        return new LendingHistoryDAO().wasLent(holding.getSerial());
    }


    public final LendingDTO getByHolding(final HoldingDTO holding) {
        return new LendingDAO().getByHolding(holding);
    }

    public final boolean checkUserLendLimit(final UserDTO user, final boolean renew) {
        final CirculationBO bo = new CirculationBO();
        final LendingDAO dao = new LendingDAO();
        final UserTypeDTO type = bo.getUserTypeById(user.getUserType());
        final Integer maxLendingCount = (type != null) ? type.getMaxLendingCount() : 1;
        final Integer count = dao.getUserLendingCount(user);

        if (renew) {
            return count <= maxLendingCount;
        } else {
            return count < maxLendingCount;
        }
    }

    public final Integer getUserLendingCount(final UserDTO user) {
        final LendingDAO dao = new LendingDAO();
        return dao.getUserLendingCount(user);
    }

    public final boolean doLend(final HoldingDTO holding, final UserDTO user) {
        final LendingDAO dao = new LendingDAO();
        final LendingDTO dto = new LendingDTO();

        dto.setHoldingSerial(holding.getSerial());
        dto.setUserSerial(user.getUserid());
        dto.setLendDate(new Date());

        Calendar returnDate = GregorianCalendar.getInstance();
        returnDate.setTime(new Date());
        final UserTypeDTO type = new AdminBO().findUserTypeBySerial(user.getUserType());

        if (type != null) {
            returnDate.add(Calendar.DATE, type.getMaxLendingDays());
        } else {
            returnDate.add(Calendar.DATE, 7);
        }

        dto.setReturnDate(returnDate.getTime());
        if (dao.insert(dto)) {
            ReservationBO rbo = new ReservationBO();
            rbo.delete(user.getUserid(), holding.getRecordSerial());
            return true;
        } else {
            return false;
        }
    }

    public final boolean doReturn(final LendingDTO dto, final UserDTO user, final String fineValue, final boolean paid) {
        final LendingDAO dao = new LendingDAO();
        final LendingFineBO fineBo = new LendingFineBO();
        dao.doReturn(dto);

        if (fineBo.isLateReturn(dto)) {
            LendingHistoryDTO history = new LendingHistoryDTO(dto);
            final LendingHistoryDAO historyDao = new LendingHistoryDAO();
            history = historyDao.findLendingHistory(history);

            fineBo.createFine(user, history, fineValue, paid);
        }
        return true;
    }

    public final boolean doRenew(final LendingDTO dto) {
        final LendingDAO dao = new LendingDAO();

        Calendar returnDate = GregorianCalendar.getInstance();
        returnDate.setTime(new Date());

        final UserDTO user = new CirculationBO().searchByUserId(dto.getUserSerial());
        if (user == null) {
            return false;
        }

        final UserTypeDTO type = new AdminBO().findUserTypeBySerial(user.getUserType());

        if (type != null) {
            returnDate.add(Calendar.DATE, type.getMaxLendingDays());
        } else {
            returnDate.add(Calendar.DATE, 7);
        }

        dto.setReturnDate(returnDate.getTime());
        return dao.doRenew(dto);
    }

    public final Collection<LendingHistoryDTO> listHistory(final UserDTO user) {
        return new LendingHistoryDAO().list(user);
    }

    public final LendingHistoryDTO getHistoryById(final Integer id) {
        return new LendingHistoryDAO().getById(id);
    }

    public final LendingHistoryDTO getLastReturn(int holdingSerial, int userId) {
        return new LendingHistoryDAO().getLastByHoldingAndUser(holdingSerial, userId);
    }

    public final Collection<LendingDTO> listLendings(final UserDTO user) {
        return new LendingDAO().list(user);
    }

    public final List<LendingInfoDTO> listByRecordSerial(final Integer recordSerial) {
        List<LendingInfoDTO> result = new ArrayList<LendingInfoDTO>();
        List<LendingDTO> lendings = new LendingDAO().listByRecordSerial(recordSerial);
        for (LendingDTO lending : lendings) {
            result.add(new LendingInfoDTO(lending));
        }
        return result;
    }

    public final List<LendingDTO> listAll() {
        return new LendingDAO().listAll();
    }

    public final Integer countLentHoldings(final int recordId) {
        return new LendingDAO().countLentHoldings(recordId);
    }

    public final Integer countLentHoldings(final RecordDTO record) {
        return this.countLentHoldings(record.getRecordSerial());
    }
}
