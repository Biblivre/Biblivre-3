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

import biblivre3.cataloging.bibliographic.BiblioDAO;
import biblivre3.cataloging.bibliographic.BiblioSearchBO;
import com.k_int.sql.data_dictionary.EntityTemplate;
import com.k_int.sql.data_dictionary.OID;
import com.k_int.sql.data_dictionary.UnknownCollectionException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.jdbc.JDBCSearchable;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelNamespaceNode;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Danniel
 */
public class BiblivreJDBCSearchable extends JDBCSearchable {

    private static Log log = LogFactory.getLog(JDBCSearchable.class);
    private String datasource_name = "z3950DataSource";
    private String dictionary_name = "persistenceDictionary";
    private com.k_int.sql.data_dictionary.Dictionary dictionary = null;
    private ApplicationContext ctx = null;
    private boolean setup_completed = false;

    private static final HashMap<String, String> accessPointMap;

    static {
        accessPointMap = new HashMap<String, String>();
        accessPointMap.put("bib-1.1.1", "AUTHOR");
        accessPointMap.put("bib-1.1.1003", "AUTHOR");
        accessPointMap.put("bib-1.1.4", "TITLE");
        accessPointMap.put("bib-1.1.21", "SUBJECT");
        accessPointMap.put("bib-1.1.7", "ISBN");
        accessPointMap.put("bib-1.1.8", "ISSN");
        accessPointMap.put("bib-1.1.1016", "ANY");
        accessPointMap.put("bib-1.1.1036", "ANY");
    }

    public BiblivreJDBCSearchable() {
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public IRResultSet evaluate(IRQuery q) {
        return evaluate(q, null);
    }

    @Override
    public IRResultSet evaluate(IRQuery q, Object user_info) {
        return evaluate(q, user_info, null);
    }

    @Override
    public IRResultSet evaluate(IRQuery q, Object user_info, Observer[] observers) {
        checkSetup();
        log.info("create JDBC Result Set");
        JDBCResultSet result = new JDBCResultSet(this);
        try {
            result.init();

            AttrPlusTermNode aptn = (AttrPlusTermNode) ((InternalModelNamespaceNode) q.query.toInternalQueryModel(ctx).getChild()).getChild();
            String accessPoint = aptn.getAccessPoint().toString();
            String term = (String) aptn.getTerm();

            String[] searchTerms = new String[1];
            String[] searchAttr = new String[1];
            String[] boolOp = new String[1];

            searchTerms[0] = term.replaceAll("[\\[\\]\\,]", "");
            searchAttr[0] = accessPointMap.get(accessPoint);

            BiblioSearchBO bo = new BiblioSearchBO();
            Object[] tuple = bo.createWhereClause(searchTerms, searchAttr, boolOp);

            if (tuple == null) {
                result.setStatus(IRResultSetStatus.FAILURE);
                return result;
            }

            final String clause = (String) tuple[0];
            final List<String> values = (ArrayList<String>) tuple[1];

            BiblioDAO dao = new BiblioDAO();
            Object[] rc = dao.searchFromZ3950(clause, values, 0, 200);

            if (rc == null) {
                result.setStatus(IRResultSetStatus.FAILURE);
                return result;
            }

            Connection c = (Connection) rc[0];
            ResultSet r = (ResultSet) rc[1];

            if (r == null || c == null) {
                result.setStatus(IRResultSetStatus.FAILURE);
                return result;
            }

            EntityTemplate et = dictionary.lookup("Records");
            while (r.next()) {
                OID key = new OID(datasource_name, "Records", new BiblivreEntityKey(et, r));
                result.add(key);
            }

            r.close();
            c.close();

            result.setStatus(IRResultSetStatus.COMPLETE);
        } catch (UnknownCollectionException unknown_collection_exception) {
            log.warn("problem evaluating query ", unknown_collection_exception);
            result.setStatus(IRResultSetStatus.FAILURE);
        } catch (java.sql.SQLException sqle) {
            result.setStatus(IRResultSetStatus.FAILURE);
        } catch (Exception e) {
            result.setStatus(IRResultSetStatus.FAILURE);
        } finally {
            log.info("evaluate complete");
        }
        return result;
    }
/*
    @Override
    public void setRecordArchetypes(Map archetypes) {
        this.archetypes = archetypes;
    }

    @Override
    public Map getRecordArchetypes() {
        return archetypes;
    }

    @Override
    public void setDatasourceName(String datasource_name) {
        this.datasource_name = datasource_name;
    }

    @Override
    public String getDataSourceName() {
        return datasource_name;
    }

    @Override
    public void setDictionaryName(String dictionary_name) {
        this.dictionary_name = dictionary_name;
    }

    @Override
    public String getDictionaryName() {
        return dictionary_name;
    }

    @Override
    public void setAccessPathsConfigName(String access_paths_config_name) {
        this.access_paths_config_name = access_paths_config_name;
    }

    @Override
    public String getAccessPathsConfigName() {
        return access_paths_config_name;
    }

    @Override
    public void setTemplatesConfigName(String templates_config_name) {
        this.templates_config_name = templates_config_name;
    }

    @Override
    public String getTemplatesConfigName() {
        return templates_config_name;
    }

    @Override
    public void setSQLDialect(String sql_dialect) {
        this.sql_dialect = sql_dialect;
    }

    @Override
    public String getSQLDialect() {
        return sql_dialect;
    }
*/
    private synchronized void checkSetup() {
        if (!setup_completed) {
            try {
                this.dictionary = (com.k_int.sql.data_dictionary.Dictionary) ctx.getBean(dictionary_name);
                setup_completed = true;
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        this.ctx = ctx;
    }
}
