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

import biblivre3.acquisition.AcquisitionSearchResultsDTO;
import biblivre3.acquisition.supplier.SupplierBO;
import biblivre3.acquisition.supplier.SupplierDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.util.ArrayList;
import java.util.List;
import mercury.BaseBO;
import mercury.DTO;

public class QuotationBO extends BaseBO {

    private int recordsPPage;
    private QuotationDAO dao;
    private ItemQuotationBO itemQuotationBO;
    private SupplierBO supplierBo;

    public QuotationBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new QuotationDAO();
            itemQuotationBO = new ItemQuotationBO();
            supplierBo = new SupplierBO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public boolean insert(QuotationDTO dto) {
        Integer serial = dao.getNextSerial("quotation_serial_quotation_seq");
        dto.setSerial(serial);
        if (dao.insertQuotation(dto)) {
            for (ItemQuotationDTO itemQuotation : dto.getItemQuotationList()) {
                itemQuotation.setSerialQuotation(serial);
                itemQuotationBO.insert(itemQuotation);
            }
        }
        return true;
    }

    public boolean update(QuotationDTO dto) {
        if (dao.updateQuotation(dto)) {
            itemQuotationBO.deleteAllByQuotationId(dto.getSerial());
            for (ItemQuotationDTO itemQuotation : dto.getItemQuotationList()) {
                itemQuotation.setSerialQuotation(dto.getSerial());
                itemQuotationBO.insert(itemQuotation);
            }
        }
        return true;
    }

    public AcquisitionSearchResultsDTO listQuotations(int offset) {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<QuotationDTO> requests = dao.listQuotations(offset, recordsPPage);
            ItemQuotationBO bo = new ItemQuotationBO();
            for (QuotationDTO quotation : requests) {
                try {
                    SupplierDTO supplierDTO = supplierBo.getSupplier(quotation.getSerialSupplier());
                    if (supplierDTO != null) {
                        quotation.setSupplierName(supplierDTO.getTrademarkName());
                    }
                    List<String> items = new ArrayList<String>();
                    ArrayList<ItemQuotationDTO> iqdtoList = bo.listItemQuotation(quotation.getSerial());
                    quotation.setItemQuotationList(iqdtoList);
                    for (ItemQuotationDTO iqdto : iqdtoList) {
                        items.add(iqdto.getQuotationQuantity() + "x " + iqdto.getAuthor() + " - " + iqdto.getTitle());
                    }
                    quotation.setItems(items);
                    dto.al.add(quotation);
                } catch (NullPointerException e) {
                }
            }
            int total = dao.getTotalNroRecords();
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

    public AcquisitionSearchResultsDTO searchQuotation(QuotationDTO example, int offset)
    {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<QuotationDTO> requests = dao.searchQuotation(example, offset, recordsPPage);
            ItemQuotationBO bo = new ItemQuotationBO();
            for (QuotationDTO quotation : requests) {
                quotation.setSupplierName(supplierBo.getSupplier(quotation.getSerialSupplier()).getTrademarkName());
                List<String> items = new ArrayList<String>();
                ArrayList<ItemQuotationDTO> iqdtoList = bo.listItemQuotation(quotation.getSerial());
                quotation.setItemQuotationList(iqdtoList);
                for (ItemQuotationDTO iqdto : iqdtoList) {
                    items.add(iqdto.getQuotationQuantity() + "x " + iqdto.getAuthor() + " - " + iqdto.getTitle());
                }
                quotation.setItems(items);
                dto.al.add(quotation);
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

    public QuotationDTO getQuotation(Integer id) {
        QuotationDTO example = new QuotationDTO();
        example.setSerial(id);
        ArrayList<QuotationDTO> quotations = dao.searchQuotation(example, 0, 1);
        for (QuotationDTO quotation : quotations) {
            quotation.setSupplierName(supplierBo.getSupplier(quotation.getSerialSupplier()).getTrademarkName());
            List<String> items = new ArrayList<String>();
            ItemQuotationBO bo = new ItemQuotationBO();
            ArrayList<ItemQuotationDTO> iqdtoList = bo.listItemQuotation(quotation.getSerial());
            quotation.setItemQuotationList(iqdtoList);
            for (ItemQuotationDTO iqdto : iqdtoList) {
                items.add(iqdto.getQuotationQuantity() + "x " + iqdto.getAuthor() + " - " + iqdto.getTitle());
            }
            quotation.setItems(items);
            return quotation;
        }
        return null;
    }

    
    public Boolean delete(QuotationDTO dto) {
        return dao.deleteQuotation(dto);
    }

    public List<QuotationDTO> listAllQuotations() {
        return dao.listQuotations(0, Integer.MAX_VALUE);
    }


}
