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

import biblivre3.enums.Database;
import biblivre3.enums.ReportType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.BaseHandler;
import mercury.Dialog;
import mercury.I18nUtils;
import mercury.MemoryFileDTO;
import org.apache.commons.lang.StringUtils;

public class ReportsHandler extends BaseHandler {

    @Override
    public String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> model) {
        ReportsBO bo = new ReportsBO();

        if (submitButton.equals("GENERATE_REPORT")) {
            ReportsDTO dto = null;
            try {
                dto = this.populateDto(request, model);
            } catch (Exception e) {
                Dialog.showWarning(request, "ERROR_FIELDS_NOT_FILLED");
                return "/jsp/administration/reports.jsp";
            }

            MemoryFileDTO report = null;
            try {
                report = bo.generateReport(dto, getI18nProperties(request, "biblivre3"));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (report != null) {
                this.returnFile(report, response);
                return "x-download";
            } else {
                Dialog.showError(request, "ERROR_REPORTS_NOT_IMPLEMENTED");
            }
        }
        return "/jsp/administration/reports.jsp";
    }

    private ReportsDTO populateDto(HttpServletRequest request, HashMap<String, String> model) throws Exception {
        ReportsDTO dto = new ReportsDTO();

        String reportId = model.get("reportId");
        ReportType type = ReportType.getById(reportId);
        dto.setType(type);

        if (type.isTimePeriod()) {
            String defaultFormat = I18nUtils.getText(request.getSession(), "biblivre3", "DEFAULT_DATE_FORMAT");
            DateFormat formatter = new SimpleDateFormat(defaultFormat);
            formatter.setLenient(false);

            String initialDate = model.get("initialDate");
            Date parsedStartDate = (Date) formatter.parse(initialDate);
            dto.setInitialDate(parsedStartDate);

            String finalDate = model.get("finalDate");
            Date parsedFinalDate = (Date) formatter.parse(finalDate);
            dto.setFinalDate(parsedFinalDate);
        }

        String database = model.get("database");
        dto.setDatabase(Database.valueOf(database));

        String order = model.get("order");
        dto.setOrder(order);

        String userId = model.get("userId");
        dto.setUserId(userId);

        String recordIds = model.get("recordIds");
        dto.setRecordIds(recordIds);

        String authorName = model.get("authorName");
        dto.setAuthorName(authorName);

        String datafield = model.get("datafield");
        dto.setDatafield(datafield);
        
        String digits = model.get("digits");
        try {
            dto.setDigits(Integer.valueOf(digits));
        } catch (Exception pokemon) {
        }
        
        return dto;
    }
}
