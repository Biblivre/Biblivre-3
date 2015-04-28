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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.BaseHandler;
import mercury.Dialog;
import mercury.I18nUtils;
import mercury.MemoryFileDTO;

public class UserCardsHandler extends BaseHandler {

    @Override
    public String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> requestParametersHash) {

        if (submitButton.equals("GENERATE_USER_CARDS_DATE")) {
            String startDate = request.getParameter("startDate");
            String finalDate = request.getParameter("endDate");

            String defaultFormat = I18nUtils.getText(request.getSession(), "biblivre3", "DEFAULT_DATE_FORMAT");

            Date parsedStartDate = null;
            Date parsedFinalDate = null;
            try {
                DateFormat formatter = new SimpleDateFormat(defaultFormat);
                formatter.setLenient(false);
                parsedStartDate = (Date) formatter.parse(startDate);
                parsedFinalDate = (Date) formatter.parse(finalDate);
            } catch (Exception e) {
                Dialog.showWarning(request, "MESSAGE_ERROR_INVALID_DATES");
                return "/jsp/circulation/user_cards.jsp";
            }

            int generatedLabels[] = new CirculationBO().generateUserCardsByDate(parsedStartDate, parsedFinalDate);

            if (generatedLabels[0] == 0) {
                Dialog.showWarning(request, "MESSAGE_NO_USERS_IN_SELECTED_RANGE");
            } else if (generatedLabels[1] == 0) {
                Dialog.showWarning(request, "MESSAGE_USER_CARDS_IN_RANGE_ARE_ALREADY_IN_LIST");
            } else {
                Dialog.showNormal(request, "SUCCESS_ADD_USER_CARD");
            }

            return "/jsp/circulation/user_cards.jsp";

        } else if (submitButton.equals("LIST_ALL_PENDING_USER_CARDS")) {
            ArrayList<UserCardDTO> list = new CirculationBO().listPendingUserCards();
            request.setAttribute("LIST_ALL_USER_CARDS", list);

            if (list.isEmpty()) {
                Dialog.showNormal(request, "MESSAGE_EMPTY_USER_CARD_LIST");
            }
            
            return "/jsp/circulation/user_cards.jsp";

        } else if (submitButton.equals("DELETE_USER_CARD")) {
            final String[] labels = request.getParameterValues("USER_CARD_SELECT");

            if (labels != null) {
                if (new CirculationBO().deleteUserCards(labels)) {
                    ArrayList<UserCardDTO> list = new CirculationBO().listPendingUserCards();
                    request.setAttribute("LIST_ALL_USER_CARDS", list);
                    Dialog.showNormal(request, "SUCCESS_DELETE_USER_CARD");
                }
            } else {
                Dialog.showWarning(request, "ERROR_DELETE_USER_CARD");
            }

            return "/jsp/circulation/user_cards.jsp";

        } else if (submitButton.equals("RECORD_FILE_PDF")) {
            //FIXME: Remove those damn DAOs from here.
            String dellabel = request.getParameter("dellabel");
            final String[] userCards = request.getParameterValues("USER_CARD_SELECT");
            final String startOffset = request.getParameter("START_OFFSET");

            if (userCards != null) {
                CirculationBO bo = new CirculationBO();
                ArrayList<UserCardDTO> cardList = bo.listSelectedUserCards(userCards);
                MemoryFileDTO pdfFile = bo.createFileUserCardsPDF(cardList, Integer.parseInt(startOffset), getI18nProperties(request, "biblivre3"));
                if (pdfFile != null) {
                    Dialog.showNormal(request, "SUCCESS_CREATE_PDF");
                    if (dellabel != null) {
                        new CirculationBO().deleteUserCards(userCards);
                    }
                    ArrayList<UserCardDTO> list = new CirculationBO().listPendingUserCards();
                    request.setAttribute("LIST_ALL_USER_CARDS", list);
                    request.setAttribute("FILE_DOWNLOAD_URL", pdfFile.getFileName());
                    request.getSession().setAttribute(pdfFile.getFileName(), pdfFile);
                }
            } else {
                Dialog.showNormal(request, "ERROR_CREATE_PDF");
            }
            return "/jsp/circulation/user_cards.jsp";

        } else if (submitButton.equals("DOWNLOAD_USER_CARDS_FILE")) {
            final String fileName = request.getParameter("FILE_NAME");
            if (fileName != null) {
                MemoryFileDTO file = (MemoryFileDTO)request.getSession().getAttribute(fileName);
                this.returnFile(file, response);
                return "x-download";
            } else {
                Dialog.showNormal(request, "ERROR_CREATE_PDF");
                return "/jsp/circulation/user_cards.jsp";
            }
        } else {
            return "";
        }
    }

}
