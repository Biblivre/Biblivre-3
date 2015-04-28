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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

public class DigitalMediaController extends HttpServlet {
   
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
                        Integer serial = (new DigitalMediaDAO()).uploadToDigitalMedia(item);

                        String filename = item.getName();
                        if (filename.lastIndexOf('\\') != -1) {
                            filename = filename.substring(filename.lastIndexOf('\\') + 1);
                        }
                        if (filename.lastIndexOf('/') != -1) {
                            filename = filename.substring(filename.lastIndexOf('/') + 1);
                        }

                        String id = serial + ":" + filename;
                        String encodedId = new String(new Base64().encode(id.getBytes()));
                        encodedId = encodedId.replaceAll("\\\\", "_");
                        if (serial != null && serial != 0) {
                            response.getWriter().write("{success: true, id: \"" + encodedId + "\"}");
                            return;
                        }
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            response.getWriter().write("{success: false}");
        } else {
            String decodedId = null;
            DigitalMediaDTO dto = null;

            try {
                String id = request.getParameter("id");
                id = id.replaceAll("_", "\\\\");
                decodedId = new String(new Base64().decode(id.getBytes()));

                String[] splitId = decodedId.split(":");
                if (splitId.length == 2 && splitId[0].matches("^\\d+$")) {
                    dto = (new DigitalMediaDAO()).getDigitalMedia(Integer.valueOf(splitId[0]), splitId[1]);
                }
            } catch (Exception e) {
                // dto should be null here
            }

            InputStream in = null;
            byte[] bytearray = null;
            int length = 0;
            String defaultFile = request.getParameter("default");
            response.reset();
            try {
                if (dto != null && dto.getIn() != null) {
                    response.setContentType(dto.getMimeType());
                    response.setHeader("Content-Disposition", "filename=" + dto.getFileName());
                    length = dto.getLength();
                    in = dto.getIn();
                }

                if (in == null && StringUtils.isNotBlank(defaultFile)) {
                    String path = getServletContext().getRealPath("/");
                    File file = new File (path + defaultFile);
                    length = (int)file.length();
                    in = new FileInputStream(file);
                }

                if (in != null) {
                    bytearray = new byte[length];
                    int index = 0;
                    OutputStream os = response.getOutputStream();
                    while ((index = in.read(bytearray)) != -1) {
                        os.write(bytearray, 0, index);
                    }
                    in.close();
                } else {
                    response.getWriter().write("{success: false}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("{success: false}");
            }
            response.flushBuffer();
        }
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
