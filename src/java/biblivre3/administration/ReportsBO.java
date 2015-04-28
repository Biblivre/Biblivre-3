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

package biblivre3.administration;

import biblivre3.administration.reports.BiblivreReportFactory;
import biblivre3.administration.reports.IBiblivreReport;
import biblivre3.administration.reports.dto.AuthorsSearchResultsDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.ReportType;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import mercury.BaseBO;
import mercury.MemoryFileDTO;

public class ReportsBO extends BaseBO {

    private int recordsPPage;
    private ReportsDAO dao;

    public ReportsBO() {
        try {
            this.recordsPPage = Integer.valueOf(Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE));
            this.dao = new ReportsDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public MemoryFileDTO generateReport(ReportsDTO dto, Properties i18n) {
        ReportType type = dto.getType();
        IBiblivreReport report = BiblivreReportFactory.getBiblivreReport(type);
        report.setI18n(i18n);
        return report.generateReport(dto);
    }

    public AuthorsSearchResultsDTO searchAuthors(String authorName, Integer offset) {
        TreeMap<String, Set<Integer>> searchResults = dao.searchAuthors(authorName);

        if (offset >= searchResults.size()) {
            return null;
        }

        TreeMap<String, Set<Integer>> data = new TreeMap<String, Set<Integer>>();

        int loopIndex = 0;
        for (String key : searchResults.keySet()) {
            if (loopIndex >= offset && loopIndex < searchResults.size() && data.size() < recordsPPage) {
                data.put(key, searchResults.get(key));
            } else if (data.size() >= recordsPPage) {
                break;
            }
            ++loopIndex;
        }
        AuthorsSearchResultsDTO adto = new AuthorsSearchResultsDTO();
        adto.nameIdsPairs = data;
        int total = searchResults.size();
        int nroPages = total / recordsPPage;
        int mod = total % recordsPPage;
        adto.recordsPerPage = recordsPPage;
        adto.totalRecords = total;
        adto.totalPages = mod == 0 ? nroPages : nroPages + 1;
        adto.currentPage = (offset / recordsPPage) + 1;
        return adto;
    }

}
