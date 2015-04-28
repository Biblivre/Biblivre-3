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

package biblivre3.administration.reports;

import biblivre3.administration.ReportsDAO;
import biblivre3.administration.ReportsDTO;
import biblivre3.administration.reports.dto.BaseReportDto;
import biblivre3.administration.reports.dto.RequestsByDateReportDto;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;

public class RequestsByDateReport extends BaseBiblivreReport {

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        ReportsDAO dao = new ReportsDAO();
        String initialDate = dateFormat.format(dto.getInitialDate());
        String finalDate = dateFormat.format(dto.getFinalDate());
        return dao.getRequestsByDateReportData(initialDate, finalDate);
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        RequestsByDateReportDto dto = (RequestsByDateReportDto)reportData;
        Paragraph p1 = new Paragraph(this.getText("REPORTS_REQUESTS_BY_DATE_TITLE"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        StringBuilder header = new StringBuilder();
        header.append(this.getText("REPORTS_FROM"));
        header.append(" ").append(dto.getInitialDate()).append(" ");
        header.append(this.getText("REPORTS_TO"));
        header.append(" ").append(dto.getFinalDate());
        Paragraph p2 = new Paragraph(this.getHeaderChunk(header.toString()));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));
        if (dto.getData() != null) {
            PdfPTable table = createTable(dto.getData());
            document.add(table);
            document.add(new Phrase("\n"));
        }
    }

    private PdfPTable createTable(List<String[]> dataList) {
        PdfPTable table = new PdfPTable(7);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        table.setWidthPercentage(100f);
        createHeader(table);
        PdfPCell cell;
        String lastQuotationId = "0";
        String requester = null;
        String title = null;
        String quantity = null;
        String unit_value = null;
        String total_value = null;
        for (String[] data : dataList) {
            if (!data[0].equals(lastQuotationId)) {
                if (!lastQuotationId.equals("0")) {
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(requester)));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(title)));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(quantity)));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(unit_value)));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(total_value)));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                    table.addCell(cell);
                }

                requester = "";
                title = "";
                quantity = "";
                unit_value = "";
                total_value = "";
            }

            lastQuotationId = data[0];
            requester = data[1] + "\n";
            title += data[2] + "\n";
            quantity += data[3] + "\n";
            unit_value += data[4] + "\n";
            total_value = (data[5] == null ? "-" : data[5]) + "\n";
        }
        
        if (!lastQuotationId.equals("0")) {
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(requester)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(title)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(quantity)));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(unit_value)));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(total_value)));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
            table.addCell(cell);
        }
        return table;
    }
	
    private void createHeader(PdfPTable table) {
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_PETITIONER"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_TITLE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_AMOUNT"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_UNIT_VALUE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_PAID_VALUE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
    }

}