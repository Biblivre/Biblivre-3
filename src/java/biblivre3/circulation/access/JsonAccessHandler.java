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

import biblivre3.administration.cards.CardBO;
import biblivre3.administration.cards.CardDTO;
import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.enums.CardStatus;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class JsonAccessHandler extends RootJsonHandler {

    private AccessBO bo;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        bo = new AccessBO();
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("get_card")) {
            dto = getCard(request);
        } else if (submitButton.equals("lend")) {
            dto = lendCard(request);
        } else if (submitButton.equals("return")) {
            dto = returnCard(request);
        }
        return dto.toJSONObject(properties);
    }

    private IFJson getCard(HttpServletRequest request) {
        String cardNumber = request.getParameter("card_number");
        if (StringUtils.isBlank(cardNumber)) {
            return new ErrorDTO("MESSAGE_ERROR_SELECT_CARD", "warning");
        }
        IFJson dto = new CardBO().getCardByNumber(cardNumber);
        if (dto == null) {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        } else {
            return dto;
        }
    }

    private IFJson lendCard(HttpServletRequest request) {
        String cardIdString = request.getParameter("card_id");
        String userIdString = request.getParameter("user_id");
        if (StringUtils.isBlank(cardIdString)) {
            return new ErrorDTO("MESSAGE_ERROR_SELECT_USER", "warning");
        } else if (StringUtils.isBlank(userIdString)) {
            return new ErrorDTO("MESSAGE_ERROR_SELECT_CARD", "warning");
        }
        UserDTO udto = null;
        try {
            udto = (new CirculationBO()).searchByUserId(Integer.valueOf(userIdString));
        } catch (Exception e) {
        }
        if (udto == null) {
            return new ErrorDTO("MESSAGE_USER_SERIAL_NOT_FOUND", "warning");
        }
        Integer cardId = 0;
        try {
            cardId = Integer.valueOf(cardIdString);
        } catch (Exception e) {
            return new ErrorDTO("MESSAGE_ERROR_SELECT_USER_CARD", "warning");
        }
        CardDTO cardDto = new CardBO().getCardById(cardId);
        if (!cardDto.getStatus().equals(CardStatus.AVAILABLE)) {
            return new ErrorDTO("MESSAGE_CARD_ALREADY_LENT", "warning");
        }
        AccessDTO existingAccess = bo.getByCardId(cardId);
        if (existingAccess != null) {
            return new ErrorDTO("MESSAGE_CARD_ALREADY_LENT", "warning");
        }
        existingAccess = bo.getByUserId(udto.getUserid());
        if (existingAccess != null) {
            return new ErrorDTO("MESSAGE_USER_ALREADY_HAS_CARD", "warning");
        }

        AccessDTO dto = new AccessDTO();
        dto.setSerialCard(cardId);
        dto.setSerialReader(udto.getUserid());
        dto.setEntranceDatetime(new Timestamp(new Date().getTime()));
        if (bo.insert(dto)) {
            return new SuccessDTO("SUCCESS_LEND_CARD");
        } else {
            return new ErrorDTO("ERROR_LEND_CARD", "warning");
        }
    }

    private IFJson returnCard(HttpServletRequest request) {
        String cardIdString = request.getParameter("card_id");
        String userIdString = request.getParameter("user_id");

        Integer cardId = null;
        Integer userId = null;

        try {
            cardId = Integer.valueOf(cardIdString);
        } catch (Exception e) {}

        try {
            userId = Integer.valueOf(userIdString);
        } catch (Exception e) {}

        if (cardId == null && userId == null) {
            return new ErrorDTO("MESSAGE_ERROR_SELECT_CARD", "warning");
        }
        
        if (bo.doExit(cardId, userId)) {
            return new SuccessDTO("SUCCESS_RETURN_CARD");
        } else {
            return new ErrorDTO("ERROR_RETURN_CARD", "warning");
        }
    }

}