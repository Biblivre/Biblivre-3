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

package biblivre3.administration.permission;

import biblivre3.circulation.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import mercury.DAO;
import mercury.DAOException;

public class PermissionDAO extends DAO {

    public final List<String> getByLoginId(Integer loginid) {
        Connection con = null;
        List<String> list = new ArrayList<String>();
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT loginid, permission FROM permissions WHERE loginid = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, loginid);

            ResultSet rs = pst.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    list.add(rs.getString("permission"));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public final boolean insert(int loginid, String permission) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = "INSERT INTO permissions (loginid, permission) VALUES (?, ?); ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, loginid);
            pstInsert.setString(2, permission);
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public final boolean deletePermissions(Integer loginid) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM permissions WHERE loginid = ?;";
            
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, loginid);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return true;
    }

    public final boolean findLoginName(String loginName) {
        Connection connection = null;
        boolean loginExists = false;

        try {
            connection = getDataSource().getConnection();

            final String sqlSelectLogin = "SELECT * FROM logins WHERE loginname = ?;";

            PreparedStatement pstSelectLogin = connection.prepareStatement(sqlSelectLogin);
            pstSelectLogin.setString(1, loginName);
            ResultSet rs = pstSelectLogin.executeQuery();

            if (rs != null && rs.next()) {
                loginExists = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return loginExists;
    }

    public final synchronized void createLogin(UserDTO userDTO, String loginName, String password) {
        Connection connection = null;
        int login_id = 0;

        try {
            connection = getDataSource().getConnection();

            final String sqlInsertLogin = "INSERT INTO logins (loginname, encpwd) VALUES (?, ?);";

            PreparedStatement pstInsertLogin = connection.prepareStatement(sqlInsertLogin);
            pstInsertLogin.setString(1, loginName);
            pstInsertLogin.setString(2, password);
            pstInsertLogin.executeUpdate();

            final String sqlSelectLogin = "SELECT loginid FROM logins WHERE loginname = ?;";

            PreparedStatement pstSelectLogin = connection.prepareStatement(sqlSelectLogin);
            pstSelectLogin.setString(1, loginName);
            ResultSet rs = pstSelectLogin.executeQuery();

            if (rs != null && rs.next()) {
                login_id = rs.getInt("loginid");
            }

            if (login_id != 0) {
                final String sqlUpdateUser = "UPDATE users SET loginid = ? WHERE userid = ?;";

                PreparedStatement pstUpdateUser = connection.prepareStatement(sqlUpdateUser);
                pstUpdateUser.setInt(1, login_id);
                pstUpdateUser.setInt(2, userDTO.getUserid());
                pstUpdateUser.executeUpdate();

                userDTO.setLoginid(login_id);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(connection);
        }
    }

    public final synchronized void removeLogin(UserDTO userDTO) {
        Connection connection = null;
        int login_id = 0;

        try {
            connection = getDataSource().getConnection();

            final String sqlSelectLogin = "SELECT loginid FROM users WHERE userid = ?;";

            PreparedStatement pstSelectLogin = connection.prepareStatement(sqlSelectLogin);
            pstSelectLogin.setInt(1, userDTO.getUserid());

            ResultSet rs = pstSelectLogin.executeQuery();

            if (rs != null && rs.next()) {
                login_id = rs.getInt("loginid");
            }

            if (login_id != 0) {
                final String sqlUpdateUser = "UPDATE users SET loginid = null WHERE userid = ?;";

                PreparedStatement pstUpdateUser = connection.prepareStatement(sqlUpdateUser);
                pstUpdateUser.setInt(1, userDTO.getUserid());
                pstUpdateUser.executeUpdate();

                final String sqlInsertLogin = "DELETE FROM logins WHERE loginid = ?;";

                PreparedStatement pstInsertLogin = connection.prepareStatement(sqlInsertLogin);
                pstInsertLogin.setInt(1, login_id);
                pstInsertLogin.executeUpdate();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(connection);
        }
    }

    public final synchronized void updatePassword(UserDTO userDTO, String loginName, String password) {
        Connection conUpdateLogin = null;

        try {

            conUpdateLogin = getDataSource().getConnection();

            final String sqlUpdateUser = "UPDATE logins SET encpwd = ? WHERE loginname = ?;";

            PreparedStatement pstUpdateUser = conUpdateLogin.prepareStatement(sqlUpdateUser);

            pstUpdateUser.setString(1, password);
            pstUpdateUser.setString(2, loginName);

            pstUpdateUser.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conUpdateLogin);
        }
    }
}
