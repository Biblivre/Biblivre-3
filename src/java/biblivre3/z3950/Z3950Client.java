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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biblivre3.z3950;

import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.TextUtils;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.iface.Searchable;
import org.jzkit.search.provider.z3950.Z3950ServiceFactory;
import org.jzkit.search.util.RecordModel.ArchetypeRecordFormatSpecification;
import org.jzkit.search.util.RecordModel.marc.iso2709;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.jzkit.search.util.ResultSet.ReadAheadEnumeration;
import org.marc4j_2_3_1.converter.impl.AnselToUnicode;
import org.marc4j_2_3_1.marc.Record;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Danniel Nascimento
 */
public class Z3950Client {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private ApplicationContext z3950Context;
    private Z3950ServiceFactory factory;
    private static final String QUERY_PREFIX = "@attrset bib-1 @attr 1=";
    private static final String CHARSET = "UTF-8";

    public void setZ3950Context(ApplicationContext z3950Context) {
        this.z3950Context = z3950Context;
    }

    public void setFactory(Z3950ServiceFactory factory) {
        this.factory = factory;
    }

    public Z3950ServiceFactory getFactory() {
        return factory;
    }

    public ApplicationContext getZ3950Context() {
        return z3950Context;
    }

    public List<Record> doSearch(final Z3950ServerDTO server, final Z3950SearchDTO search) {
        List<Record> listRecords = new ArrayList<Record>();

        factory.setHost(server.getUrl());
        factory.setPort(server.getPort());
        factory.setCharsetEncoding("UTF-8");

        factory.setApplicationContext(z3950Context);
        factory.setDefaultRecordSyntax("usmarc");
        factory.setDefaultElementSetName("F");

        factory.setDoCharsetNeg(true);

        factory.getRecordArchetypes().put("Default","usmarc::F");
        factory.getRecordArchetypes().put("FullDisplay","usmarc::F");
        factory.getRecordArchetypes().put("BriefDisplay","usmarc::B");

        final String qry = QUERY_PREFIX + search.getType() + " \"" + TextUtils.removeDiacriticals(search.getValue()) + "\"";

        IRQuery query = new IRQuery();
        query.collections = new Vector();
        query.collections.add(server.getCollection());
        query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString(qry);

        try {
            Searchable s = factory.newSearchable();
            s.setApplicationContext(z3950Context);
            IRResultSet result = s.evaluate(query);

            // Wait without timeout until result set is complete or failure
            result.waitForStatus(IRResultSetStatus.COMPLETE | IRResultSetStatus.FAILURE, 0);
            if (result.getStatus() == IRResultSetStatus.FAILURE) {
                log.error("IRResultSetStatus == FAILURE");
            }
            if (result.getFragmentCount() == 0) {
                return listRecords;

            }

            String encoding = server.getCharset();
            AnselToUnicode atu = new AnselToUnicode();

            Enumeration e = new ReadAheadEnumeration(result, new ArchetypeRecordFormatSpecification("Default"));
            int errorRecords = 0;
            Record record = null;
            for (int i = 0; e.hasMoreElements(); i++) {
                iso2709 o = (iso2709) e.nextElement();

                try {
                    String iso = "";
                    if (encoding.equals("MARC-8")) {
                        iso = Normalizer.normalize(atu.convert(new String((byte[]) o.getOriginalObject(), "ISO-8859-1")), Normalizer.Form.NFC);
                    } else {
                        iso = new String((byte[]) o.getOriginalObject(), encoding);
                    }

                    try {
                        record = MarcUtils.iso2709ToRecordAsIso(iso, false);
                    } catch (Exception encodeE) {}

                    if (record == null) {
                        try {
                            record = MarcUtils.iso2709ToRecord(iso, false);
                        } catch (Exception encodeE) {}
                    }

                    if (record == null) {
                        try {
                            record = MarcUtils.iso2709ToRecordAsIso(new String((byte[]) o.getOriginalObject(), "ISO-8859-1"), false);
                        } catch (Exception encodeE) {}
                    }

                    if (record == null) {
                        try {
                            record = MarcUtils.iso2709ToRecord(new String((byte[]) o.getOriginalObject(), "ISO-8859-1"), false);
                        } catch (Exception encodeE) {}
                    }
                } catch (Exception ex) {
                }

                if (record != null) {
                    listRecords.add(record);
                } else {
                    ++errorRecords;
                }
            }
            if (errorRecords > 0) {
                log.warn("Total number of records that failed the conversion: " + errorRecords);
            }
            try {
                result.close();
                s.close();
            } catch (Exception closingException) {
                log.error(closingException.getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("returning results");
        return listRecords;
    }

}
