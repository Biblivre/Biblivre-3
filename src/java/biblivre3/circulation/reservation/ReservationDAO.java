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
 * @author Danniel Nascimento
 */
public class ReservationDAO extends DAO {

    /**
     * 
     * @param reservationId
     * @return
     */
    public final ReservationDTO getById(final int reservationId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM reservation " +
                    " WHERE reservation_serial = ?;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, reservationId);
            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final ReservationDTO getLastById(final int userId, final int recordSerial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM reservation " +
                    " WHERE userid = ? " +
                    " AND record_serial = ? " +
                    " AND expires > localtimestamp " +
                    " ORDER BY created DESC;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, userId);
            ppst.setInt(2, recordSerial);
            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final List<ReservationDTO> getByUserId(final int userId) {
        Connection con = null;
        List<ReservationDTO> result = new ArrayList<ReservationDTO>();
        try {
            con = getDataSource().getConnection();
            final String sql = " SELECT * FROM reservation WHERE userid = ? AND expires > localtimestamp;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, userId);
            final ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                result.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return result;
    }

    public final List<ReservationDTO> listAll() {
        Connection con = null;
        List<ReservationDTO> result = new ArrayList<ReservationDTO>();
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT R.* FROM reservation R INNER JOIN idx_sort_biblio S "+
                               "ON S.record_serial = R.record_serial WHERE R.expires > localtimestamp ORDER BY S.index_word ASC;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            final ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                result.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return result;
    }

    public final List<ReservationDTO> getByRecordId(final int recordId) {
        Connection con = null;
        List<ReservationDTO> result = new ArrayList<ReservationDTO>();
        try {
            con = getDataSource().getConnection();
            final String sql = " SELECT * FROM reservation WHERE record_serial = ? AND expires > localtimestamp;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, recordId);
            final ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                result.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return result;
    }

    public final int countReservedHoldings(final int recordId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT count(*) FROM reservation WHERE record_serial = ? AND expires > localtimestamp;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, recordId);

            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return 0;
    }


    public final boolean deleteExpired() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM reservation WHERE expires < localtimestamp;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            return ppst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
    }

    public final boolean delete(Integer reservationId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM reservation WHERE reservation_serial = ?;";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, reservationId);
            ppst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return true;
    }

    public final boolean delete(final int userId, final int recordSerial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " DELETE FROM reservation WHERE reservation_serial IN " +
                    " (SELECT reservation_serial FROM reservation WHERE userid = ? " +
                    " AND record_serial = ? " +
                    " AND expires > localtimestamp " +
                    " ORDER BY expires ASC LIMIT 1); ";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, userId);
            ppst.setInt(2, recordSerial);
            ppst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
        } finally {
            closeConnection(con);
        }
        return true;
    }


    public final boolean insert(final ReservationDTO dto) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " INSERT INTO reservation " +
                    " (record_serial, userid, created, expires) " +
                    " VALUES (?, ?, ?, ?); ";
            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, dto.getRecordSerial());
            ppst.setInt(2, dto.getUserid());
            ppst.setTimestamp(3, new java.sql.Timestamp(dto.getCreated().getTime()));
            ppst.setTimestamp(4, new java.sql.Timestamp(dto.getExpires().getTime()));
            return ppst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ReservationDAO");
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
    private ReservationDTO populateDTO(final ResultSet rs) throws SQLException {
        final ReservationDTO dto = new ReservationDTO();
        dto.setReservationSerial(rs.getInt("reservation_serial"));
        dto.setRecordSerial(rs.getInt("record_serial"));
        dto.setUserid(rs.getInt("userid"));
        dto.setCreated(rs.getTimestamp("created"));
        dto.setExpires(rs.getTimestamp("expires"));
        return dto;
    }
}
