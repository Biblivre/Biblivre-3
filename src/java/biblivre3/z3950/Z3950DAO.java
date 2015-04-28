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


package biblivre3.z3950;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mercury.DAO;
import mercury.DAOException;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  10/02/2009
 */
public class Z3950DAO extends DAO {

    public final List<Z3950ServerDTO> listServers() {
        final List<Z3950ServerDTO> list = new ArrayList<Z3950ServerDTO>();
        Connection con = null;
        final String sql = "SELECT * FROM z3950_server ORDER BY server_id ASC; ";
        try {
            con = getDataSource().getConnection();
            final PreparedStatement pstt = con.prepareStatement(sql);
            final ResultSet rs = pstt.executeQuery();
            while (rs.next()) {
                list.add(this.populateServer(rs));
            }
        } catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
            throw new DAOException(sqle.getMessage());
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public final Z3950ServerDTO findById(Z3950ServerDTO dto) {
        Connection con = null;
        final String sql = "SELECT * FROM z3950_server WHERE server_id = ?; ";
        try {
            con = getDataSource().getConnection();
            final PreparedStatement pstt = con.prepareStatement(sql);
            pstt.setInt(1, dto.getServerId());
            final ResultSet rs = pstt.executeQuery();
            while (rs.next()) {
                return this.populateServer(rs);
            }
        } catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
            throw new DAOException(sqle.getMessage());
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final boolean insert(Z3950ServerDTO dto) {
        Connection con = null;
        final String sql =
                " INSERT INTO z3950_server " +
                " (server_name, server_url, server_port, server_dbname, server_charset) " +
                " VALUES (?, ?, ?, ?, ?); ";
        try {
            con = getDataSource().getConnection();
            final PreparedStatement pstt = con.prepareStatement(sql);
            pstt.setString(1, dto.getName());
            pstt.setString(2, dto.getUrl() );
            pstt.setInt(3, dto.getPort());
            pstt.setString(4, dto.getCollection());
            pstt.setString(5, dto.getCharset());
            final Integer result = pstt.executeUpdate();
            return result > 0;
        } catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
            throw new DAOException(sqle.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public final boolean update(Z3950ServerDTO dto) {
        Connection con = null;
        final String sql =
                " UPDATE z3950_server " +
                " SET server_name = ?, " +
                " server_url = ?," +
                " server_port = ?, " +
                " server_dbname = ?, " +
                " server_charset = ? " +
                " WHERE server_id = ?; ";
        try {
            con = getDataSource().getConnection();
            final PreparedStatement pstt = con.prepareStatement(sql);
            pstt.setString(1, dto.getName());
            pstt.setString(2, dto.getUrl ());
            pstt.setInt(3, dto.getPort());
            pstt.setString(4, dto.getCollection());
            pstt.setString(5, dto.getCharset());
            pstt.setInt(6, dto.getServerId());
            final Integer result = pstt.executeUpdate();
            return result > 0;
        } catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
            throw new DAOException(sqle.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public final boolean delete(Z3950ServerDTO dto) {
        Connection con = null;
        final String sql =
                " DELETE FROM z3950_server " +
                " WHERE server_id = ?; ";
        try {
            con = getDataSource().getConnection();
            final PreparedStatement pstt = con.prepareStatement(sql);
            pstt.setInt(1, dto.getServerId());
            final Integer result = pstt.executeUpdate();
            return result > 0;
        } catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
            throw new DAOException(sqle.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    private Z3950ServerDTO populateServer(final ResultSet rs)
    throws SQLException
    {
        Z3950ServerDTO dto = new Z3950ServerDTO();
        dto.setServerId(rs.getInt("server_id"));
        dto.setName(rs.getString("server_name").trim());
        dto.setUrl(rs.getString("server_url").trim());
        dto.setPort(rs.getInt("server_port"));
        dto.setCollection(rs.getString("server_dbname"));
        dto.setCharset(rs.getString("server_charset"));
        return dto;
    }

}
