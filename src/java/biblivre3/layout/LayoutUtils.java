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

package biblivre3.layout;

import biblivre3.authorization.AuthorizationPoints;
import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mercury.I18nUtils;


public class LayoutUtils {
    
    private PageContext pageContext;
    
    public LayoutUtils(PageContext pageContext) {
        this.pageContext = pageContext;
    }
    
    private static final Pattern p = Pattern.compile("\\\\u([0-9A-F]{4})");

    public static String unescapeUTF8(String s) {
        String res = s;
        Matcher m = p.matcher(res);
        while(m.find()) {
            res = res.replaceAll("\\" + m.group(0),
            Character.toString((char)Integer.parseInt(m.group( 1), 16)));
        }
        return res;
    }

    public String getLanguages() {
        HttpSession session = this.pageContext.getSession();

        Properties prop = (java.util.Properties)session.getServletContext().getAttribute("LANGUAGES");
        Enumeration en = prop.propertyNames();
        StringBuilder sb = new StringBuilder();

        while (en.hasMoreElements()) {
            String s1 = (String) en.nextElement();
            String s2 = unescapeUTF8(prop.getProperty(s1));

            sb.append("<option value=\"").append(s1).append("\"");

            if (((String) session.getAttribute("I18N")).equals(s1)) {
                sb.append(" selected=\"selected\"");
            }

            sb.append(">").append(s2).append("</option>");
        }
        
        return sb.toString();
    }

    public String getMenuOption() {
        String s = this.pageContext.getRequest().getParameter("menuOption");

        return (s != null) ? s : "";
    }
    
    public String getText(String module, String textKey) {
        return I18nUtils.getText(this.pageContext.getSession(), module, textKey);
    }    
    
    public String getDialog() {
        HttpSession session = this.pageContext.getSession();

        String level = (String) session.getAttribute("MESSAGE_LEVEL");
        String textKey = (String) session.getAttribute("MESSAGE_TEXT");
        if (level == null) {
            level = "NORMAL";
        }

        if (textKey == null) {
            textKey = "DIALOG_VOID";
        }

        return "<div id=\"message\" class=\"" + level.toLowerCase() + "\"><p>" +
                    I18nUtils.getText(session, "biblivre3", textKey) +
                "</p></div>";
    }

    public String menuEntry(AuthorizationPoints atp, String value, String text) {
        boolean allowed = (atp == null || atp.menuAllowed(value));

        return "<li " + (allowed ? "" : "class=\"disabled\"") + " rel=\"" + value + "\">" + this.getText("biblivre3", text) + "</li>";
    }
}
