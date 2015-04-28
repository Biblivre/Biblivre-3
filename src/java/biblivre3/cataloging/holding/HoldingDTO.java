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


package biblivre3.cataloging.holding;

import biblivre3.enums.Availability;
import biblivre3.enums.Database;
import biblivre3.utils.NaturalOrderComparator;
import java.util.Date;
import java.util.Properties;
import mercury.DTO;
import mercury.I18nUtils;
import mercury.IFJson;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  09/03/2009
 */
public class HoldingDTO extends DTO implements IFJson, Comparable<Object> {
    
    private Integer serial;
    private Integer recordSerial;
    private String assetHolding;
    private String locationD;
    private Database database;

    private Date created;
    private Date modified;

    private String iso2709;

    private Boolean lent;
    private Availability availability;

    private String[] location;

    private String marc;
    private JSONObject json;


    public final Availability getAvailability() {
        return availability;
    }

    public final void setAvailability(Availability availability) {
        this.availability = availability;
    }
    
    public final Integer getRecordSerial() {
        return recordSerial;
    }

    public final void setRecordSerial(Integer recordSerial) {
        this.recordSerial = recordSerial;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
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

    public final String getIso2709() {
        return iso2709;
    }

    public final void setIso2709(String iso2709) {
        this.iso2709 = iso2709;
    }

    public final Integer getSerial() {
        return serial;
    }

    public final void setSerial(Integer serial) {
        this.serial = serial;
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

    public Boolean getLent() {
        return lent;
    }

    public void setLent(Boolean lent) {
        this.lent = lent;
    }

    public String[] getLocation() {
        return location;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }

    public String getAssetHolding() {
        return assetHolding;
    }

    public void setAssetHolding(String assetHolding) {
        this.assetHolding = assetHolding;
    }

    public String getLocationD() {
        return locationD;
    }

    public void setLocationD(String locationD) {
        this.locationD = locationD;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject data = new JSONObject();
        try {
            data.put("serial", this.getSerial());
            data.putOpt("created", this.getCreated());

            if (this.getAvailability() != null) {
                data.putOpt("available_text", I18nUtils.getText(properties, this.getAvailability().getLabel()));
                data.putOpt("available", this.getAvailability().ordinal());
            }

            if (this.getLent() != null) {
                String lentLabel = this.lent ? I18nUtils.getText(properties, "LABEL_IS_LENT") : I18nUtils.getText(properties, "LABEL_IS_NOT_LENT");
                data.putOpt("lent", lentLabel);
            }

            final String iLocation = StringUtils.join(this.getLocation(), "  ");
            if (StringUtils.isNotBlank(iLocation)) {
                data.put("location", iLocation);
            }

            data.putOpt("assetHolding", this.getAssetHolding());
            data.putOpt("location_d", this.getLocationD());

            if (this.getJson() != null) {
                data.put("data", this.getJson());
            }

            if (this.getMarc() != null) {
                data.put("data", this.getMarc());
            }
        } catch (JSONException e) {}
        return data;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 0;
        }

        if (!(o instanceof HoldingDTO)) {
            return 0;
        }

        return NaturalOrderComparator.NUMERICAL_ORDER.compare(this.getAssetHolding(), ((HoldingDTO) o).getAssetHolding());
    }
}
