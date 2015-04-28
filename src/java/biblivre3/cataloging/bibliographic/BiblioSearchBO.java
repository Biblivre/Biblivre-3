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

package biblivre3.cataloging.bibliographic;

import biblivre3.circulation.lending.LendingBO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.AttributeType;
import java.util.ArrayList;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import org.marc4j_2_3_1.marc.Record;
import java.util.Collection;
import mercury.BaseBO;
import biblivre3.enums.Database;
import biblivre3.enums.MaterialType;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.holding.HoldingDAO;
import biblivre3.circulation.reservation.ReservationBO;
import biblivre3.utils.TextUtils;
import java.util.List;

public class BiblioSearchBO extends BaseBO {

    private int recordsPPage;

    public BiblioSearchBO() {
        try {
            final String rpp = Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE);
            this.recordsPPage = Integer.valueOf(rpp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.recordsPPage = 25;
        }
    }

    public BiblioSearchResultsDTO search(String base, String itemType, String[] searchTerms, String[] searchAttr, String[] boolOp, final int offset) {
        BiblioSearchResultsDTO bdto = null;
        final BiblioDAO dao = new BiblioDAO();

        boolean searchingAssetHolding = false;
        boolean searchingHoldingSerial = false;
        String assetHolding = null;
        String holdingSerial = null;

        for (int k = 0; k < searchAttr.length; k++) {

            if (searchAttr[k].equals("HOLDING")) {
                searchingAssetHolding = true;
                assetHolding = searchTerms[k];

            } else if (searchAttr[k].equals("BAR_CODE")) {
                searchingHoldingSerial = true;
                holdingSerial = searchTerms[k];
            }
        }

        final Database database = Database.valueOf(base);
        Collection<RecordDTO> records;
        int total;

        if (searchingAssetHolding) {
            records = new ArrayList<RecordDTO>();
            RecordDTO record = dao.searchByAssetHolding(assetHolding, database);

            if (record != null) {
                records.add(record);
            }
            
            total = records.size();

        } else if (searchingHoldingSerial) {
            records = new ArrayList<RecordDTO>();

            try {
                RecordDTO record = dao.searchByHoldingSerial(Integer.parseInt(holdingSerial), database);

                if (record != null) {
                    records.add(record);
                }

            } catch (NumberFormatException e) {
                // dont find anything
            }

            total = records.size();

        } else {
            searchTerms[0] = searchTerms[0].trim();
            Object[] tuple = this.createWhereClause(searchTerms, searchAttr, boolOp);

            if (tuple == null) {
                return null;
            }

            final String clause = (String) tuple[0];
            final List<String> values = (ArrayList<String>) tuple[1];

            if (clause.isEmpty()) {
                return null;
            }


            final MaterialType materialType = MaterialType.getByCode(itemType);

            records = dao.search(database, materialType, clause, values, offset, recordsPPage, false);
            total = dao.count(database, materialType, clause, values);
        }

        return this.populateBiblioDTO(records, total, offset);
    }


    public BiblioSearchResultsDTO list(String base, String itemType, int offset) {
        final BiblioDAO dao = new BiblioDAO();

        final Database database = Database.valueOf(base);
        final MaterialType materialType = MaterialType.getByCode(itemType);

        final Collection<RecordDTO> records = dao.list(database, materialType, offset, recordsPPage, false);
        int total = dao.countAll(database, materialType);

        return this.populateBiblioDTO(records, total, offset);
    }

    private BiblioSearchResultsDTO populateBiblioDTO(Collection<RecordDTO> records, int total, int offset) {
        if (records != null && !records.isEmpty()) {
            BiblioSearchResultsDTO bdto = new BiblioSearchResultsDTO();
            HoldingBO hbo = new HoldingBO();
            LendingBO lbo = new LendingBO();
            ReservationBO rbo = new ReservationBO();

            final ArrayList<ResultRow> al = new ArrayList<ResultRow>();

            for (RecordDTO dto : records) {
                Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
                ResultRow rr = new ResultRow();

                final int recordSerial = dto.getRecordSerial();

                rr.setRecordSerial(recordSerial);
                rr.setTitle(Indexer.listOneTitle(record));
                rr.setAuthor(Indexer.listAuthors(record));
                rr.setDate(Indexer.listYearOfPublication(record));
                rr.setIsbn(Indexer.listIsbn(record));
                rr.setLocation(Indexer.listLocation(record));

                rr.setCreated(dto.getCreated());
                rr.setModified(dto.getModified());

                //quant - Recebe a quantidade total de exemplares de uma obra(disponíveis + indisponíveis)
                int quant = new HoldingDAO().countHoldings(rr.getRecordSerial());
                rr.setNrholdings(quant);

                int totalHoldings = hbo.countHoldings(recordSerial);
                int availableHoldings = hbo.countAvailableHoldings(recordSerial);
                int lentCount = 0;
                int reservedCount = 0;

                if (availableHoldings > 0) {
                    lentCount = lbo.countLentHoldings(recordSerial);
                    reservedCount = rbo.countReservedHoldings(recordSerial);
                }

                rr.setHoldingsCount(totalHoldings);
                rr.setHoldingsAvailable(availableHoldings - lentCount);
                rr.setHoldingsLent(lentCount);
                rr.setHoldingsReserved(reservedCount);

                al.add(rr);
            }

            bdto.al = al;

            int pageCount = total / recordsPPage;
            bdto.totalPages = (total % recordsPPage == 0) ? pageCount : pageCount + 1;
            bdto.currentPage = (offset / recordsPPage) + 1;
            bdto.recordsPerPage = recordsPPage;
            bdto.totalRecords = total;

            return bdto;
        } else {
            return null;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Creates a where clause for a prepared statement of biblio search and the corresponding values.
     * @param searchTerms Array of Strings with the search terms.
     * @param searchAttr Array of Strings with the corresponding search attributes.
     * @param boolOp Array of boolean operators corresponding to the search terms and attributes.
     * @return An array of two objects: a string with concatenated clauses and boolean operators
     * and an ArrayList of Strings with the values.
     */
    public Object[] createWhereClause(final String[] searchTerms, final String[] searchAttr, final String[] boolOp) {
        if (searchAttr == null || searchTerms == null) {
            return null;
        }

        final BiblioDAO dao = new BiblioDAO();
        ArrayList<String> clauses = new ArrayList<String>();

        final Object[] result = new Object[2];
        ArrayList<String> values = new ArrayList<String>();

        ArrayList<String> newBoolOp = new ArrayList<String>();

        for (int i = 0; i < searchAttr.length; i++) {
            final AttributeType type = AttributeType.getAttributeTypeByCode(searchAttr[i]);
            final String terms = TextUtils.removeDiacriticals(searchTerms[i]).toLowerCase();

            if (terms == null || terms.isEmpty()) {
                continue;
            }

            String s[] = terms.split("\\s+");
            ArrayList<String> validTerms = new ArrayList<String>(s.length);
            for (int j = 0; j < s.length; j++) {
                if (s[j] != null && (s[j].length() > 1 || TextUtils.isInteger(s[j]))) {
                    validTerms.add(s[j]);
                    if (j > 0) {
                        newBoolOp.add("AND");
                    }
                }
            }

            if (validTerms.size() > 0 && i < searchAttr.length - 1) {
                newBoolOp.add(boolOp[i]);
            }

            for (String term : validTerms) {

                switch (type) {
                    case ANY: {
                        clauses.add(dao.createIndexClause("idx_any"));
                        values.add(term);
                        break;
                    }
                    case AUTHOR: {
                        clauses.add(dao.createIndexClause("idx_author"));
                        values.add(term);
                        break;
                    }
                    case YEAR: {
                        clauses.add(dao.createIndexClause("idx_year"));
                        values.add(term);
                        break;
                    }
                    case ISBN: {
                        clauses.add(dao.createIndexClause("idx_isbn"));
                        values.add(term);
                        break;
                    }
                    case ISSN: {
                        clauses.add(dao.createIndexClause("idx_isbn"));
                        values.add(term);
                        break;
                    }
                    case SERIAL: {
                        clauses.add(dao.createSerialClause(term));
                        break;
                    }
                    case SUBJECT: {
                        clauses.add(dao.createIndexClause("idx_subject"));
                        values.add(term);
                        break;
                    }
                    case TITLE: {
                        clauses.add(dao.createIndexClause("idx_title"));
                        values.add(term);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        
        String[] newBoolOpArr = new String[newBoolOp.size()];
        newBoolOp.toArray(newBoolOpArr);
        final String clause = concatenateBooleanOperators(clauses, newBoolOpArr);

        result[0] = clause;
        result[1] = values;
        return result;
    }

    private String concatenateBooleanOperators(final ArrayList<String> clauses, final String[] boolOp) {

        if (clauses != null && !clauses.isEmpty()) {
            boolean notClause = false;
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < (clauses.size() - 1); i++) {
                String clause = clauses.get(i);
                if (notClause) {
                    clause = clause.replaceAll("in \\(", "not in \\(");
                }
                builder.append(clause);
                if (boolOp[i].equals("AND_NOT")) {
                    builder.append(" AND ");
                    notClause = true;
                } else {
                    builder.append(" ").append(boolOp[i]).append(" ");
                    notClause = false;
                }
            }
            String clause = clauses.get(clauses.size() - 1);
            if (notClause) {
                clause = clause.replaceAll("in \\(", "not in \\(");
            }
            builder.append(clause);
            return builder.toString();
        }
        return "";
    }

    public RecordDTO getById(final int serial) {
        final BiblioDAO dao = new BiblioDAO();
        return dao.getById(serial);
    }

    public RecordDTO getById(final String serial) {
        final BiblioDAO dao = new BiblioDAO();
        return dao.getById(Integer.valueOf(serial));
    }
}
