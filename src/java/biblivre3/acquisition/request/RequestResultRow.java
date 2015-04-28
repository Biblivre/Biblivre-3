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

import java.util.Date;
import java.util.Properties;
import mercury.DTO;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestResultRow extends DTO {

    private String recordSerial;
    private String author;
    private String title;
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getRecordSerial() {
        return recordSerial;
    }

    public void setRecordSerial(String recordSerial) {
        this.recordSerial = recordSerial;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();
        try {
            json.put("serial", this.getRecordSerial());
            json.put("author", this.getAuthor());
            json.put("title", this.getTitle());
            json.put("created", this.getCreated());
        } catch (JSONException e) {
        }
        return json;
    }
}
