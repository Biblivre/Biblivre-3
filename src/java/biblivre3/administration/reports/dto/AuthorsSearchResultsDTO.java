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

package biblivre3.administration.reports.dto;

import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import mercury.DTO;
import mercury.IFJson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class AuthorsSearchResultsDTO extends DTO implements IFJson {
    public TreeMap<String, Set<Integer>> nameIdsPairs;
    public int totalPages;
    public int totalRecords;
    public int currentPage;
    public int recordsPerPage;
    
    
    public int size() {
        return this.nameIdsPairs.size();
    }
    
    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();
        
        try {
            json.put("totalPages", this.totalPages);
            json.put("totalRecords", this.totalRecords);
            json.put("currentPage", this.currentPage);
            json.put("recordsPerPage", this.recordsPerPage);

            for (String key : this.nameIdsPairs.keySet()) {
                JSONObject result = new JSONObject();
                result.putOpt("name", key);
                result.putOpt("resultRecordIds", nameIdsPairs.get(key).toString());
                result.putOpt("size", nameIdsPairs.get(key).size());
                json.append("results", result);
            }
        } catch (JSONException e) {}
        
        return json;
    }
}
