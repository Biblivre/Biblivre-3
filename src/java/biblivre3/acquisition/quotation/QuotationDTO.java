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

import java.util.Date;
import java.util.List;
import java.util.Properties;
import mercury.DTO;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuotationDTO extends DTO {
    

    private Integer serial;
    private Integer serialSupplier;
    private Date quotationDate;
    private Date responseDate;
    private Date expirationDate;
    private Integer deliveryTime;
    private String responsible;
    private String obs;
    transient private List<ItemQuotationDTO> itemQuotationList;
    transient private List<String> items;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<ItemQuotationDTO> getItemQuotationList() {
        return itemQuotationList;
    }

    public void setItemQuotationList(List<ItemQuotationDTO> itemQuotationList) {
        this.itemQuotationList = itemQuotationList;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Date getQuotationDate() {
        return quotationDate;
    }

    public void setQuotationDate(Date quotationDate) {
        this.quotationDate = quotationDate;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public Integer getSerialSupplier() {
        return serialSupplier;
    }

    public void setSerialSupplier(Integer serialSupplier) {
        this.serialSupplier = serialSupplier;
    }

    transient private String supplierName;
    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }


    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = super.toJSONObject(properties);
        try {
            if (this.getItems() != null && this.getItems().size() != 0) {
                JSONArray itemArray = new JSONArray(this.getItems());
                json.put("items", itemArray);
            }
        } catch (Exception e) {
            
        }
        return json;
    }



}
