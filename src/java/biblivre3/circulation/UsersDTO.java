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

package biblivre3.circulation;

import java.util.ArrayList;
import java.util.Properties;
import mercury.DTO;
import mercury.IFJson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alberto Wagner
 */
public class UsersDTO extends DTO implements IFJson {
    public ArrayList<UserDTO> users;
    public int totalPages;
    public int totalRecords;
    public int currentPage;
    public int recordsPerPage;
    
    public UsersDTO() {
        this.users = new ArrayList<UserDTO>();
    }
    
    public void add(UserDTO user) {
        this.users.add(user);
    }
    
    public UserDTO getFirst() {
        if (this.users.size() > 0) {
            return this.users.get(0);
        } else {
            return null;
        }
    }
    
    public int size() {
        return this.users.size();
    }
    
    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();
        
        try {
            json.put("totalPages", this.totalPages);
            json.put("totalRecords", this.totalRecords);
            json.put("currentPage", this.currentPage);
            json.put("recordsPerPage", this.recordsPerPage);            
            
            for (UserDTO user : this.users) {
                json.append("results", user.toJSONObject(null));
            }
        } catch (JSONException e) {}
        
        return json;
    }
}
