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

package biblivre3.administration;

import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserTypeDTO;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.BaseHandler;
import mercury.DTOCollection;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;


public class JsonUserTypeHandler extends RootJsonHandler {

    private CirculationBO circulationBO;
    private AdminBO adminBO;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        circulationBO = new CirculationBO();
        adminBO = new AdminBO();
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("list")) {
            dto = list();
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("delete")) {
            dto = delete(request);
        }
        return dto.toJSONObject(properties);
    }

    private IFJson list() {
        return new DTOCollection().addAll(circulationBO.findAllUserType());
    }

    private IFJson save(HttpServletRequest request) {
        String data = request.getParameter("data");
        String id = request.getParameter("serial");
        boolean isNew = StringUtils.isBlank(id) || id.equals("0");
        boolean result = false;
        UserTypeDTO dto = new UserTypeDTO();
        try {
            dto = populateDtoFromJson(data, dto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }

        if (!validateDto(dto)) {
            return new ErrorDTO("ERROR_FIELDS_NOT_FILLED", "warning");
        }

        if (isNew) {
            adminBO.addUserType(dto);
            result = true;
        } else {
            dto.setSerial(Integer.valueOf(id));
            adminBO.updateUserType(dto);
            result = true;
        }
        if (result) {
            if (isNew) {
                return new SuccessDTO("SUCCESS_CREATE_RECORD");
            } else {
                return new SuccessDTO("SUCCESS_UPDATE_RECORD");
            }
        } else {
            if (isNew) {
                return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
            } else {
                return new ErrorDTO("ERROR_UPDATE_RECORD", "warning");
            }

        }
    }

    private boolean validateDto(UserTypeDTO dto) {
        return
                StringUtils.isNotBlank(dto.getName()) &&
                StringUtils.isNotBlank(dto.getDescription()) &&
                (dto.getMaxLendingCount() != null && dto.getMaxLendingCount() >= 0) &&
                (dto.getMaxLendingDays() != null && dto.getMaxLendingDays() > 0) &&
                (dto.getMaxReservationDays() != null && dto.getMaxReservationDays() > 0);
    }

    private IFJson delete(HttpServletRequest request) {
        String id = request.getParameter("serial");
        Integer serial;
        try {
            serial = Integer.valueOf(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
        Integer userCount = new CirculationBO().countUsersByUserType(serial);
        if (userCount > 0) {
            return new ErrorDTO("MESSAGE_USER_TYPE_IN_USE", "warning");
        } else if (adminBO.deleteUserType(serial)) {
            return new SuccessDTO("SUCCESS_REMOVE_USER_TYPE");
        } else {
            return new ErrorDTO("ERROR_REMOVE_USER_TYPE", "warning");
        }
    }

}