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

package biblivre3.circulation;

import biblivre3.circulation.lending.LendingDTO;
import biblivre3.circulation.lending.LendingFineDTO;
import biblivre3.circulation.lending.LendingHistoryDTO;
import java.util.List;
import mercury.DTO;

/**
 *
 * @author Danniel Nascimento
 */
public class UserHistoryDTO extends DTO {

    public List<LendingFineDTO> currentFines;
    public List<LendingFineDTO> pastFines;
    public List<LendingDTO> currentLendings;
    public List<LendingHistoryDTO> pastLendings;

    public List<LendingFineDTO> getCurrentFines() {
        return currentFines;
    }

    public void setCurrentFines(List<LendingFineDTO> currentFines) {
        this.currentFines = currentFines;
    }

    public List<LendingDTO> getCurrentLendings() {
        return currentLendings;
    }

    public void setCurrentLendings(List<LendingDTO> currentLendings) {
        this.currentLendings = currentLendings;
    }

    public List<LendingFineDTO> getPastFines() {
        return pastFines;
    }

    public void setPastFines(List<LendingFineDTO> pastFines) {
        this.pastFines = pastFines;
    }

    public List<LendingHistoryDTO> getPastLendings() {
        return pastLendings;
    }

    public void setPastLendings(List<LendingHistoryDTO> pastLendings) {
        this.pastLendings = pastLendings;
    }

}
