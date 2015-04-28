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

package biblivre3.acquisition.supplier;

import biblivre3.acquisition.AcquisitionSearchResultsDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.util.ArrayList;
import java.util.List;
import mercury.BaseBO;
import mercury.DTO;

public class SupplierBO extends BaseBO {

    private int recordsPPage;
    private SupplierDAO dao;

    public SupplierBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new SupplierDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public boolean insertSupplier(SupplierDTO dto) {
        return dao.insertSupplier(dto);
    }

    public boolean updateSupplier(SupplierDTO dto) {
        return dao.updateSupplier(dto);
    }

    public AcquisitionSearchResultsDTO listSuppliers(int offset) {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<SupplierDTO> suppliers = dao.listSuppliers(offset, recordsPPage);
            for (SupplierDTO supplier : suppliers) {
                SupplierResultRow row = new SupplierResultRow();
                row.setRecordSerial(String.valueOf(supplier.getSerial()));
                row.setName(supplier.getTrademarkName());
                row.setCreated(supplier.getCreated());
                row.setModified(supplier.getModified());
                dto.al.add(row);
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

    public AcquisitionSearchResultsDTO searchSupplier(
            SupplierDTO example,
            int offset)
    {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<SupplierDTO> suppliers = dao.searchSupplier(example, offset, recordsPPage);
            for (SupplierDTO supplier : suppliers) {
                SupplierResultRow row = new SupplierResultRow();
                row.setRecordSerial(String.valueOf(supplier.getSerial()));
                row.setName(supplier.getTrademarkName());
                row.setCreated(supplier.getCreated());
                row.setModified(supplier.getModified());
                dto.al.add(row);
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

    public SupplierDTO getSupplier(Integer id) {
        SupplierDTO example = new SupplierDTO();
        example.setSerial(id);
        ArrayList<SupplierDTO> suppliers = dao.searchSupplier(example, 0, 1);
        for (SupplierDTO supplier : suppliers) {
            return supplier;
        }
        return null;
    }

    
    public Boolean deleteSupplier(SupplierDTO dto) {
        return dao.deleteSupplier(dto);
    }

    public List<SupplierDTO> listAllSuppliers() {
        return dao.listSuppliers(0, Integer.MAX_VALUE);
    }

}
