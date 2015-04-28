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

package biblivre3.acquisition.quotation;

import biblivre3.acquisition.AcquisitionSearchResultsDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.I18nUtils;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonQuotationHandler extends RootJsonHandler {

    private QuotationBO quotationBo;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        this.quotationBo = new QuotationBO();
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
        final QuotationDTO dto = quotationBo.getQuotation(Integer.valueOf(recordId));
        if (dto == null) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }
        return dto;
    }

    private IFJson search(final HttpServletRequest request) {
        String searchTerms = request.getParameter("search_term");
        String searchType = request.getParameter("search_type");
        String searchData = 
                "{" +
                    "\"" + searchType + "\": \"" + searchTerms + "\"" +
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
                dto = quotationBo.listQuotations(offset);
            } else {
                QuotationDTO example = this.populateDtoFromJson(searchData, new QuotationDTO());
                dto = quotationBo.searchQuotation(example, offset);
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
        QuotationDTO dto = new QuotationDTO();
        try {
            dto = this.populateDtoFromJson(data, dto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }
        if (dto.getSerialSupplier() == null || dto.getSerialSupplier() == -1) {
            return new ErrorDTO("MESSAGE_ERROR_SELECT_SUPPLIER", "warning");
        }
        if (dto.getQuotationDate() == null || dto.getResponseDate() == null
                || dto.getExpirationDate() == null || dto.getDeliveryTime() == null) {
            return new ErrorDTO("ERROR_FIELDS_NOT_FILLED", "warning");
        }

        try {
            JSONObject dataObj = new JSONObject(data);
            if (dataObj.has("itemQuotationList")) {
                JSONArray quotationList = dataObj.getJSONArray("itemQuotationList");
                List<ItemQuotationDTO> listItems = new ArrayList<ItemQuotationDTO>();
                for (int i = 0; i < quotationList.length(); i++) {
                    JSONObject jsonObj = quotationList.getJSONObject(i);
                    ItemQuotationDTO iqdto = new ItemQuotationDTO();
                    iqdto.setSerialRequisition(jsonObj.getInt("request_id"));
                    iqdto.setQuotationQuantity(jsonObj.getInt("qtd"));
                    iqdto.setUnitValue(((Double)jsonObj.getDouble("value")).floatValue());
                    listItems.add(iqdto);
                }
                dto.setItemQuotationList(listItems);
            }
        } catch (JSONException je) {}

        if (dto.getItemQuotationList() == null || dto.getItemQuotationList().isEmpty()) {
            return new ErrorDTO("MESSAGE_ERROR_ADD_REQUEST", "warning");
        }
        
        if (id == null || id.equals("0")) {
            result = quotationBo.insert(dto);
        } else {
            dto.setSerial(Integer.valueOf(id));
            result = quotationBo.update(dto);
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
        QuotationDTO dto = new QuotationDTO();
        dto.setSerial(Integer.valueOf(serial));
        if (quotationBo.delete(dto)) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
    }
}
