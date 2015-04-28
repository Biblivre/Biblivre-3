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

import java.util.Properties;
import mercury.DTO;
import org.json.JSONObject;

public class ItemQuotationDTO extends DTO {

    private Integer serialRequisition;
    private Integer serialQuotation;
    private Integer quotationQuantity;
    private Float unitValue;
    private Integer responseQuantity;

    public Integer getQuotationQuantity() {
        return quotationQuantity;
    }

    public void setQuotationQuantity(Integer quotationQuantity) {
        this.quotationQuantity = quotationQuantity;
    }

    public Integer getResponseQuantity() {
        return responseQuantity;
    }

    public void setResponseQuantity(Integer responseQuantity) {
        this.responseQuantity = responseQuantity;
    }

    public Integer getSerialQuotation() {
        return serialQuotation;
    }

    public void setSerialQuotation(Integer serialQuotation) {
        this.serialQuotation = serialQuotation;
    }

    public Integer getSerialRequisition() {
        return serialRequisition;
    }

    public void setSerialRequisition(Integer serialRequisition) {
        this.serialRequisition = serialRequisition;
    }

    public Float getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(Float unitValue) {
        this.unitValue = unitValue;
    }


    transient private String title;
    public String getTitle() {
        return (title == null) ? "" : title.trim();
    }
    public void setTitle(String title) {
        this.title = title;
    }

    transient private String author;
    public String getAuthor() {
        return (author == null) ? "" : title.trim();
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = super.toJSONObject(properties);
        try {
            json.put("request_id", getSerialRequisition());
            json.put("quotation_id", getSerialQuotation());
            json.put("qtd", getQuotationQuantity());
            json.put("value", getUnitValue());
            json.put("response_qtd", getResponseQuantity());
        } catch (Exception e) {}
        return json;
    }

}
