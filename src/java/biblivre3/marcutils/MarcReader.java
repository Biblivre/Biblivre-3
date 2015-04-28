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

package biblivre3.marcutils;

import biblivre3.enums.MaterialType;
import biblivre3.enums.RecordStatus;
import biblivre3.utils.ApplicationConstants;
import biblivre3.utils.HtmlEntityEscaper;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.marc4j_2_3_1.marc.ControlField;
import org.marc4j_2_3_1.marc.DataField;
import org.marc4j_2_3_1.marc.Leader;
import org.marc4j_2_3_1.marc.MarcFactory;
import org.marc4j_2_3_1.marc.Record;
import org.marc4j_2_3_1.marc.Subfield;
import org.marc4j_2_3_1.marc.VariableField;

/**
 * @author BibLivre
 *
 */
public class MarcReader {

    private static final Logger log = Logger.getLogger(MarcReader.class);

    public static String detectSplitter(final String marc) {
        // Try to detect the first split.
        if (!StringUtils.isBlank(marc)) {
            String[] lines = marc.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.length() > 7) {
                    char separator = line.charAt(6);
                    if (separator == '|' || separator == '$') {
                        return String.valueOf(separator);
                    }
                }
            }
        }

        return ApplicationConstants.DEFAULT_SPLITTER;
    }

    public static String marcToIso2709(final String marc, final MaterialType mt, final RecordStatus status) {
        final Record record = marcToRecord(marc, mt, status);
        return MarcUtils.recordToIso2709(record);
    }

    public static Record marcToRecord(final String marc, final MaterialType mt, final RecordStatus status) {
        final String splitter = detectSplitter(marc);
        final String escaped = HtmlEntityEscaper.replaceHtmlEntities(marc);
        Scanner scanner = null;

        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(escaped.getBytes("UTF-8"));
            scanner = new Scanner(bais, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
            scanner = new Scanner(escaped);
        }

        final List<String> text = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.trim().length() > 3) {
                text.add(line);
            }
        }

        final String tags[] = new String[text.size()];
        final String values[] = new String[text.size()];

        for (int i = 0; i < text.size(); i++) {
            final String line = text.get(i);
            if (!line.toUpperCase().startsWith("LEADER")) {
                tags[i] = line.substring(0, 3).toUpperCase();
                values[i] = line.substring(4, text.get(i).length()).trim();
            } else {
                tags[i] = line.substring(0, 6).toUpperCase();
                values[i] = line.substring(7, text.get(i).length()).trim();
            }
        }

        final Leader leader = setLeader(tags, values, mt, status);
        final MarcFactory factory = MarcFactory.newInstance();
        final Record record = factory.newRecord(leader);
        setControlFields(record, tags, values);
        setDataFields(record, tags, values, splitter);
        return record;
    }

    private static Leader setLeader(final String[] tags, final String values[], final MaterialType mt, final RecordStatus status) {
        Leader leader = null;
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].equals("000") || tags[i].equals("LDR") || tags[i].equals("LEADER")) {
                leader = MarcUtils.createLeader(values[i], mt, status);
                break;
            }
        }
        if (leader == null) {
            leader = MarcUtils.createBasicLeader(mt, status);
        }
        return leader;
    }

    private static void setControlFields(Record record, String[] tags, String[] values) {
        final MarcFactory factory = MarcFactory.newInstance();
        for (int i = 0; i < tags.length; i++) {
            if (!tags[i].equals("000") && !tags[i].equals("LDR") && !tags[i].equals("LEADER")) {
                if (((Integer.parseInt(tags[i])) < 10)
                        && ((Integer.parseInt(tags[i])) > 0)) {
                    ControlField controlField =
                            factory.newControlField(tags[i], values[i]);
                    record.addVariableField(controlField);
                }
            }
        }
    }

    private static void setDataFields(Record record, String[] tags, String[] values, String splitter) {
        splitter = (splitter != null ? splitter : ApplicationConstants.DEFAULT_SPLITTER);
        splitter = "\\" + splitter;
        final MarcFactory factory = MarcFactory.newInstance();
        for (int i = 0; i < tags.length; i++) {
            if (!tags[i].equals("000") && !tags[i].equals("LDR") && !tags[i].equals("LEADER")) {
                if ((Integer.parseInt(tags[i])) >= 10) {
                    String tag = tags[i];
                    char ind1 = values[i].charAt(0) != '_' ? values[i].charAt(0) : ' ';
                    char ind2 = values[i].charAt(1) != '_' ? values[i].charAt(1) : ' ';
                    DataField dataField = factory.newDataField(tag, ind1, ind2);
                    record.addVariableField(dataField);
                    String value = values[i].substring(2).trim();
                    String[] subfs = value.split(splitter);
                    for (int j = 0; j < subfs.length; j++) {
                        String data = subfs[j];
                        if (data != null && data.trim().length() != 0) {
                            Subfield subfield = factory.newSubfield(data.charAt(0), data.substring(1).trim());
                            dataField.addSubfield(subfield);
                        }
                    }
                }
            }
        }
    }

    public static String iso2709ToMarc(final String iso2709) {
        final Record record = MarcUtils.iso2709ToRecord(iso2709);
        final StringBuffer sb = new StringBuffer();
        sb.append("000 ");
        sb.append(record.getLeader().toString());
        sb.append('\n');
        final Iterator i = record.getVariableFields().iterator();
        while (i.hasNext()) {
            final VariableField field = (VariableField) i.next();
            if (field instanceof DataField) {
                final DataField datafield = (DataField) field;
                final StringBuffer dfsb = new StringBuffer();

                dfsb.append(datafield.getTag());
                dfsb.append(' ');
                final char ind1 = datafield.getIndicator1();
                dfsb.append(ind1 != ' ' ? ind1 : '_');
                final char ind2 = datafield.getIndicator2();
                dfsb.append(ind2 != ' ' ? ind2 : '_');

                final Iterator j = datafield.getSubfields().iterator();
                while (j.hasNext()) {
                    Subfield sf = (Subfield) j.next();
                    dfsb.append(ApplicationConstants.DEFAULT_SPLITTER).append(sf.getCode()).append(sf.getData());
                }

                sb.append(dfsb.toString());
                sb.append('\n');
            } else {
                sb.append(field.toString());
                sb.append('\n');
            }
        }
        return sb.toString();
    }

}
