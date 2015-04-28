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

import biblivre3.administration.reports.dto.LendingsByDateReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class LendingsByDateReport extends BaseBiblivreReport {

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        ReportsDAO dao = new ReportsDAO();
        String initialDate = dateFormat.format(dto.getInitialDate());
        String finalDate = dateFormat.format(dto.getFinalDate());
        return dao.getLendingsByDateReportData(initialDate, finalDate);
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        LendingsByDateReportDto dto = (LendingsByDateReportDto)reportData;
        Paragraph p1 = new Paragraph(this.getText("REPORTS_LENDINGS_BY_DATE_TITLE"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        StringBuilder header = new StringBuilder();
        header.append(this.getText("REPORTS_FROM"));
        header.append(" " + dto.getInitialDate() + " ");
        header.append(this.getText("REPORTS_TO"));
        header.append(" " + dto.getFinalDate());
        Paragraph p2 = new Paragraph(this.getHeaderChunk(header.toString()));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));

        Paragraph p3 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_LENDINGS_BY_DATE_TOTAL") + ":  " + dto.getTotals()[0]));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p3);
        Paragraph p4 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_LENDINGS_BY_DATE_TOTAL_OUT") + ":  " + dto.getTotals()[1]));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p4);
        Paragraph p5 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_LENDINGS_BY_DATE_TOTAL_LATE") + ":  " + dto.getTotals()[2]));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p5);
        document.add(new Phrase("\n"));

        document.add(new Phrase(this.getText("REPORTS_LENDINGS_BY_DATE_TOP_LENDINGS")));
        document.add(new Phrase("\n"));

        if (dto != null) {
            PdfPTable table = createTable(dto);
            document.add(table);
            document.add(new Phrase("\n"));
        }
    }

    private PdfPTable createTable(LendingsByDateReportDto dto) {
        PdfPTable table = new PdfPTable(5);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        table.setWidthPercentage(100f);
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_LENDINGS"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_TITLE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_AUTHOR"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        //Table body
        if (dto.getData() == null || dto.getData().isEmpty()) return table;
        for (String[] data : dto.getData()) {
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[0])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[1])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[2])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        return table;
    }

    
}
