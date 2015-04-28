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

package mercury;

import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;

public class SuccessDTO implements IFJson {
    
    private String message;
    private String errorLevel;
    private String data;
    
    public SuccessDTO(String message) {
        this.message = message;
        this.errorLevel = "normal";
    }

    public SuccessDTO(String message, String errorLevel) {
        this.message = message;
        this.errorLevel = errorLevel;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();
        
        try {
            json.put("success", true);
            json.put("message", I18nUtils.getText(properties, this.message));
            json.put("data", this.data);
            json.put("errorLevel", this.errorLevel);
        } catch (JSONException e) {
        }
        
        return json;
    }
}
