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

package biblivre3.administration.cards;

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.CardStatus;
import java.util.ArrayList;
import java.util.List;
import mercury.BaseBO;
import mercury.DTO;

public class CardBO extends BaseBO {

    private int recordsPPage;
    private CardDAO dao;

    public CardBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new CardDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }


    public boolean addCard(CardDTO dto) {
        return dao.addCard(dto);
    }

    public CardSearchResultsDTO list(int offset) {
        CardSearchResultsDTO dto = new CardSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            List<CardDTO> cards = dao.listCards(offset, recordsPPage);
            if (!cards.isEmpty()) {
                dto.al.addAll(cards);
                int total = dao.getTotalNroRecords();
                int nroPages = total / recordsPPage;
                int mod = total % recordsPPage;
                dto.totalRecords = total;
                dto.recordsPerPage = recordsPPage;
                dto.totalPages = mod == 0 ? nroPages : nroPages + 1;
                dto.currentPage = (offset / recordsPPage) + 1;
                return dto;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public CardSearchResultsDTO searchCards(String searchTerms, int offset) {
        CardSearchResultsDTO dto = new CardSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            List<CardDTO> cards = dao.searchCards(searchTerms, offset, recordsPPage);
            if (!cards.isEmpty()) {
                dto.al.addAll(cards);
                int total = dao.getSearchCardsCount(searchTerms);
                int nroPages = total / recordsPPage;
                int mod = total % recordsPPage;
                dto.totalRecords = total;
                dto.recordsPerPage = recordsPPage;
                dto.totalPages = mod == 0 ? nroPages : nroPages + 1;
                dto.currentPage = (offset / recordsPPage) + 1;
                return dto;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public CardDTO getCardById(Integer cardId) {
        return dao.getCardById(cardId);
    }

    public CardDTO getCardByNumber(String cardNumber) {
        return dao.getCardByNumber(cardNumber);
    }

    public boolean addSequenceCard(List<CardDTO> cardList) {
        boolean result = true;
        for (CardDTO dto : cardList) {
            result &= this.addCard(dto);
        }
        return result;
    }

    public boolean removeCard(Integer cardId) {
        return this.updateCardStatus(CardStatus.CANCELLED, cardId);
    }

    public final boolean updateCardStatus(CardStatus newStatus, Integer cardId) {
        return dao.updateCardStatus(newStatus, cardId);
    }


}
