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
import biblivre3.administration.reports.dto.HoldingCreationByDateReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoldingCreationByDatetReport extends BaseBiblivreReport {

	private static Map<String, Integer> totalUsuario;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        ReportsDAO dao = new ReportsDAO();
        String initialDate = dateFormat.format(dto.getInitialDate());
        String finalDate = dateFormat.format(dto.getFinalDate());
        return dao.getHoldingCreationByDateReportData(initialDate, finalDate);
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        HoldingCreationByDateReportDto dto = (HoldingCreationByDateReportDto)reportData;
        totalUsuario = new HashMap<String, Integer>();
        Paragraph p1 = new Paragraph(this.getText("REPORTS_INSERTION_BY_DATE_TITLE"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        String dateSpan = this.getText("REPORTS_FROM") + " " + dto.getInitialDate() + " " + this.getText("REPORTS_TO")+ " " + dto.getFinalDate();
        Paragraph p2 = new Paragraph(this.getHeaderChunk(dateSpan));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));

        if (dto.getData() != null) {
            PdfPTable table = createTable(dto.getData());
            document.add(table);
            document.add(new Phrase("\n"));
        }

        if (totalUsuario.size() > 0) {
            Paragraph p3 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_INSERTION_BY_DATE_USER_TOTAL")));
            p3.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p3);
            document.add(new Phrase("\n"));
            PdfPTable table = new PdfPTable(2);
            PdfPCell cell;
            cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_NAME"))));
            cell.setBackgroundColor(headerBgColor);
            cell.setBorderWidth(headerBorderWidth);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_TOTAL"))));
            cell.setBackgroundColor(headerBgColor);
            cell.setBorderWidth(headerBorderWidth);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            for (String nome : totalUsuario.keySet()) {
                cell = new PdfPCell(new Paragraph(this.getNormalChunk(nome)));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(totalUsuario.get(nome)))));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);
            }
            document.add(table);
            document.add(new Phrase("\n"));
        }

        //Database totals table
		Paragraph p3 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_INSERTION_BY_DATE_DATABASE_TOTALS")));
        p3.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p3);
        document.add(new Phrase("\n"));
        PdfPTable table = new PdfPTable(3);
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_DATABASE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_BIBLIO"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_HOLDING"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_MAIN"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        String biblio = dto.getTotalBiblioMain();
        biblio = biblio != null ? biblio : "";
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(biblio)));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        String tombos = dto.getTotalHoldingMain();
        tombos = tombos != null ? tombos : "";
        PdfPCell cell2 = new PdfPCell(new Paragraph(this.getNormalChunk(tombos)));
        cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell2);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_WORK"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        biblio = dto.getTotalBiblioWork();
        biblio = biblio != null ? biblio : "";
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(biblio)));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        tombos = dto.getTotalHoldingWork();
        tombos = tombos != null ? tombos : "";
        cell2 = new PdfPCell(new Paragraph(this.getNormalChunk(tombos)));
        cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell2);

        document.add(table);

    }

    private PdfPTable createTable(List<String[]> dataList) {
        PdfPTable table = new PdfPTable(4);
        table.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        createHeader(table);
        PdfPCell cell;
        for (String[] data : dataList) {
            String name = data[1];
            int total = Integer.valueOf(data[2]);
            if (totalUsuario.containsKey(name)) {
                totalUsuario.put(name, totalUsuario.get(name) + total);
            } else {
                totalUsuario.put(name, total);
            }
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[0])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(name)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(total))));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        return table;
    }

    private void createHeader(PdfPTable table) {
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_DATE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_NAME"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_TOTAL"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
    }

}
