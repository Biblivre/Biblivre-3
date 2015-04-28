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

import biblivre3.utils.TextUtils;
import mercury.BaseBO;
import mercury.LoginDTO;




public class LoginBO extends BaseBO {

    public final LoginDTO login(final String login, final String passwd) {
        final LoginDAO loginDao = new LoginDAO();
        final String encpasswd = TextUtils.encodePassword(passwd);

        return loginDao.login(login, encpasswd);
    }

    
/*
    public final boolean createLogin(String login, String password, String retype, String[] permissions, String[] groups, int userid) {
        String encodedPassword = TextUtils.encodePassword(password);
        LoginDTO user = new LoginDTO();
        UserDTO id = new UserDTO();
        user.setEncpwd(encodedPassword);
        user.setLoginName(login);
        id.setUserid(userid);
        LoginDAO loginDao = new LoginDAO();
        boolean update = loginDao.createLogin(user, id);
        final String loginId = loginDao.searchUser(login);
        final Integer loginID = Integer.valueOf(loginId);
        final AuthorizationDAO authDao = new AuthorizationDAO();
        final GroupBO groupBo = new GroupBO();
        if (permissions != null) {
            for (final String actionId : permissions) {
                authDao.insertUserAllowList(Integer.valueOf(actionId), loginID);
            }
        }
        if (groups != null) {
            for (final String groupId : groups) {
                groupBo.insertUserGroup(loginID.toString(), groupId);
            }
        }
        return update;
    }

    public final boolean updateLogin(String login, String password, String retype, String[] permissions, String[] groups, int userid, int loginid ) {
        String encodedPassword = TextUtils.encodePassword(password);
        LoginDTO user = new LoginDTO();
        UserDTO id = new UserDTO();
        user.setEncpwd(encodedPassword);
        user.setLoginName(login);
        id.setUserid(userid);
        id.setLoginid(loginid);
        LoginDAO loginDao = new LoginDAO();
        boolean update = loginDao.updateLogin(user, id);
        final String loginId = loginDao.searchUser(login);
        final Integer loginID = Integer.valueOf(loginId);
        final AuthorizationDAO authDao = new AuthorizationDAO();
        final GroupBO groupBo = new GroupBO();
        if (permissions != null) {
            for (final String actionId : permissions) {
                authDao.insertUserAllowList(Integer.valueOf(actionId), loginID);
            }
        }
        if (groups != null) {
            for (final String groupId : groups) {
                groupBo.insertUserGroup(loginID.toString(), groupId);
            }
        }
        return update;
    }*/

    public final boolean savePassword(Integer loginId, String password) {
        String encodedPassword = TextUtils.encodePassword(password);
        LoginDAO loginDao = new LoginDAO();
        return loginDao.savePassword(loginId, encodedPassword);
    }

    public final LoginDTO getLoginByID(Integer loginId) {
        LoginDAO loginDAO = new LoginDAO();
        return loginDAO.getLoginByID(loginId);
    }

    public final void deleteLogin(Integer loginId) {
        LoginDAO loginDAO = new LoginDAO();
        loginDAO.deleteLogin(loginId);
    }
}
