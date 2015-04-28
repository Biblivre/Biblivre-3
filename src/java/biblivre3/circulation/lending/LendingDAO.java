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

import biblivre3.circulation.*;
import biblivre3.cataloging.holding.HoldingDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import mercury.DAO;
import mercury.ExceptionUser;

/**
 *
 * @author Danniel Nascimento
 * @since  Mar 17, 2009
 */
public class LendingDAO extends DAO {
    
    /**
     * 
     * @param lendingId
     * @return
     */
    public final LendingDTO getById(final Integer lendingId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending " +
                    " WHERE lending_serial = ?;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, lendingId);
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
     * @param holding
     * @return
     */
    public final LendingDTO getByHolding(final HoldingDTO holding) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending " +
                    " WHERE holding_serial = ? " +
                    " ORDER BY lending_serial DESC;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, holding.getSerial());
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
    
    public final Collection<LendingDTO> list(final UserDTO user) {
        Connection con = null;
        Collection<LendingDTO> list = new ArrayList<LendingDTO>();
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending " +
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

    public final List<LendingDTO> listByRecordSerial(final Integer recordSerial) {
        Connection con = null;
        List<LendingDTO> list = new ArrayList<LendingDTO>();
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM lending l " +
                    " INNER JOIN cataloging_holdings c " +
                    " ON l.holding_serial = c.holding_serial " +
                    " WHERE c.record_serial = ?; ";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, recordSerial);
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


    public final List<LendingDTO> listAll() {
        Connection con = null;
        List<LendingDTO> list = new ArrayList<LendingDTO>();
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM lending ORDER BY return_date ASC;";
            final PreparedStatement ppst = con.prepareStatement(sql);
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
    
    
    /**
     * Gets the number of titles already lent to the specified user.
     * 
     * @param user
     * @return
     */
    public final Integer getUserLendingCount(final UserDTO user) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT COUNT(*) FROM lending " +
                    " WHERE user_serial = ?;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, user.getUserid());
            final ResultSet rs = ppst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            closeConnection(con);
        }
        return 0;
    }

    
    public final boolean insert(final LendingDTO lending) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " INSERT INTO lending " +
                    " (holding_serial, user_serial, " +
                    " lending_date, return_date) " +
                    " VALUES (?, ?, ?, ?) ";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, lending.getHoldingSerial());
            ppst.setInt(2, lending.getUserSerial());
            ppst.setDate(3, new java.sql.Date(lending.getLendDate().getTime()));
            ppst.setDate(4, new java.sql.Date(lending.getReturnDate().getTime()));
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
     * @param lending
     * @return
     */
    public final boolean doReturn(final LendingDTO lending) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            con.setAutoCommit(false);
            final String lendingSql =
                    " DELETE FROM lending " +
                    " WHERE lending_serial = ?;";
            final PreparedStatement lpst = con.prepareStatement(lendingSql);
            lpst.setInt(1, lending.getSerial());
            lpst.executeUpdate();
            
            final String historySql =
                    " INSERT INTO lending_history " +
                    " (holding_serial, user_serial, " +
                    " lending_date, return_date) " +
                    " VALUES (?, ?, ?, ?); ";
            final PreparedStatement hpst = con.prepareStatement(historySql);
            hpst.setInt(1, lending.getHoldingSerial());
            hpst.setInt(2, lending.getUserSerial());
            hpst.setDate(3, new java.sql.Date(lending.getLendDate().getTime()));
            hpst.setDate(4, new java.sql.Date(new Date().getTime()));
            return hpst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rollBack(con);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            commit(con);
            closeConnection(con);
        }
    }
    
    /**
     * 
     * @param lending
     * @return
     */
    public final boolean doRenew(final LendingDTO lending) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            con.setAutoCommit(false);
            final String lendingSql =
                    " DELETE FROM lending " +
                    " WHERE lending_serial = ?;";
            final PreparedStatement lpst = con.prepareStatement(lendingSql);
            lpst.setInt(1, lending.getSerial());
            lpst.executeUpdate();
            
            final String historySql =
                    " INSERT INTO lending_history " +
                    " (holding_serial, user_serial, " +
                    " lending_date, return_date) " +
                    " VALUES (?, ?, ?, ?); ";
            final PreparedStatement hpst = con.prepareStatement(historySql);
            hpst.setInt(1, lending.getHoldingSerial());
            hpst.setInt(2, lending.getUserSerial());
            hpst.setDate(3, new java.sql.Date(lending.getLendDate().getTime()));
            hpst.setDate(4, new java.sql.Date(new Date().getTime()));
            
            final String relendSql =
                    " INSERT INTO lending " +
                    " (holding_serial, user_serial, " +
                    " lending_date, return_date) " +
                    " VALUES (?, ?, ?, ?) ";
            final PreparedStatement rePst = con.prepareStatement(relendSql);
            rePst.setInt(1, lending.getHoldingSerial());
            rePst.setInt(2, lending.getUserSerial());
            rePst.setDate(3, new java.sql.Date(lending.getLendDate().getTime()));
            rePst.setDate(4, new java.sql.Date(lending.getReturnDate().getTime()));
            return rePst.executeUpdate() > 0;
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rollBack(con);
            throw new ExceptionUser("LendingDAO_Exception");
        } finally {
            commit(con);
            closeConnection(con);
        }
    }
    
    public final Integer countLentHoldings(final int recordId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT COUNT(*) FROM lending L INNER JOIN cataloging_holdings H " +
                               "ON L.holding_serial = H.holding_serial " +
                               "WHERE H.record_serial = ? AND H.availability = 0;";

            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, recordId);
            final ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("LENDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return null;
    }
    
    
    /**
     * 
     * @param rs
     * @return
     * @throws java.sql.SQLException
     */
    private final LendingDTO populateDTO(final ResultSet rs) 
    throws SQLException {
        final LendingDTO dto = new LendingDTO();

        dto.setSerial(rs.getInt("lending_serial"));
        dto.setHoldingSerial(rs.getInt("holding_serial"));
        dto.setUserSerial(rs.getInt("user_serial"));
        dto.setLendDate(rs.getDate("lending_date"));
        dto.setReturnDate(rs.getDate("return_date"));
        return dto;
    }

}
