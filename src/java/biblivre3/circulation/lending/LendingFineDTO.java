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

import java.util.Date;
import mercury.DTO;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  23/03/2009
 */
public class LendingFineDTO extends DTO {
    
    private Integer serial;
    private Integer userSerial;
    private Integer lendingHistorySerial;
    private Float value;
    private Date payment;

    public final Integer getLendingHistorySerial() {
        return lendingHistorySerial;
    }

    public final void setLendingHistorySerial(Integer lendingHistorySerial) {
        this.lendingHistorySerial = lendingHistorySerial;
    }

    public final Date getPayment() {
        return payment;
    }

    public final void setPayment(Date payment) {
        this.payment = payment;
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

    public final void setUserSerial(Integer userSerial) {
        this.userSerial = userSerial;
    }

    public final Float getValue() {
        return value;
    }

    public final void setValue(Float value) {
        this.value = value;
    }
            
            

}
