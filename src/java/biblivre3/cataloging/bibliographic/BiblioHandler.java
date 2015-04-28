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

package biblivre3.cataloging.bibliographic;

import biblivre3.cataloging.holding.HoldingDAO;
import biblivre3.cataloging.holding.LabelConfigDTO;
import biblivre3.cataloging.holding.LabelDTO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.BaseHandler;
import mercury.Dialog;
import mercury.I18nUtils;
import mercury.MemoryFileDTO;

public class BiblioHandler extends BaseHandler {

    @Override
    public String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> requestParametersHash) {

        if (submitButton.equals("GENERATE_LABELS_DATE")) {
            String startDate = request.getParameter("startDate");
            String finalDate = request.getParameter("endDate");
            String base = request.getParameter("BASE");

            String defaultFormat = I18nUtils.getText(request.getSession(), "biblivre3", "DEFAULT_DATE_FORMAT");

            Date parsedStartDate = null;
            Date parsedFinalDate = null;
            try {
                DateFormat formatter = new SimpleDateFormat(defaultFormat);
                formatter.setLenient(false);
                parsedStartDate = (Date) formatter.parse(startDate);
                parsedFinalDate = (Date) formatter.parse(finalDate);
            } catch (Exception e) {
                Dialog.showWarning(request, "MESSAGE_ERROR_INVALID_DATES");
                return "/jsp/cataloging/label.jsp";
            }

            int generatedLabels[] = new BiblioBO().generateLabelsByDate(parsedStartDate, parsedFinalDate, base);

            if (generatedLabels[0] == 0) {
                Dialog.showWarning(request, "MESSAGE_NO_HOLDINGS_IN_SELECTED_RANGE");
            } else if (generatedLabels[1] == 0) {
                Dialog.showWarning(request, "MESSAGE_LABEL_ALL_LABELS_IN_RANGE_ARE_ALREADY_IN_LIST");
            } else {
                Dialog.showNormal(request, "SUCCESS_ADD_LABEL");
            }

            return "/jsp/cataloging/label.jsp";

        } else if (submitButton.equals("LIST_ALL_PENDING_LABELS")) {
            ArrayList<LabelDTO> list = new HoldingDAO().listPendingLabels();
            Collections.sort(list);
            request.setAttribute("LIST_ALL_LABEL", list);

            if (list.isEmpty()) {
                Dialog.showNormal(request, "MESSAGE_EMPTY_LABEL_LIST");
            }
            return "/jsp/cataloging/label.jsp";

        } else if (submitButton.equals("DELETE_LABEL")) {
            final String[] labels = request.getParameterValues("HOLDING_SELECT");

            if (labels != null) {
                if (new BiblioDAO().deleteLabels(labels)) {
                    Dialog.showNormal(request, "SUCCESS_DELETE_LABEL");
                }
            } else {
                Dialog.showWarning(request, "ERROR_DELETE_LABEL");
            }

            return "/jsp/cataloging/label.jsp";

        } else if (submitButton.equals("RECORD_FILE_TXT")) {
            //FIXME: Remove those damn DAOs from here.
            String dellabel = request.getParameter("dellabel");
            final String[] labels = request.getParameterValues("HOLDING_SELECT");

            if (labels != null) {
                MemoryFileDTO txtFile = new BiblioBO().createFileLabelsTXT(new HoldingDAO().listSelectedLabels(labels));
                if (txtFile != null) {
                    Dialog.showNormal(request, "SUCCESS_CREATE_TXT");
                    if (dellabel != null) {
                        new BiblioDAO().deleteLabels(labels);
                    }
                    ArrayList<LabelDTO> list = new HoldingDAO().listPendingLabels();
                    
                    Collections.sort(list);
                    request.setAttribute("LIST_ALL_LABEL", list);
                    request.setAttribute("FILE_DOWNLOAD_URL", txtFile.getFileName());
                    request.getSession().setAttribute(txtFile.getFileName(), txtFile);
                }
            } else {
                Dialog.showNormal(request, "ERROR_CREATE_TXT");
            }
            return "/jsp/cataloging/label.jsp";

        } else if (submitButton.equals("RECORD_FILE_PDF")) {
            //FIXME: Remove those damn DAOs from here.
            String dellabel = request.getParameter("dellabel");
            String[] labels = request.getParameterValues("HOLDING_SELECT");
            String startOffset = request.getParameter("START_OFFSET");
            String width = request.getParameter("WIDTH");
            String height = request.getParameter("HEIGHT");
            String columns = request.getParameter("COLUMNS");
            String rows = request.getParameter("ROWS");


            LabelConfigDTO labelConfig = new LabelConfigDTO();
            try {
                labelConfig.setOffset(Integer.parseInt(startOffset));
                labelConfig.setWidth(Float.parseFloat(width));
                labelConfig.setHeight(Float.parseFloat(height));
                labelConfig.setColumns(Integer.parseInt(columns));
                labelConfig.setRows(Integer.parseInt(rows));
            } catch (Exception e) {
                Dialog.showNormal(request, "ERROR_CREATE_PDF");
            }

            if (labels != null) {
                MemoryFileDTO pdfFile = new BiblioBO().createFileLabelsPDF(new HoldingDAO().listSelectedLabels(labels), labelConfig);
                if (pdfFile != null) {
                    Dialog.showNormal(request, "SUCCESS_CREATE_PDF");
                    if (dellabel != null) {
                        new BiblioDAO().deleteLabels(labels);
                    }
                    
                    ArrayList<LabelDTO> list = new HoldingDAO().listPendingLabels();
                    Collections.sort(list);
                    request.setAttribute("LIST_ALL_LABEL", list);
                    request.setAttribute("FILE_DOWNLOAD_URL", pdfFile.getFileName());
                    request.getSession().setAttribute(pdfFile.getFileName(), pdfFile);
                }
            } else {
                Dialog.showNormal(request, "ERROR_CREATE_PDF");
            }
            return "/jsp/cataloging/label.jsp";

        } else if (submitButton.equals("DOWNLOAD_LABEL_FILE")) {
            final String fileName = request.getParameter("FILE_NAME");
            if (fileName != null) {
                MemoryFileDTO file = (MemoryFileDTO)request.getSession().getAttribute(fileName);
                returnFile(file, response);
                return "x-download";
            } else {
                Dialog.showNormal(request, "ERROR_CREATE_PDF");
                return "/jsp/cataloging/label.jsp";
            }
        } else {
            return "";
        }
    }

}
