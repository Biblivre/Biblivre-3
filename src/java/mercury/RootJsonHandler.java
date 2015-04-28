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

import biblivre3.utils.DateUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import javax.servlet.http.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

public abstract class RootJsonHandler {

    public abstract JSONObject process(HttpServletRequest request, HttpServletResponse response);

    protected DateFormat dateFormatter = DateUtils.dd_MM_yyyy;

    protected <T extends DTO> T populateDtoFromJson(String jsonData, T dto) {
        try {
            JSONObject json = new JSONObject(jsonData);
            Class clazz = dto.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    String name = field.getName();
                    Class fieldClass = field.getType();
                    Object value = null;
                    if (fieldClass.equals(String.class)) {
                        value = json.has(name) ? json.getString(name) : null;
                    } else if (fieldClass.equals(Integer.class)) {
                        try {
                            value = json.has(name) ? json.getInt(name) : null;
                        } catch (Exception e) {
                            value = -1;
                        }
                    } else if (fieldClass.equals(Float.class)) {
                        String sValue = json.has(name) ? json.getString(name).replaceAll(",", ".") : null;
                        value = sValue != null ? Float.valueOf(sValue) : null;
                    } else if (fieldClass.equals(Date.class)) {
                        value = json.has(name) ? json.getString(name) : null;
                        value = value != null ? this.dateFormatter.parse((String)value) : null;
                    }

                    if (value == null) {
                        continue;
                    }

                    Method setter = clazz.getDeclaredMethod("set" + StringUtils.capitalize(name), fieldClass);
                    if (setter != null) {
                        setter.invoke(dto, value);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (JSONException je) {
        }
        return dto;
    }
}
