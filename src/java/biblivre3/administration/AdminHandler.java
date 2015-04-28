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

import biblivre3.cataloging.bibliographic.BiblioBO;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.enums.Database;
import biblivre3.login.LoginBO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.BaseHandler;
import mercury.Dialog;
import mercury.I18nUtils;
import mercury.LoginDTO;
import org.apache.commons.lang.StringUtils;

public class AdminHandler extends BaseHandler {

    @Override
    public String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> requestParametersHash) {
        if (submitButton.equals("CHANGE_PASSWORD")) {
            String oldpasswd = request.getParameter("OLDPASSWORD");
            String newpasswd = request.getParameter("NEWPASSWORD");
            String nprpasswd = request.getParameter("NPRPASSWORD");

            if (StringUtils.isEmpty(oldpasswd) || StringUtils.isEmpty(newpasswd) || StringUtils.isEmpty(nprpasswd)) {
                Dialog.showWarning(request, "ERROR_FIELDS_NOT_FILLED");
                return "/jsp/administration/password.jsp";
            }

            if (!newpasswd.equals(nprpasswd)) {
                Dialog.showWarning(request, "ERROR_PASSWORDS_ARENT_EQUAL");
                return "/jsp/administration/password.jsp";
            }

            LoginDTO login = (LoginDTO) request.getSession().getAttribute("LOGGED_USER");
            String loginName = login.getLoginName();
            LoginDTO checkLogin = new LoginBO().login(loginName, oldpasswd);

            if (checkLogin == null) {
                Dialog.showWarning(request, "ERROR_WRONG_PASSWORD");
                return "/jsp/administration/password.jsp";
            } else {
                new LoginBO().savePassword(checkLogin.getLoginId(), newpasswd);

                boolean warningPassword = newpasswd.equals("abracadabra") && login.getLoginName().equals("admin");
                request.getSession().setAttribute("SYSTEM_WARNING_PASSWORD", warningPassword);
            }

            Dialog.showNormal(request, "SUCCESS_CHANGE_PASSWORD");
            return "/jsp/administration/password.jsp";

        } else if (submitButton.equals("REINDEX_BIBLIO_BASE")) {
            AdminBO adminBO = new AdminBO();
            adminBO.reindexBiblioBase();
            if (!new AdminBO().isIndexOutdated()) {
                request.getSession().setAttribute("SYSTEM_WARNING_REINDEX", false);
            }

            Dialog.showNormal(request, "SUCCESS_REINDEX_BIBLIO_TABLE");

            return "/jsp/administration/maintenance.jsp";

        } else if (submitButton.equals("REINDEX_AUTHORITIES_BASE")) {
            AdminBO adminBO = new AdminBO();
            adminBO.reindexAuthoritiesBase();
            if (!new AdminBO().isIndexOutdated()) {
                request.getSession().setAttribute("SYSTEM_WARNING_REINDEX", false);
            }

            Dialog.showNormal(request, "SUCCESS_REINDEX_AUTHORITIES_TABLE");

            return "/jsp/administration/maintenance.jsp";

        } else if (submitButton.equals("REINDEX_THESAURUS_BASE")) {
            AdminBO adminBO = new AdminBO();
            adminBO.reindexThesaurusBase();
            if (!new AdminBO().isIndexOutdated()) {
                request.getSession().setAttribute("SYSTEM_WARNING_REINDEX", false);
            }

            Dialog.showNormal(request, "SUCCESS_REINDEX_THESAURUS_TABLE");

            return "/jsp/administration/maintenance.jsp";

        } else if (submitButton.equals("BACKUP")) {
            String path = request.getSession().getServletContext().getRealPath(".");

            Date now = new Date();
            BackupBO bo = new BackupBO();

            System.out.println(path);
            
            boolean backuped = bo.doBackup(path, String.valueOf(now.getTime()));
            if (backuped) {
                Dialog.showNormal(request, "SUCCESS_BACKUP_DATABASE");
                
                AdminBO adminBO = new AdminBO();
                adminBO.insertLastBackupDate(now);
                request.getSession().setAttribute("SYSTEM_WARNING_BACKUP", false);

                List<Date> dates = adminBO.getLastFiveBackups();
                List<String[]> formattedDates = new ArrayList<String[]>();

                String defaultFormat = I18nUtils.getText(request.getSession(), "biblivre3", "DEFAULT_DATETIME_FORMAT");
                SimpleDateFormat formatter = new SimpleDateFormat(defaultFormat);

                for (Date date : dates) {
                    formattedDates.add(new String[]{formatter.format(date), String.valueOf(date.getTime())});
                }

                request.getSession().setAttribute("SYSTEM_LAST_FIVE_BACKUPS", formattedDates);

            } else {
                Dialog.showError(request, "ERROR_BACKUP_DATABASE");
            }

            return "/jsp/administration/maintenance.jsp";
        } else if (submitButton.equals("EXPORT_ALL")) {
            // iso2709 || xml
            String format = request.getParameter("format");
            //MAIN || WORK || NULL
            String base = request.getParameter("base");
            //biblio || holding
            String type = request.getParameter("type");

            Database db = null;
            try {
                db = Database.valueOf(base);
            } catch (Exception e) {}

            String name = "";
            String ext = "";
            if (StringUtils.isBlank(format)) {
                ext = ".txt";
            } else if (format.equals("iso2709")) {
                ext = ".mrc";
            } else if (format.equals("xml")) {
                ext = ".xml";
            }

            File exportFile = null;
            if (StringUtils.isBlank(type)) {
                Dialog.showError(request, "ERROR_CREATE_TXT");
            } else if (type.equals("biblio")) {
                name = "biblivre_biblio_export_";
                exportFile = new BiblioBO().exportRecords(format, db);
            } else if (type.equals("holding")) {
                name = "biblivre_holding_export_";
                exportFile = new HoldingBO().exportRecords(format, db);
            }

            if (exportFile != null) {
                String date = String.valueOf(new Date().getTime());
                returnFile(exportFile, name + date + ext, response);
                return "x-download";
            } else {
                Dialog.showError(request, "ERROR_CREATE_TXT");
                return "/jsp/administration/maintenance.jsp";
            }
        } else {
            return "";
        }
    }

}
