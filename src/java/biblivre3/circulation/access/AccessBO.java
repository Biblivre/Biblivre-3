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

package biblivre3.circulation.access;

import biblivre3.administration.cards.CardBO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.CardStatus;
import java.sql.Timestamp;
import java.util.Date;
import mercury.BaseBO;

public class AccessBO extends BaseBO {

    private int recordsPPage;
    private AccessDAO dao;

    public AccessBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new AccessDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public boolean insert(AccessDTO dto) {
        if (dao.insert(dto)) {
            return new CardBO().updateCardStatus(CardStatus.IN_USE, dto.getSerialCard());
        }
        return false;
    }

    public boolean doExit(Integer cardId, Integer userId) {
        CardStatus newStatus = cardId != null ? CardStatus.AVAILABLE : CardStatus.IN_USE_AND_BLOCKED;
        AccessDTO dto = null;
        if (cardId != null) {
            dto = this.getByCardId(cardId);
        } else {
            dto = this.getByUserId(userId);
        }
        dto.setDepartureDatetime(new Timestamp(new Date().getTime()));
        if (this.update(dto)) {
            return new CardBO().updateCardStatus(newStatus, dto.getSerialCard());
        }
        return false;
    }


    public boolean update(AccessDTO dto) {
        return dao.update(dto);
    }

    public AccessDTO getByCardId(Integer cardId) {
        return dao.getByCardId(cardId);
    }

    public AccessDTO getByUserId(Integer userId) {
        return dao.getByUserId(userId);
    }

}
