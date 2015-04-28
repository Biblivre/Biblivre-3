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

/**
 *
 */
package mercury;

import biblivre3.authorization.AuthorizationBO;
import biblivre3.authorization.AuthorizationPoints;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URL;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <P>
 * &nbsp;&nbsp;&nbsp;
 * The first class of the framework to receive an HTTP request.
 * Extends javax.servlet.http.HttpServlet. Receives either common Post http
 * requests or multipart Post http requests. Routes the request to a target
 * handler based on the "targetHandler" parameter sent by the page's form.
 */
public class JsonController extends HttpServlet {

    public static Properties targetJsonHandlers;
    public static Properties i18nModules;
    public static Properties languages;
    private final Logger log = Logger.getLogger(this.getClass());

    //private String encoding;
    /**
     *
     */
    @Override
    public void init() {
        //--- Runs just once !
        try {
            //--- Loads the mapping "jsp page X Handler"
            ClassLoader cl = Controller.class.getClassLoader();
            URL fileURL = cl.getResource("/targetJsonHandlers.properties");
            JsonController.targetJsonHandlers = new Properties();
            JsonController.targetJsonHandlers.load(fileURL.openStream());
        } catch (IOException ioe) {
            System.out.println("[mercury.JsonController.init()] Error: " + ioe.getMessage());
        }
    }

    private JSONObject jsonFailure(HttpSession session, String errorLevel, String message) {
        ErrorDTO edto = new ErrorDTO(message, errorLevel);
        return edto.toJSONObject(BaseHandler.getI18nProperties(session, "biblivre3"));
    }
    
    /**
     *
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        this.log.debug("processRequest start: " + this.getMemoryInfo());

        String submitButton = null;
        String thisPage = null;
        HttpSession session = request.getSession();
        JSONObject jsonResponse = null;
        Integer status = null;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            submitButton = request.getParameter("submitButton");
            thisPage = request.getParameter("thisPage");

            AuthorizationPoints atps = (AuthorizationPoints) request.getSession().getAttribute("LOGGED_USER_ATPS");
            if (atps == null) {
                atps = new AuthorizationPoints(null);
                request.getSession().setAttribute("LOGGED_USER_ATPS", atps);
            }

            AuthorizationBO authBO = new AuthorizationBO();
            String handlerName = JsonController.targetJsonHandlers.getProperty(thisPage);

            if (thisPage.equals("ping") && submitButton.equals("ping")) {
                jsonResponse = new SuccessDTO("OK").toJSONObject(BaseHandler.getI18nProperties(session, "biblivre3"));

            } else if (authBO.authorize(atps, handlerName, submitButton)) {
                RootJsonHandler handler = (RootJsonHandler) Class.forName(handlerName).newInstance();
                jsonResponse = handler.process(request, response);

            } else {
                if (atps == null) {
                    status = 403;
                }

                jsonResponse = this.jsonFailure(session, "warning", "ERROR_NO_PERMISSION");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("====== [mercury.Controller.processRequest()] Exception 1: " + e);
            jsonResponse = this.jsonFailure(session, "error", "DIALOG_VOID");

        } catch (ExceptionUser e) {
            System.out.println("====== [mercury.Controller.processRequest()] Exception 2: " + e.getKeyText());
            jsonResponse = this.jsonFailure(session, "error", e.getKeyText());

        } catch (Exception e) {
            System.out.println("====== [mercury.Controller.processRequest()] Exception 3: " + e);
            e.printStackTrace();
            jsonResponse = this.jsonFailure(session, "error", "DIALOG_VOID");

        } finally {
            // Print response to browser
            if (status != null) {
                response.setStatus(status);
            }

            if (jsonResponse != null) {
                try {
                    jsonResponse.putOnce("success", true); // Only put success on response if not already present.
                } catch (JSONException e){}
                response.getWriter().print(jsonResponse.toString());
            }
        }
    }

    /**
     *
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     *
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     *
     */
    @Override
    public String getServletInfo() {
        return "Json results controller";
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

