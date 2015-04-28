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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biblivre3.circulation;

import biblivre3.cataloging.bibliographic.BiblioSearchBO;
import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.circulation.lending.LendingBO;
import biblivre3.circulation.lending.LendingDTO;
import biblivre3.circulation.lending.LendingFineBO;
import biblivre3.circulation.lending.LendingFineDTO;
import biblivre3.circulation.lending.LendingHistoryDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.holding.HoldingDTO;
import biblivre3.utils.ApplicationConstants;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import mercury.BaseBO;
import mercury.I18nUtils;
import mercury.MemoryFileDTO;

/**
 *
 */
public class CirculationBO extends BaseBO {

    private int recordsPPage;
    private CirculationDAO dao;
    
    public CirculationBO() {
        try {
            final String rpp = Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE);
            this.recordsPPage = Integer.valueOf(rpp);
            dao = new CirculationDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }
    
    public final UserDTO searchByName(String name) {
        return dao.searchByName(name);
    }

    public final UserDTO searchByUserId(int userid) {
        UserDTO dto = dao.searchByUserId(userid);
        if (dto != null) {
            List<LendingFineDTO> fines = new LendingFineBO().listLendingFines(dto, true);
            float totalFines = 0f;
            for (LendingFineDTO fine : fines) {
                totalFines += fine.getValue();
            }
            dto.setTotalFines(totalFines);
        }
        return dto;
    }

    public final UserDTO searchByLoginId(int loginId) {
        return dao.searchByLoginId(loginId);
    }

    public final UsersDTO list(String name, int userId, int offset) {
        return dao.list(name, userId, 0, offset, this.recordsPPage);
    }    
    
    public final UserTypeDTO getUserTypeById(final Integer serial) {
        return dao.findUserTypeById(serial);
    }
    
    public final int addUser(UserDTO userDTO) {
        return dao.addUser(userDTO);
    }

    public final void deleteUser(int userId) {
        dao.deleteUser(userId);
    }

    public final boolean blockUser(int userId, boolean block) {
        return dao.blockUser(userId, block);
    }

    public final void updateUser(UserDTO userDTO, int userid) {
        dao.updateUser(userDTO, userid);
    }

    public Collection<UserTypeDTO> findAllUserType() {
        return dao.findAllUserType();
    }

    public boolean existsUserCard(int userId, String userType) {
        return dao.existsUserCard(userId, userType);
    }

    public boolean insertUserCard(UserCardDTO userCard) {
        return dao.insertUserCard(userCard);
    }

    public Integer countUsersByUserType(Integer userTypeId) {
        return dao.countUsersByUserType(userTypeId);
    }

    public Integer countUserCards() {
        return dao.countUserCards();
    }

    public UserHistoryDTO getUserHistory(Integer userID) {
        UserHistoryDTO dto = new UserHistoryDTO();
        UserDTO user = new UserDTO();
        user.setUserid(userID);

        LendingBO lendingBo = new LendingBO();
        HoldingBO holdingBo = new HoldingBO();
        LendingFineBO fineBo = new LendingFineBO();
        BiblioSearchBO biblioBo = new BiblioSearchBO();
        List<LendingDTO> currentLendings = (List<LendingDTO>)lendingBo.listLendings(user);
        //Populate author and title
        for (LendingDTO ldto : currentLendings) {
            HoldingDTO holding = holdingBo.getById(ldto.getHoldingSerial());
            RecordDTO record = biblioBo.getById(holding.getRecordSerial());
            String[] detail = holdingBo.getBibliographicDetails(record);

            ldto.setAuthor(detail[0]);
            ldto.setTitle(detail[1]);
            ldto.setAssetHolding(holding.getAssetHolding());
        }
        dto.setCurrentLendings(currentLendings);

        List<LendingHistoryDTO> pastLendings = (List<LendingHistoryDTO>)lendingBo.listHistory(user);
        //Populate author and title
        for (LendingHistoryDTO ldto : pastLendings) {
            HoldingDTO holding = holdingBo.getById(ldto.getHoldingSerial());
            RecordDTO record = biblioBo.getById(holding.getRecordSerial());
            String[] detail = holdingBo.getBibliographicDetails(record);
            
            ldto.setAuthor(detail[0]);
            ldto.setTitle(detail[1]);
            ldto.setAssetHolding(holding.getAssetHolding());
        }
        dto.setPastLendings(pastLendings);

        Collection<LendingFineDTO> fines = fineBo.listLendingFines(user);
        List<LendingFineDTO> currentFines = new ArrayList<LendingFineDTO>();
        List<LendingFineDTO> pastFines = new ArrayList<LendingFineDTO>();
        for (LendingFineDTO fine : fines) {
            if (fine.getPayment() != null) {
                pastFines.add(fine);
            } else {
                currentFines.add(fine);
            }
        }
        dto.setCurrentFines(currentFines);
        dto.setPastFines(pastFines);

        return dto;
    }

    public int[] generateUserCardsByDate(Date startDate, Date endDate) {
        SimpleDateFormat expectedFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<UserDTO> list = new CirculationDAO().getUsersByDate(expectedFormat.format(startDate), expectedFormat.format(endDate));
        HashMap<Integer, UserTypeDTO> userTypes = new HashMap<Integer, UserTypeDTO>();
        int total = list.size();
        int added = 0;

        for (int i = 0; i < total; i++) {
            UserDTO userDto = list.get(i);
            UserTypeDTO userTypeDto;

            if (userTypes.containsKey(userDto.getUserType())) {
                userTypeDto = userTypes.get(userDto.getUserType());
            } else {
                userTypeDto = this.getUserTypeById(userDto.getUserType());
                userTypes.put(userDto.getUserType(), userTypeDto);
            }
            String userTypeName = userTypeDto != null ? userTypeDto.getName() : "";
            boolean alreadyInList = new CirculationDAO().existsUserCard(userDto.getUserid(), userTypeName);
            if (alreadyInList) {
                continue;
            }

            UserCardDTO userCard = new UserCardDTO();
            userCard.setUserId(userDto.getUserid());
            userCard.setUserName(userDto.getName());
            userCard.setUserType(userTypeName);
            if (this.insertUserCard(userCard)) {
                added++;
            }
        }

        int result[] = new int[2];
        result[0] = total;
        result[1] = added;

        return result;
    }

    public final ArrayList<UserCardDTO> listPendingUserCards() {
        return dao.listPendingUserCards();
    }

    public final ArrayList<UserCardDTO> listSelectedUserCards(String[] cards) {
        return dao.listSelectedUserCards(cards);
    }

    public boolean deleteUserCards(String[] cards) {
        return dao.deleteUserCards(cards);
    }

    public MemoryFileDTO createFileUserCardsPDF(ArrayList<UserCardDTO> cards, int startOffset, Properties i18n) {
        Document document = new Document();
        MemoryFileDTO file = new MemoryFileDTO();
        file.setFileName("user_cards_" + new Date().getTime() + ".pdf");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.setPageSize(PageSize.A4);
            document.setMargins(7.15f  * ApplicationConstants.MM_UNIT, 7.15f * ApplicationConstants.MM_UNIT, 9.09f * ApplicationConstants.MM_UNIT, 9.09f * ApplicationConstants.MM_UNIT);
            document.open();
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100f);
            PdfPCell cell;
            int i = 0;
            for (i = 0; i < startOffset; i++) {
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(46.47f * ApplicationConstants.MM_UNIT);
                table.addCell(cell);
            }
            for (UserCardDTO card : cards) {
                String userEnrollNumber = String.valueOf(card.getUserId());
                PdfContentByte cb = writer.getDirectContent();
                Barcode39 code39 = new Barcode39();
                code39.setExtended(true);
                while (userEnrollNumber.length() < 10) {
                    userEnrollNumber = "0" + userEnrollNumber;
                }
                code39.setCode(userEnrollNumber);
                code39.setStartStopText(false);
                Image image39 = code39.createImageWithBarcode(cb, null, null);
                image39.scalePercent(110f);
                Paragraph para = new Paragraph();
                String name = card.getUserName();
                name = name.length() >= 30 ? name.substring(0, 30) : name;
                Phrase p1 = new Phrase(name + "\n");
                Phrase p2 = new Phrase(new Chunk(image39, 0, 0));
                Phrase p3 = new Phrase(I18nUtils.getText(i18n, "LABEL_USER_SERIAL") + ": " + card.getUserId() + "\n");
                Phrase p4 = new Phrase(I18nUtils.getText(i18n, "LABEL_USER_TYPE") + ": " + card.getUserType() + "\n\n");
                para.add(p1);
                para.add(p3);
                para.add(p4);
                para.add(p2);
                cell = new PdfPCell(para);
                i++;
                cell.setFixedHeight(46.47f * ApplicationConstants.MM_UNIT);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }
            if ((i % 3) != 0) {
                while ((i % 3) != 0) {
                    i++;
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
                }
            }
            document.add(table);
            writer.flush();
            document.close();
            writer.close();
            file.setFileData(baos.toByteArray());
        } catch (DocumentException de) {
            System.out.println(de.getMessage());
        }
        return file;
    }

}
