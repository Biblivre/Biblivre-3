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

import biblivre3.utils.DateUtils;
import java.util.Date;
import mercury.DTO;

/**
 *
 * @author Danniel Nascimento
 * @since  Mar 17, 2009
 */
public class LendingDTO extends DTO {
    
    private Integer serial;
    private Integer holdingSerial;
    private Integer userSerial;
    private Date lendDate;
    private Date returnDate;
    private String author;
    private String title;
    private String assetHolding;

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

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public final String getLendDateFormatted() {
        return DateUtils.format(DateUtils.dd_MM_yyyy, this.getLendDate());
    }

    public final String getReturnDateFormatted() {
        return DateUtils.format(DateUtils.dd_MM_yyyy, this.getReturnDate());
    }

    public String getAssetHolding() {
        return assetHolding;
    }

    public void setAssetHolding(String assetHolding) {
        this.assetHolding = assetHolding;
    }
}
