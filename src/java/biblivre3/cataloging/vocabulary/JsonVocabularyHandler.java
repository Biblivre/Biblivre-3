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

package biblivre3.cataloging.vocabulary;

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

public class JsonVocabularyHandler extends RootJsonHandler {

    private VocabularyBO bo;

    @Override
    public JSONObject process(HttpServletRequest request, HttpServletResponse response) {
        bo = new VocabularyBO();
        String submitButton = request.getParameter("submitButton");
        Properties properties = BaseHandler.getI18nProperties(request, "biblivre3");
        IFJson dto = null;
        if (submitButton == null || submitButton.isEmpty()) {
        } else if (submitButton.equals("search")) {
            dto = search(request);
        } else if (submitButton.equals("auto_complete")) {
            dto = auto_complete(request);
        } else if (submitButton.equals("open")) {
            dto = getRecord(request);
        } else if (submitButton.equals("switch")) {
            dto = switchRecord(request);
        } else if (submitButton.equals("save")) {
            dto = save(request);
        } else if (submitButton.equals("delete")) {
            dto = delete(request);
        } 
        return dto.toJSONObject(properties);
    }

    private IFJson getRecord(HttpServletRequest request) {
        String recordId = request.getParameter("record_id");
        if (recordId == null || recordId.isEmpty()) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }
        String type = request.getParameter("type");
        if (type == null) {
            type = "record";
        }

        final VocabularyDTO dto = bo.getById(Integer.valueOf(recordId));
        if (dto == null) {
            return new ErrorDTO("ERROR_RECORD_NOT_FOUND", "error");
        }
        final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());

        if (type.equals("record")) {
            dto.setFields(MarcUtils.createFieldsListVocabulary(record));
        } else if (type.equals("marc")) {
            dto.setMarc(MarcReader.iso2709ToMarc(dto.getIso2709()));
        } else if (type.equals("form")) {
            dto.setJson(MarcUtils.recordToJson(record));
        }
        
        return dto;
    }

    private IFJson search(final HttpServletRequest request) {
        String searchTerms = request.getParameter("search_term");
        String searchType = request.getParameter("search_type");
        boolean listAll = StringUtils.isBlank(searchTerms);

        int offset;

        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
            offset = 0;
        }

        VocabularySearchResultsDTO dto = null;

        if (listAll) {
            dto = bo.list(offset);
        } else {
            VocabularyDTO example = new VocabularyDTO();

            dto = bo.search(searchTerms, searchType, offset);
        }
        if (dto != null && dto.al != null && dto.al.size() > 0) {
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

        VocabularyBO bo = new VocabularyBO();
        DTOCollection<VocabularyDTO> dto = bo.autoComplete(query);

        if (dto != null) {
            return dto;
        } else {
            return new ErrorDTO("MESSAGE_FOUND_NONE", "warning");
        }
    }

    private IFJson switchRecord(HttpServletRequest request) {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String data = request.getParameter("data");
        VocabularyDTO dto = new VocabularyDTO();
        String serial = request.getParameter("serial");
        RecordStatus status =  RecordStatus.NEW;
        if (StringUtils.isNotBlank(serial) && !serial.equals("0")) {
            status = RecordStatus.CORRECTED;
        }
        Record record = null;
        try {
            if (from.equals("marc")) {
                record = MarcReader.marcToRecord(data, MaterialType.VOCABULARY, status);
            } else if (from.equals("form")) {
                record = MarcUtils.jsonToRecord(new JSONObject(data), MaterialType.VOCABULARY, status);
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

    private IFJson save(final HttpServletRequest request) {
        String type = request.getParameter("type");
        String data = request.getParameter("data");
        String id = request.getParameter("record_id");
        RecordStatus status = (id == null || id.equals("0")) ? RecordStatus.NEW : RecordStatus.CORRECTED;

        boolean result = false;
        if (StringUtils.isBlank(type)) return new ErrorDTO("ERROR", "warning");
        Record record = null;
        try {
            if (type.equals("form")) {
                record = MarcUtils.jsonToRecord(new JSONObject(data), MaterialType.VOCABULARY, status);
            } else if (type.equals("marc")) {
                record = MarcReader.marcToRecord(data, MaterialType.VOCABULARY, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorDTO("ERROR", "warning");
        }

        if (id == null || id.equals("0")) {
            result = bo.insert(record);
        } else {
            result = bo.update(record, id);
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
        String serial = request.getParameter("record_id");
        if (StringUtils.isBlank(serial)) return new ErrorDTO("ERROR", "warning");
        VocabularyDTO dto = new VocabularyDTO();
        dto.setSerial(Integer.valueOf(serial));
        boolean result = bo.delete(dto);
        if (result) {
            return new SuccessDTO("SUCCESS_REMOVE_RECORD");
        } else {
            return new ErrorDTO("ERROR_REMOVE_RECORD", "warning");
        }
    }

}
