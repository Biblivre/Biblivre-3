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

/**
 * 
 */
package biblivre3.marcutils;

import biblivre3.utils.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.marc.DataField;
import org.marc4j_2_3_1.marc.Record;
import org.marc4j_2_3_1.marc.Subfield;

/**
 * @author BibLivre
 *ISBN,TITLE,AUTHOR_PERSONAL_NAME,AUTHOR_CORPORATION_NAME,AUTHOR_CONGRESS_NAME,
 * AUTOR_SECUNDARIO_1, 2 e 3,LOCATION,PUBLICATION,SUBJECT_TOPIC,DDCN,EDITION
 */
public class Indexer {

    public static String listIsbn(Record record) {
        String retorno = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.ISBN);
        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');
            String isbn = subField != null ? subField.getData() : null;
            if (StringUtils.isNotBlank(isbn)) {
                if (Character.isDigit(isbn.charAt(0)) && (isbn.length() >= 10)) {
                    retorno = isbn;
                }
            }
        }

        return retorno;
    }

    public static String listIssn(Record record) {
        String retorno = "";
        DataField dataField = MarcUtils.getDataField(record, MarcConstants.ISSN);
        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');
            String isbn = subField != null ? subField.getData() : null;
            if (StringUtils.isNotBlank(isbn)) {
                if (Character.isDigit(isbn.charAt(0)) && (isbn.length() >= 10)) {
                    retorno = isbn;
                }
            }
        }
        return retorno;
    }

    public static String listTitle(Record record) {
        return Indexer.listTitle(record, false, true);
    }

    public static String listOneTitle(Record record) {
        return Indexer.listTitle(record, true, false);
    }

    public static String listTitle(Record record, boolean tokenize, boolean listAll) {
        StringBuilder title = new StringBuilder();

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.TITLE);
        StringBuilder value = new StringBuilder();

        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');
            if (subField != null) {
                value.append(subField.getData());
            }

            subField = dataField.getSubfield('b');
            if (tokenize && value.length() > 0 && subField != null) {
                if (TextUtils.endsInValidCharacter(value.toString())) {
                    value.append(":");
                }
            }

            if (subField != null) {
                if (value.length() > 0) {
                    value.append(" ");
                }
                value.append(subField.getData());
            }
            
            if (value.length() > 0) {
                title.append(value);
            }
        }

        if (!listAll && title.length() > 0) {
            return title.toString().trim();
        }

        dataField = MarcUtils.getDataField(record, MarcConstants.COLLECTIVE_UNIFORM_TITLE);
        value = new StringBuilder();

        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');

            if (subField != null) {
                value.append(subField.getData());
            }

            subField = dataField.getSubfield('f');
            if (subField != null) {
                if (value.length() > 0) {
                    value.append(" ");
                }
                value.append(subField.getData());
            }

            if (value.length() > 0) {
                if (title.length() > 0) {
                    title.append(tokenize && TextUtils.endsInValidCharacter(title.toString()) ? " - " : " ");
                }
                title.append(value);
            }
        }

        if (!listAll && title.length() > 0) {
            return title.toString().trim();
        }

        dataField = MarcUtils.getDataField(record, MarcConstants.UNIFORM_TITLE);
        value = new StringBuilder();

        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');
            if (subField != null) {
                value.append(subField.getData());
            }

            if (value.length() > 0) {
                if (title.length() > 0) {
                    title.append(tokenize && TextUtils.endsInValidCharacter(title.toString()) ? " - " : " ");
                }
                title.append(value);
            }
        }

        if (!listAll && title.length() > 0) {
            return title.toString().trim();
        }

        dataField = MarcUtils.getDataField(record, MarcConstants.ADDED_UNIFORM_TITLE);
        value = new StringBuilder();

        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');
            if (subField != null) {
                value.append(subField.getData());
            }

            if (value.length() > 0) {
                if (title.length() > 0) {
                    title.append(tokenize && TextUtils.endsInValidCharacter(title.toString()) ? " - " : " ");
                }
                title.append(value);
            }
        }

        if (!listAll && title.length() > 0) {
            return title.toString().trim();
        }

        dataField = MarcUtils.getDataField(record, MarcConstants.ADDED_ANALYTICAL_TITLE);
        value = new StringBuilder();

        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');
            if (subField != null) {
                value.append(subField.getData());
            }

            subField = dataField.getSubfield('n');
            if (subField != null) {
                if (value.length() > 0) {
                    value.append(" ");
                }
                value.append(subField.getData());
            }

            subField = dataField.getSubfield('p');
            if (subField != null) {
                if (value.length() > 0) {
                    value.append(" ");
                }
                value.append(subField.getData());
            }

            if (value.length() > 0) {
                if (title.length() > 0) {
                    title.append(tokenize && TextUtils.endsInValidCharacter(title.toString()) ? " - " : " ");
                }
                title.append(value);
            }
        }

        if (!listAll && title.length() > 0) {
            return title.toString().trim();
        }

        dataField = MarcUtils.getDataField(record, MarcConstants.SECONDARY_INPUT_SERIAL_TITLE);
        value = new StringBuilder();

        if (dataField != null) {
            Subfield subField = dataField.getSubfield('a');
            if (subField != null) {
                value.append(subField.getData());
            }

            subField = dataField.getSubfield('v');
            if (subField != null) {
                if (value.length() > 0) {
                    value.append(" ");
                }
                value.append(subField.getData());
            }

            if (value.length() > 0) {
                if (title.length() > 0) {
                    title.append(tokenize && TextUtils.endsInValidCharacter(title.toString()) ? " - " : " ");
                }
                title.append(value);
            }
        
        }

        return title.toString().trim();
    }

    public static String listAuthors(Record record) {
        StringBuilder autores = new StringBuilder();
        autores.append(listPrimaryAuthor(record));
        
        String autSecond = listSecondaryAuthor(record);

        if (autSecond != null && autSecond.trim().length() != 0) {
            if (StringUtils.isNotBlank(autores.toString())) {
                autores.append(" | ");
            }
            autores.append(autSecond); 
        }
        
        return autores.toString();
    }

    public static String listFirstAuthors(Record record) {
        StringBuilder autores = new StringBuilder();
        autores.append(listPrimaryAuthor(record));

        return autores.toString();
    }

    public static String listPrimaryAuthor(Record record) {
        StringBuilder autor = new StringBuilder();

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.AUTHOR_PERSONAL_NAME);
        if (dataField != null && dataField.getSubfield('a') != null) {
            Subfield subfield = dataField.getSubfield('a');
            autor.append(subfield.getData()).append(" ");
        }

        dataField = MarcUtils.getDataField(record, MarcConstants.AUTHOR_CORPORATION_NAME);
        if (dataField != null && dataField.getSubfield('a') != null) {
            Subfield subfield = dataField.getSubfield('a');
            autor.append(subfield.getData()).append(" ");
        }

        dataField = MarcUtils.getDataField(record, MarcConstants.AUTHOR_CONGRESS_NAME);
        if (dataField != null && dataField.getSubfield('a') != null) {
            Subfield subfield = dataField.getSubfield('a');
            autor.append(subfield.getData());
        }

        return autor.toString().trim();
    }

    public static String listSecondaryAuthor(Record record) {
        StringBuilder builder = new StringBuilder();

        List<DataField> dataFields = MarcUtils.getDataFieldList(record, MarcConstants.SECONDARY_AUTHOR_PERSONAL_NAME);
        for (DataField dataField : dataFields) {
            if (dataField != null && dataField.getSubfield('a') != null) {
                Subfield subfield = dataField.getSubfield('a');
                builder.append(subfield.getData()).append(", ");
            }
        }

        dataFields = MarcUtils.getDataFieldList(record, MarcConstants.SECONDARY_AUTHOR_CORPORATION_NAME);
        for (DataField dataField : dataFields) {
            if (dataField != null && dataField.getSubfield('a') != null) {
                Subfield subfield = dataField.getSubfield('a');
                builder.append(subfield.getData()).append(", ");
            }
        }

        dataFields = MarcUtils.getDataFieldList(record, MarcConstants.SECONDARY_AUTHOR_CONGRESS_NAME);
        for (DataField dataField : dataFields) {
            if (dataField != null && dataField.getSubfield('a') != null) {
                Subfield subfield = dataField.getSubfield('a');
                builder.append(subfield.getData()).append(", ");
            }
        }

        String author = builder.toString();
        if (author.length() < 2) {
            return author;
        } else {
            return author.substring(0, author.length() - 2);
        }
    }

    public static String listAuthorOtherNames(Record record) {
        StringBuilder autor = new StringBuilder();

        List<DataField> dataFields = MarcUtils.getDataFieldList(record, MarcConstants.AUTHOR_OTHER_PERSONAL_NAMES);
        for (DataField dataField : dataFields) {
            if (dataField != null && dataField.getSubfield('a') != null) {
                Subfield subfield = dataField.getSubfield('a');
                autor.append(subfield.getData()).append(" ");
            }
        }

        dataFields = MarcUtils.getDataFieldList(record, MarcConstants.AUTHOR_OTHER_CORPORATION_NAMES);
        for (DataField dataField : dataFields) {
            if (dataField != null && dataField.getSubfield('a') != null) {
                Subfield subfield = dataField.getSubfield('a');
                autor.append(subfield.getData()).append(" ");
            }
        }

        dataFields = MarcUtils.getDataFieldList(record, MarcConstants.AUTHOR_OTHER_CONGRESS_NAMES);
        for (DataField dataField : dataFields) {
            if (dataField != null && dataField.getSubfield('a') != null) {
                Subfield subfield = dataField.getSubfield('a');
                autor.append(subfield.getData()).append(" ");
            }
        }

        return autor.toString();
    }

    public static String[] listLocation(Record record) {
        String localA = "";
        String localB = "";
        String localC = "";
        String localD = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.LOCATION);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield('a');
            localA = subfield != null ? subfield.getData() : localA;

            subfield = dataField.getSubfield('b');
            localB = subfield != null ? subfield.getData() : localB;

            subfield = dataField.getSubfield('c');
            localC = subfield != null ? subfield.getData() : localC;

            subfield = dataField.getSubfield('d');
            localD = subfield != null ? subfield.getData() : localD;
        }

        return new String[]{localA, localB, localC, localD};
    }

    public static String listFormattedLocation(Record record) {
        String[] loc = listLocation(record);
        StringBuilder builder = new StringBuilder();
        for (String data : loc) {
            if (StringUtils.isNotBlank(data)) {
                builder.append(data).append(" ");
            }
        }
        return builder.toString().trim();
    }


    public static String listAssetHolding(Record record) {
        Subfield subfield = MarcUtils.getSubfield(record, MarcConstants.ASSET_HOLDING, 'a');

        return subfield != null ? subfield.getData() : null;
    }

    public static String listYearOfPublication(Record record) {
        String year = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.PUBLICATION);
        if (dataField != null && dataField.getSubfield('c') != null) {
            year = dataField.getSubfield('c').getData();
        }

        return year;
    }

    public static String listEditor(Record record) {
        String editor = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.PUBLICATION);
        if (dataField != null && dataField.getSubfield('b') != null) {
            editor = dataField.getSubfield('b').getData();
        }

        return editor;
    }

    public static String[] listPublicationFull(Record record) {
        String placePublication = "";
        String namePublisher = "";
        String datePublication = "";
        String manufacturer = "";
        String miscellaneous = "";
        String dateManufacture = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.PUBLICATION);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield('a');
            placePublication = subfield != null ? subfield.getData() : placePublication;

            subfield = dataField.getSubfield('b');
            namePublisher = subfield != null ? subfield.getData() : namePublisher;

            subfield = dataField.getSubfield('c');
            datePublication = subfield != null ? subfield.getData() : datePublication;

            subfield = dataField.getSubfield('d');
            manufacturer = subfield != null ? subfield.getData() : manufacturer;

            subfield = dataField.getSubfield('e');
            miscellaneous = subfield != null ? subfield.getData() : miscellaneous;

            subfield = dataField.getSubfield('f');
            dateManufacture = subfield != null ? subfield.getData() : dateManufacture;
        }
        
        String[] resultado = {
            placePublication,
            namePublisher,
            datePublication,
            manufacturer,
            miscellaneous,
            dateManufacture
        };
        return resultado;
    }

    public static String listSubject(Record record) {
        StringBuilder subject = new StringBuilder();
        Subfield subfield;
        List<DataField> dataFieldList;

        for (String df : new String[]{
            MarcConstants.SUBJECT_TOPIC,
            MarcConstants.SUBJECT_PERSONAL_NAME,
            MarcConstants.SUBJECT_CORPORATE_NAME,
            MarcConstants.SUBJECT_EVENT,
            MarcConstants.SUBJECT_TITLE,
            MarcConstants.SUBJECT_GEOGRAPHIC_NAME,
            MarcConstants.ASSUNTO_TESAURO                
        }) {
            dataFieldList = MarcUtils.getDataFieldList(record, df);
            for (DataField dataField : dataFieldList) {
                if (dataField == null) {
                    continue;
                }

                for (Character sf : new Character[]{ 'a', 'x', 'y', 'z' }) {
                    subfield = dataField.getSubfield(sf);
                    if (subfield != null) {
                        subject.append(subfield.getData()).append(" ");
                    }
                }
            }
        }

        return subject.toString();
    }

    public static String listDDCN(Record record) {
        String classification = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.DDCN);
        if (dataField != null && dataField.getSubfield('a') != null) {
            Subfield subfield = dataField.getSubfield('a');
            classification = subfield.getData();
        }

        return classification;
    }

    public static String listEdition(Record record) {
        String edition = "";
        DataField dataField = MarcUtils.getDataField(record, MarcConstants.EDITION);
        if (dataField != null && dataField.getSubfield('a') != null) {
            Subfield subfield = dataField.getSubfield('a');
            edition = subfield.getData();
        }
        return edition;
    }

    public static String[] listSourceAcquisitionNotes(Record record) {
        String localA = "";
        String localB = "";
        String localC = "";
        String localD = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.SOURCE_ACQUISITION_NOTES);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield('a');
            localA = subfield != null ? subfield.getData() : localA;

            subfield = dataField.getSubfield('b');
            localB = subfield != null ? subfield.getData() : localB;

            subfield = dataField.getSubfield('c');
            localC = subfield != null ? subfield.getData() : localC;

            subfield = dataField.getSubfield('d');
            localD = subfield != null ? subfield.getData() : localD;
        }

        String[] resultado = {localA, localB, localC, localD};
        return resultado;
    }

    public static String listSourceAcquisitionDate(Record record) {
        String localD = "";
        DataField dataField = MarcUtils.getDataField(record, MarcConstants.SOURCE_ACQUISITION_NOTES);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield('d');
            localD = subfield != null ? subfield.getData() : localD;
        }
        return localD;
    }


    public static String list949(Record record) {
        String patrimonial = "";

        DataField dataField = MarcUtils.getDataField(record, MarcConstants.ASSET_HOLDING);
        if (dataField != null && dataField.getSubfield('a') != null) {
            Subfield subfield = dataField.getSubfield('a');
            patrimonial = subfield.getData();
        }
        
        return patrimonial;
    }

    public static List<String> listTagValues(Record record, String tag, char[] subfields) {
        List<String> wordsList = new ArrayList<String>();
        List<DataField> dataFieldList = MarcUtils.getDataFieldList(record, tag);
        for (DataField df : dataFieldList) {
            for (char sf : subfields) {
                Subfield subfield = df.getSubfield(sf);
                if (subfield != null) {
                    String data = subfield.getData();
                    if (StringUtils.isNotBlank(data)) {
                        wordsList.addAll(Arrays.asList(data.split(" ")));
                    }
                }
            }
        }
        return wordsList;
    }
}
