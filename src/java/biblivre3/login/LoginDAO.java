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

package biblivre3.login;

import biblivre3.circulation.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import mercury.DAO;
import mercury.DAOException;
import mercury.LoginDTO;
import java.util.*;

public class LoginDAO extends DAO {

    private static final Format ISO8601_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss.SSS");

    public LoginDAO() {
        super();
    }

    public final LoginDTO login(String login, String passwd) {
        try {
            final Connection con = getDataSource().getConnection();
            final String sql =
                    " Select l.loginid, l.loginname, l.encpwd, coalesce(u.username, l.loginname) as username " +
                    " from logins l left join users u on u.loginid = l.loginid where " +
                    " l.loginname = ? and l.encpwd = ?;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, login);
            pst.setString(2, passwd);
            ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                LoginDTO user = new LoginDTO();
                user.setLoginId(rs.getInt(1));
                user.setLoginName(rs.getString(2));
                user.setEncpwd(rs.getString(3));
                user.setFirstName(rs.getString(4));
                con.close();
                return user;
            }
            con.close();
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        }
    }

    public final String searchUser(String loginname) {
        try {
            final Connection con = getDataSource().getConnection();
            final String sql =
                    " Select loginid from logins where " +
                    " loginname = '" + loginname + "';";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs != null && rs.next()) {
                String s = rs.getString("loginid");
                con.close();
                return s != null ? s : "";
            }
            con.close();
            return "";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        }
    }

public int searchLoginByUserName(String username){
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT loginid " +
                    " FROM users " +
                    " WHERE username = ? ;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username.trim());
            ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                return rs.getInt("loginid");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return 0;

    }


    public final LoginDTO getLoginByID(Integer loginId) throws DAOException {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " Select * from logins where " +
                    " loginid = ?;";
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, loginId);
            ResultSet rs = st.executeQuery();
            if (rs != null && rs.next()) {
                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setLoginId(rs.getInt("loginid"));
                loginDTO.setLoginName(rs.getString("loginname"));
                loginDTO.setEncpwd(rs.getString("encpwd"));
                return loginDTO;
            }

            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

    }

    public final boolean createLogin(LoginDTO user, UserDTO id) {
        Connection con = null;        
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " insert into logins (loginname, encpwd) " +
                    " values (?, ?);";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, user.getLoginName());
            pst.setString(2, user.getEncpwd());
            boolean result = pst.executeUpdate() > 0;
            if (result == true) {
                final String sql2 =
                        " UPDATE users SET loginid = (select loginid from logins where loginname = ?)" +
                        " WHERE userid = ?; ";
                PreparedStatement pst2 = con.prepareStatement(sql2);
                String userlogin = user.getLoginName();
                int user_id = id.getUserid();
                pst2.setString(1, user.getLoginName());
                pst2.setInt(2, id.getUserid());
                boolean result2 = pst2.executeUpdate() > 0;
                pst2.executeUpdate();
                if (result2 == true) {
                } else {
                    final String sql3 =
                        " DELETE FROM logins WHERE loginname = ?;";
                PreparedStatement pst3 = con.prepareStatement(sql3);
                pst3.setString(1, user.getLoginName());
                pst3.executeUpdate();
                result = false;
                }
            } else {
                result = false;
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }
    //--------------------------------------------------------------------

    public final boolean updateLogin(LoginDTO user, UserDTO id) {
        String testelogin = user.getLoginName();
        String testeencpaswd = user.getEncpwd();
        int testeidlogin = id.getLoginid();

        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " UPDATE logins SET loginname = ? , encpwd = ? " +
                    " WHERE loginid = ? ;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, user.getLoginName());
            pst.setString(2, user.getEncpwd());
            pst.setInt(3, id.getLoginid());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    //--------------------------------------------------------------------

    public final boolean savePassword(int loginId, String encpwd) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " UPDATE logins SET encpwd = ? " +
                    " WHERE loginid = ? ;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, encpwd);
            pst.setInt(2, loginId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    //--------------------------------------------------------------------

    public final void deleteLogin(Integer loginId) {
        Connection con = null;
        Date date = new Date();
        String timestamp = ISO8601_FORMAT.format(date);

        try {
            con = getDataSource().getConnection();
            final String sql =
                    " UPDATE logins SET encpwd = ?, loginname  = " + timestamp +
                    " WHERE loginid = ? ; ";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, null);
            pst.setInt(2, loginId);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }
//--------------------------------------------------------------------


//--------------------------------------------------------------------
}
