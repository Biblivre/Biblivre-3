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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biblivre3.z3950;

import java.io.Serializable;
import java.util.Properties;
import mercury.IFJson;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

/**
 *
 * @author Danniel
 */
public class Z3950ResultRow implements Serializable, IFJson {

    private Record record;
    private String serverName;
    private String title;
    private String author;
    private String publication;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
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
            json.put("index", this.getIndex());
            json.put("server_name", this.getServerName());
            json.put("title", this.getTitle());
            json.put("author", this.getAuthor());
            json.put("publication", this.getPublication());
        } catch (JSONException e) {
        }

        return json;
    }


}
