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

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import mercury.I18nUtils;

public class Head extends BodyTagSupport {

    public Head() {
        super();
    }

    private void writeTagBodyContent(JspWriter out, BodyContent bodyContent) throws IOException {
        bodyContent.writeOut(out);
        bodyContent.clearBody();
    }
    
    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.println("<!-- " + ((HttpServletRequest) pageContext.getRequest()).getRequestURI() + " -->");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            out.println("<head>");
            out.println("    <title>" + Config.getConfigProperty(ConfigurationEnum.LIBRARY_NAME.name()) + "</title>");

            out.println("    <link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"images/favicon.ico\" />");

            out.println("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
            out.println("    <meta http-equiv=\"Cache-Control\" content=\"no-Cache\" />");
            out.println("    <meta http-equiv=\"Pragma\" content=\"no-Cache\" />");
            out.println("    <meta name=\"google\" value=\"notranslate\" />");

            out.println("    <link rel=\"stylesheet\" type=\"text/css\" href=\"css/main.jsp\" />");
            out.println("    <script type=\"text/javascript\" src=\"scripts/jquery.js\"></script>");
            out.println("    <script type=\"text/javascript\" src=\"scripts/jquery.extras.js\"></script>");
            out.println("    <script type=\"text/javascript\" src=\"scripts/i18n_" + I18nUtils.getCurrentLanguage(pageContext.getSession()) + ".js\"></script>");
            out.println("    <script type=\"text/javascript\" src=\"scripts/main.js\"></script>");
        } catch (Exception e){}

        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.println("</head>");
        } catch (Exception e){}
        return EVAL_PAGE;
    }

    @Override
    public int doAfterBody() throws JspException {
        try {
            // This code is generated for tags whose bodyContent is "JSP"
            BodyContent bodyCont = getBodyContent();
            JspWriter out = bodyCont.getEnclosingWriter();
            
            writeTagBodyContent(out, bodyCont);
        } catch (Exception ex) {
            handleBodyContentException(ex);
        }

        return EVAL_PAGE;
    }
    
    private void handleBodyContentException(Exception ex) throws JspException {
        // Since the doAfterBody method is guarded, place exception handing code here.
        throw new JspException("Error in Head tag", ex);
    }
}
