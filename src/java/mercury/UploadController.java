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

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadController extends HttpServlet {
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            try {
                List items = upload.parseRequest(request);
                Iterator iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (!item.isFormField()) {
                        String targetUrl = Config.getConfigProperty(ConfigurationEnum.DIGITAL_MEDIA);
                        if (StringUtils.isBlank(targetUrl)) {
                            targetUrl = request.getRequestURL().toString();
                            targetUrl = targetUrl.substring(0, targetUrl.lastIndexOf('/'));
                        }
                        targetUrl += "/DigitalMediaController";
                        PostMethod filePost = new PostMethod(targetUrl);
                        filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);
                        UploadPartSource src = new UploadPartSource(item.getName(), item.getSize(), item.getInputStream());
                        Part[] parts = new Part[1];
                        parts[0] = new FilePart(item.getName(), src, item.getContentType(), null);
                        filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
                        HttpClient client = new HttpClient();
                        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                        int status = client.executeMethod(filePost);
                        if (status == HttpStatus.SC_OK) {
                            String data = filePost.getResponseBodyAsString();
                            JSONObject json = new JSONObject(data);
                            if (json.has("id")) {
                                JSONObject responseJson = new JSONObject();
                                responseJson.put("success", true);
                                responseJson.put("id", json.getString("id"));
                                responseJson.put("uri", targetUrl + "?id=" + json.getString("id"));
                                response.getWriter().write(responseJson.toString());
                            }
                        }
                        filePost.releaseConnection();
                        return;
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }
        response.getWriter().write("{success: false}");
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
