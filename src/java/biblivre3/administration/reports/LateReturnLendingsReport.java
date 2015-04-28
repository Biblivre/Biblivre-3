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
import biblivre3.administration.reports.dto.DelayedLendingsDto;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;

public class LateReturnLendingsReport extends BaseBiblivreReport {

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        return new ReportsDAO().getLateReturnLendingsReportData();
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        DelayedLendingsDto dto = (DelayedLendingsDto)reportData;
        Paragraph p1 = new Paragraph(this.getText("REPORTS_DELAYED_LENDING_TITLE"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));

        if (dto.getData().size() != 0) {
            Paragraph p2 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_DELAYED_LENDING_TOTAL") + ":  " + dto.getData().size()));
            p2.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(p2);
            document.add(new Phrase("\n"));

            PdfPTable table = createTable(dto.getData());
            document.add(table);
            document.add(new Phrase("\n"));
        }
    }

    private PdfPTable createTable(List<String[]> lendings) {
        PdfPTable table = new PdfPTable(6);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        table.setWidthPercentage(100f);
        
        createHeader(table);

        PdfPCell cell;
        for (String[] lending : lendings) {
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[0])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[1])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[2])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[3])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        return table;
    }

    private void createHeader(PdfPTable table) {
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ENROL"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_NAME"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_TITLE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ESTIMATED_DATE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
    }

}
