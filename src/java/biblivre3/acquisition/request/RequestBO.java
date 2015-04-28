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

package biblivre3.acquisition.request;

import biblivre3.acquisition.AcquisitionSearchResultsDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.util.ArrayList;
import java.util.List;
import mercury.BaseBO;
import mercury.DTO;

public class RequestBO extends BaseBO {

    private int recordsPPage;
    private RequestDAO dao;

    public RequestBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new RequestDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public boolean insertRequest(RequestDTO dto) {
        return dao.insertRequest(dto);
    }

    public boolean updateRequest(RequestDTO dto) {
        return dao.updateRequest(dto);
    }

    public boolean updateRequestStatus(Integer buyOrderSerial, String status) {
        return dao.updateRequestStatus(buyOrderSerial, status);
    }

    public AcquisitionSearchResultsDTO listRequests(String status, int offset) {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<RequestDTO> requests = dao.listRequests(status, offset, recordsPPage);
            for (RequestDTO request : requests) {
                RequestResultRow row = new RequestResultRow();
                row.setRecordSerial(String.valueOf(request.getSerial()));
                row.setAuthor(request.getAuthor());
                row.setTitle(request.getTitle());
                row.setCreated(request.getRequestDate());
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

    public AcquisitionSearchResultsDTO searchRequest(RequestDTO example, int offset)
    {
        AcquisitionSearchResultsDTO dto = new AcquisitionSearchResultsDTO();
        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<RequestDTO> requests = dao.searchRequest(example, offset, recordsPPage);
            for (RequestDTO request : requests) {
                RequestResultRow row = new RequestResultRow();
                row.setRecordSerial(String.valueOf(request.getSerial()));
                row.setAuthor(request.getAuthor());
                row.setTitle(request.getTitle());
                row.setCreated(request.getRequestDate());
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

    public RequestDTO getRequest(Integer id) {
        RequestDTO example = new RequestDTO();
        example.setSerial(id);
        ArrayList<RequestDTO> requests = dao.searchRequest(example, 0, 1);
        for (RequestDTO request : requests) {
            return request;
        }
        return null;
    }

    
    public Boolean deleteRequest(RequestDTO dto) {
        return dao.deleteRequest(dto);
    }

    public List<RequestDTO> listAllRequests() {
        return dao.listRequests(null, 0, Integer.MAX_VALUE);
    }

    public List<RequestDTO> listAllPendingRequests() {
        return dao.listRequests("0", 0, Integer.MAX_VALUE);
    }


}
