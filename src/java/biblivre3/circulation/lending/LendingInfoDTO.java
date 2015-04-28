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

package biblivre3.circulation.lending;

import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.cataloging.bibliographic.BiblioSearchBO;
import biblivre3.circulation.CirculationBO;
import biblivre3.circulation.UserDTO;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.holding.HoldingDTO;
import biblivre3.circulation.reservation.ReservationBO;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import java.util.Date;
import java.util.Properties;
import mercury.DTO;
import mercury.I18nUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

/**
 *
 * @author Danniel Nascimento
 * @since  Mar 17, 2009
 */
public final class LendingInfoDTO extends DTO {

    // Biblio Info
    private Integer serial;
    private String title;
    private String author;

    // Holding Info
    private Integer holdingSerial;
    private String assetHolding;
    private boolean lent;
    private Date lendDate;
    private Date returnDate;

    private boolean reserved;

    // User Info
    private Integer userSerial;
    private String userName;
    private String userPhoneNumber;
    private String userEmail;

    private String message;

    private Integer daysLate;
    private Float fineValue;
    private boolean finePaid;

    public LendingInfoDTO() {
    }

    public LendingInfoDTO(HoldingDTO hdto) {
        this(hdto, 0);
    }

    public LendingInfoDTO(HoldingDTO hdto, int userId) {
        if (hdto == null) {
            throw new NullPointerException();
        }

        // Setting holding info
        this.setHoldingSerial(hdto.getSerial());
        this.setAssetHolding(hdto.getAssetHolding());

        // Getting the biblio info for this holding
        BiblioSearchBO bso = new BiblioSearchBO();
        RecordDTO rdto = bso.getById(hdto.getRecordSerial());

        if (rdto == null) {
            throw new NullPointerException();
        }

        Record record = MarcUtils.iso2709ToRecord(rdto.getIso2709());

        if (record == null) {
            throw new NullPointerException();
        }

        // Setting biblio info
        this.setSerial(hdto.getRecordSerial());
        this.setTitle(Indexer.listOneTitle(record));
        this.setAuthor(Indexer.listAuthors(record));

        // Getting lending info
        LendingBO lbo = new LendingBO();
        LendingDTO ldto = lbo.getByHolding(hdto);


                // Setting values
        this.setLent(ldto != null);

        if (this.isLent()) {
            this.setLendDate(ldto.getLendDate());
            this.setReturnDate(ldto.getReturnDate());

            // Getting user info
            CirculationBO cbo = new CirculationBO();
            UserDTO udto = cbo.searchByUserId(ldto.getUserSerial());

            ReservationBO rbo = new ReservationBO();
            int reservedCount = rbo.countReservedHoldings(hdto.getRecordSerial());
            this.setReserved(reservedCount > 0);

            if (udto != null) {
                // Settint user info
                this.setUserSerial(udto.getUserid());
                this.setUserName(udto.getName());
                this.setUserEmail(udto.getEmail());
                this.setUserPhoneNumber(udto.getTelRef1());
            }
        } else {
            HoldingBO hbo = new HoldingBO();
            ReservationBO rbo = new ReservationBO();

            int availableCount = hbo.countAvailableHoldings(hdto.getRecordSerial());
            int lendCount = lbo.countLentHoldings(hdto.getRecordSerial());
            int reservedCount = rbo.countReservedHoldings(hdto.getRecordSerial());

            this.setReserved(reservedCount >= (availableCount - lendCount));

            LendingHistoryDTO lhdto = lbo.getLastReturn(hdto.getSerial(), userId);

            if (lhdto != null) {
                this.setLendDate(lhdto.getLendDate());
                this.setReturnDate(lhdto.getReturnDate());

                // Getting user info
                CirculationBO cbo = new CirculationBO();
                UserDTO udto = cbo.searchByUserId(userId);
                
                LendingFineDTO lfd = new LendingFineBO().getByHistoryId(lhdto.getSerial());
                
                if (lfd != null) {
                    this.setFineValue(lfd.getValue());
                    this.setFinePaid(lfd.getPayment() != null);
                }

                if (udto != null) {
                    // Settint user info
                    this.setUserSerial(udto.getUserid());
                    this.setUserName(udto.getName());
                    this.setUserEmail(udto.getEmail());
                    this.setUserPhoneNumber(udto.getTelRef1());
                }
            }
        }
    }

    public LendingInfoDTO(LendingDTO ldto) {
        // Ugly
        this((new HoldingBO()).getById(ldto.getHoldingSerial()));

    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();

        try {
            json.put("serial", this.getSerial());
            json.put("title", this.getTitle());
            json.put("author", this.getAuthor());

            json.put("holdingSerial", this.getHoldingSerial());
            json.put("assetHolding", this.getAssetHolding());

            json.put("lent", this.isLent());
            json.putOpt("lendDate", this.getLendDate());
            json.putOpt("returnDate", this.getReturnDate());

            json.put("reserved", this.isReserved());

            json.putOpt("userSerial", this.getUserSerial());
            json.putOpt("userName", this.getUserName());

            json.putOpt("daysLate", this.getDaysLate());
            json.putOpt("fineValue", this.getFineValue());

            if (this.getMessage() != null) {
                json.put("message", I18nUtils.getText(properties, this.getMessage()));
            }
        } catch (JSONException e) {
        }

        return json;
    }

    /**
     * @return the serial
     */
    public Integer getSerial() {
        return serial;
    }

    /**
     * @param serial the serial to set
     */
    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the holdingSerial
     */
    public Integer getHoldingSerial() {
        return holdingSerial;
    }

    /**
     * @param holdingSerial the holdingSerial to set
     */
    public void setHoldingSerial(Integer holdingSerial) {
        this.holdingSerial = holdingSerial;
    }

    public String getAssetHolding() {
        return assetHolding;
    }

    public void setAssetHolding(String assetHolding) {
        this.assetHolding = assetHolding;
    }


    /**
     * @return the lent
     */
    public boolean isLent() {
        return lent;
    }

    /**
     * @param lent the lent to set
     */
    public void setLent(boolean lent) {
        this.lent = lent;
    }

    /**
     * @return the lendDate
     */
    public Date getLendDate() {
        return lendDate;
    }

    /**
     * @param lendDate the lendDate to set
     */
    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }


    /**
     * @return the lendDate
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * @param lendDate the lendDate to set
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * @return the userSerial
     */
    public Integer getUserSerial() {
        return userSerial;
    }

    /**
     * @param userSerial the userSerial to set
     */
    public void setUserSerial(Integer userSerial) {
        this.userSerial = userSerial;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getDaysLate() {
        return daysLate;
    }

    public void setDaysLate(Integer daysLate) {
        this.daysLate = daysLate;
    }

    public Float getFineValue() {
        return fineValue;
    }

    public void setFineValue(Float fineValue) {
        this.fineValue = fineValue;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public boolean isFinePaid() {
        return finePaid;
    }

    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }



}
