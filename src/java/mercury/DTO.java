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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Properties;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

public abstract class DTO implements IFJson, Serializable {

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();
        try {
            Class clazz = this.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                String name = field.getName();
                Method getter = clazz.getDeclaredMethod("get" + StringUtils.capitalize(name));
                if (getter != null) {
                    Object value = getter.invoke(this);
                    if (value != null) {
                        if (value instanceof Collection) {
                            Collection col = (Collection)value;

                            for (Object item : col) {
                                if (item == null) {
                                    continue;
                                }

                                if (item instanceof DTO) {
                                    json.append(name, ((DTO) item).toJSONObject(null));
                                } else if (item instanceof String) {
                                    json.append(name, ((String) item).trim());
                                } else {
                                    json.append(name, item);
                                }
                            }
                        } else if (value instanceof DTO) {
                            json.putOpt(name, ((DTO)value).toJSONObject(properties));
                        } else if (value instanceof String) {
                            json.putOpt(name, ((String) value).trim());
                        } else {
                            json.putOpt(name, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return json;
    }

}
