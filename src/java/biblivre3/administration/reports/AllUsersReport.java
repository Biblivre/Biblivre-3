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
import biblivre3.administration.reports.dto.AllUsersReportDto;
import biblivre3.administration.reports.dto.BaseReportDto;
import java.util.ArrayList;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;
import java.util.Map;

public class AllUsersReport extends BaseBiblivreReport {

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        ReportsDAO dao = new ReportsDAO();
        return dao.getAllUsersReportData();
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        AllUsersReportDto dto = (AllUsersReportDto)reportData;
        Paragraph p1 = new Paragraph(this.getText("REPORTS_ALL_USERS"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        Paragraph p2 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ALL_USERS_TYPE_TOTALS")));
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(p2);
        document.add(new Phrase("\n"));
        PdfPTable summaryTable = createSummaryTable(dto.getTypesMap());
        document.add(summaryTable);
        document.add(new Phrase("\n"));

        ArrayList<PdfPTable> listTable = createListTable(dto.getData());
        if (listTable != null) {
            Paragraph p3 = new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ALL_USERS_TYPE_LIST")));
            p3.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(p3);
            document.add(new Phrase("\n"));
            for (PdfPTable tabela : listTable) {
                document.add(tabela);
                document.add(new Phrase("\n"));
            }
        }
    }

    private final PdfPTable createSummaryTable(Map<String, Integer> tipos) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50f);
        table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);

        int total = 0;
        PdfPCell cell;
        for (String description : tipos.keySet()) {
        	total += tipos.get(description);
            cell = new PdfPCell(new Paragraph(this.getHeaderChunk(description.toUpperCase())));
            cell.setBackgroundColor(headerBgColor);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(tipos.get(description)))));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_TOTAL"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(total))));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        return table;
    }

    private final ArrayList<PdfPTable> createListTable(Map<String, List<String>> data) {
    	try {
    		ArrayList<PdfPTable> tabelas = new ArrayList<PdfPTable>();
    		PdfPTable table = null;
    		PdfPCell cell;
    		for (String description : data.keySet()) {
                table = new PdfPTable(4);
                table.setWidthPercentage(100f);
                cell = new PdfPCell(new Paragraph(this.getHeaderChunk(description.toUpperCase())));
                cell.setColspan(4);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_NAME"))));
                cell.setBackgroundColor(headerBgColor);
                cell.setBorderWidth(headerBorderWidth);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ENROL"))));
                cell.setBackgroundColor(headerBgColor);
                cell.setBorderWidth(headerBorderWidth);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_INSERTION_DATE"))));
                cell.setBackgroundColor(headerBgColor);
                cell.setBorderWidth(headerBorderWidth);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_UPDATE_DATE"))));
                cell.setBackgroundColor(headerBgColor);
                cell.setBorderWidth(headerBorderWidth);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                table.addCell(cell);

                for (String line : data.get(description)) {
                    String[] dados = line.split("\t");
                    //Nome
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[0])));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    table.addCell(cell);

                    //Matricula
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[1])));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    table.addCell(cell);

                    //Data de Inclusao
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[2])));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    table.addCell(cell);

                    //Data de Cancelamento/Alteracao
                    cell = new PdfPCell(new Paragraph(this.getNormalChunk(dados[3])));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    table.addCell(cell);
                }
                if (table != null) {
                    tabelas.add(table);
                }
    		}
    		return tabelas;
    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    		return null;
    	}
    }
    
}
