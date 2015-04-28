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

package biblivre3.cataloging.authorities;

import biblivre3.enums.MaterialType;
import biblivre3.enums.RecordStatus;
import biblivre3.marcutils.MarcReader;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.ApplicationConstants;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mercury.ErrorDTO;
import mercury.IFJson;
import mercury.BaseHandler;
import mercury.DTOCollection;
import mercury.RootJsonHandler;
import mercury.SuccessDTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

public class JsonAuthoritiesHandler extends RootJsonHandler {

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("auto_complete")) {
            dto = auto_complete(request);
        } else if (submitButton.equals("open")) {
            dto = getAuthorityRecord(request);
        } else if (submitButton.equals("switch")) {
            dto = switchRecord(request);
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("delete")) {
            dto = delete(request);
        }
        return dto.toJSONObject(properties);
    }

    
    private IFJson getAuthorityRecord(HttpServletRequest request) {
        String recordId = request.getParameter("record_id");
        String type = request.getParameter("type");
        if (recordId == null || recordId.isEmpty()) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");  
        }
        if (type == null) {
            type = "record";
        }
        final AuthoritiesBO authBo = new AuthoritiesBO();
        final AuthorityRecordDTO dto = authBo.getById(recordId);

        if (dto == null) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }

        final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
        if (type.equals("form")) {
            dto.setJson(MarcUtils.recordToJson(record));
        } else if (type.equals("record")) {
            dto.setFields(MarcUtils.createFieldsListAuthority(record));
        } else if (type.equals("marc")) {
            dto.setMarc(MarcReader.iso2709ToMarc(dto.getIso2709()));
        }
        return dto;
    }

    private IFJson switchRecord(HttpServletRequest request) {
        String from = request.getParameter("from");
        String to   = request.getParameter("to");
        String data = request.getParameter("data");
        String serial = request.getParameter("serial");
        RecordStatus status =  RecordStatus.NEW;
        if (StringUtils.isNotBlank(serial) && !serial.equals("0")) {
            status = RecordStatus.CORRECTED;
        }

        AuthorityRecordDTO dto = new AuthorityRecordDTO();
        Record record = null;
        try {
            if (from.equals("marc")) {
                record = MarcReader.marcToRecord(data, MaterialType.AUTHORITIES, status);
            } else if (from.equals("form")) {
                record = MarcUtils.jsonToRecord(new JSONObject(data), MaterialType.AUTHORITIES, status);
            }

            if (to.equals("marc")) {
                String iso2709 = MarcUtils.recordToIso2709(record);
                dto.setMarc(MarcReader.iso2709ToMarc(iso2709));
            } else if (to.equals("form")) {
                dto.setJson(MarcUtils.recordToJson(record));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR_MARC_SWITCH", "warning");
        }

        return dto;
    }


    private IFJson search(final HttpServletRequest request) {
        String searchTerms = request.getParameter("SEARCH_TERM");
        boolean listAll = StringUtils.isBlank(searchTerms);
        int offset;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }
        AuthoritiesBO bo = new AuthoritiesBO();
        AuthoritySearchResultsDTO dto;
        if (listAll) {
            dto = bo.list(offset);
        } else {
            dto = bo.search(searchTerms, offset);
        }
        if (dto != null && dto.al != null) {
            return dto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");        
        }
    }

    private IFJson auto_complete(final HttpServletRequest request) {
        String query = request.getParameter("q");
        if (StringUtils.isBlank(query)) {
            return new SuccessDTO("");
        }

        AuthoritiesBO bo = new AuthoritiesBO();
        DTOCollection<AuthorityRecordDTO> dto = bo.autoComplete(query);

        if (dto != null) {
            return dto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson save(final HttpServletRequest request) {
        String type = request.getParameter("type");
        String data = request.getParameter("data");
        String id   = request.getParameter("record_id");
        boolean result = false;
        if (StringUtils.isBlank(type)) return new ErrorDTO("ERROR", "warning");
        RecordStatus status =  RecordStatus.NEW;
        if (StringUtils.isNotBlank(id) && !id.equals("0")) {
            status = RecordStatus.CORRECTED;
        }

        Record record = null;
        try {
            if (type.equals("form")) {
                record = MarcUtils.jsonToRecord(new JSONObject(data), MaterialType.AUTHORITIES, status);
            } else if (type.equals("marc")) {
                record = MarcReader.marcToRecord(data, MaterialType.AUTHORITIES, status);
            }
        } catch (Exception e) {
            return new ErrorDTO("MESSAGE_AUTHORITIES_SAVE_ERROR", "warning");
        }

        AuthoritiesBO bo = new AuthoritiesBO();
        if (status.equals(RecordStatus.NEW)) {
            result = bo.insert(record);
        } else {
            result = bo.update(record, Integer.valueOf(id));
        }
        
        if (result) {
            if (id == null || id.equals("0")) {
                return new SuccessDTO("SUCCESS_CREATE_RECORD");
            } else {
                return new SuccessDTO("SUCCESS_UPDATE_RECORD");
            }
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson delete(final HttpServletRequest request) {
        String serial = request.getParameter("serial");
        if (StringUtils.isBlank(serial)) return new ErrorDTO("ERROR", "warning");
        AuthoritiesBO bo = new AuthoritiesBO();
        boolean result = bo.delete(new String[]{serial});

        if (result) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }
}
