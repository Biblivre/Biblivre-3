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

import biblivre3.authorization.AuthorizationBO;
import biblivre3.authorization.AuthorizationPoints;
import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import java.net.URL;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * <P>
 * &nbsp;&nbsp;&nbsp;
 * The first class of the framework to receive an HTTP request.
 * Extends javax.servlet.http.HttpServlet. Receives either common Post http
 * requests or multipart Post http requests. Routes the request to a target
 * handler based on the "targetHandler" parameter sent by the page's form.
 */
public class Controller extends HttpServlet {

    private static String errorJsp = "/jsp/login.jsp";
    public static Properties targetHandlers;
    public static Properties i18nModules;
    public static Properties languages;
    private final Logger log = Logger.getLogger(this.getClass());

    @Override
    public void init() {
        //--- Runs just once !
        try {
            ClassLoader cl = Controller.class.getClassLoader();
            URL fileURL = cl.getResource("/targetHandlers.properties");
            Controller.targetHandlers = new Properties();
            Controller.targetHandlers.load(fileURL.openStream());
        } catch (IOException ioe) {
            System.out.println("[mercury.Controller.init()] Error: " + ioe.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("processRequest start: " + this.getMemoryInfo());

        String submitButton = null;
        String jspURL = null;

        HashMap<String, String> requestParametersHash = null;

        String thisPage = null;
        String lastVisitedPage = null;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            //--- gets all request parameters, either multipart or not
            this.putAllRequestParametersInAttributes(request);
            requestParametersHash = (HashMap<String, String>) request.getAttribute("REQUEST_PARAMETERS");

            //--- sets the last visited page to errorJsp if it is the first time the controller runs
            lastVisitedPage = (String) request.getSession().getAttribute("LAST_VISITED_PAGE");

            if (StringUtils.isBlank(lastVisitedPage)) {
                lastVisitedPage = errorJsp;
                jspURL = lastVisitedPage;
                request.getSession().setAttribute("LAST_VISITED_PAGE", jspURL);
            }

            submitButton = requestParametersHash.get("submitButton");
            thisPage = requestParametersHash.get("thisPage");

            AuthorizationPoints atps = (AuthorizationPoints) request.getSession().getAttribute("LOGGED_USER_ATPS");
            if (atps == null) {
                atps = new AuthorizationPoints(null);
                request.getSession().setAttribute("LOGGED_USER_ATPS", atps);
            }

            AuthorizationBO authBO = new AuthorizationBO();
            String handlerName = Controller.targetHandlers.getProperty(thisPage);

            if (submitButton.equals("i18n")) {
                request.getSession().setAttribute("I18N", requestParametersHash.get("i18n"));
                jspURL = lastVisitedPage;

            } else if (authBO.authorize(atps, handlerName, submitButton)) {
                BaseHandler handler = (BaseHandler) Class.forName(handlerName).newInstance();
                jspURL = handler.process(request, response);

            } else {
                Dialog.showWarning(request, "ERROR_NO_PERMISSION");
                jspURL = lastVisitedPage;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("====== [mercury.Controller.processRequest()] Exception 1: " + e);
            Dialog.showError(request, "DIALOG_VOID");
            jspURL = lastVisitedPage;

        } catch (ExceptionUser e) {
            System.out.println("====== [mercury.Controller.processRequest()] Exception 2: " + e.getKeyText());
            Dialog.showError(request, e.getKeyText());
            jspURL = lastVisitedPage;

        } catch (DAOException e) {
            System.out.println("====== [mercury.Controller.processRequest()] Exception 3: " + e);
            Dialog.showError(request, "ERROR_DATABASE");
            jspURL = lastVisitedPage;

        } catch (Exception e) {
            System.out.println("====== [mercury.Controller.processRequest()] Exception 4: " + e);
            e.printStackTrace();
            Dialog.showError(request, "DIALOG_VOID");
            jspURL = lastVisitedPage;

        } finally {
            if (jspURL == null || jspURL.equals("")) {
                jspURL = errorJsp;
                request.getSession().setAttribute("LAST_VISITED_PAGE", jspURL);
                return;
            } else if (jspURL.equals("x-download")) {
                return;
            } else if (jspURL.equals("x-json")) {
                return;
            } else {
                request.getSession().setAttribute("LAST_VISITED_PAGE", jspURL);
                request.getRequestDispatcher(jspURL).forward(request, response);
                return;
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    public void putAllRequestParametersInAttributes(HttpServletRequest request) {
        ArrayList fileBeanList = new ArrayList();
        HashMap<String, String> ht = new HashMap<String, String>();

        String fieldName = null;
        String fieldValue = null;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            java.util.List items = null;
            try {
                items = upload.parseRequest(request);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    fieldName = item.getFieldName();
                    fieldValue = item.getString();
                    ht.put(fieldName, fieldValue);
                } else if (!item.isFormField()) {
                    UploadedFileBean bean = new UploadedFileBean();
                    bean.setFileItem(item);
                    bean.setContentType(item.getContentType());
                    bean.setFileName(item.getName());
                    try {
                        bean.setInputStream(item.getInputStream());
                    } catch (Exception e) {
                        System.out.println("=== Erro: " + e);
                    }
                    bean.setIsInMemory(item.isInMemory());
                    bean.setSize(item.getSize());
                    fileBeanList.add(bean);
                    request.getSession().setAttribute("UPLOADED_FILE", bean);
                }
            }
        } else if (!isMultipart) {
            Enumeration<String> en = request.getParameterNames();

            String name = null;
            String value = null;
            while (en.hasMoreElements()) {
                name = en.nextElement();
                value = request.getParameter(name);
                ht.put(name, value);
            }
        }

        request.setAttribute("REQUEST_PARAMETERS", ht);
    }

    public final String getMemoryInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(" (Max: ");
        sb.append(Runtime.getRuntime().maxMemory());
        sb.append(", Total: ");
        sb.append(Runtime.getRuntime().totalMemory());
        sb.append(", Free: ");
        sb.append(Runtime.getRuntime().freeMemory());
        sb.append(")");
        return sb.toString();
    }
}

