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

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.TextUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mercury.BaseBO;
import mercury.DTO;
import mercury.DTOCollection;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.marc.Record;
import org.marc4j_2_3_1.marc.Subfield;

public class VocabularyBO extends BaseBO {

    private int recordsPPage;
    private VocabularyDAO dao;

    public VocabularyBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new VocabularyDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public VocabularySearchResultsDTO list(int offset) {
        VocabularySearchResultsDTO dto = new VocabularySearchResultsDTO();

        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<VocabularyDTO> terms = dao.list(offset, recordsPPage);

            for (VocabularyDTO term : terms) {
                Record record = MarcUtils.iso2709ToRecord(term.getIso2709());
                Subfield subfield = MarcUtils.getSubfield(record, "150", 'a');
                if (subfield != null) {
                    term.setTerm(subfield.getData());
                }
                dto.al.add(term);
            }

            int total = dao.countAll();
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

    public VocabularySearchResultsDTO search(String searchTerms, String controlfield, int offset) {
        VocabularySearchResultsDTO dto = new VocabularySearchResultsDTO();

        try {
            dto.al = new ArrayList<DTO>();
            ArrayList<VocabularyDTO> terms;

            String[] searchArray = null;

            if (searchTerms != null) {
                searchArray = TextUtils.removeDiacriticals(searchTerms).toLowerCase().split("\\s+");
            }

            if (searchArray == null) {
                terms = dao.list(offset, recordsPPage);
            } else {
                terms = dao.search(searchArray, controlfield, offset, recordsPPage);
            }

            for (VocabularyDTO term : terms) {
                Record record = MarcUtils.iso2709ToRecord(term.getIso2709());
                Subfield subfield = MarcUtils.getSubfield(record, "150", 'a');
                if (subfield != null) {
                    term.setTerm(subfield.getData());
                }
                dto.al.add(term);
            }

            int total;
            if (searchArray == null) {
                total = dao.countAll();
            } else {
                total = dao.count(searchArray, controlfield);
            }

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

    public DTOCollection<VocabularyDTO> autoComplete(String query) {
        DTOCollection<VocabularyDTO> dto = new DTOCollection<VocabularyDTO>();

        try {
            String[] searchArray = null;

            if (query != null) {
                searchArray = TextUtils.removeDiacriticals(query).toLowerCase().split("\\s+");
            }

            if (searchArray == null) {
                return dto;
            }

            ArrayList<VocabularyDTO> terms = dao.search(searchArray, "150", 0, 1000);

            if (!terms.isEmpty()) {
                for (VocabularyDTO term : terms) {
                    Record record = MarcUtils.iso2709ToRecord(term.getIso2709());
                    Subfield subfield = MarcUtils.getSubfield(record, "150", 'a');
                    if (subfield != null) {
                        term.setTerm(subfield.getData());
                        term.setIso2709(null);
                        dto.add(term);
                    }
                }

                return dto;
            }
        } catch (Exception e) {
            System.out.println("[VOCABULARYBO.autoComplete(..)] Exception: " + e);
        }

        return dto;
    }

    public final boolean insert(final Record record) {
        final Integer serial = dao.getNextSerial("vocabulary_vocabulary_serial_seq");
        MarcUtils.setCF001(record, serial);


        final Date now = new Date();
        MarcUtils.setCF005(record, now);
        MarcUtils.setCF008(record);
        final String iso2709 = MarcUtils.recordToIso2709(record);

        if (StringUtils.isEmpty(iso2709)) {
            throw new IllegalArgumentException("Record is empty");
        }

        final VocabularyDTO dto = new VocabularyDTO();
        dto.setSerial(serial);
        dto.setCreated(now);
        dto.setModified(now);
        dto.setIso2709(iso2709);

        if (dao.insert(dto)) {
            return this.insertIndex(iso2709, serial);
        }

        return false;
    }

    public final boolean update(final Record record, final String idRecord) {
        final Date now = new Date();
        MarcUtils.setCF005(record, now);
        final String iso2709 = MarcUtils.recordToIso2709(record);

        if (StringUtils.isEmpty(iso2709)) {
            throw new IllegalArgumentException("Record is empty");
        }

        final VocabularyDTO dto = this.getById(Integer.valueOf(idRecord));
        dto.setModified(now);
        dto.setIso2709(iso2709);

        if (dao.update(dto)) {
            this.deleteIndex(dto.getSerial());
            return this.insertIndex(record, dto.getSerial());
        }
        
        return false;
    }


    public VocabularyDTO getById(Integer id) {
        return dao.getById(id);
    }


    public Boolean delete(VocabularyDTO dto) {
        return dao.delete(dto);
    }

    private boolean insertIndex(String iso2709, Integer serial) {
        Record record = MarcUtils.iso2709ToRecord(iso2709);
        return this.insertIndex(record, serial);
    }

    private boolean insertIndex(Record record, Integer serial) {
        List<String> teList = Indexer.listTagValues(record, "150", new char[] {'a', 'x', 'y', 'z'});
        List<String> upList = Indexer.listTagValues(record, "450", new char[] {'a'});
        List<String> tgList = Indexer.listTagValues(record, "550", new char[] {'a', 'x', 'y', 'z'});
        List<String> vtTeTrList = Indexer.listTagValues(record, "360", new char[] {'a', 'x', 'y', 'z'});
        
        for (String word : teList) {
            if (StringUtils.isNotBlank(word)) {
                this.saveIndex(serial, word, "150");
            }
        }

        for (String word : upList) {
            if (StringUtils.isNotBlank(word)) {
                this.saveIndex(serial, word, "450");
            }
        }

        for (String word : tgList) {
            if (StringUtils.isNotBlank(word)) {
                this.saveIndex(serial, word, "550");
            }
        }

        for (String word : vtTeTrList) {
            if (StringUtils.isNotBlank(word)) {
                this.saveIndex(serial, word, "360");
            }
        }

        StringBuilder sort = new StringBuilder();
        for (String word : teList) {
            sort.append(word).append(" ");
        }
        
        dao.insertSortIdx(serial, TextUtils.removeDiacriticals(sort.toString()).toLowerCase());

        return true;
    }

    private boolean saveIndex(Integer serial, String word, String cf) {
        return dao.insertIdx(serial, TextUtils.removeDiacriticals(word).toLowerCase(), cf);
    }

    private boolean deleteIndex(Integer recordId) {
        dao.deleteIdx(recordId);
        dao.deleteSortIdx(recordId);

        return true;
    }

    public void clearAllIndexes() {
        dao.deleteAllIdx();
        dao.deleteAllSortIdx();
    }

    public final boolean reindexBase() {
        this.clearAllIndexes();
        final int limit = 100;
        final int recordCount = dao.countAll();
        for (int offset = 0; offset < recordCount; offset += limit) {
            ArrayList<VocabularyDTO> records = dao.list(offset, limit);

            for (VocabularyDTO dto : records) {
                final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
                this.insertIndex(record, dto.getSerial());
            }
        }
        return true;
    }

}
