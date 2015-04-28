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

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import mercury.DTO;
import mercury.IFJson;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultRow extends DTO implements Serializable, IFJson {

    private int recordSerial;
    private String title;
    private String author;
    private String date;

    private Date created;
    private Date modified;

    private String isbn;
    private String[] location;
    private String[] listPublicationFull;
    private int nrholdings;

    private int holdingsCount;
    private int holdingsAvailable;
    private int holdingsLent;
    private int holdingsReserved;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }    

    public int getRecordSerial() {
        return recordSerial;
    }

    public void setRecordSerial(int recordSerial) {
        this.recordSerial = recordSerial;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String[] getLocation() {
        return location;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }

    public String[] getListPublicationFull() {
        return listPublicationFull;
    }

    public void setListPublicationFull(String[] listPublicationFull) {
        this.listPublicationFull = listPublicationFull;
    }

    public int getNrholdings() {
        return nrholdings;
    }

    public void setNrholdings(int nrholdings) {
        this.nrholdings = nrholdings;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();

        try {
            json.put("serial", this.getRecordSerial());
            json.put("title", this.getTitle());
            json.put("author", this.getAuthor());
            json.put("date", this.getDate());
            json.put("ISBN", this.getIsbn());
            json.put("created", this.getCreated());
            json.put("modified", this.getModified());

            final String iLocation = StringUtils.join(this.getLocation(), " ");
            if (StringUtils.isNotBlank(iLocation)) {
                json.put("location", iLocation);
            }

            json.put("holdings_count", this.getHoldingsCount());
            json.put("holdings_available", this.getHoldingsAvailable());
            json.put("holdings_lent", this.getHoldingsLent());
            json.put("holdings_reserved", this.getHoldingsReserved());
        } catch (JSONException e) {
        }

        return json;
    }

    public int getHoldingsCount() {
        return holdingsCount;
    }

    public void setHoldingsCount(int holdingsCount) {
        this.holdingsCount = holdingsCount;
    }

    public int getHoldingsAvailable() {
        return holdingsAvailable;
    }

    public void setHoldingsAvailable(int holdingsAvailable) {
        this.holdingsAvailable = holdingsAvailable;
    }

    public int getHoldingsLent() {
        return holdingsLent;
    }

    public void setHoldingsLent(int holdingsLent) {
        this.holdingsLent = holdingsLent;
    }

    public int getHoldingsReserved() {
        return holdingsReserved;
    }

    public void setHoldingsReserved(int holdingsReserved) {
        this.holdingsReserved = holdingsReserved;
    }
}
