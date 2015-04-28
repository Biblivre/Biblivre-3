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

import biblivre3.administration.reports.dto.SummaryReportDto;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.lang.StringUtils;

/**
 * @author Danniel Nascimento
 *
 */
public class SummaryReport extends BaseBiblivreReport implements Comparator<String[]> {

    private Integer index;

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        Integer order = 1;
        if (StringUtils.isNotBlank(dto.getOrder()) && StringUtils.isNumeric(dto.getOrder().trim())) {
            order = Integer.valueOf(dto.getOrder().trim());
        }
        switch (order) {
            case 1 : this.index = 6; break; //dewey
            case 2 : this.index = 0; break; //title
            case 3 : this.index = 1; break; //author
            default : this.index = 6; //dewey
        }
        return new ReportsDAO().getSummaryReportData(dto.getDatabase());
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        SummaryReportDto dto = (SummaryReportDto)reportData;
        Paragraph p1 = new Paragraph(this.getText("REPORTS_SUMMARY_TITLE"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100f);
        createHeader(table);
        Collections.sort(dto.getData(), this);
        PdfPCell cell;
        for (String[] data : dto.getData()) {
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[6])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[0])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[1])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[2])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[3])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[4])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[5])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[7])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        document.add(table);
    }

    private void createHeader(PdfPTable table) {
        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_DEWEY"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_TITLE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_AUTHOR"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_ISBN"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_EDITOR"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_YEAR"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_EDITION"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("REPORTS_HOLDINGS_TOTAL"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
    }


    @Override
    public int compare(String[] o1, String[] o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1[index] == null && o2[index] == null) return 0;
        return o1[index].compareTo(o2[index]);
    }

}
