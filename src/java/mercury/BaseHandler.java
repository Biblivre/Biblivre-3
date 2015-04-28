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

import biblivre3.administration.AdminBO;
import biblivre3.administration.BackupBO;
import biblivre3.acquisition.quotation.QuotationBO;
import biblivre3.acquisition.quotation.QuotationDTO;
import biblivre3.acquisition.request.RequestBO;
import biblivre3.acquisition.request.RequestDTO;
import biblivre3.acquisition.supplier.SupplierBO;
import biblivre3.acquisition.supplier.SupplierDTO;
import biblivre3.authorization.AuthorizationBO;
import biblivre3.authorization.AuthorizationPointTypes;
import biblivre3.authorization.AuthorizationPoints;
import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserTypeDTO;
import biblivre3.circulation.reservation.ReservationBO;
import biblivre3.login.LoginBO;
import biblivre3.utils.DateUtils;
import biblivre3.z3950.Z3950BO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 *
 * @author Alberto Wagner
 */
public abstract class BaseHandler {

    protected Logger log = Logger.getLogger(this.getClass());

    public String process(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, String> requestParametersHash = (HashMap<String, String>) request.getAttribute("REQUEST_PARAMETERS");

        String submitButton = requestParametersHash.get("submitButton");
        String jspUrl = this.processMenu(request, submitButton);

        return (jspUrl != null) ? jspUrl : this.processModule(request, response, submitButton, requestParametersHash);
    }

    public abstract String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> requestParametersHash);

    private String processMenu(HttpServletRequest request, String submitButton) {
        HttpSession session = request.getSession();

        if (submitButton.equals("LOGIN")) {
            String username = request.getParameter("USERNAME");
            String password = request.getParameter("PASSWORD");

            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                Dialog.showWarning(request, "ERROR_ACCESS_DENIED");
                return "/jsp/login.jsp";
            }

            LoginBO bo = new LoginBO();
            LoginDTO user = bo.login(username, password);

            if (user != null) {
                AuthorizationBO authBO = new AuthorizationBO();
                AuthorizationPoints atps = authBO.getUserAuthPoints(user);
                atps.setAdmin(user.getLoginId() == 1);

                session.setAttribute("LOGGED_USER", user);
                session.setAttribute("LOGGED_USER_ATPS", atps);

                if (atps.isAdmin()) {
                    boolean warningPassword = password.toLowerCase().equals("abracadabra");
                    session.setAttribute("SYSTEM_WARNING_PASSWORD", warningPassword);
                }

                if (atps.isAllowed(AuthorizationPointTypes.ADMIN_BACKUP)) {
                    boolean warningBackup = false;
                    AdminBO adminBO = new AdminBO();

                    Date lastBackup = adminBO.getLastBackupDate();
                    if (lastBackup == null) {
                        String path = request.getSession().getServletContext().getRealPath(".");
                        //String path = session.getServletContext().getRealPath("/WEB-INF/sqldump/");
                        Date now = new Date();

                        boolean b = (new BackupBO()).doBackup(path, String.valueOf(now.getTime()));
                        if (b) {
                            adminBO.insertLastBackupDate(now);
                        }
                    } else {
                        int diff = DateUtils.dateDiff(lastBackup, new Date());
                        warningBackup = (diff >= 7);
                    }

                    session.setAttribute("SYSTEM_WARNING_BACKUP", warningBackup);
                }

                if (atps.isAllowed(AuthorizationPointTypes.ADMIN_REINDEX)) {
                    session.setAttribute("SYSTEM_WARNING_REINDEX", new AdminBO().isIndexOutdated());
                }

                Dialog.showNormal(request, "DIALOG_WELCOME_MESSAGE");
                return "/jsp/logged.jsp";
            } else {
                Dialog.showWarning(request, "ERROR_ACCESS_DENIED");
                return "/jsp/login.jsp";
            }

        } else if (submitButton.equals("LOGOUT")) {
            String lang = (String) session.getAttribute("I18N");
            session.invalidate();
            session = request.getSession();
            session.setAttribute("I18N", lang);
            session.removeAttribute("LOGGED_USER");

            Dialog.showNormal(request, "DIALOG_GOODBYE_MESSAGE");
            return "/jsp/login.jsp";

        } else if (submitButton.equals("SEARCH_BIBLIO")) {
            Dialog.showNormal(request, "DIALOG_BIBLIO_SEARCH");
            return "/jsp/search/biblio.jsp";

        } else if (submitButton.equals("SEARCH_AUTH")) {
            Dialog.showNormal(request, "DIALOG_AUTH_SEARCH");
            return "/jsp/search/auth.jsp";

        } else if (submitButton.equals("SEARCH_THESAURUS")) {
            Dialog.showNormal(request, "DIALOG_THESAURUS_SEARCH");
            return "/jsp/search/vocabulary.jsp";

        } else if (submitButton.equals("SEARCH_Z3950")) {
            Z3950BO bo = new Z3950BO();
            session.setAttribute("z3950_server_list", bo.listServers());

            Dialog.showNormal(request, "DIALOG_Z3950_SEARCH");
            return "/jsp/search/distributed.jsp";

        } else if (submitButton.equals("CIRCULATION_REGISTER")) {
            CirculationBO circulationBO = new CirculationBO();
            Collection<UserTypeDTO> listAllUserType = circulationBO.findAllUserType();
            session.setAttribute("LIST_USERS_TYPE", listAllUserType);

            Dialog.showNormal(request, "DIALOG_REGISTER");
            return "/jsp/circulation/user.jsp";

        } else if (submitButton.equals("CIRCULATION_LENDING")) {
            new ReservationBO().deleteExpired();

            Dialog.showNormal(request, "DIALOG_LENDING");
            return "/jsp/circulation/lending.jsp";

        } else if (submitButton.equals("CIRCULATION_RESERVATION")) {
            new ReservationBO().deleteExpired();

            Dialog.showNormal(request, "DIALOG_RESERVATION");
            return "/jsp/circulation/reservation.jsp";

        } else if (submitButton.equals("CIRCULATION_ACCESS")) {
            Dialog.showNormal(request, "DIALOG_ACCESS");
            return "/jsp/circulation/access.jsp";

        } else if (submitButton.equals("CIRCULATION_USER_CARDS")) {
            Dialog.showNormal(request, "DIALOG_USER_CARDS");
            return "/jsp/circulation/user_cards.jsp";

        } else if (submitButton.equals("CATALOGING_BIBLIO")) {
            Dialog.showNormal(request, "DIALOG_BIBLIO_CATALOGING");
            return "/jsp/cataloging/biblio.jsp";

        } else if (submitButton.equals("CATALOGING_AUTH")) {
            Dialog.showNormal(request, "DIALOG_AUTH_CATALOGING");
            return "/jsp/cataloging/auth.jsp";

        } else if (submitButton.equals("CATALOGING_VOCABULARY")) {
            Dialog.showNormal(request, "DIALOG_THESAURUS_CATALOGING");
            return "/jsp/cataloging/vocabulary.jsp";

        } else if (submitButton.equals("CATALOGING_IMPORT")) {
            Dialog.showNormal(request, "DIALOG_IMPORT");
            return "/jsp/cataloging/import.jsp";

        } else if (submitButton.equals("CATALOGING_LABEL")) {
            Dialog.showNormal(request, "DIALOG_LABELS");
            return "/jsp/cataloging/label.jsp";

        } else if (submitButton.equals("CATALOGING_BIBLIO_MOVE")) {
            Dialog.showNormal(request, "DIALOG_BIBLIO_MOVE");
            return "/jsp/cataloging/move.jsp";

        } else if (submitButton.equals("ACQUISITION_SUPPLIER")) {
            Dialog.showNormal(request, "DIALOG_SUPPLIER");
            return "/jsp/acquisition/supplier.jsp";

        } else if (submitButton.equals("ACQUISITION_REQUISITION")) {
            Dialog.showNormal(request, "DIALOG_REQUISITION");
            return "/jsp/acquisition/requisition.jsp";

        } else if (submitButton.equals("ACQUISITION_QUOTATION")) {
            SupplierBO bo = new SupplierBO();
            List<SupplierDTO> suppliers = bo.listAllSuppliers();
            session.setAttribute("supplierList", suppliers);
            RequestBO rbo = new RequestBO();
            List<RequestDTO> requests = rbo.listAllPendingRequests();
            session.setAttribute("requestList", requests);

            Dialog.showNormal(request, "DIALOG_QUOTATION");
            return "/jsp/acquisition/quotation.jsp";

        } else if (submitButton.equals("ACQUISITION_ORDER")) {
            SupplierBO bo = new SupplierBO();
            List<SupplierDTO> suppliers = bo.listAllSuppliers();
            session.setAttribute("supplierList", suppliers);
            QuotationBO qbo = new QuotationBO();
            List<QuotationDTO> quotations = qbo.listAllQuotations();
            session.setAttribute("quotationList", quotations);

            Dialog.showNormal(request, "DIALOG_ORDER");
            return "/jsp/acquisition/order.jsp";

        } else if (submitButton.equals("ADMINISTRATION_MAINTENANCE")) {
            session.setAttribute("MOVE_RECORDS", "ESCONDER");
            session.setAttribute("BUTTON_MOVE_RECORDS", "EXIBIR");
            session.setAttribute("TOTAL_WORK_REC", new AdminBO().totalRecords("WORK"));
            session.setAttribute("TOTAL_MAIN_REC", new AdminBO().totalRecords("MAIN"));
            session.removeAttribute("LIST_RECORDS");

            String defaultFormat = I18nUtils.getText(session, "biblivre3", "DEFAULT_DATETIME_FORMAT");
            SimpleDateFormat formatter = new SimpleDateFormat(defaultFormat);
            AdminBO bo = new AdminBO();
            List<Date> dates = bo.getLastFiveBackups();
            List<String[]> formattedDates = new ArrayList<String[]>();
            for (Date date : dates) {
                formattedDates.add(new String[]{formatter.format(date), String.valueOf(date.getTime())});
            }
            session.setAttribute("SYSTEM_LAST_FIVE_BACKUPS", formattedDates);

            Dialog.showNormal(request, "DIALOG_MAINTENANCE");
            return "/jsp/administration/maintenance.jsp";

        } else if (submitButton.equals("ADMINISTRATION_PASSWORD")) {
            Dialog.showNormal(request, "DIALOG_PASSWORD");
            return "/jsp/administration/password.jsp";

        } else if (submitButton.equals("ADMINISTRATION_REPORTS")) {
            Dialog.showNormal(request, "DIALOG_REPORTS");
            return "/jsp/administration/reports.jsp";

        } else if (submitButton.equals("ADMINISTRATION_PERMISSIONS")) {
            Dialog.showNormal(request, "DIALOG_PERMISSIONS");
            return "/jsp/administration/permissions.jsp";

        } else if (submitButton.equals("ADMINISTRATION_USER_TYPES")) {
            Dialog.showNormal(request, "DIALOG_USER_TYPES_ADMIN");
            return "/jsp/administration/usertypes.jsp";

        } else if (submitButton.equals("ADMINISTRATION_ACCESSCARDS")) {
            Dialog.showNormal(request, "DIALOG_ACCESSCARDS");
            return "/jsp/administration/accesscards.jsp";

        } else if (submitButton.equals("ADMINISTRATION_Z3950SERVERS")) {
            final Z3950BO bo = new Z3950BO();
            session.setAttribute("serverStatus", bo.getServerStatus());

            Dialog.showNormal(request, "DIALOG_Z3950SERVER");
            return "/jsp/administration/z3950.jsp";

        } else if (submitButton.equals("ADMINISTRATION_CONFIGURATION")) {
            Dialog.showNormal(request, "DIALOG_CONFIGURATION");
            return "/jsp/administration/configuration.jsp";

        } else if (submitButton.equals("HELP_ABOUT")) {
            Dialog.showNormal(request, "DIALOG_ABOUT");
            return "/jsp/help/about.jsp";
        }

        Dialog.showNormal(request, "DIALOG_VOID");
        return null;
    }

    public static Properties getI18nProperties(final HttpServletRequest request, String module) {
        return BaseHandler.getI18nProperties(request.getSession(), module);
    }

    public static Properties getI18nProperties(final HttpSession session, String module) {
        String lang = (String) (session.getAttribute("I18N"));

        if (lang == null || lang.trim().equals("")) {
            lang = "pt_BR";
            session.setAttribute("I18N", lang);
        }

        String attrName = "I18N_" + module + "." + lang;
        ServletContext sc = session.getServletContext();

        return (Properties) (sc.getAttribute(attrName));
    }


    protected void returnFile(final MemoryFileDTO file, final HttpServletResponse response) {
        try {
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getFileName());
            final OutputStream out = response.getOutputStream();
            final InputStream in = new ByteArrayInputStream(file.getFileData());
            byte[] buf = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
        }
    }

    protected void returnFile(final File file, final HttpServletResponse response) {
        this.returnFile(file, file.getName(), response);
    }

    protected void returnFile(final File file, final String fileName, final HttpServletResponse response) {
        try {
            String name = fileName != null ? fileName : file.getName();
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=" + name);
            final OutputStream out = response.getOutputStream();
            final InputStream in = new FileInputStream(file);
            byte[] buf = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
        }
    }

    protected void returnJson(final JSONObject json, final HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");

            final PrintWriter out = response.getWriter();

            String r = json.toString();
            out.write(r.replaceAll("<", "&lt;").replace(">", "&gt;"));
            out.close();
        } catch (Exception e) {
            logError(e.getMessage(), e);
        }
    }

    protected void logError(String message, Throwable t) {
        log.error(message, t);
        throw new ExceptionUser(message);
    }
}
