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
public class LendingHistoryDTO extends DTO {
    
    private Integer serial;
    private Integer holdingSerial;
    private Integer userSerial;
    private Date lendDate;
    private Date returnDate;
    private String author;
    private String title;
    private String assetHolding;
    
    public LendingHistoryDTO(){}
    
    public LendingHistoryDTO(final LendingDTO dto) {
        this.setHoldingSerial(dto.getHoldingSerial());
        this.setUserSerial(dto.getUserSerial());
        this.setLendDate(dto.getLendDate());
        this.setReturnDate(new Date());
    }
    
    public final Integer getHoldingSerial() {
        return holdingSerial;
    }

    public final void setHoldingSerial(Integer holdingId) {
        this.holdingSerial = holdingId;
    }

    public final Date getLendDate() {
        return lendDate;
    }

    public final void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    public final Date getReturnDate() {
        return returnDate;
    }

    public final void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public final Integer getSerial() {
        return serial;
    }

    public final void setSerial(Integer serial) {
        this.serial = serial;
    }

    public final Integer getUserSerial() {
        return userSerial;
    }

    public final void setUserSerial(Integer userId) {
        this.userSerial = userId;
    }
    
    public final String getLendDateFormatted() {
        return DateUtils.format(DateUtils.dd_MM_yyyy, this.getLendDate());
    }
    
    public final String getReturnDateFormatted() {
        return DateUtils.format(DateUtils.dd_MM_yyyy, this.getReturnDate());
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

    public String getAssetHolding() {
        return assetHolding;
    }

    public void setAssetHolding(String assetHolding) {
        this.assetHolding = assetHolding;
    }

}
