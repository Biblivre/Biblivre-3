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

package biblivre3.cataloging.bibliographic;

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.MaterialType;
import biblivre3.cataloging.holding.HoldingDTO;
import biblivre3.marcutils.MarcReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import mercury.DTO;
import mercury.I18nUtils;
import mercury.IFJson;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.DataField;
import org.marc4j_2_3_1.marc.Subfield;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  11/02/2009
 */
public class RecordDTO extends DTO implements IFJson {
    
    private Integer recordSerial;
    private String iso2709;

    private Date created;
    private Date modified;

    private List<HoldingDTO> holdings;

    private MaterialType materialType;
    private String title;
    private ArrayList<String[]> fields;
    private ArrayList<DataField> links;

    private int totalCount;
    private int availableCount;
    private int lentCount;
    private int reservedCount;

    private String marc;
    private JSONObject json;

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

    public final String getIso2709() {
        return iso2709;
    }

    public final void setIso2709(String iso2709) {
        this.iso2709 = iso2709;
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

    public final MaterialType getMaterialType() {
        return materialType;
    }

    public final void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public final Integer getRecordSerial() {
        return recordSerial;
    }

    public final void setRecordSerial(Integer recordSerial) {
        this.recordSerial = recordSerial;
    }
    
    public final String getFreeMarc() {
        if (this.getIso2709() != null) {
            return MarcReader.iso2709ToMarc(this.getIso2709());
        }
        return "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String[]> getFields() {
        return fields;
    }

    public void setFields(ArrayList<String[]> fields) {
        this.fields = fields;
    }

    public ArrayList<DataField> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<DataField> links) {
        this.links = links;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public int getLentCount() {
        return lentCount;
    }

    public void setLentCount(int lentCount) {
        this.lentCount = lentCount;
    }

    public int getReservedCount() {
        return reservedCount;
    }

    public void setReservedCount(int reservedCount) {
        this.reservedCount = reservedCount;
    }

    public List<HoldingDTO> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<HoldingDTO> holdings) {
        this.holdings = holdings;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject data = new JSONObject();
        try {
            data.putOpt("title", this.getTitle());

            data.putOpt("holdings_count", this.getTotalCount());
            data.putOpt("holdings_available", this.getAvailableCount());
            data.putOpt("holdings_lent", this.getLentCount());
            data.putOpt("holdings_reserved", this.getReservedCount());

            if (this.getMaterialType() != null) {
                data.putOpt("material_type", this.getMaterialType().getCode());
            }

            if (this.getFields() != null) {
                for (String[] field : this.getFields()) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("field", field[0]);
                    jsonField.put("label", I18nUtils.getText(properties, field[0]));
                    jsonField.put("value", field[1]);
                    
                    data.append("fields", jsonField);
                }
            }

            if (this.getLinks() != null) {
                Subfield subF = null;
                Subfield subY = null;
                Subfield subD = null;
                Subfield subU = null;
                
                String file = null;
                String name = null;
                String path = null;
                String uri = null;
                
                for (DataField field : this.getLinks()) {
                    JSONObject jsonLink = new JSONObject();
                    
                    subF = field.getSubfield('f');
                    file = subF == null ? "" : subF.getData();
                    subY = field.getSubfield('y');
                    name  = subY == null ? "" : subY.getData();
                    subD = field.getSubfield('d');
                    path = subD == null ? "" : subD.getData();
                    subU = field.getSubfield('u');
                    uri = subU == null ? "" : subU.getData();

                    if (StringUtils.isBlank(file)) {
                        file = name;
                    }

                    if (StringUtils.isBlank(file)) {
                        file = uri;
                    }

                    if (StringUtils.isNotBlank(file)) {
                        if (path == null || path.isEmpty()) {
                            path = Config.getConfigProperty(ConfigurationEnum.DIGITAL_MEDIA);
                        }
                        if (name == null || name.isEmpty()) {
                            name = file;
                        }

                        jsonLink.put("path", path);
                        jsonLink.put("file", file);
                        jsonLink.put("name", name);
                        jsonLink.put("uri", uri);
                        
                        data.append("links", jsonLink);
                    }
                }
            }

            if (this.getHoldings() != null) {
                for (HoldingDTO dto : this.getHoldings()) {
                    data.append("holdings", dto.toJSONObject(properties));
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
