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

package biblivre3.acquisition.order;

import biblivre3.acquisition.AcquisitionSearchResultsDTO;
import biblivre3.acquisition.quotation.QuotationBO;
import biblivre3.acquisition.quotation.QuotationDTO;
import biblivre3.acquisition.request.RequestBO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.util.ArrayList;
import mercury.BaseBO;
import mercury.DTO;

public class BuyOrderBO extends BaseBO {

    private int recordsPPage;
    private BuyOrderDAO dao;
    private QuotationBO quotationBo;

    public BuyOrderBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new BuyOrderDAO();
            quotationBo = new QuotationBO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public boolean insertBuyOrder(BuyOrderDTO dto) {
        return dao.insertBuyOrder(dto);
    }

    public boolean updateBuyOrder(BuyOrderDTO dto) {
        dao.updateBuyOrder(dto);
        new RequestBO().updateRequestStatus(dto.getSerial(), dto.getStatus());
        return true;
    }

    public AcquisitionSearchResultsDTO listBuyOrders(String status, int offset) {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<BuyOrderDTO> orders = dao.listBuyOrders(status, offset, recordsPPage);
            for (BuyOrderDTO order : orders) {
                QuotationDTO quotation = quotationBo.getQuotation(order.getSerialQuotation());
                order.setQuotation(quotation);
                dto.al.add(order);
            }
            int total = dao.getTotalNroRecords(status);
            int nroPages = total / recordsPPage;
            int mod = total % recordsPPage;
            dto.recordsPerPage = recordsPPage;
            dto.totalRecords = total;
            dto.totalPages = mod == 0 ? nroPages : nroPages + 1;
            dto.currentPage = (offset / recordsPPage) + 1;
            return dto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public AcquisitionSearchResultsDTO searchBuyOrder(BuyOrderDTO example, int offset)
    {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<BuyOrderDTO> orders = dao.searchBuyOrder(example, offset, recordsPPage);
            for (BuyOrderDTO order : orders) {
                QuotationDTO quotation = quotationBo.getQuotation(order.getSerialQuotation());
                order.setQuotation(quotation);
                dto.al.add(order);
            }
            int total = dao.getSearchCount(example);
            int nroPages = total / recordsPPage;
            int mod = total % recordsPPage;
            dto.recordsPerPage = recordsPPage;
            dto.totalRecords = total;
            dto.totalPages = mod == 0 ? nroPages : nroPages + 1;
            dto.currentPage = (offset / recordsPPage) + 1;
            return dto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public BuyOrderDTO getBuyOrder(Integer id) {
        BuyOrderDTO example = new BuyOrderDTO();
        example.setSerial(id);
        ArrayList<BuyOrderDTO> orders = dao.searchBuyOrder(example, 0, 1);
        for (BuyOrderDTO order : orders) {
            QuotationDTO quotation = quotationBo.getQuotation(order.getSerialQuotation());
            order.setQuotation(quotation);
            return order;
        }
        return null;
    }

    
    public Boolean deleteBuyOrder(BuyOrderDTO dto) {
        return dao.deleteBuyOrder(dto);
    }

}
