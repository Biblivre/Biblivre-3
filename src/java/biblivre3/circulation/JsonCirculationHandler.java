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

package biblivre3.circulation;

import biblivre3.administration.permission.PermissionBO;
import biblivre3.circulation.lending.LendingBO;
import biblivre3.utils.DateUtils;
import biblivre3.utils.ApplicationConstants;
import biblivre3.utils.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.SuccessDTO;
import mercury.IFJson;
import mercury.LoginDTO;
import mercury.BaseHandler;
import mercury.I18nUtils;
import mercury.RootJsonHandler;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class JsonCirculationHandler extends RootJsonHandler {

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");

        IFJson dto = null;

        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("record")) {
            dto = searchById(request);
        } else if (submitButton.equals("save_user")) {
            dto = saveUser(request);
        } else if (submitButton.equals("delete_user")) {
            dto = deleteUser(request);
        } else if (submitButton.equals("user_history")) {
            dto = getUserHistory(request);
        } else if (submitButton.equals("create_user_card")) {
            dto = createUserCard(request);
        } else if (submitButton.equals("block_user")) {
            dto = blockUser(request, true);
        } else if (submitButton.equals("unblock_user")) {
            dto = blockUser(request, false);
        }

        return dto.toJSONObject(properties);
    }

    public IFJson search(final HttpServletRequest request) {
        String searchName = request.getParameter("SEARCH_NAME");
        
        int searchUserid;

        try {
            searchUserid = Integer.parseInt(request.getParameter("SEARCH_USER_ID"));
        } catch (Exception e) {
            searchUserid = 0;
        }
        
        int offset;

        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }

        CirculationBO cbo = new CirculationBO();
        UsersDTO udto = cbo.list(searchName, searchUserid, offset);

        if (udto != null && udto.size() > 0) {
            return udto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    public IFJson searchById(final HttpServletRequest request) {
        int userid;

        try {
            userid = Integer.parseInt(request.getParameter("userid"));
        } catch (Exception e) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "warning");
        }

        //-- BO searches records...
        CirculationBO cbo = new CirculationBO();
        UserDTO udto = cbo.searchByUserId(userid);

        if (udto != null) {
            return udto;
        } else {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "warning");
        }
    }

    public IFJson saveUser(final HttpServletRequest request) {
        String name = TextUtils.sanitize(request.getParameter("NAME"), "");
        String address = TextUtils.sanitize(request.getParameter("ADDRESS"), "");
        String number = TextUtils.sanitize(request.getParameter("NUMBER"), "");
        String completion = TextUtils.sanitize(request.getParameter("COMPLETION"), "");
        String zipCode = TextUtils.sanitize(request.getParameter("ZIP_CODE"), "");
        String city = TextUtils.sanitize(request.getParameter("CITY"), "");
        String state = TextUtils.sanitize(request.getParameter("STATE"), "");
        String socialIdNumber = TextUtils.sanitize(request.getParameter("SOCIAL_ID_NUMBER"), "");
        String dlicense = TextUtils.sanitize(request.getParameter("DLICENSE"), "");
        String email = TextUtils.sanitize(request.getParameter("EMAIL"), "");
        String telRef1 = TextUtils.sanitize(request.getParameter("TEL_REF_1"), "");
        String telRef2 = TextUtils.sanitize(request.getParameter("TEL_REF_2"), "");
        String birthday = TextUtils.sanitize(request.getParameter("BIRTHDAY"), "");
        String obs = TextUtils.sanitize(request.getParameter("OBS"), "");
        String cellPhone = TextUtils.sanitize(request.getParameter("CEL_PHONE"), "");
        String ramal = TextUtils.sanitize(request.getParameter("RAMAL"), "");
        String userType = TextUtils.sanitize(request.getParameter("USER_TYPE"), "0");
        String photo = TextUtils.sanitize(request.getParameter("NEW_USER_PHOTO"), "");

        if (name.isEmpty()) {
            return new ErrorDTO("ERROR_BLANK_NAME", "error");
        }

        Date parsedDate = DateUtils.verifyDate(birthday, I18nUtils.getText(request.getSession(), "biblivre3", "DEFAULT_DATE_FORMAT"));
        if (parsedDate == null && StringUtils.isNotBlank(birthday)) {
            return new ErrorDTO("ERROR_INVALID_DATE", "error");
        }
        
        if (parsedDate != null) {
            SimpleDateFormat expectedFormat = new SimpleDateFormat("dd/MM/yyyy");
            birthday = expectedFormat.format(parsedDate);
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setName(name);
        userDTO.setAddress(address);
        userDTO.setNumber(number);
        userDTO.setCompletion(completion);
        userDTO.setZip_code(zipCode);
        userDTO.setCity(city);
        userDTO.setState(state);
        userDTO.setSocial_id_number(socialIdNumber);
        userDTO.setDlicense(dlicense);
        userDTO.setEmail(email);
        userDTO.setTelRef1(telRef1);
        userDTO.setTelRef2(telRef2);
        userDTO.setBirthday(birthday);
        userDTO.setObs(obs);
        userDTO.setCellphone(cellPhone);
        userDTO.setExtension_line(ramal);
        userDTO.setUsernameascii(TextUtils.removeDiacriticals(name));
        userDTO.setUserType((new Integer(userType)).intValue());
        userDTO.setPhoto(photo);

        CirculationBO cbo = new CirculationBO();

        String userIdStr = TextUtils.sanitize(request.getParameter("user_id"), "0");
        int userId = (new Integer(userIdStr).intValue());

        if (userId == 0) {
            userId = cbo.addUser(userDTO);

            if (userId != 0) {
                userDTO.setUserid(userId);
                return new SuccessDTO("SUCCESS_ADD_USER");
            }
        } else {
            cbo.updateUser(userDTO, userId);
            userDTO.setUserid(userId);
            return new SuccessDTO("SUCCESS_UPDATE_USER");
        }

        return new SuccessDTO("ERROR_SAVE_USER");
    }

    public IFJson deleteUser(final HttpServletRequest request) {
        int userId;

        try {
            userId = Integer.valueOf(request.getParameter("user_id"));
        } catch (Exception e) {
            userId = 0;
        }


        if (userId == 0) {
            return new ErrorDTO("ERROR_INVALID_USER", "error");
        }

        if (ApplicationConstants.CODE_ADMIN.equals(String.valueOf(userId))) {
            return new ErrorDTO("MESSAGE_CANT_REMOVE_ADMIN", "error");
        }

        CirculationBO circulationBO = new CirculationBO();

        UserDTO userDTO = circulationBO.searchByUserId(userId);
        int loginId = userDTO.getLoginid();

        LoginDTO id = (LoginDTO) request.getSession().getAttribute("LOGGED_USER");
        int loggedId = id.getLoginId();

        if (new LendingBO().getUserLendingCount(userDTO) > 0) {
            return new ErrorDTO("MESSAGE_FAILED_CANNOT_DELETE_USER_WITH_LENDINGS", "error");
        }

        if (loginId == loggedId) {
            return new ErrorDTO("MESSAGE_FAILED_CANNOT_DELETE_YOU_ARE_USING_THIS_ACCOUNT", "error");
        }

        if (loginId != 0) {
            (new PermissionBO()).removeLogin(userDTO);
        }

        circulationBO.deleteUser(userId);

        return new SuccessDTO("SUCCESS_REMOVE_USER");
    }

    public IFJson getUserHistory(final HttpServletRequest request) {
        String param = request.getParameter("userid");
        Integer userid = 0;
        try {
            userid = Integer.valueOf(param);
        } catch (NumberFormatException nfe) {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }

        CirculationBO cbo = new CirculationBO();
        UserHistoryDTO udto = cbo.getUserHistory(userid);

        if (udto != null) {
            return udto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson blockUser(HttpServletRequest request, boolean block) {
        int userid;
        
        try {
            userid = Integer.parseInt(request.getParameter("user_id"));
        } catch (Exception e) {
            userid = 0;
        }

        if (userid == 0) {
            return new ErrorDTO("ERROR_INVALID_USER", "error");
        }
        
        if (ApplicationConstants.CODE_ADMIN.equals(String.valueOf(userid))) {
            return new ErrorDTO("MESSAGE_CANT_BLOCK_ADMIN", "error");
        }

        CirculationBO circulationBO = new CirculationBO();
        UserDTO userDTO = circulationBO.searchByUserId(userid);

        int loginId = userDTO.getLoginid();
        LoginDTO id = (LoginDTO) request.getSession().getAttribute("LOGGED_USER");

        int loggedId = id.getLoginId();
        if (loginId == loggedId) {
            return new ErrorDTO("MESSAGE_FAILED_CANNOT_DELETE_YOU_ARE_USING_THIS_ACCOUNT", "error");
        }

        if (loginId != 0) {
            (new PermissionBO()).removeLogin(userDTO);
        }

        boolean success = circulationBO.blockUser(userid, block);
        if (block) {
            if (success) {
                return new SuccessDTO("SUCCESS_BLOCK_USER");
            } else {
                return new ErrorDTO("ERROR_BLOCK_USER", "error");
            }
        } else {
            if (success) {
                return new SuccessDTO("SUCCESS_UNBLOCK_USER");
            } else {
                return new ErrorDTO("ERROR_UNBLOCK_USER", "error");
            }
        }
    }

    private IFJson createUserCard(HttpServletRequest request) {
        String param = request.getParameter("userid");
        Integer userid = 0;
        try {
            userid = Integer.valueOf(param);
        } catch (NumberFormatException nfe) {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }

        boolean success = false;
        boolean cardExists = false;
        CirculationBO bo = new CirculationBO();
        UserDTO userDto = bo.searchByUserId(userid);
        UserTypeDTO userTypeDto = bo.getUserTypeById(userDto.getUserType());
        String userTypeName = userTypeDto != null ? userTypeDto.getName() : "";

        cardExists = bo.existsUserCard(userDto.getUserid(), userTypeName);

        if (cardExists) {
            return new ErrorDTO("MESSAGE_USER_CARD_ALREADY_IN_QUEUE", "warning");
        } else {
            UserCardDTO userCard = new UserCardDTO();
            userCard.setUserId(userDto.getUserid());
            userCard.setUserName(userDto.getName());
            userCard.setUserType(userTypeName);
            success = bo.insertUserCard(userCard);
        }

        if (success) {
            return new SuccessDTO("SUCCESS_CREATE_USER_CARD");
        } else {
            return new ErrorDTO("ERROR_CREATE_USER_CARD", "warning");
        }
    }


}