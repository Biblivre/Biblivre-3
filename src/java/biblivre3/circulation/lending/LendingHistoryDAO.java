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

import biblivre3.circulation.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import mercury.DAO;
import mercury.ExceptionUser;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  23/03/2009
 */
public class LendingHistoryDAO extends DAO {
    
    public final LendingHistoryDTO findLendingHistory(final LendingHistoryDTO dto) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending_history " +
                    " WHERE holding_serial = ? " +
                    " AND user_serial = ? " +
                    " AND lending_date = ? " +
                    " ORDER BY lending_history_serial DESC;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, dto.getHoldingSerial());
            ppst.setInt(2, dto.getUserSerial());
            
            ppst.setDate(3, this.prepareDate(dto.getLendDate()));
            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
        return null;
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public final LendingHistoryDTO getById(final Integer id) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending_history " +
                    " WHERE lending_history_serial = ?; ";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, id);
            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    /**
     * 
     * @param user
     * @return
     */
    public final Collection<LendingHistoryDTO> list(final UserDTO user) {
        Connection con = null;
        Collection<LendingHistoryDTO> list = new ArrayList<LendingHistoryDTO>();
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending_history " +
                    " WHERE user_serial = ? " +
                    " ORDER BY lending_date DESC;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, user.getUserid());
            final ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                list.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public final boolean wasLent(final int serial) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();


            String sql = "SELECT * FROM lending_history WHERE holding_serial = ? LIMIT 1;";

            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, serial);

            final ResultSet rs = ppst.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
        return false;
    }

    public final LendingHistoryDTO getLastByHoldingAndUser(int holdingSerial, int userId) {
        Connection con = null;
        LendingHistoryDTO dto = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending_history " +
                    " WHERE holding_serial = ? AND user_serial = ?" +
                    " ORDER BY lending_history_serial DESC LIMIT 1;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, holdingSerial);
            ppst.setInt(2, userId);

            final ResultSet rs = ppst.executeQuery();

            if (rs.next()) {
                dto = this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    /**
     * 
     * @param rs
     * @return
     * @throws java.sql.SQLException
     */
    private final LendingHistoryDTO populateDTO(final ResultSet rs) throws SQLException {
        final LendingHistoryDTO dto = new LendingHistoryDTO();
        
        dto.setSerial(rs.getInt ("lending_history_serial"));
        dto.setHoldingSerial(rs.getInt ("holding_serial"));
        dto.setUserSerial(rs.getInt ("user_serial"));
        dto.setLendDate(rs.getDate("lending_date"));
        dto.setReturnDate(rs.getDate("return_date"));

        return dto;
    }
}
