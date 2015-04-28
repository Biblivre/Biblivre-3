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

/**
 * This class holds the constants related to Marc records used throughout the
 * system.
 *
 * @author Danniel Nascimento
 * @since 07/10/2008
 */
public final class MarcConstants {

    /**
     *  Private constructor to avoid instantiation.
     */
    private MarcConstants(){};
    
    public static final String INFORMACOES_GERAIS = "008";
    
    /** 
     * Value = "260"
     * editor: subfield b;
     * year of publication: subfield c;
     */
    public static final String PUBLICATION = "260"; 
    public static final String EDITION = "250";
    public static final String COLLECTIVE_UNIFORM_TITLE = "243";
    public static final String FILE_INFO = "256";
    public static final String PRODUCTION_PLACE = "257";
    public static final String MATERIAL_INFO = "258";
    public static final String ISBN = "020";
    public static final String ISSN = "022";
    public static final String ISRC = "024";
    public static final String LANGUAGE = "041";
    //082 - Dewey Decimal Classification Number (R)
    public static final String UDCN = "080";
    public static final String DDCN = "082";
    public static final String LOCATION = "090";
    public static final String CNPQ = "095";
    public static final String PATENT = "013";
    
    /** Value = "245" 
     *  title: subfield a;
     *  subtitle: subfield b;
     */
    public static final String TITLE = "245";
    public static final String DATA_AUTOR = "100";
    public static final String AUTHOR_PERSONAL_NAME = "100";
    public static final String AUTHOR_CORPORATION_NAME = "110";
    public static final String AUTHOR_CONGRESS_NAME = "111";
    public static final String AUTHOR_OTHER_PERSONAL_NAMES = "400";
    public static final String AUTHOR_OTHER_CORPORATION_NAMES = "410";
    public static final String AUTHOR_OTHER_CONGRESS_NAMES = "411";
    
    public static final String ANONIMOUS = "130";
    public static final String UNIFORM_TITLE = "240";

    /* Indicação de série */
    public static final String SERIES = "490";
    
    public static final String NONPUBLIC_GENERAL_NOTE = "667";
    public static final String SOURCE_DATA_NOT_FOUND = "675";
    public static final String BIOGRAPHICAL_HISTORICAL_DATA = "678";
    public static final String PUBLIC_GENERAL_NOTE = "680";
    
    public static final String SECONDARY_AUTHOR_PERSONAL_NAME = "700";
    public static final String SECONDARY_AUTHOR_CORPORATION_NAME = "710";
    public static final String SECONDARY_AUTHOR_CONGRESS_NAME = "711";

    public static final String SUBJECT_PERSONAL_NAME = "600";
    public static final String SUBJECT_CORPORATE_NAME = "610";
    public static final String SUBJECT_EVENT = "611";
    public static final String SUBJECT_TITLE = "630";
    public static final String SUBJECT_TOPIC = "650";
    public static final String DATA_SOURCE = "670";
    public static final String SUBJECT_GEOGRAPHIC_NAME = "651";
    public static final String ASSUNTO_TESAURO = "699";
    
    public static final String LINK = "856"; // link para o arquivo digital do texto - subcampo u;
    public static final String NOTES_PUBLIC = "852"; // link para o arquivo digital do texto - subcampo u;

    public static final String HOLDING_BIBLIO_REFERENCE = "004";    
    public static final String ASSET_HOLDING = "949";
    public static final String ANY = "any";
    
    /** Value = "310" */
    public static final String PERIODICITY = "310";

    /** Value = "362" */
    public static final String FIRST_PUBLICATION_DATE = "362";
    
    /** Value = "555" */
    public static final String UNITS = "555";
    
    /** Value = "500" */
    public static final String NOTES_GENERAL = "500";
    
    /** Value = "504" */
    public static final String NOTES_THESIS = "502";

    /** Value = "504" */
    public static final String NOTES_BIBLIOGRAFIC = "504";

    /** Value = "505" */
    public static final String NOTES_CONTENT = "505";

    /** Value = "506" */
    public static final String NOTES_RESTRICT_ACCESS = "506";

    /** Value = "876" */
    public static final String NOTES_HOLDING_RESTRICT_ACCESS = "876";


    /** Value = "520" */
    public static final String NOTES_SUMMARY = "520";
    
    /** Value = "521" */
    public static final String NOTES_TARGET_PUBLIC = "521";
    
    /** Value = "534" */
    public static final String NOTES_FACSIMILE = "534";

    /* Notas sobre a fonte de aquisição */
    public static final String SOURCE_ACQUISITION_NOTES= "541";
    
    /** Value = "590" */
    public static final String NOTES_LOCAL = "590";
    
    /** Value = "300" */
    public static final String FORMAT = "300";

    /* Entrada Secundária - Série - Título */
    public static final String SECONDARY_INPUT_SERIAL_TITLE = "830";

    public static final String LENGTH = "306";

    public static final String GEO_CODE = "043";
    public static final String CHRONO_CODE = "045";
    public static final String SCALE = "255";


    public static final String THESAURUS_TE = "150";
    public static final String THESAURUS_UP = "450";
    public static final String THESAURUS_TG = "550";
    public static final String THESAURUS_VT_TA_TR = "360";
    public static final String THESAURUS_SCOPE_NOTES = "680";
    public static final String THESAURUS_HISTORY_NOTES = "685";

    public static final String ADDED_UNIFORM_TITLE = "730";
    public static final String ADDED_ANALYTICAL_TITLE = "740";
}
