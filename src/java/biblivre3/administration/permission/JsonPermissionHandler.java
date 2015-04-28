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

import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.utils.TextUtils;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.json.JSONObject;

public class JsonPermissionHandler extends RootJsonHandler {

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("open")) {
            dto = open(request);
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("remove")) {
            dto = remove(request);
        }
        return dto.toJSONObject(properties);
    }

    private IFJson open(HttpServletRequest request) {
        int userid;

        try {
            userid = Integer.parseInt(request.getParameter("userid"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_USER", "warning");
        }

        CirculationBO cbo = new CirculationBO();
        UserDTO userDTO = cbo.searchByUserId(userid);

        if (userDTO == null) {
            return new ErrorDTO("ERROR_INVALID_USER", "warning");
        }

        int loginid = userDTO.getLoginid();

        PermissionDTO dto = new PermissionDTO();
        dto.setUser(userDTO);

        if (loginid > 0) {
            final PermissionBO bo = new PermissionBO();
            final List<String> list = bo.getByLoginId(loginid);

            if (list != null) {
                dto.setPermissions(list);
            }
        }

        return dto;
    }

    private IFJson save(final HttpServletRequest request) {
        int userid;

        try {
            userid = Integer.parseInt(request.getParameter("userid"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_USER", "warning");
        }

        CirculationBO cbo = new CirculationBO();
        UserDTO userDTO = cbo.searchByUserId(userid);

        if (userDTO == null) {
            return new ErrorDTO("ERROR_INVALID_USER", "warning");
        }

        String login = TextUtils.sanitize(request.getParameter("login"), "");
        String password = TextUtils.sanitize(request.getParameter("password"), "");
        String password2 = TextUtils.sanitize(request.getParameter("password_2"), "");

        String currentLogin = userDTO.getLoginName();
        boolean newLogin = (currentLogin == null || currentLogin.isEmpty());

        PermissionBO pbo = new PermissionBO();

        if (newLogin && pbo.findLoginName(login)) {
            return new ErrorDTO("ERROR_LOGIN_ALREADY_EXISTS", "warning");
        }

        if (newLogin && login.isEmpty()) {
            return new ErrorDTO("ERROR_EMPTY_LOGIN", "warning");
        }

        if (newLogin && password.isEmpty()) {
            return new ErrorDTO("ERROR_PASSWORDS_DONT_MATCH", "warning");
        }

        if (!newLogin && !login.isEmpty()) {
            return new ErrorDTO("ERROR_USER_ALREADY_HAVE_LOGIN_NAME", "warning");
        }

        if (!newLogin && !password.isEmpty() && !password.equals(password2)) {
            return new ErrorDTO("ERROR_PASSWORDS_DONT_MATCH", "warning");
        }

        if (newLogin) {
            pbo.createLogin(userDTO, login, TextUtils.encodePassword(password));
        } else if (!password.isEmpty()) {
            pbo.updatePassword(userDTO, userDTO.getLoginName(), TextUtils.encodePassword(password));
        }

        String[] permissions = request.getParameterValues("permissions");
        boolean result = pbo.save(userDTO.getLoginid(), permissions);

        if (result) {
            if (newLogin) {
                return new SuccessDTO("SUCCESS_CREATE_LOGIN");
            } else if (!password.isEmpty()) {
                return new SuccessDTO("SUCCESS_PASSWORD_CHANGED");
            } else {
                return new SuccessDTO("SUCCESS_PERMISSIONS_CHANGED");
            }
        } else {
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }
    }

    private IFJson remove(final HttpServletRequest request) {
        int userid;

        try {
            userid = Integer.parseInt(request.getParameter("userid"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_INVALID_USER", "warning");
        }

        CirculationBO cbo = new CirculationBO();
        UserDTO userDTO = cbo.searchByUserId(userid);

        if (userDTO == null) {
            return new ErrorDTO("ERROR_INVALID_USER", "warning");
        }

        (new PermissionBO()).removeLogin(userDTO);

        return new SuccessDTO("SUCCESS_REMOVE_LOGIN");

    }
}
