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
import java.util.List;
import mercury.DAO;
import mercury.ExceptionUser;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  23/03/2009
 */
public class LendingFineDAO extends DAO {
    
    /**
     * 
     * @param fine
     * @return
     */
    public final LendingFineDTO insert(final LendingFineDTO fine) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " INSERT INTO lending_fine " +
                    " (user_serial, lending_history_serial, value, payment) " +
                    " VALUES (?, ?, ?, ?);";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, fine.getUserSerial());
            ppst.setInt(2, fine.getLendingHistorySerial());
            ppst.setFloat(3, fine.getValue());
            ppst.setDate(4, (fine.getPayment() == null) ? null : new java.sql.Date(fine.getPayment().getTime()));
            ppst.executeUpdate();
            
            return fine;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
    }
    
    /**
     * 
     * @param lendingFineId
     * @return
     */
    public final LendingFineDTO getById(final Integer lendingFineId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending_fine " +
                    " WHERE serial = ?;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, lendingFineId);
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

    public final LendingFineDTO getByHistoryId(final Integer historyId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending_fine " +
                    " WHERE lending_history_serial = ?;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, historyId);
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
    public final List<LendingFineDTO> list(final UserDTO user, final boolean hidePaid) {
        Connection con = null;
        List<LendingFineDTO> list = new ArrayList<LendingFineDTO>();
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM lending_fine WHERE user_serial = ? ");

            if (hidePaid) {
                sql.append(" AND payment is null ");
            }

            sql.append(" ORDER BY serial DESC ");
            final PreparedStatement ppst = con.prepareStatement(sql.toString());
            ppst.setInt(1, user.getUserid());
            final ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                list.add(populateDTO(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
        return list;
    }

    /**
     * 
     * @param fine
     * @return
     */
    public final boolean update(LendingFineDTO fine) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " UPDATE lending_fine SET " +
                    " payment = ?, " +
                    " value = ? " +
                    " WHERE serial = ?; ";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setDate(1, this.prepareDate(fine.getPayment()));
            ppst.setFloat(2, fine.getValue());
            ppst.setInt(3, fine.getSerial());
            return ppst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
    }
    
    /**
     * 
     * @param rs
     * @return
     * @throws java.sql.SQLException
     */
    private final LendingFineDTO populateDTO(final ResultSet rs) 
    throws SQLException
    {
        final LendingFineDTO dto = new LendingFineDTO();
        dto.setSerial(rs.getInt("serial"));
        dto.setUserSerial(rs.getInt("user_serial"));
        dto.setLendingHistorySerial(rs.getInt("lending_history_serial"));
        dto.setValue(rs.getFloat("value") );
        dto.setPayment(rs.getDate("payment") );
        return dto;
    }

}
