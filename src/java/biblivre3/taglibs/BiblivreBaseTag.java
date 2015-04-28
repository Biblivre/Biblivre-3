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

package biblivre3.taglibs;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import mercury.I18nUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  04/02/2009
 */
public abstract class BiblivreBaseTag extends SimpleTagSupport {
    
    protected Logger log = Logger.getLogger(this.getClass());

    @Override
    public abstract void doTag() throws JspException, IOException;
    
    protected final String getColor(Integer rowNumber) {
        return rowNumber % 2 == 0 ? "#ffffff" : "#dddddd";
    }
    
    protected final String getLabel(String labelKey) {
        final PageContext pageContext = (PageContext) getJspContext();
        final HttpSession sess = pageContext.getSession();
        return I18nUtils.getText(sess, "biblivre3", labelKey);
    }
}
