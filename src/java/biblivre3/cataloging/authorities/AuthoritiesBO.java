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

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.TextUtils;
import java.util.ArrayList;
import mercury.BaseBO;
import java.util.Collection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import mercury.DTOCollection;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.marc.Record;

public class AuthoritiesBO extends BaseBO {

    private int recordsPPage;
    private AuthoritiesDAO dao;

    public AuthoritiesBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            dao = new AuthoritiesDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public AuthoritySearchResultsDTO list(int offset) {
        return this.search(null, offset);
    }

    public AuthoritySearchResultsDTO search(String searchTerms, int offset) {
        AuthoritySearchResultsDTO dto = new AuthoritySearchResultsDTO();
        
        try {
            dto.al = new ArrayList<AuthoritiesResultRow>();
            ArrayList<AuthorityRecordDTO> authorities;

            String[] searchArray = null;

            if (searchTerms != null) {
                searchArray = TextUtils.removeDiacriticals(searchTerms).toLowerCase().split("\\s+");
            }

            if (searchArray == null) {
                authorities = dao.list(offset, recordsPPage);
            } else {
                authorities = dao.search(searchArray, offset, recordsPPage);
            }

            if (!authorities.isEmpty()) {
                for (AuthorityRecordDTO authRecord : authorities) {
                    Record fmRecord = MarcUtils.iso2709ToRecord(authRecord.getIso2709());
                    AuthoritiesResultRow rr = new AuthoritiesResultRow();
                    rr.setRecordSerial(String.valueOf(authRecord.getRecordId()));
                    rr.setName(Indexer.listPrimaryAuthor(fmRecord));
                    rr.setCreated(authRecord.getCreated());
                    rr.setModified(authRecord.getModified());
                    dto.al.add(rr);
                }

                int total;
                if (searchTerms == null) {
                    total = dao.countAll();
                } else {
                    total = dao.count(searchArray);
                }

                int nroPages = total / recordsPPage;
                int mod = total % recordsPPage;
                dto.recordsPerPage = recordsPPage;
                dto.totalRecords = total;
                dto.totalPages = mod == 0 ? nroPages : nroPages + 1;
                dto.currentPage = (offset / recordsPPage) + 1;
                
                return dto;
            }
        } catch (Exception e) {
            System.out.println("[AUTHBO.searchAuthorities(..)] Exception: " + e);
        }
        return null;
    }

    public DTOCollection<AuthorityRecordDTO> autoComplete(String query) {
        DTOCollection<AuthorityRecordDTO> dto = new DTOCollection<AuthorityRecordDTO>();

        try {
            String[] searchArray = null;

            if (query != null) {
                searchArray = TextUtils.removeDiacriticals(query).toLowerCase().split("\\s+");
            }

            if (searchArray == null) {
                return dto;
            }

            ArrayList<AuthorityRecordDTO> authorities = dao.search(searchArray, 0, 1000);

            if (!authorities.isEmpty()) {
                for (AuthorityRecordDTO adto : authorities) {
                    Record record = MarcUtils.iso2709ToRecord(adto.getIso2709());
                    adto.setName(Indexer.listPrimaryAuthor(record));
                    adto.setJson(MarcUtils.recordToJson(record));
                    dto.add(adto);
                }

                return dto;
            }
        } catch (Exception e) {
            System.out.println("[AUTHBO.autoComplete(..)] Exception: " + e);
        }
        
        return dto;
    }

    public AuthorityRecordDTO getById(final String serial) {
        return dao.getById(Integer.valueOf(serial));
    }

    public boolean delete(final String[] recordIds) {
        final Collection<AuthorityRecordDTO> records = new ArrayList<AuthorityRecordDTO>();
        for (String id : recordIds) {
            final AuthorityRecordDTO dto = new AuthorityRecordDTO();
            dto.setRecordId(Integer.valueOf(id));
            records.add(dto);
        }
        if (dao.delete(records)) {
            for (AuthorityRecordDTO dto : records) {
                this.deleteIdx(dto.getRecordId());
            }
        }
        return true;
    }

    public boolean deleteIdx(int recordId) {
        dao.deleteIdx(recordId);
        dao.deleteSortIdx(recordId);

        return true;
    }

    public boolean insert(final Record authRecord) {
        AuthorityRecordDTO dto = new AuthorityRecordDTO();
        dto.setIso2709(MarcUtils.recordToIso2709(authRecord));

        Date now = new Date();
        dto.setCreated(now);
        dto.setModified(now);
        
        return dao.insert(dto) && this.insertIndex(dto, authRecord);
    }

    public  boolean update(final Record authRecord, int id) {
        AuthorityRecordDTO dto = new AuthorityRecordDTO();
        dto.setRecordId(id);

        dto.setModified(new Date());
        dto.setIso2709(MarcUtils.recordToIso2709(authRecord));

        return dao.update(dto) && this.updateIdx(dto, authRecord);
    }

    public boolean insertIndex(AuthorityRecordDTO dto, Record record) {
        try {
            String primary = Indexer.listPrimaryAuthor(record);
            String secondary = Indexer.listSecondaryAuthor(record);
            String otherNames = Indexer.listAuthorOtherNames(record);
            String[] words = TextUtils.removeDiacriticals(primary + " " + secondary + " " + otherNames).toLowerCase().split("\\s+");

            Set<String> wordsSet = new HashSet(Arrays.asList(words));
            for (String word : wordsSet) {
                if (StringUtils.isNotBlank(word) && word.length() >= 2) {
                    dao.insertIdx(dto.getRecordId(), word);
                }
            }

            dao.insertSortIdx(dto.getRecordId(), TextUtils.removeDiacriticals(primary).toLowerCase());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public boolean updateIdx(final AuthorityRecordDTO dto, Record authRecord) {
        return this.deleteIdx(dto.getRecordId()) && this.insertIndex(dto, authRecord);
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
            ArrayList<AuthorityRecordDTO> records = dao.list(offset, limit);

            for (AuthorityRecordDTO dto : records) {
                final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
                this.insertIndex(dto, record);
            }
        }
        return true;
    }

}
