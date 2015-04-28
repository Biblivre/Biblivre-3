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

package biblivre3.acquisition.request;

import biblivre3.acquisition.AcquisitionSearchResultsDTO;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.I18nUtils;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class JsonRequestHandler extends RootJsonHandler {

    private RequestBO bo;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        this.bo = new RequestBO();
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");

        String defaultFormat = I18nUtils.getText(request.getSession(), "biblivre3", "DEFAULT_DATE_FORMAT");
        this.dateFormatter = new SimpleDateFormat(defaultFormat);
        IFJson dto = null;

        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("open")) {
            dto = getRecord(request);
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("delete")) {
            dto = delete(request);
        }
        return dto.toJSONObject(properties);
    }

    
    private IFJson getRecord(HttpServletRequest request) {
        String recordId = request.getParameter("serial");
        if (recordId == null || recordId.isEmpty()) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");  
        }
        final RequestDTO dto = bo.getRequest(Integer.valueOf(recordId));
        if (dto == null) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }
        return dto;
    }

    private IFJson search(final HttpServletRequest request) {
        String searchTerms = request.getParameter("search_term");
        String searchType = request.getParameter("search_type");
        String status = request.getParameter("search_status");
        String searchData = 
                "{" +
                    "\"" + searchType + "\": \"" + searchTerms + "\", " +
                    "\"status\": \"" + status + "\" " +
                 "}";
        boolean listAll = StringUtils.isBlank(searchTerms);
        int offset;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }
        try {
            AcquisitionSearchResultsDTO dto;
            if (listAll) {
                dto = bo.listRequests(status, offset);
            } else {
                RequestDTO example = this.populateDtoFromJson(searchData, new RequestDTO());
                dto = bo.searchRequest(example, offset);
            }
            if (dto != null && dto.al != null && dto.al.size() > 0) {
                return dto;
            } else {
                return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
            }
        } catch (Exception e) {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson save(final HttpServletRequest request) {
        String data = request.getParameter("data");
        String id   = request.getParameter("serial");
        boolean result = false;
        RequestDTO dto = new RequestDTO();
        try {
            dto = this.populateDtoFromJson(data, dto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }
        if (id == null || id.equals("0")) {
            result = bo.insertRequest(dto);
        } else {
            dto.setSerial(Integer.valueOf(id));
            result = bo.updateRequest(dto);
        }
        if (result) {
            if (id == null || id.equals("0")) {
                return new SuccessDTO("SUCCESS_CREATE_RECORD");
            } else {
                return new SuccessDTO("SUCCESS_UPDATE_RECORD");
            }
        } else {
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }
    }

    private IFJson delete(final HttpServletRequest request) {
        String serial = request.getParameter("serial");
        if (StringUtils.isBlank(serial)) return new ErrorDTO("ERROR", "warning");
        RequestDTO dto = new RequestDTO();
        dto.setSerial(Integer.valueOf(serial));
        if (bo.deleteRequest(dto)) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
    }

}
