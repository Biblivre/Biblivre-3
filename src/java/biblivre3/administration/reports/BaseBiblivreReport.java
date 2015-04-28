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

import biblivre3.administration.ReportsDTO;
import biblivre3.administration.reports.dto.BaseReportDto;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import mercury.I18nUtils;
import mercury.MemoryFileDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Danniel
 */
public abstract class BaseBiblivreReport extends PdfPageEventHelper implements IBiblivreReport {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    protected final Float headerBorderWidth = 0.8f;
    protected final Float smallFontSize = 8f;
    protected final Float reportFontSize = 10f;
    protected final Float pageNumberFontSize = 8f;
    protected final Color headerBgColor = new Color(239, 239, 239);
    protected final Font smallFont = FontFactory.getFont("Arial", smallFontSize, Font.NORMAL, Color.BLACK);
    protected final Font textFont = FontFactory.getFont("Arial", reportFontSize, Font.NORMAL, Color.BLACK);
    protected final Font boldFont = FontFactory.getFont("Arial", smallFontSize, Font.BOLD, Color.BLACK);
    protected final Font headerFont = FontFactory.getFont("Arial", reportFontSize, Font.BOLD, Color.BLACK);
    protected final Font footerFont = FontFactory.getFont(FontFactory.COURIER, pageNumberFontSize, Font.BOLD, Color.BLACK);
    protected Properties i18n;
    private PdfWriter writer;
    private Date generationDate;
    protected DateFormat dateFormat;

    @Override
    public final MemoryFileDTO generateReport(ReportsDTO dto) {
        this.generationDate = new Date();
        dateFormat = new SimpleDateFormat(this.getText("DEFAULT_DATETIME_FORMAT"));
        BaseReportDto reportData = getReportData(dto);
        String fileName = this.getFileName(dto);
        return generateReportFile(reportData, fileName);
    }

    protected abstract BaseReportDto getReportData(ReportsDTO dto);

    protected MemoryFileDTO generateReportFile(BaseReportDto reportData, String fileName) {
        Document document = new Document(PageSize.A4);
        MemoryFileDTO report = new MemoryFileDTO();
        report.setFileName(fileName);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(this);
            writer.setFullCompression();
            document.open();
            generateReportBody(document, reportData);
            writer.flush();
            document.close();
            report.setFileData(baos.toByteArray());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } 
        return report;
    }

    private String getFileName(ReportsDTO dto) {
        final String reportName = dto.getType().getName();
        final Calendar c = Calendar.getInstance();
        final int dia = c.get(Calendar.DATE);
        final int mes = c.get(Calendar.MONTH) + 1;
        final int hora = c.get(Calendar.HOUR_OF_DAY);
        final int minuto = c.get(Calendar.MINUTE);
        return reportName + dia + mes + hora + minuto + ".pdf";
    }

    protected abstract void generateReportBody(Document document, BaseReportDto reportData) throws Exception;

    @Override
    public final void setI18n(Properties i18n) {
        this.i18n = i18n;
    }

    protected final String getText(String key) {
        return I18nUtils.getText(i18n, key);
    }


    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            Rectangle page = document.getPageSize();
            
            PdfPTable head = new PdfPTable(1);
            PdfPCell cell = new PdfPCell(new Paragraph(this.getText("REPORTS_HEADER")));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOTTOM);
            head.addCell(cell);
            head.setTotalWidth( (page.width() / 2) - document.leftMargin());
            head.writeSelectedRows(0, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());

            PdfPTable date = new PdfPTable(1);
            PdfPCell dateCell = new PdfPCell(new Paragraph(dateFormat.format(generationDate)));
            dateCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            dateCell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            dateCell.setBorder(Rectangle.BOTTOM);
            date.addCell(dateCell);
            date.setTotalWidth( (page.width() / 2) - document.rightMargin());
            date.writeSelectedRows(0, -1, (page.width() / 2), page.height() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());


            PdfPTable foot = new PdfPTable(1);
            Chunk pageNumber = new Chunk(String.valueOf(document.getPageNumber()));
            pageNumber.setFont(footerFont);
            cell = new PdfPCell(new Paragraph(pageNumber));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setBorder(Rectangle.TOP);
            foot.addCell(cell);
            foot.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
            foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    protected Chunk getNormalChunk(String text) {
        Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
        chunk.setFont(textFont);
        return chunk;
    }

    protected Chunk getBoldChunk(String text) {
        Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
        chunk.setFont(boldFont);
        return chunk;
    }

    protected Chunk getSmallFontChunk(String text) {
        Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
        chunk.setFont(smallFont);
        return chunk;
    }

    protected Chunk getHeaderChunk(String text) {
        Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
        chunk.setFont(headerFont);
        return chunk;
    }


    protected PdfWriter getWriter() {
        return this.writer;
    }

}
