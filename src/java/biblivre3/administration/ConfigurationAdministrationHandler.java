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

package biblivre3.administration;

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.BaseHandler;
import mercury.Dialog;


public class ConfigurationAdministrationHandler extends BaseHandler {

    enum SaveResult {
        savedOK,
        parameterError,
        fileError
    }

    @Override
    public String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> requestParametersHash) {

        if (submitButton.equals("SAVE_CHANGES")) {
            biblivre3.config.Config.ht = readConfigParameters(request);
            final SaveResult saved = (new ConfigurationBO()).saveChanges(biblivre3.config.Config.ht, request);
            switch (saved) {
                case savedOK: {
                    Dialog.showNormal(request, "SUCCESS_SAVE_CONFIGURATIONS");
                    break;
                }
                case fileError: {
                    Dialog.showError(request, "ERROR_SAVE_CONFIGURATIONS");
                    break;
                }
            }

            return "/jsp/administration/configuration.jsp";

        } else if (submitButton.equals("CANCEL_CHANGES")) {
            Dialog.showNormal(request, "DIALOG_VOID");

            return "/jsp/administration/configuration.jsp";
        }

        return "";
    }

    private HashMap<String, String> readConfigParameters(HttpServletRequest request) {
        final HashMap<String, String> ht = new HashMap();
        String name = null;
        String value = null;

        for (ConfigurationEnum ce : ConfigurationEnum.values()) {
            name = ce.toString();
            value = request.getParameter(name);

            if (ce.equals(ConfigurationEnum.DATABASE_NAME)) {
                ht.put(name, Config.getConfigProperty(ce));
            } else {
                ht.put(name, value != null ? value : "");
            }
        }
        
        return ht;
    }
}
