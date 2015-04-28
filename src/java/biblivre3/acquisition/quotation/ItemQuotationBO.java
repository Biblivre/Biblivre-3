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

package biblivre3.acquisition.quotation;

import java.util.ArrayList;
import mercury.BaseBO;

public class ItemQuotationBO extends BaseBO {

    private ItemQuotationDAO dao;

    public ItemQuotationBO() {
        try {
            dao = new ItemQuotationDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean insert(ItemQuotationDTO dto) {
        return dao.insertItemQuotation(dto);
    }

    public boolean update(ItemQuotationDTO dto) {
        return dao.updateQuotation(dto);
    }

    public ArrayList<ItemQuotationDTO> listItemQuotation(Integer serialQuotation) {
        return dao.listItemQuotations(serialQuotation);
    }

    public Boolean delete(ItemQuotationDTO dto) {
        return dao.deleteItemQuotation(dto);
    }

    public Boolean deleteAllByQuotationId(Integer quotationId) {
        return dao.deleteAllByQuotationId(quotationId);
    }

}
