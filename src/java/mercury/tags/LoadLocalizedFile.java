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

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpSession;
import mercury.Controller;

public class LoadLocalizedFile extends SimpleTagSupport {

    private String filename;

    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();
        try {
            PageContext pageContext = (PageContext) getJspContext();
            HttpSession sess = pageContext.getSession();
            String lang = (String) (sess.getAttribute("I18N"));
            StringBuffer sb = new StringBuffer();
            ClassLoader cl2 = Controller.class.getClassLoader();
            try {
                URL fileURL2 = cl2.getResource(filename + "." + lang);
                BufferedReader br = new BufferedReader(new InputStreamReader(fileURL2.openStream(), "UTF-8"));
                while(br.ready()){
                    sb.append(br.readLine());
                }
            } catch (Exception e) {
                URL fileURL2 = cl2.getResource(filename + ".pt_BR");
                BufferedReader br = new BufferedReader(new InputStreamReader(fileURL2.openStream(), "UTF-8"));
                while(br.ready()){
                    sb.append(br.readLine());
                }
            }
            out.println(sb.toString());
        } catch (java.io.IOException ex) {
            throw new JspException("Error in InitialMessage tag", ex);
        }
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

}
