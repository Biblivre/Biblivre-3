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

public class SimpleMessageDTO implements IFJson  {
    private String i18nKeyMessage;
    private MessageLevel msgLevel;

    public String getI18nKeyMessage() {
        return i18nKeyMessage;
    }

    public void setI18nKeyMessage(String i18nKeyMessage) {
        this.i18nKeyMessage = i18nKeyMessage;
    }

    public MessageLevel getMsgLevel() {
        return msgLevel;
    }

    public void setMsgLevel(MessageLevel msgLevel) {
        this.msgLevel = msgLevel;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();
        try {
            json.putOpt("msglevel", msgLevel);
            json.putOpt("message", i18nKeyMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
