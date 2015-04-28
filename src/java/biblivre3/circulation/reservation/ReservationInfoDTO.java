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

package biblivre3.circulation.reservation;

import java.util.Date;
import java.util.Properties;
import mercury.DTO;
import mercury.I18nUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Danniel Nascimento
 * @since  Mar 17, 2009
 */
public final class ReservationInfoDTO extends DTO {

    private Integer reservationSerial;

    // Biblio Info
    private Integer recordSerial;
    private String title;
    private String author;
    
    private Date created;
    private Date expires;

    // User Info
    private Integer userSerial;
    private String userName;
    private String message;
    private String userPhoneNumber;
    private String userEmail;

    /**
     * @return the serial
     */
    public Integer getRecordSerial() {
        return recordSerial;
    }

    /**
     * @param serial the serial to set
     */
    public void setRecordSerial(Integer recordSerial) {
        this.recordSerial = recordSerial;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
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

    public Integer getReservationSerial() {
        return reservationSerial;
    }

    public void setReservationSerial(Integer reservationSerial) {
        this.reservationSerial = reservationSerial;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = super.toJSONObject(properties);
        try {
            if (this.getMessage() != null) {
                json.put("message", I18nUtils.getText(properties, this.getMessage()));
            }
        } catch (JSONException e) {
        }
        return json;
    }

}
