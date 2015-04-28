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
import biblivre3.administration.reports.dto.UserReportDto;
import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.circulation.UserHistoryDTO;
import biblivre3.circulation.lending.LendingDTO;
import biblivre3.circulation.lending.LendingHistoryDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserReport extends BaseBiblivreReport {

    @Override
    protected BaseReportDto getReportData(ReportsDTO dto) {
        UserReportDto urdto = new UserReportDto();
        CirculationBO cbo = new CirculationBO();
        Integer userid = Integer.valueOf(dto.getUserId());
        UserHistoryDTO history = cbo.getUserHistory(userid);
        UserDTO user = cbo.searchByUserId(userid);
        urdto.setUser(user);

        List<String[]> returnedLendings = new ArrayList<String[]>();
        for (LendingHistoryDTO lhdto : history.getPastLendings()) {
            String[] data = new String[3];
            data[0] = lhdto.getLendDateFormatted();
            data[1] = lhdto.getTitle();
            data[2] = lhdto.getAuthor();
            returnedLendings.add(data);
        }
        urdto.setReturnedLendings(returnedLendings);

        List<String[]> currentLendings = new ArrayList<String[]>();
        List<String[]> lateLendings = new ArrayList<String[]>();
        Date today = new Date();
        for (LendingDTO ldto : history.getCurrentLendings()) {
            String[] data = new String[3];
            data[0] = ldto.getLendDateFormatted();
            data[1] = ldto.getTitle();
            data[2] = ldto.getAuthor();
            if (today.after(ldto.getReturnDate())) {
                lateLendings.add(data);
            } else {
                currentLendings.add(data);
            }
        }
        urdto.setLendings(currentLendings);
        urdto.setLateLendings(lateLendings);

        return urdto;
    }

    @Override
    protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
        UserReportDto dto = (UserReportDto)reportData;
        Paragraph p1 = new Paragraph(this.getText("REPORTS_USER_TITLE"));
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p1);
        document.add(new Phrase("\n"));

        PdfPTable dataTable = createUserDataTable(dto.getUser());
        document.add(dataTable);

        Paragraph p2 = new Paragraph(this.getText("REPORTS_ADDRESS"));
        p2.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p2);
        document.add(new Phrase("\n"));
        document.add(createAddressTable(dto.getUser()));

        Paragraph p3 = null;
        if (dto.getLendings() != null && dto.getLendings().size() > 0) {
            document.add(new Phrase("\n"));
            p3 = new Paragraph(this.getText("REPORTS_USER_LENDINGS"));
            p3.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p3);
            document.add(new Phrase("\n"));
            document.add(createLendingsTable(dto.getLendings()));
        }

        if (dto.getLateLendings() != null && dto.getLateLendings().size() > 0) {
            document.add(new Phrase("\n"));
            p3 = new Paragraph(this.getText("REPORTS_USER_LATE_LENDINGS"));
            p3.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p3);
            document.add(new Phrase("\n"));
            document.add(createLendingsTable(dto.getLateLendings()));
        }

        if (dto.getReturnedLendings() != null && dto.getReturnedLendings().size() > 0) {
            document.add(new Phrase("\n"));
            p3 = new Paragraph(this.getText("REPORTS_USER_RETURNED_LENDINGS"));
            p3.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p3);
            document.add(new Phrase("\n"));
            document.add(createLendingsTable(dto.getReturnedLendings()));
        }
    }


    private PdfPTable createLendingsTable(List<String[]> lendings) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);

        PdfPCell cell;
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_DATE"))));
        cell.setBackgroundColor(headerBgColor);
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
        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_AUTHOR"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setColspan(2);
        cell.setBorderWidth(headerBorderWidth);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        for (String[] lending : lendings) {
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[0])));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[1])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[2])));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            table.addCell(cell);
        }
        return table;
    }

    private PdfPTable createUserDataTable(UserDTO user) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        PdfPCell cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_NAME"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getName())));
        cell.setColspan(3);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ENROL"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(user.getUserid()))));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_SIGNUP_DATE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        Date signupDate = user.getSignup_date();
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(signupDate != null ? dateFormat.format(signupDate) : "")));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_SOCIAL_NUMBER"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getSocial_id_number())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ID"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getDlicense() + " " + user.getTypeId())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_BIRTHDATE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getBirthday())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_OBS"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getObs())));
        cell.setColspan(4);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        return table;
    }

    private PdfPTable createAddressTable(UserDTO user) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        PdfPCell cell;

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ADDRESS"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getAddress())));
        cell.setColspan(3);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_NUMBER"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getNumber())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ADDRESS_2"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getCompletion())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_CITY"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getCity())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_STATE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getState())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_COUNTRY"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getCountry())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_ZIP_CODE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getZip_code())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_HOME_PHONE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getTelRef1())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_MOBILE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getCellphone())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_OFFICE_PHONE"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getTelRef2())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_EXTENSION"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getExtension_line())));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("REPORTS_EMAIL"))));
        cell.setBackgroundColor(headerBgColor);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getEmail())));
        cell.setColspan(3);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);

        return table;
    }
    
}