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

package biblivre3.administration.cards;

import biblivre3.circulation.access.AccessBO;
import biblivre3.enums.CardStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.LoginDTO;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class JsonCardHandler extends RootJsonHandler {

    private CardBO bo;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        bo = new CardBO();
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("add_card")) {
            dto = addCard(request);
        } else if (submitButton.equals("add_card_list")) {
            dto = addCardList(request);
        } else if (submitButton.equals("delete_card")) {
            dto = delete(request);
        } else if (submitButton.equals("search_cards")) {
            dto = search(request);
        } else if (submitButton.equals("block_card")) {
            dto = changeCardStatus(request, true);
        } else if (submitButton.equals("unblock_card")) {
            dto = changeCardStatus(request, false);
        }
        return dto.toJSONObject(properties);
    }

    private IFJson search(HttpServletRequest request) {
        String searchTerms = request.getParameter("SEARCH_TERM");
        boolean listAll = StringUtils.isBlank(searchTerms);
        int offset;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }
        CardSearchResultsDTO dto;
        if (listAll) {
            dto = bo.list(offset);
        } else {
            dto = bo.searchCards(searchTerms, offset);
        }
        if (dto != null && dto.al != null) {
            return dto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson addCard(HttpServletRequest request) {
        String newCard = request.getParameter("newCard");
        if (StringUtils.isBlank(newCard)) {
            return new ErrorDTO("MESSAGE_EMPTY_CARD_NUMBER", "warning");
        }
        CardDTO cardDTO = this.createCard(request, newCard);
        CardDTO existingCard = bo.getCardByNumber(newCard);
        if (existingCard != null) {
            return new ErrorDTO("MESSAGE_CARD_ALREADY_EXISTS", "warning");
        }
        if (bo.addCard(cardDTO)) {
            return new SuccessDTO("SUCCESS_CREATE_RECORD");
        } else {
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }
    }

    private IFJson addCardList(HttpServletRequest request) {
        String prefix = request.getParameter("prefix");
        String suffix = request.getParameter("suffix");
        String startString = request.getParameter("start");
        String endString = request.getParameter("end");
        int start = 0;
        int end = 0;
        try {
            start = Integer.parseInt(startString);
            end = Integer.parseInt(endString);
        } catch (Exception e) {
            return new ErrorDTO("MESSAGE_ERROR_CARD_START_END_NUMBERS", "warning");
        }
        List<String> cardNumbers = new ArrayList<String>();
        int prefixPadding = startString.length();

        for (int i = start; i <= end; i++) {
            String number = String.valueOf(i);

            while (number.length() < prefixPadding) {
                number = "0" + number;
            }

            cardNumbers.add(prefix + number + suffix);
        }
        for (String number : cardNumbers) {
            CardDTO existingCard = bo.getCardByNumber(number);
            if (existingCard != null) {
                return new ErrorDTO("MESSAGE_ERROR_CARD_CARD_EXISTS", "warning");
            }
        }
        Date today = new Date();
        List<CardDTO> cardList = new ArrayList<CardDTO>();
        for (String number : cardNumbers) {
            CardDTO dto = this.createCard(request, number);
            dto.setDateTime(today);
            cardList.add(dto);
        }
        if (bo.addSequenceCard(cardList)) {
            return new SuccessDTO("SUCCESS_CREATE_RECORD");
        } else {
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }
    }

    private IFJson delete(HttpServletRequest request) {
        String param = request.getParameter("card_id");
        if (StringUtils.isBlank(param)) {
            return new ErrorDTO("MESSAGE_ERROR_CARD_SELECT_CARD", "warning");
        }
        Integer id = Integer.valueOf(param);
        if (new AccessBO().getByCardId(id) != null) {
            return new ErrorDTO("MESSAGE_CARD_ALREADY_LENT", "warning");
        }
        
        if (bo.removeCard(id)) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
    }

    private IFJson changeCardStatus(HttpServletRequest request, boolean block) {
        String param = request.getParameter("card_id");
        CardStatus newStatus = block ? CardStatus.BLOCKED : CardStatus.AVAILABLE;
        Integer id = Integer.valueOf(param);
        if (bo.updateCardStatus(newStatus, id)) {
            return new SuccessDTO("SUCCESS_UPDATE_RECORD");
        } else {
            return new ErrorDTO("ERROR_UPDATE_RECORD", "warning");
        }
    }

    private final CardDTO createCard(HttpServletRequest request, String cardNumber) {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardNumber(cardNumber);
        cardDTO.setStatus(CardStatus.AVAILABLE);
        cardDTO.setDateTime(new Date());
        LoginDTO user = (LoginDTO) request.getSession().getAttribute("LOGGED_USER");
        cardDTO.setUserid(user.getLoginId());
        return cardDTO;
    }

}