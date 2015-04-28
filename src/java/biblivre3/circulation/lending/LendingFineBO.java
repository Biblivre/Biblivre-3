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

import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.circulation.UserTypeDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.utils.DateUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mercury.BaseBO;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  23/03/2009
 */
public class LendingFineBO extends BaseBO {

    private LendingFineDAO dao;

    public LendingFineBO() {
        try {
            dao = new LendingFineDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * Creates the Fine in the Database.
     * 
     * 
     * @param user
     * @param history
     * @return
     */
    public final LendingFineDTO createFine(final UserDTO user, final LendingHistoryDTO history, final String value, final boolean paid) {
        final LendingFineDTO fine = new LendingFineDTO();
        float fineValue = 0.0f;

        try {
            fineValue = Float.valueOf(value.replaceAll("\\.", ",").replaceAll(",", "\\."));
        } catch (NumberFormatException nfe) {
        }

        fine.setUserSerial(user.getUserid());
        fine.setLendingHistorySerial(history.getSerial());
        fine.setValue(fineValue);
        if (paid) {
            fine.setPayment(new Date());
        }
        dao.insert(fine);
        return fine;
    }
    
    /**
     * 
     * @param user
     * @param lendDate
     * @return
     */
    public final Float calculateFineValue(final Integer daysLate) {
        if (daysLate == null || daysLate <= 0) {
            return 0.0f;
        }

        String fineAmout = Config.getConfigProperty(ConfigurationEnum.FINE_AMOUNT);
        fineAmout = fineAmout.replaceAll("\\.", ",").replaceAll(",", "\\.");
        return daysLate * Float.valueOf(fineAmout);
    }
    
    /**
     * 
     * @param user
     * @param lendDate
     * @return
     */
    public final Integer getDaysLate(final Date expectedReturnDate) {
        Integer diff = DateUtils.dateDiff(expectedReturnDate, new Date());
        return (diff < 0) ? 0 : diff;
    }

    public final boolean isLateReturn(final LendingDTO lending) {
        final Date expectedReturnDate = lending.getReturnDate();

        return this.getDaysLate(expectedReturnDate) > 0;
    }
    
    /**
     * 
     * @param user
     * @param lendDate
     * @return
     */
    public final Date getExpectedReturnDate(final UserDTO user, final Date lendDate) {
        final CirculationBO bo = new CirculationBO();
        final UserTypeDTO type = bo.getUserTypeById(user.getUserType());
        final Integer maxDays = type.getMaxLendingDays();
        return DateUtils.add(lendDate, Calendar.DATE, maxDays);
    }
    
    /**
     * 
     * @param user
     * @return
     */
    public final List<LendingFineDTO> listLendingFines(final UserDTO user) {
        return dao.list(user, false);
    }
    
    public final List<LendingFineDTO> listLendingFines(final UserDTO user, final boolean hidePaid) {
        return dao.list(user, hidePaid);
    }

    public final LendingFineDTO getByHistoryId(final Integer historyId) {
        return dao.getByHistoryId(historyId);
    }

    /**
     * 
     * @param serial
     * @return
     */
    public final LendingFineDTO getById(final Integer serial) {
        return dao.getById(serial);
    }

    
    public final boolean update(final LendingFineDTO fine) {
        return dao.update(fine);
    }
}
