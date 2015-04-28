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

package biblivre3.administration;

import biblivre3.circulation.UserTypeDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mercury.DAO;
import mercury.DAOException;
import mercury.ExceptionUser;

public class AdminDAO extends DAO {

    public void addUserType(UserTypeDTO uDTO) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO users_type(description, number_max_itens, time_returned, usertype, max_reservation_days) " +
                               "VALUES (?, ?, ?, ?, ?); ";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, uDTO.getDescription());
            pst.setInt(2, uDTO.getMaxLendingCount());
            pst.setInt(3, uDTO.getMaxLendingDays());
            pst.setString(4, uDTO.getName());
            pst.setInt(5, uDTO.getMaxReservationDays());
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public final boolean deleteUserType(int serial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            String sqlupdate = "UPDATE users SET user_type = 0 WHERE user_type = ?;";
            String sql = "DELETE FROM users_type WHERE serial = ?;";
                         
            PreparedStatement pstupdate = con.prepareStatement(sqlupdate);
            PreparedStatement pst = con.prepareStatement(sql);

            pstupdate.setInt(1, serial);
            pst.setInt(1, serial);

            pstupdate.executeUpdate();
            
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public boolean isIndexOutdated() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sqlAuthA = "SELECT count(*) FROM idx_sort_authorities;";
            final String sqlAuthB = "SELECT count(*) FROM cataloging_authorities;";

            final String sqlBiblioA = "SELECT count(*) FROM idx_sort_biblio;";
            final String sqlBiblioB = "SELECT count(*) FROM cataloging_biblio;";

            final String sqlVocabularyA = "SELECT count(*) FROM idx_sort_vocabulary;";
            final String sqlVocabularyB = "SELECT count(*) FROM cataloging_vocabulary;";

            ResultSet rsAuthA = con.createStatement().executeQuery(sqlAuthA);
            ResultSet rsAuthB = con.createStatement().executeQuery(sqlAuthB);
            
            ResultSet rsBiblioA = con.createStatement().executeQuery(sqlBiblioA);
            ResultSet rsBiblioB = con.createStatement().executeQuery(sqlBiblioB);

            ResultSet rsVocabularyA = con.createStatement().executeQuery(sqlVocabularyA);
            ResultSet rsVocabularyB = con.createStatement().executeQuery(sqlVocabularyB);

            int countAuthA = 0;
            int countAuthB = 0;
            int countBiblioA = 0;
            int countBiblioB = 0;
            int countVocabularyA = 0;
            int countVocabularyB = 0;

            if (rsAuthA.next()) {
                countAuthA = rsAuthA.getInt(1);
            }
            if (rsAuthB.next()) {
                countAuthB = rsAuthB.getInt(1);
            }

            if (rsBiblioA.next()) {
                countBiblioA = rsBiblioA.getInt(1);
            }
            if (rsBiblioB.next()) {
                countBiblioB = rsBiblioB.getInt(1);
            }

            if (rsVocabularyA.next()) {
                countVocabularyA = rsVocabularyA.getInt(1);
            }
            if (rsVocabularyB.next()) {
                countVocabularyB = rsVocabularyB.getInt(1);
            }
            return (countAuthA != countAuthB) || (countBiblioA != countBiblioB) || (countVocabularyA != countVocabularyB);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public void updateUserType(UserTypeDTO userTypeDTO) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "UPDATE users_type SET " +
                               "description = ?, number_max_itens = ?, time_returned = ?, " +
                               "usertype = ?, max_reservation_days = ? WHERE serial = ?;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, userTypeDTO.getDescription());
            pst.setInt(2, userTypeDTO.getMaxLendingCount());
            pst.setInt(3, userTypeDTO.getMaxLendingDays());
            pst.setString(4, userTypeDTO.getName());
            pst.setInt(5, userTypeDTO.getMaxReservationDays());
            pst.setInt(6, userTypeDTO.getSerial());

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public UserTypeDTO findUserTypeBySerial(int serial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT serial, description, number_max_itens, time_returned, max_reservation_days, usertype " +
                               "FROM users_type WHERE serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, serial);

            final ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                UserTypeDTO userTypeDTO = new UserTypeDTO();
                userTypeDTO.setSerial(rs.getInt("serial"));
                userTypeDTO.setDescription(rs.getString("description"));
                userTypeDTO.setMaxLendingCount(rs.getInt("number_max_itens"));
                userTypeDTO.setMaxLendingDays(rs.getInt("time_returned"));
                userTypeDTO.setMaxReservationDays(rs.getInt("max_reservation_days"));
                userTypeDTO.setName(rs.getString("usertype"));
                return userTypeDTO;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final Date getLastBackupDate() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = "SELECT MAX(backuped) as backuped FROM backups; ";

            final PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                return rs.getDate("backuped");
            }
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.getLastBackupDate");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final List<Date> getLastFiveBackups() {
        List<Date> dates = new ArrayList<Date>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = "SELECT backuped FROM backups order by backuped desc limit 5; ";
            final PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    dates.add(rs.getTimestamp("backuped"));
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.getLastFiveBackups");
        } finally {
            closeConnection(con);
        }
        return dates;
    }

    public final boolean insertLastBackupDate(Date newDate) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = "INSERT INTO backups (backuped) VALUES (?);";
            final PreparedStatement pst = con.prepareStatement(sql);
            newDate = (newDate != null) ? newDate : new Date();

            pst.setTimestamp(1, new Timestamp(newDate.getTime()));
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.insertLastBackupDate");
        } finally {
            closeConnection(con);
        }
    }

    public String getDatabaseVersion() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT version() as version;";

            final PreparedStatement pst = con.prepareStatement(sql);
            final ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getString("version");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return "";
    }
}
