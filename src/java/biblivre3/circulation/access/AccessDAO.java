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

package biblivre3.circulation.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import mercury.DAO;
import mercury.DAOException;
import mercury.ExceptionUser;

public class AccessDAO extends DAO {

    public boolean insert(AccessDTO dto) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                   " INSERT INTO access_control " +
                   " (serial_card, serial_reader, entrance_datetime) " +
                   " VALUES (?, ?, ?);";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, dto.getSerialCard());
            pst.setInt(2, dto.getSerialReader());
            pst.setTimestamp(3, new java.sql.Timestamp(dto.getEntranceDatetime().getTime()));
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public boolean update(AccessDTO dto) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " UPDATE access_control " +
                    " SET departure_datetime = ? " +
                    " WHERE serial = ?;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setTimestamp(1, new java.sql.Timestamp(dto.getDepartureDatetime().getTime()));
            pst.setInt(2, dto.getSerial());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public ArrayList<AccessDTO> list(int offset, int limit) {
        ArrayList<AccessDTO> requestList = new ArrayList<AccessDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM access_control " +
                    " ORDER BY serial ASC offset ? limit ? ";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, offset);
            pst.setInt(2, limit);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return requestList;
            }
            while (rs.next()) {
                AccessDTO dto = this.populateDto(rs);
                requestList.add(dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return requestList;
    }

    public AccessDTO getByCardId(Integer cardId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM access_control " +
                    " WHERE serial_card = ? and " +
                    " departure_datetime is null;";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, cardId);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return null;
            }
            while (rs.next()) {
                return this.populateDto(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public AccessDTO getByUserId(Integer userId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " SELECT * FROM access_control " +
                    " WHERE serial_reader = ? and " +
                    " departure_datetime is null;";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return null;
            }
            while (rs.next()) {
                return this.populateDto(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return null;
    }

    private final AccessDTO populateDto(ResultSet rs) throws Exception {
        final AccessDTO dto = new AccessDTO();
        dto.setSerial(rs.getInt("serial"));
        dto.setSerialCard(rs.getInt("serial_card"));
        dto.setSerialStation(rs.getInt("serial_station"));
        dto.setSerialReader(rs.getInt("serial_reader"));
        dto.setEntranceDatetime(rs.getTimestamp("entrance_datetime"));
        dto.setDepartureDatetime(rs.getTimestamp("departure_datetime"));
        return dto;
    }

    public int getTotalNroRecords() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final Statement st = con.createStatement();
            final String query = "SELECT count(*) FROM access_control;";
            final ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ACCESS_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

}
