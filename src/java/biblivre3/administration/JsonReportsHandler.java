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

import biblivre3.administration.reports.dto.AuthorsSearchResultsDTO;
import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UsersDTO;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class JsonReportsHandler extends RootJsonHandler {

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");

        IFJson dto = null;

        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("search_authors")) {
            dto = searchAuthors(request);
        } 

        return dto.toJSONObject(properties);
    }

    public IFJson search(final HttpServletRequest request) {
        String searchName = request.getParameter("SEARCH_NAME");

        int userId;

        try {
            userId = Integer.parseInt(request.getParameter("SEARCH_USER_ID"));
        } catch (Exception e) {
            userId = 0;
        }

        if (StringUtils.isBlank(searchName) && userId == 0) {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }

        int offset;

        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }

        CirculationBO cbo = new CirculationBO();
        UsersDTO udto = cbo.list(searchName, userId, offset);

        if (udto != null && udto.size() > 0) {
            return udto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    public IFJson searchAuthors(final HttpServletRequest request) {
        String searchName = request.getParameter("SEARCH_NAME");
        if (StringUtils.isBlank(searchName) || searchName.trim().length() <= 3) {
            return new ErrorDTO("MESSAGE_SEARCH_MIN_LENGTH", "warning");
        }
        int offset;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }
        ReportsBO bo = new ReportsBO();
        AuthorsSearchResultsDTO dto = bo.searchAuthors(searchName, offset);

        if (dto != null && dto.size() > 0) {
            return dto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }
}