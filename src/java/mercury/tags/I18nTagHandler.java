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

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpSession;
import mercury.I18nUtils;
import org.apache.log4j.Logger;

public class I18nTagHandler extends SimpleTagSupport {

    private String module;
    private String textKey;
    protected Logger log = Logger.getLogger(this.getClass());

    @Override
    public void doTag() throws JspException {
        final JspWriter out = getJspContext().getOut();
        try {
            final PageContext pageContext = (PageContext) getJspContext();
            final HttpSession sess = pageContext.getSession();
            final String text = I18nUtils.getText(sess, module, textKey);
            out.print(text);
        } catch (Exception ex)  {
            log.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage());
        }
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setTextKey(String textKey) {
        this.textKey = textKey;
    }
}