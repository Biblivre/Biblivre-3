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
import biblivre3.utils.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j_2_3_1.MarcException;
import org.marc4j_2_3_1.MarcStreamReader;
import org.marc4j_2_3_1.MarcStreamWriter;
import org.marc4j_2_3_1.MarcWriter;
import org.marc4j_2_3_1.marc.ControlField;
import org.marc4j_2_3_1.marc.DataField;
import org.marc4j_2_3_1.marc.Leader;
import org.marc4j_2_3_1.marc.MarcFactory;
import org.marc4j_2_3_1.marc.Record;
import org.marc4j_2_3_1.marc.Subfield;

/**
 * Classe com m&eacute;todos utilit&aacute;rios para processamento
 * de registros MARC.
 *
 * @author Danniel Nascimento
 * @since 23/09/2008
 */
public class MarcUtils {

    private static Logger log = Logger.getLogger(MarcUtils.class);
    private static Format ISO8601_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
    private static Format CF001_FORMAT = new DecimalFormat("0000000");
    private static Format CF008_FORMAT = new SimpleDateFormat("yyMMdd");

    public static String recordToIso2709(Record record) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MarcWriter writer = new MarcStreamWriter(baos, "UTF-8");
        writer.write(record);
        writer.close();
        try {
            return new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
            return baos.toString();
        }
    }

    public static Record iso2709ToRecord(String iso2709, boolean logging) {
        Record record = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(iso2709.getBytes("UTF-8"));
            MarcStreamReader reader = new MarcStreamReader(bais, "UTF-8");

            if (reader.hasNext()) {
                record = reader.next();
            }
        } catch (UnsupportedEncodingException uee) {
            if (logging) {
                log.error(uee.getMessage(), uee);
            }
        } catch (MarcException me) {
            if (logging) {
                log.error(me.getMessage(), me);
            }
            record = iso2709ToRecordAsIso(iso2709);
        }
        return record;
    }

    public static Record iso2709ToRecord(String iso2709) {
        return iso2709ToRecord(iso2709, true);
    }

    public static Record iso2709ToRecordAsIso(String iso2709, boolean logging) {
        Record record = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(iso2709.getBytes("ISO-8859-1"));
            MarcStreamReader reader = new MarcStreamReader(bais);

            if (reader.hasNext()) {
                record = reader.next();
            }
        } catch (Exception uee) {
            if (logging) {
                log.error(uee.getMessage(), uee);
            }
        }
        return record;
    }

    public static Record iso2709ToRecordAsIso(String iso2709) {
        return iso2709ToRecordAsIso(iso2709, true);
    }

    public static Record iso2709ToRecord(byte[] iso2709) {
        Record record = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(iso2709);
        MarcStreamReader reader = new MarcStreamReader(bais, "UTF-8");
        if (reader.hasNext()) {
            record = reader.next();
        }
        return record;
    }

    public static JSONObject recordToJson(Record record) {
        JSONObject json = new JSONObject();

        if (record == null) {
            return json;
        }

        try {
            json.putOpt("000", record.getLeader().marshal());

            ArrayList<ControlField> controlFields = (ArrayList) record.getControlFields();
            for (ControlField cf : controlFields) {
                json.putOpt(cf.getTag(), cf.getData());
            }

            ArrayList<DataField> dataFields = (ArrayList) record.getDataFields();
            for (DataField df : dataFields) {
                JSONObject datafieldJson = new JSONObject();

                datafieldJson.putOpt("ind1", df.getIndicator1());
                datafieldJson.putOpt("ind2", df.getIndicator2());

                ArrayList<Subfield> subFields = (ArrayList) df.getSubfields();
                for (Subfield sf : subFields) {
                    datafieldJson.append(String.valueOf(sf.getCode()), sf.getData());
                }

                json.append(df.getTag(), datafieldJson);
            }
        } catch (JSONException je) {
        }

        return json;
    }

    public static Record jsonToRecord(JSONObject json, MaterialType mt, RecordStatus status) {
        if (json == null) {
            return null;
        }

        Record record = null;

        try {
            String strLeader = null;
            if (json.has("000")) {
                strLeader = json.getString("000");
            }

            MarcFactory factory = MarcFactory.newInstance();

            Leader leader = MarcUtils.createLeader(strLeader, mt, status);
            record = factory.newRecord(leader);

            Iterator<String> dataFieldsIterator = json.sortedKeys();
            while (dataFieldsIterator.hasNext()) {
                String dataFieldTag = dataFieldsIterator.next();

                try {
                    Integer dataFieldIntTag = Integer.valueOf(dataFieldTag);

                    if (dataFieldIntTag == 0) {
                        continue;

                    } else if (dataFieldIntTag < 10) {
                        ControlField cf = factory.newControlField(dataFieldTag, json.getString(dataFieldTag));
                        record.addVariableField(cf);

                    } else {
                        JSONArray subFieldsArray = json.getJSONArray(dataFieldTag);

                        for (int i = 0; i < subFieldsArray.length(); i++) {
                            JSONObject subFieldJson = subFieldsArray.getJSONObject(i);

                            DataField df = factory.newDataField();
                            df.setTag(dataFieldTag);
                            df.setIndicator1(' ');
                            df.setIndicator2(' ');

                            Iterator<String> dfIterator = subFieldJson.sortedKeys();
                            while (dfIterator.hasNext()) {
                                String subFieldTag = dfIterator.next();

                                if (subFieldTag.equals("ind1")) {
                                    df.setIndicator1(subFieldJson.getString(subFieldTag).charAt(0));
                                } else if (subFieldTag.equals("ind2")) {
                                    df.setIndicator2(subFieldJson.getString(subFieldTag).charAt(0));
                                } else {
                                    JSONArray subFieldDataArray = subFieldJson.getJSONArray(subFieldTag);

                                    for (int j = 0; j < subFieldDataArray.length(); j++) {
                                        String subfieldData = subFieldDataArray.getString(j);

                                        Subfield sf = factory.newSubfield(subFieldTag.charAt(0), subfieldData);
                                        df.addSubfield(sf);
                                    }
                                }
                            }

                            record.addVariableField(df);
                        }
                    }
                } catch (NumberFormatException nfe) {
                    log.error(nfe.getMessage(), nfe);
                }
            }
        } catch (JSONException je) {
            log.error(je.getMessage(), je);
        }

        return record;
    }

    /*
    public static void main(String[] args) {
    Record record = MarcUtils.iso2709ToRecord("01313nam a2200361 a 4500001000200000005001900002008003900021010001300060020002100073020002200094035002100116040001800137042001000155043001200165050002400177082002200201245015200223250001200375260004700387300003200434500008000466504003600546600002700582650003400609650002700643700002300670700005600693906004500749922000700794955007000801985001600871991006400887320091212202140.531941006m19949999bl a     k    000 0 por  a94832848  a8571640793 (set)  a8571640807 (v. 1)  9(DLC)   94832848  aDLCcDLCdDLC  alcode  as-bl---00aML410.B923bC5 199400a782.42164/09222000aChico Buarque :bletra e música ; incluindo Carta ao Chico de Tom Jobim e Gol de letras de Humberto Werneck ; edição gráfica Hélio de Almeida.  a2a. ed.  aSão Paulo :bCompanhia das Letras,c1994-  av. <1   > :bill. ;c24 cm.  aVol. 1 contains lyrics and a biography of the composer by Humberto Werneck.  aDiscography: p. 274-289 (v. 1).10aBuarque, Chico,d1944- 0aComposerszBrazilxBiography. 0aPopular musiczBrazil.1 aWerneck, Humberto.1 aBuarque, Chico,d1944-tSongs.kSelections.xTexts.  a7bcbccorigoded2encipf19gy-gencatlg  aap  awr06; 08/18/94; desc; vn22 06-06-95; vk10 11-03-95; vk90 11-06-95  eAPIF/ODE-rj  bc-MusichML410.B923iC5 1994p00038542059tCopy 1v1wBOOKS");

    JSONObject json = MarcUtils.recordToJson(record);
    System.out.println(json);

    json.remove("000");

    Record record2 = MarcUtils.jsonToRecord(json);
    System.out.println(MarcUtils.recordToJson(record2));
    }
     */
    public static Leader createBasicLeader(MaterialType mt, final RecordStatus status) {
        if (mt == null) {
            mt = MaterialType.ALL;
        }
        Leader leader = MarcFactory.newInstance().newLeader();
        leader.setRecordStatus(status.getCode());
        leader.setTypeOfRecord(mt.getTypeOfRecord());
        leader.setImplDefined1(mt.getImplDefined1().toCharArray());
        leader.setCharCodingScheme('a');
        leader.setIndicatorCount(2);
        leader.setSubfieldCodeLength(2);

        if (mt.equals(MaterialType.AUTHORITIES)) {
            leader.setImplDefined2("n  ".toCharArray());
        } else if (mt.equals(MaterialType.HOLDINGS)) {
            leader.setImplDefined2("un ".toCharArray());
        } else if (mt.equals(MaterialType.VOCABULARY)) {
            leader.setImplDefined2("o  ".toCharArray());
        } else {//BIBLIO
            leader.setImplDefined2(" a ".toCharArray());
        }

        leader.setEntryMap("4500".toCharArray());
        return leader;
    }

    public static Leader createLeader(final String pLeader, final MaterialType mt, final RecordStatus status) {
        Leader leader = MarcFactory.newInstance().newLeader();
        if (pLeader != null && pLeader.length() == 24) {
            leader.setRecordStatus(status.getCode());
            
            if (mt != null && !mt.equals(MaterialType.ALL)) {
                leader.setTypeOfRecord(mt.getTypeOfRecord());
                leader.setImplDefined1(mt.getImplDefined1().toCharArray());
            } else {
                leader.setTypeOfRecord(pLeader.charAt(6));
                char $07 = pLeader.charAt(7);
                char $08 = (pLeader.charAt(8)) == 'a' ? 'a' : ' ';
                char[] implDef1 = {$07, $08};
                leader.setImplDefined1(implDef1);
            }

            char $09 = (pLeader.charAt(9)) == 'a' ? 'a' : ' ';
            leader.setCharCodingScheme($09);
            leader.setIndicatorCount(2);
            leader.setSubfieldCodeLength(2);
            leader.setImplDefined2(pLeader.substring(17, 20).toCharArray());
            leader.setEntryMap((pLeader.substring(20)).toCharArray());
        } else {
            leader = createBasicLeader(mt, status);
        }
        return leader;
    }

    public static DataField getDataField(Record record, String tag) {
        if (record != null && StringUtils.isNotBlank(tag)) {
            for (Object obj : record.getDataFields()) {
                DataField dataField = (DataField) obj;
                if (dataField.getTag().equals(tag)) {
                    return dataField;
                }
            }
        }
        return null;
    }

    public static List<DataField> getDataFieldList(Record record, String tag) {
        List<DataField> dataFieldList = new ArrayList<DataField>();
        if (record != null && StringUtils.isNotBlank(tag)) {
            for (Object obj : record.getDataFields()) {
                DataField dataField = (DataField) obj;
                if (dataField.getTag().equals(tag)) {
                    dataFieldList.add(dataField);
                }
            }
        }
        return dataFieldList;
    }

    public static ControlField getControlField(Record record, String tag) {
        if (record != null && StringUtils.isNotBlank(tag)) {
            for (Object obj : record.getControlFields()) {
                ControlField controlField = (ControlField) obj;
                if (controlField.getTag().equals(tag)) {
                    return controlField;
                }
            }
        }
        return null;
    }

    public static Subfield getSubfield(Record record, String tag, char subfield) {
        DataField datafield = getDataField(record, tag);
        return datafield != null ? datafield.getSubfield(subfield) : null;
    }

    /**
     * Method that returns the first subfield found from the parameter list.
     *
     * @param record
     * @param tag
     * @param subfields
     * @return
     */
    public static Subfield getSubfield(Record record, String tag, char[] subfields) {
        Subfield sub = null;
        for (char subfield : subfields) {
            sub = getSubfield(record, tag, subfield);
            if (sub != null) {
                break;
            }
        }
        return sub;
    }

    public static ArrayList<String[]> createFieldsList(Record record) {
        final ArrayList<String[]> fields = new ArrayList<String[]>();
        Subfield subfield;
        StringBuilder builder;
        String value;

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_PERSONAL_NAME, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.AUTHOR_PERSONAL_NAME, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CORPORATION_NAME, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.AUTHOR_CORPORATION_NAME, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CONGRESS_NAME, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.AUTHOR_CONGRESS_NAME, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.ANONIMOUS, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.ANONIMOUS, subfield.getData()});
        }

        for (String datafieldNumber : new String[]{
            MarcConstants.SECONDARY_AUTHOR_PERSONAL_NAME,
            MarcConstants.SECONDARY_AUTHOR_CORPORATION_NAME,
            MarcConstants.SECONDARY_AUTHOR_CONGRESS_NAME
        }) {        
            builder = new StringBuilder();
            for (DataField df : getDataFieldList(record, datafieldNumber)) {
                subfield = df.getSubfield('a');
                if (subfield != null) {
                    builder.append(subfield.getData()).append("<br/> ");
                }
            }

            value = builder.toString();
            if (StringUtils.isNotBlank(value)) {
                fields.add(new String[]{datafieldNumber, value.substring(0, value.length() - 6)});
            }
        }

        value = Indexer.listTitle(record, true, true);
        if (StringUtils.isNotBlank(value)) {
            fields.add(new String[]{MarcConstants.TITLE, value});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.EDITION, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.EDITION, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.PRODUCTION_PLACE, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.PRODUCTION_PLACE, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.MATERIAL_INFO, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.MATERIAL_INFO, subfield.getData()});
        }

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.COLLECTIVE_UNIFORM_TITLE, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.COLLECTIVE_UNIFORM_TITLE, 'f');
        if (subfield != null) {
            builder.append(subfield.getData());
        }

        if (!StringUtils.isBlank(builder.toString())) {
            fields.add(new String[]{MarcConstants.COLLECTIVE_UNIFORM_TITLE, builder.toString()});
        }

        builder = new StringBuilder();
        for (DataField df : getDataFieldList(record, MarcConstants.PUBLICATION)) {
            boolean hasA = false, hasB = false, hasC = false;
            String a = "", b = "", c = "";

            subfield = df.getSubfield('a');
            if (subfield != null) {
                a = subfield.getData();
                hasA = StringUtils.isNotBlank(a);
            }

            subfield = df.getSubfield('b');
            if (subfield != null) {
                b = subfield.getData();
                hasB = StringUtils.isNotBlank(b);
            }

            subfield = df.getSubfield('c');
            if (subfield != null) {
                c = subfield.getData();
                hasC = StringUtils.isNotBlank(c);
            }

            if (hasA) {
                builder.append(a);
            }

            if (hasB) {
                if (hasA) {
                    if (TextUtils.endsInValidCharacter(builder.toString())) {
                        builder.append(": ");
                    } else {
                        builder.append(" ");
                    }
                }
                builder.append(b);
            }

            if (hasC) {
                if (hasB || hasA) {
                    if (TextUtils.endsInValidCharacter(builder.toString())) {
                        builder.append(", ");
                    } else {
                        builder.append(" ");
                    }
                }
                builder.append(c);
            }

            if (hasA || hasB || hasC) {
                builder.append("<br/> ");
            }
        }

        value = builder.toString();
        if (StringUtils.isNotBlank(value)) {
            fields.add(new String[]{MarcConstants.PUBLICATION, value.substring(0, value.length() - 6)});
        }

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.FORMAT, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.FORMAT, 'b');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.FORMAT, 'c');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.FORMAT, 'e');
        if (subfield != null) {
            builder.append(subfield.getData());
        }

        value = builder.toString();
        if (StringUtils.isNotBlank(value)) {
            fields.add(new String[]{MarcConstants.FORMAT, value});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.LENGTH, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.LENGTH, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.SERIES, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.SERIES, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_SUMMARY, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_GENERAL, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_GENERAL, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_GENERAL, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_BIBLIOGRAFIC, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_BIBLIOGRAFIC, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_CONTENT, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_CONTENT, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_TARGET_PUBLIC, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_TARGET_PUBLIC, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_FACSIMILE, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_FACSIMILE, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_LOCAL, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_LOCAL, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_THESIS, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_THESIS, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_RESTRICT_ACCESS, 'c');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_RESTRICT_ACCESS, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_HOLDING_RESTRICT_ACCESS, 'h');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_HOLDING_RESTRICT_ACCESS, subfield.getData()});
        }

        for (String datafieldNumber : new String[]{
            MarcConstants.SUBJECT_PERSONAL_NAME,
            MarcConstants.SUBJECT_CORPORATE_NAME,
            MarcConstants.SUBJECT_EVENT,
            MarcConstants.SUBJECT_TITLE,
            MarcConstants.SUBJECT_TOPIC,
            MarcConstants.SUBJECT_GEOGRAPHIC_NAME
        }) {     
            builder = new StringBuilder();
            for (DataField df : getDataFieldList(record, datafieldNumber)) {
                boolean has = false;
                for (Object obj : df.getSubfields()) {
                    subfield = (Subfield) obj;
                    if (subfield != null && !StringUtils.isBlank(subfield.getData())) {
                        if (has) {
                            builder.append(" - ");
                        }
                        has = true;
                        builder.append(subfield.getData());
                    }
                }

                if (has) {
                    builder.append("<br/> ");
                }
            }

            value = builder.toString();
            if (!StringUtils.isBlank(value)) {
                fields.add(new String[]{datafieldNumber, value.substring(0, value.length() - 6)});
            }
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.ISBN, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.ISBN, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.ISSN, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.ISSN, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.ISRC, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.ISRC, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.FILE_INFO, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.FILE_INFO, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.GEO_CODE, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.GEO_CODE, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.CHRONO_CODE, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.CHRONO_CODE, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.SCALE, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.SCALE, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.LANGUAGE, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.LANGUAGE, subfield.getData()});
        }

        builder = new StringBuilder();        
        subfield = MarcUtils.getSubfield(record, MarcConstants.LOCATION, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.LOCATION, 'b');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.LOCATION, 'c');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }
        
        value = builder.toString();
        if (!StringUtils.isBlank(value)) {
            fields.add(new String[]{MarcConstants.LOCATION, value.trim()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.PERIODICITY, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.PERIODICITY, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.FIRST_PUBLICATION_DATE, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.FIRST_PUBLICATION_DATE, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.UNITS, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.UNITS, subfield.getData()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.NOTES_PUBLIC, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.NOTES_PUBLIC, subfield.getData()});
        }

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.UDCN, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.UDCN, '2');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        value = builder.toString();
        if (!StringUtils.isBlank(value)) {
            fields.add(new String[]{MarcConstants.UDCN, value.trim()});
        }

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.DDCN, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.DDCN, '2');
        if (subfield != null) {
            builder.append(subfield.getData()).append(" ");
        }

        value = builder.toString();
        if (!StringUtils.isBlank(value)) {
            fields.add(new String[]{MarcConstants.DDCN, value.trim()});
        }

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.PATENT, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append("; ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.PATENT, 'b');
        if (subfield != null) {
            builder.append(subfield.getData()).append("; ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.PATENT, 'c');
        if (subfield != null) {
            builder.append(subfield.getData()).append("; ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.PATENT, 'd');
        if (subfield != null) {
            builder.append(subfield.getData()).append("; ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.PATENT, 'e');
        if (subfield != null) {
            builder.append(subfield.getData()).append("; ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.PATENT, 'f');
        if (subfield != null) {
            builder.append(subfield.getData()).append("; ");
        }

        value = builder.toString();
        if (!StringUtils.isBlank(value)) {
            fields.add(new String[]{MarcConstants.PATENT, value.trim()});
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.CNPQ, 'a');
        if (subfield != null) {
            fields.add(new String[]{MarcConstants.CNPQ, subfield.getData()});
        }

        return fields;
    }

    public static ArrayList<String[]> createFieldsListAuthority(Record record) {
        final ArrayList<String[]> fields = new ArrayList<String[]>();
        Subfield subfield;
        StringBuilder builder;
        String value;

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_PERSONAL_NAME, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_PERSONAL_NAME, 'b');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_PERSONAL_NAME, 'c');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_PERSONAL_NAME, 'q');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_PERSONAL_NAME, 'd');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        value = builder.toString();
        if (StringUtils.isNotBlank(value)) {
            fields.add(new String[]{MarcConstants.AUTHOR_PERSONAL_NAME, value.substring(0, value.length() - 2)});
        }

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CORPORATION_NAME, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CORPORATION_NAME, 'b');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CORPORATION_NAME, 'c');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CORPORATION_NAME, 'd');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CORPORATION_NAME, 'l');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CORPORATION_NAME, 'n');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        value = builder.toString();
        if (StringUtils.isNotBlank(value)) {
            fields.add(new String[]{MarcConstants.AUTHOR_CORPORATION_NAME, value.substring(0, value.length() - 2)});
        }

        builder = new StringBuilder();
        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CONGRESS_NAME, 'a');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CONGRESS_NAME, 'b');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CONGRESS_NAME, 'c');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CONGRESS_NAME, 'd');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CONGRESS_NAME, 'l');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        subfield = MarcUtils.getSubfield(record, MarcConstants.AUTHOR_CONGRESS_NAME, 'n');
        if (subfield != null) {
            builder.append(subfield.getData()).append(", ");
        }

        value = builder.toString();
        if (StringUtils.isNotBlank(value)) {
            fields.add(new String[]{MarcConstants.AUTHOR_CORPORATION_NAME, value.substring(0, value.length() - 2)});
        }

        for (String datafieldNumber : new String[]{
            MarcConstants.AUTHOR_OTHER_PERSONAL_NAMES,
            MarcConstants.AUTHOR_OTHER_CORPORATION_NAMES,
            MarcConstants.AUTHOR_OTHER_CONGRESS_NAMES,
            MarcConstants.DATA_SOURCE
        }) {
            builder = new StringBuilder();
            for (DataField df : getDataFieldList(record, datafieldNumber)) {
                subfield = df.getSubfield('a');
                if (subfield != null) {
                    builder.append(subfield.getData()).append("<br/> ");
                }

                if (datafieldNumber.equals(MarcConstants.DATA_SOURCE)) {
                    subfield = df.getSubfield('b');
                    if (subfield != null) {
                        builder.append(subfield.getData()).append("<br/> ");
                    }
                }
            }
            value = builder.toString();
            if (StringUtils.isNotBlank(value)) {
                fields.add(new String[]{datafieldNumber, value.substring(0, value.length() - 6)});
            }
        }

        return fields;
    }

    public static ArrayList<String[]> createFieldsListVocabulary(Record record) {
        final ArrayList<String[]> fields = new ArrayList<String[]>();

        StringBuilder builder = new StringBuilder();
        String value = null;

        Subfield dataTe = MarcUtils.getSubfield(record, MarcConstants.THESAURUS_TE, 'a');
        if (dataTe != null) {
            builder.append(dataTe.getData()).append(", ");
        }

        dataTe = MarcUtils.getSubfield(record, MarcConstants.THESAURUS_TE, 'i');
        if (dataTe != null) {
            builder.append(dataTe.getData()).append(", ");
        }

        value = builder.toString();
        if (StringUtils.isNotBlank(value)) {
            fields.add(new String[]{"V" + MarcConstants.THESAURUS_TE, value.substring(0, value.length() - 2)});
        }

        for (String datafield : new String[]{
            MarcConstants.THESAURUS_UP,
            MarcConstants.THESAURUS_TG,
            MarcConstants.THESAURUS_VT_TA_TR,
            MarcConstants.THESAURUS_SCOPE_NOTES,
            MarcConstants.THESAURUS_HISTORY_NOTES
        }) {
            builder = new StringBuilder();
            for (DataField df : getDataFieldList(record, datafield)) {
                Subfield upData = df.getSubfield('a');
                builder.append(upData.getData()).append("<br/> ");
            }

            value = builder.toString();
            if (StringUtils.isNotBlank(value)) {
                fields.add(new String[]{"V" + datafield, value.substring(0, value.length() - 6)});
            }
        }

        return fields;
    }

    public static ArrayList<DataField> getLinks(Record record) {
        final ArrayList<DataField> links = new ArrayList<DataField>();
        for (Iterator it = record.getDataFields().iterator(); it.hasNext();) {
            DataField datafield = (DataField) it.next();
            if (datafield.getTag().equals(MarcConstants.LINK)) {
                links.add(datafield);
            }
        }
        return links;
    }

    public static Record createLinksDatafield(final Record record, final String uri, final String description) {
        final MarcFactory factory = MarcFactory.newInstance();
        final DataField field = factory.newDataField(MarcConstants.LINK, ' ', ' ');
        final Subfield subfieldD = factory.newSubfield('d', uri.replaceAll(".*\\/", ""));
        field.addSubfield(subfieldD);
        final Subfield subfieldF = factory.newSubfield('f', uri.replaceAll("[^\\/]*$", ""));
        field.addSubfield(subfieldF);
        final Subfield subfieldU = factory.newSubfield('u', uri);
        field.addSubfield(subfieldU);
        final Subfield subfieldY = factory.newSubfield('y', description);
        field.addSubfield(subfieldY);
        record.addVariableField(field);
        return record;
    }

    public static Record setAssetHolding(final Record holding, final String assetHolding) {
        final MarcFactory factory = MarcFactory.newInstance();

        DataField field = MarcUtils.getDataField(holding, MarcConstants.ASSET_HOLDING);
        if (field == null) {
            field = factory.newDataField(MarcConstants.ASSET_HOLDING, ' ', ' ');
            holding.addVariableField(field);
        }

        Subfield subfield = field.getSubfield('a');
        if (subfield == null) {
            subfield = factory.newSubfield('a');
            field.addSubfield(subfield);
        }

        subfield.setData(assetHolding);

        return holding;
    }

    public static Record setCF001(final Record record, final Integer controlNumber) {
        final MarcFactory factory = MarcFactory.newInstance();
        final ControlField field = factory.newControlField("001");
        field.setData(CF001_FORMAT.format(controlNumber));
        record.addVariableField(field);
        return record;
    }

    public static String setCF001(final String iso2709, final Integer controlNumber) {
        Record record = iso2709ToRecord(iso2709);
        final MarcFactory factory = MarcFactory.newInstance();
        final ControlField field = factory.newControlField("001");
        field.setData(CF001_FORMAT.format(controlNumber));
        record.addVariableField(field);
        return recordToIso2709(record);
    }

    public static Record setCF004(final Record holding, final Integer recordId) {
        final MarcFactory factory = MarcFactory.newInstance();
        ControlField field = (ControlField) holding.getVariableField("004");
        if (field == null) {
            field = factory.newControlField("004");
            holding.addVariableField(field);
        }
        field.setData(recordId.toString());
        return holding;
    }

    public static Record setCF005(final Record record) {
        return setCF005(record, new Date());
    }

    public static Record setCF005(final Record record, final Date date) {
        final MarcFactory factory = MarcFactory.newInstance();
        ControlField field = (ControlField) record.getVariableField("005");
        if (field == null) {
            field = factory.newControlField("005");
            record.addVariableField(field);
        }
        field.setData(ISO8601_FORMAT.format(date));
        return record;
    }

    public static Record setCF008(final Record record) {
        final MarcFactory factory = MarcFactory.newInstance();
        ControlField field = (ControlField) record.getVariableField("008");
        if (field == null) {
            //Following the specs, this field should be constructed only
            //if it doesn't already exist.  Otherwise, keep what has
            //come with the freemarc string.
            field = factory.newControlField("008");
            StringBuilder data = new StringBuilder();
            //posicoes de 01 a 06
            data.append(CF008_FORMAT.format(new Date()));
            //posicao 07 a 40
            data.append("s||||     bl|||||||||||||||||por|u");
            field.setData(data.toString());
            record.addVariableField(field);
        }
        return record;
    }
}
