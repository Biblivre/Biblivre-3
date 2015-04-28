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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Danniel Nascimento
 * @since 03/02/2009
 */
public class I18nUtils {

    public static String formatCurrent;

    private static final Pattern pattern = Pattern.compile("\\\\u([0-9A-F]{4})");

    public static String getCurrentLanguage(final HttpSession session) {
        String lang = (String)(session.getAttribute("I18N"));

        if (lang == null || lang.trim().equals("")) {
            lang = "pt_BR";
            session.setAttribute("I18N", lang);
        }

        return lang;
    }

    public static Properties getProperties(final HttpSession session, final String module) {
        String lang = I18nUtils.getCurrentLanguage(session);

        final String attrName = "I18N_" + module + "." + lang;
        final ServletContext sc = session.getServletContext();
        return (Properties)(sc.getAttribute(attrName));
    }

    public static String getText(final HttpSession session, final String module, final String key) {
        final Properties i18nProperties = I18nUtils.getProperties(session, module);
        return I18nUtils.getText(i18nProperties, key);
    }

    public static String getText(final Properties i18nProperties, final String key) {
        String text = null;

        if (i18nProperties != null) {
            text = i18nProperties.getProperty(key);

            if (text != null) {
                text = unescapeUTF8(text);
            }
        }

        if (text == null) {
            System.out.println("I18N Failed: " + key);
        }

        return text;
    }

    public static String unescapeUTF8(final String s) {
        String res = s;
        Matcher m = pattern.matcher(res);

        while (m.find()) {
            res = res.replaceAll("\\" + m.group(0), Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }

        return res;
    }

    public static void currentLanguage(HttpServletRequest request) {

        String lang = I18nUtils.getText(request.getSession(), "biblivre3", "DEFAULT_DATE_FORMAT");

        formatCurrent = lang;
    }
}
