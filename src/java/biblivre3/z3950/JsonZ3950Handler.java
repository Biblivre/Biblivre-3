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

package biblivre3.z3950;

import biblivre3.cataloging.bibliographic.FreeMarcBO;
import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.Database;
import biblivre3.enums.MaterialType;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcReader;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.ApplicationConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import mercury.BaseHandler;
import mercury.DTOCollection;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

public class JsonZ3950Handler extends RootJsonHandler {

    private Z3950BO bo;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        bo = new Z3950BO();
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("paginate")) {
            dto = paginate(request);
        } else if (submitButton.equals("open")) {
            dto = open(request);
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("change_server_status")) {
            dto = changeServerStatus(request);
        } else if (submitButton.equals("list_servers")) {
            dto = listServers();
        } else if (submitButton.equals("save_server")) {
            dto = saveServer(request);
        } else if (submitButton.equals("delete_server")) {
            dto = deleteServer(request);
        }
        return dto.toJSONObject(properties);
    }

    private IFJson deleteServer(HttpServletRequest request) {
        String id = request.getParameter("serial");
        Z3950ServerDTO dto = new Z3950ServerDTO();
        try {
            dto.setServerId(Integer.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
        if (bo.delete(dto)) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
    }

    private IFJson listServers() {
        return new DTOCollection().addAll(bo.listServers());
    }

    private IFJson saveServer(HttpServletRequest request) {
        String data = request.getParameter("data");
        String id = request.getParameter("serial");
        boolean isNew = StringUtils.isBlank(id) || id.equals("0");
        boolean result = false;
        Z3950ServerDTO dto = new Z3950ServerDTO();
        try {
            dto = populateDtoFromJson(data, dto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
        }

        if (dto.getPort() == null || dto.getPort() <= 0) {
            return new ErrorDTO("MESSAGE_Z3950_SERVER_INVALID_PORT", "warning");
        }

        if (!validateDto(dto)) {
            return new ErrorDTO("ERROR_FIELDS_NOT_FILLED", "warning");
        }

        if (isNew) {
            result = bo.insert(dto);
        } else {
            dto.setServerId(Integer.valueOf(id));
            result = bo.update(dto);
        }
        if (result) {
            if (isNew) {
                return new SuccessDTO("SUCCESS_CREATE_RECORD");
            } else {
                return new SuccessDTO("SUCCESS_UPDATE_RECORD");
            }
        } else {
            if (isNew) {
                return new ErrorDTO("ERROR_CREATE_RECORD", "warning");
            } else {
                return new ErrorDTO("ERROR_UPDATE_RECORD", "warning");
            }

        }
    }

    private IFJson search(final HttpServletRequest request) {
        final List<Z3950ServerDTO> servers = this.readServers(request);
        if (servers == null || servers.isEmpty()) {
            return new ErrorDTO("Z3950_NO_SERVER_SELECTED", "warning");
        }

        final Z3950SearchDTO dto = this.readSearch(request);
        if (dto.getValue().isEmpty()) {
            return new ErrorDTO("Z3950_NO_SEARCH_VALUE", "warning");
        }

        Map<Z3950ServerDTO, List<Record>> recordMap = bo.doSearch(servers, dto);

        if (this.isSearchEmpty(recordMap)) {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }

        List<Z3950ResultRow> result = this.populateResult(recordMap);
        request.getSession().setAttribute("DISTRIBUTED_SEARCH_RESULTS", result);

        return this.paginate(request);
    }

    private IFJson paginate(final HttpServletRequest request) {
        List<Z3950ResultRow> cachedResults = (ArrayList<Z3950ResultRow>) request.getSession().getAttribute("DISTRIBUTED_SEARCH_RESULTS");

        String strOffset = request.getParameter("offset");
        String recordsPerPage = Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE);
        int offset = 0;
        try {
            offset = Integer.valueOf(strOffset);
        } catch (Exception e) {}

        if (offset > cachedResults.size()) {
            offset = 0;
        }

        int recordsPPage = 10;
        try {
            recordsPPage = Integer.valueOf(recordsPerPage);
        } catch (Exception e) {}

        Z3950SearchResultDTO resultDTO = new Z3950SearchResultDTO();

        int total = cachedResults.size();
        int pageCount = total / recordsPPage;
        resultDTO.totalPages = (total % recordsPPage == 0) ? pageCount : pageCount + 1;
        resultDTO.currentPage = (offset / recordsPPage) + 1;
        resultDTO.recordsPerPage = recordsPPage;
        resultDTO.totalRecords = total;

        resultDTO.al = new ArrayList<Z3950ResultRow>();
        for (int i = offset; i < cachedResults.size() && i < offset + recordsPPage; i++) {
            resultDTO.al.add(cachedResults.get(i));
        }

        return resultDTO;
    }

    private IFJson open(HttpServletRequest request) {
        String strIndex = request.getParameter("index");
        int index = 0;
        try {
            index = Integer.valueOf(strIndex);
        } catch (Exception e) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }

        String type = request.getParameter("type");
        if (type == null) {
            type = "record";
        }

        Z3950ResultRow rr = this.getRecordFromIndex(request, index);
        if (rr == null) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }

        final RecordDTO dto = new RecordDTO();
        final Record record = rr.getRecord();

        dto.setTitle(Indexer.listOneTitle(record));

        if (type.equals("record")) {
            dto.setFields(MarcUtils.createFieldsList(record));
        } else if (type.equals("marc")) {
            String iso2709 = MarcUtils.recordToIso2709(record);
            String freeMarc = MarcReader.iso2709ToMarc(iso2709);
            dto.setMarc(freeMarc);
        }

        return dto;
    }

    private IFJson save(final HttpServletRequest request) {
        String freemarc = request.getParameter("freemarc");
        if (freemarc == null || freemarc.isEmpty()) {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "error");
        }

        final String materialType = MaterialType.BOOK.getCode();

        final FreeMarcBO freeMarcBo = new FreeMarcBO();
        RecordDTO dto = null;
        try {
            dto = freeMarcBo.insert(freemarc, Database.WORK, materialType);
        } catch (RuntimeException re) {}

        if (dto == null) {
            return new ErrorDTO("ERROR_CREATE_RECORD", "error");
        }

        return new SuccessDTO("SUCCESS_CREATE_RECORD_WORK_DATABASE");
    }

    private IFJson changeServerStatus(final HttpServletRequest request) {
        String action = request.getParameter("status");
        HttpSession session = request.getSession();

        if (action.equals("activate")) {
            final Boolean status  = bo.getServerStatus();
            if (status == Boolean.TRUE) {
                session.setAttribute("serverStatus", status);
                return new ErrorDTO("Z3950_SERVER_ALREADY_ACTIVATED", "warning");
            }
            final Boolean isActive = bo.startServer();
            String message = isActive ? "Z3950_SERVER_ACTIVATION_SUCCESS" : "Z3950_SERVER_ACTIVATION_FAILURE";
            session.setAttribute("serverStatus", isActive);
            return new SuccessDTO(message);
        } else {
            final Boolean status  = bo.getServerStatus();
            if (status == Boolean.FALSE) {
                session.setAttribute("serverStatus", status);
                return new ErrorDTO("Z3950_SERVER_ALREADY_DEACTIVATED", "warning");
            }
            final Boolean isActive = bo.stopServer();
            String message = !isActive ? "Z3950_SERVER_DEACTIVATION_SUCCESS" : "Z3950_SERVER_DEACTIVATION_FAILURE";
            session.setAttribute("serverStatus", isActive);
            return new SuccessDTO(message);
        }
    }

    private List<Z3950ServerDTO> readServers(final HttpServletRequest request) {
        final List<Z3950ServerDTO> serverList = new ArrayList<Z3950ServerDTO>();

        String[] serverIds = request.getParameterValues("serverIds");
        if (serverIds == null || serverIds.length == 0) {
            return serverList;
        }

        Z3950BO serverBo = new Z3950BO();
        for (String id : serverIds) {
            serverList.add(serverBo.findById(id));
        }

        return serverList;
    }

    private Z3950SearchDTO readSearch(final HttpServletRequest request) {
        final Z3950SearchDTO dto = new Z3950SearchDTO();

        dto.setType(request.getParameter("SEARCH_ATTR"));
        dto.setValue(request.getParameter("SEARCH_TERM"));

        return dto;
    }

    private boolean isSearchEmpty(Map<Z3950ServerDTO, List<Record>> recordMap) {
        boolean hasRecords = false;
        try {
            if (recordMap == null) return true;
            for (List<Record> recordList : recordMap.values()) {
                 if (recordList != null || !recordList.isEmpty()) {
                     hasRecords = true;
                 }
            }
        } catch (Exception e) {
            return true;
        }
        return !hasRecords;
    }

    private List<Z3950ResultRow> populateResult(Map<Z3950ServerDTO, List<Record>> recordMap) {
        Map<Z3950ServerDTO, Z3950SearchResultDTO> result = new HashMap<Z3950ServerDTO, Z3950SearchResultDTO>();
        List<Z3950ResultRow> al = new ArrayList<Z3950ResultRow>();

        int index = 0;
        for (Z3950ServerDTO server : recordMap.keySet()) {
            List<Record> recordList = recordMap.get(server);

            for (Record record : recordList) {
                Z3950ResultRow resultRow = new Z3950ResultRow();

                resultRow.setRecord(record);
                resultRow.setServerName(server.getName());
                resultRow.setAuthor(Indexer.listPrimaryAuthor(record));
                resultRow.setTitle(Indexer.listOneTitle(record));
                resultRow.setPublication(Indexer.listYearOfPublication(record));
                resultRow.setIndex(index++);

                al.add(resultRow);
            }
        }

        return al;
    }

    private Z3950ResultRow getRecordFromIndex(HttpServletRequest request, int index) {
        List<Z3950ResultRow> cachedResults = (ArrayList<Z3950ResultRow>) request.getSession().getAttribute("DISTRIBUTED_SEARCH_RESULTS");
        Z3950ResultRow resultRow = null;

        try {
            resultRow = cachedResults.get(index);
        } catch (Exception e) {}

        return resultRow;
    }

    private boolean validateDto(Z3950ServerDTO dto) {
        return StringUtils.isNotBlank(dto.getName()) &&
                StringUtils.isNotBlank(dto.getUrl()) &&
                StringUtils.isNotBlank(dto.getCollection()) &&
                (dto.getPort() != null && dto.getPort() > 0);
    }

}