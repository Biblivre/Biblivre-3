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

package biblivre3.cataloging.vocabulary;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import mercury.DTO;
import mercury.I18nUtils;
import org.json.JSONException;
import org.json.JSONObject;


public class VocabularyDTO extends DTO {

    private Integer serial;
    private String iso2709;
    private Date created;
    private Date modified;

    transient private String term;
    transient private String controlField;
    transient private List<String[]> fields;
    transient private String marc;
    transient private JSONObject json;

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getIso2709() {
        return iso2709;
    }

    public void setIso2709(String iso2709) {
        this.iso2709 = iso2709;
    }

    public List<String[]> getFields() {
        return fields;
    }

    public void setFields(List<String[]> fields) {
        this.fields = fields;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getMarc() {
        return marc;
    }

    public void setMarc(String marc) {
        this.marc = marc;
    }

    public String getControlField() {
        return controlField;
    }

    public void setControlField(String controlField) {
        this.controlField = controlField;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject data = super.toJSONObject(properties);

        try {
            data.remove("fields");
            
            if (this.getFields() != null) {
                for (String[] field : this.getFields()) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("field", field[0]);
                    jsonField.put("label", I18nUtils.getText(properties, field[0]));
                    jsonField.put("value", field[1]);

                    data.append("fields", jsonField);
                }
            }
            if (this.getJson() != null) {
                data.put("data", this.getJson());
            }
            if (this.getMarc() != null) {
                data.put("data", this.getMarc());
            }
        } catch (JSONException e) {}

        return data;
    }


}