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

package mercury.tags;

import java.util.Properties;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SetInitialLocale extends SimpleTagSupport {
    private static String errorJsp = "/jsp/login.jsp";
    private static String defaultLanguage = "pt_BR";
    protected Logger log = Logger.getLogger(this.getClass());

    @Override
    public void doTag() throws JspException {
        try {
            PageContext pageContext = (PageContext) getJspContext();

            if (pageContext.getServletContext().getAttribute("FIRST_EXECUTION") != null) {
                pageContext.getServletContext().removeAttribute("FIRST_EXECUTION");
                pageContext.getSession().removeAttribute("LAST_VISITED_PAGE");
                pageContext.getSession().removeAttribute("MESSAGE_TEXT");
                pageContext.getSession().removeAttribute("MESSAGE_LEVEL");
                pageContext.getSession().setAttribute("MESSAGE_TEXT", "DIALOG_VOID");
                pageContext.getSession().setAttribute("MESSAGE_LEVEL", "NORMAL");
            }

            String lastVisitedPage = (String) pageContext.getSession().getAttribute("LAST_VISITED_PAGE");
            if (lastVisitedPage == null || lastVisitedPage.trim().equals("")) {
                lastVisitedPage = errorJsp;
                pageContext.getSession().setAttribute("LAST_VISITED_PAGE", lastVisitedPage);
            }

            if ((String) pageContext.getSession().getAttribute("I18N") == null) {
                String locale = pageContext.getRequest().getLocale().toString();
                Properties languages = (Properties) pageContext.getServletContext().getAttribute("LANGUAGES");

                if (StringUtils.isNotBlank(locale) && languages.containsKey(locale)) {
                    pageContext.getSession().setAttribute("I18N", locale);
                } else {
                    pageContext.getSession().setAttribute("I18N", defaultLanguage);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new JspException("[mercury.SetInitialLocale.doTag()] Exception: " + ex.getMessage());
        }
    }
}