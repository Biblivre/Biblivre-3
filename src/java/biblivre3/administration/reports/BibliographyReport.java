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

import biblivre3.administration.reports.dto.BibliographyReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.List;

public class BibliographyReport extends BaseBiblivreReport {

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        String ids = dto.getRecordIds();
        ids = ids.substring(1, ids.length() - 1);
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<Integer>();
        for (int i = 0; i < idArray.length; i++) {
            try {
                idList.add(Integer.valueOf(idArray[i].trim()));
            } catch (Exception e) {}
        }
        return new ReportsDAO().getBibliographyReportData(dto.getAuthorName(), idList.toArray(new Integer[]{}));
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        BibliographyReportDto dto = (BibliographyReportDto)reportData;
        Paragraph p1 = new Paragraph(this.getText("REPORTS_BIBLIOGRAPHY_TITLE"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        Paragraph p2 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_AUTHOR") + ":  " + dto.getAuthorName()));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));
        if (dto.getData() != null) {
            PdfPTable table = new PdfPTable(8);
            table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
            createHeader(table);
            PdfPCell cell;
        	for (String[] data : dto.getData()) {
                cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[0])));
                cell.setColspan(3);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[1])));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[2])));
                cell.setColspan(2);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[3])));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[4])));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);
        	}
            document.add(table);
            document.add(new Phrase("\n"));
        }
    }

    private void createHeader(PdfPTable table) {
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_TITLE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(3);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_EDITION"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_EDITOR"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_YEAR"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_LOCAL"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
    }
    
}
