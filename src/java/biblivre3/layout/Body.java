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
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class Body extends BodyTagSupport {

    private String thisPage;
    private String multiPart;
    private boolean isMultiPart;

    public Body() {
        super();
    }
    
    public String getThisPage() {
        return thisPage;
    }

    public void setThisPage(String thisPage) {
        this.thisPage = thisPage;
    }

    public String getMultiPart() {
        return this.multiPart;
    }

    public void setMultiPart(String multiPart) {
        this.multiPart = multiPart;
        this.isMultiPart = (multiPart != null && multiPart.equals("true"));
    }

    private boolean isLogged() {
        return (this.pageContext.getSession().getAttribute("LOGGED_USER") != null);
    }

    private void writeTagBodyContent(JspWriter out, BodyContent bodyContent) throws IOException {
        bodyContent.writeOut(out);
        bodyContent.clearBody();
    }

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        LayoutUtils utils = new LayoutUtils(this.pageContext);

        AuthorizationPoints atps = (AuthorizationPoints) this.pageContext.getSession().getAttribute("LOGGED_USER_ATPS");
        if (atps == null) {
            atps = new AuthorizationPoints(null);
        }

        if (this.thisPage == null || this.thisPage.isEmpty()) {
            throw new JspException();
        }

        try {
            out.println("<body>");
            if (this.isMultiPart) {
                out.println("    <form id=\"FORM_1\" action=\"Controller\" name=\"FORM_1\" method=\"post\" enctype=\"multipart/form-data\" accept-charset=\"UTF-8\">");
            } else {
                out.println("    <form id=\"FORM_1\" action=\"Controller\" name=\"FORM_1\" method=\"post\" onsubmit=\"return false;\">");
            }

            out.println("        <input type=\"hidden\" name=\"submitButton\" />");
            out.println("        <input type=\"hidden\" name=\"thisPage\" value=\"" + this.thisPage + "\" />");
            out.println("        <input type=\"hidden\" name=\"menuOption\" id=\"menuOption\" value=\"" + utils.getMenuOption() + "\" />");

            out.println("        <div id=\"header\">");
            out.println("            <div id=\"header_overlay\"></div>");


            out.println("            <div id=\"header_title\">" + Config.getConfigProperty(ConfigurationEnum.LIBRARY_NAME.name()) + " <span id=\"header_subtitle\">" + Config.getConfigProperty(ConfigurationEnum.LIBRARY_SUBNAME.name()) + "</span></div>");

            out.println("            <div id=\"biblivre_logo\" class=\"pointer\" title=\"Biblivre 3\" onclick=\"window.open('http://www.biblivre.org.br/');\"></div>");

            out.println("            <div id=\"sponsor_logo\" class=\"pointer\" title=\"Itaú Cultural\" onclick=\"window.open('http://www.itaucultural.org.br/');\">");
            out.println("               <div id=\"logo_time\"><br/></div>");
            out.println("               <div><img src=\"images/logo_itau.png\" width=\"63\" height=\"42\" border=\"0\" alt=\"Itaú Cultural\" /></div>");
            out.println("            </div>");

            out.println("            <div id=\"logos\">");
            out.println("               <div><img src=\"images/logo_pedro_i.gif\" width=\"72\" height=\"95\" border=\"0\" alt=\"Organização Pedro I\" /></div>");
            out.println("               <div><img src=\"images/logo_sabin.gif\" width=\"72\" height=\"95\" border=\"0\" alt=\"SABIN\" /></div>");
            out.println("               <div>");
            out.println("                   <a href=\"http://www.bn.br/\" target=\"_blank\">");
            out.println("                        <img src=\"images/logo_biblioteca_nacional.gif\" width=\"72\" height=\"95\" border=\"0\" alt=\"Biblioteca Nacional\" />");
            out.println("                    </a>");
            out.println("               </div>");
            out.println("               <div>");
            out.println("                   <a href=\"http://www.cultura.gov.br/\" target=\"_blank\">");
            out.println("                        <img src=\"images/logo_lei_de_incentivo.jpg\" width=\"72\" height=\"95\" border=\"0\" alt=\"" + utils.getText("biblivre3", "LABEL_LAW") + "\" />");
            out.println("                    </a>");
            out.println("               </div>");
            out.println("            </div>");

            out.println("            <div id=\"language_picker\">");
            out.println("                <img src=\"images/flag_ptb.gif\" width=\"35\" height=\"18\" border=\"0\" onclick=\"$(this).siblings('select').val('pt_BR'); submitForm('FORM_1','i18n');\" alt=\"Português\" />");
            out.println("                <img src=\"images/flag_esp.gif\" width=\"35\" height=\"18\" border=\"0\" onclick=\"$(this).siblings('select').val('es'); submitForm('FORM_1','i18n');\" alt=\"Español\" />");
            out.println("                <img src=\"images/flag_enu.gif\" width=\"35\" height=\"18\" border=\"0\" onclick=\"$(this).siblings('select').val('en_US'); submitForm('FORM_1','i18n');\" alt=\"English\" /><br/>");
            out.println("                <select name=\"i18n\" onchange=\"submitForm('FORM_1', 'i18n');\">");
            out.println(utils.getLanguages());
            out.println("                </select>");
            out.println("            </div>");

            if (this.isLogged()) {
                // LOGGED IN MENU
                out.println("            <div id=\"menu\">");
                out.println("                <ul id=\"menu_root\">");
                out.println("                    <li rel=\"busca\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_SEARCH"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "SEARCH_BIBLIO", "MENU_BIBLIO_SEARCH"));
                out.println(utils.menuEntry(atps, "SEARCH_AUTH", "MENU_AUTH_SEARCH"));
                out.println(utils.menuEntry(atps, "SEARCH_THESAURUS", "MENU_THESAURUS_SEARCH"));
                out.println(utils.menuEntry(atps, "SEARCH_Z3950", "MENU_Z3950_SEARCH"));
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"circulacao\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_CIRCULATION"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "CIRCULATION_REGISTER", "MENU_REGISTER"));
                out.println(utils.menuEntry(atps, "CIRCULATION_LENDING", "MENU_LENDING"));
                out.println(utils.menuEntry(atps, "CIRCULATION_RESERVATION", "MENU_RESERVATION"));
                out.println(utils.menuEntry(atps, "CIRCULATION_ACCESS", "MENU_ACCESS"));
                out.println(utils.menuEntry(atps, "CIRCULATION_USER_CARDS", "MENU_USER_CARDS"));
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"catalogacao\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_CATALOGING"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "CATALOGING_BIBLIO", "MENU_BIBLIO_CATALOGING"));
                out.println(utils.menuEntry(atps, "CATALOGING_AUTH", "MENU_AUTH_CATALOGING"));
                out.println(utils.menuEntry(atps, "CATALOGING_VOCABULARY", "MENU_THESAURUS_CATALOGING"));
                out.println(utils.menuEntry(atps, "CATALOGING_IMPORT", "MENU_IMPORT"));
                out.println(utils.menuEntry(atps, "CATALOGING_LABEL", "MENU_LABELS"));
                out.println(utils.menuEntry(atps, "CATALOGING_BIBLIO_MOVE", "MENU_BIBLIO_MOVE"));
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"aquisicao\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_ACQUISITION"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "ACQUISITION_SUPPLIER", "MENU_SUPPLIER"));
                out.println(utils.menuEntry(atps, "ACQUISITION_REQUISITION", "MENU_REQUISITION"));
                out.println(utils.menuEntry(atps, "ACQUISITION_QUOTATION", "MENU_QUOTATION"));
                out.println(utils.menuEntry(atps, "ACQUISITION_ORDER", "MENU_ORDER"));
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"administracao\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_ADMINISTRATION"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "ADMINISTRATION_PASSWORD", "MENU_PASSWORD"));
                out.println(utils.menuEntry(atps, "ADMINISTRATION_MAINTENANCE", "MENU_MAINTENANCE"));
                out.println(utils.menuEntry(atps, "ADMINISTRATION_REPORTS", "MENU_REPORTS"));
                out.println(utils.menuEntry(atps, "ADMINISTRATION_PERMISSIONS", "MENU_PERMISSIONS"));
                out.println(utils.menuEntry(atps, "ADMINISTRATION_USER_TYPES", "MENU_USER_TYPES_ADMIN"));
                out.println(utils.menuEntry(atps, "ADMINISTRATION_ACCESSCARDS", "MENU_ACCESSCARDS"));
                out.println(utils.menuEntry(atps, "ADMINISTRATION_Z3950SERVERS", "MENU_Z3950SERVER"));
                out.println(utils.menuEntry(atps, "ADMINISTRATION_CONFIGURATION", "MENU_CONFIGURATION"));
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"ajuda\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_HELP"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "HELP_ABOUT", "MENU_ABOUT"));
                out.println("                            <li rel=\"http://www.biblivre.org.br/faq\">" + utils.getText("biblivre3", "MENU_FAQ") + "</li>");
                //Versão com manual digital
                out.println("                            <li rel=\"./Manual_Biblivre3.pdf\">Manual</li>");
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"sair\" class=\"logout\">");
                out.println("                        <button onclick=\"submitForm('FORM_1', 'LOGOUT');\" type=\"button\">" + utils.getText("biblivre3", "LABEL_LOGOUT") + "</button>");
                out.println("                    </li>");
                out.println("                </ul>");
                out.println("            </div>");
            } else {
                // LOGGED OFF MENU
                out.println("            <div id=\"menu\">");
                out.println("                <ul id=\"menu_root\">");
                out.println("                    <li rel=\"busca\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_SEARCH"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "SEARCH_BIBLIO", "MENU_BIBLIO_SEARCH"));
                out.println(utils.menuEntry(atps, "SEARCH_AUTH", "MENU_AUTH_SEARCH"));
                out.println(utils.menuEntry(atps, "SEARCH_THESAURUS", "MENU_THESAURUS_SEARCH"));
                out.println(utils.menuEntry(atps, "SEARCH_Z3950", "MENU_Z3950_SEARCH"));
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"ajuda\">");
                out.println("                        " + utils.getText("biblivre3", "MENU_HELP"));
                out.println("                        <ul class=\"submenu\">");
                out.println(utils.menuEntry(atps, "HELP_ABOUT", "MENU_ABOUT"));
                out.println("                            <li rel=\"http://www.biblivre.org.br/faq\">" + utils.getText("biblivre3", "MENU_FAQ") + "</li>");
                //Versão com manual digital
                out.println("                            <li rel=\"./Manual_Biblivre3.pdf\">Manual</li>");
                out.println("                        </ul>");
                out.println("                    </li>");
                out.println("                    <li rel=\"login\" class=\"login\">");
                out.println("                        " + utils.getText("biblivre3", "LABEL_USERNAME") + ":");
                out.println("                        <input type=\"text\" name=\"USERNAME\" />");
                out.println("                        " + utils.getText("biblivre3", "LABEL_PASSWORD") + ":");
                out.println("                        <input type=\"password\" name=\"PASSWORD\" />");
                out.println("                        <button onclick=\"submitForm('FORM_1', 'LOGIN', '');\">" + utils.getText("biblivre3", "LABEL_LOGIN") + "</button>");
                out.println("                    </li>");
                out.println("                </ul>");
                out.println("            </div>");
            }
            out.println("        </div>");

            out.println("        <div id=\"content_outer\">");
            out.println("            <div id=\"content\">");
            
            if (this.isLogged()) {
                Boolean pwdWarning = (Boolean) this.pageContext.getSession().getAttribute("SYSTEM_WARNING_PASSWORD");
                Boolean bkpWarning = (Boolean) this.pageContext.getSession().getAttribute("SYSTEM_WARNING_BACKUP");
                Boolean idxWarning = (Boolean) this.pageContext.getSession().getAttribute("SYSTEM_WARNING_REINDEX");

                if (pwdWarning == null) {
                    pwdWarning = false;
                }

                if (bkpWarning == null) {
                    bkpWarning = false;
                }

                if (idxWarning == null) {
                    idxWarning = false;
                }

                if (pwdWarning || bkpWarning) {
                     out.println("<div id=\"system_warnings\">");

                     if (pwdWarning) {
                         out.println(utils.getText("biblivre3", "SYSTEM_WARNING_PASSWORD") + ". ");
                         out.println(utils.getText("biblivre3", "SYSTEM_WARNING_RESOLVE") + ", ");
                         out.println("<a href=\"javascript:submitForm('FORM_1', 'ADMINISTRATION_PASSWORD');\">" + utils.getText("biblivre3", "LABEL_CLICK_HERE") + "</a>.");
                         out.println("<br/>");
                     }

                     if (bkpWarning) {
                         out.println(utils.getText("biblivre3", "SYSTEM_WARNING_BACKUP") + ". ");
                         out.println(utils.getText("biblivre3", "SYSTEM_WARNING_RESOLVE") + ", ");
                         out.println("<a href=\"javascript:void(0);\" onclick=\"submitForm('FORM_1', 'ADMINISTRATION_MAINTENANCE');\">" + utils.getText("biblivre3", "LABEL_CLICK_HERE") + "</a>.");
                         out.println("<br/>");
                     }

                     if (idxWarning) {
                         out.println(utils.getText("biblivre3", "SYSTEM_WARNING_REINDEX") + ". ");
                         out.println(utils.getText("biblivre3", "SYSTEM_WARNING_RESOLVE") + ", ");
                         out.println("<a href=\"javascript:void(0);\" onclick=\"submitForm('FORM_1', 'ADMINISTRATION_MAINTENANCE');\">" + utils.getText("biblivre3", "LABEL_CLICK_HERE") + "</a>.");
                         out.println("<br/>");
                     }

                     out.println("</div>");
                }
            }

            out.println("                <div id=\"breadcrumb\"></div>");
            out.println(utils.getDialog());
            out.println("                <div id=\"content_inner\">");
        } catch (Exception e){
            e.printStackTrace();
        }
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.println("                </div>");

            out.println("                <div id=\"footer\"></div>");
            out.println("            </div>");
            out.println("            <div id=\"copyright\">Copyright &copy; <a href=\"http://biblivre.org.br\" target=\"_blank\">BIBLIVRE</a></div>");
            out.println("        </div>");
            out.println("    </form>");
            out.println("</body>");
            out.println("</html>");
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
        throw new JspException("Error in BodyBeforeLogin tag", ex);
    }
}
