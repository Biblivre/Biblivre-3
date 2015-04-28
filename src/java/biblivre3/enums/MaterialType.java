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

package biblivre3.enums;

public enum MaterialType {

    BOOK("BOOK", 'a', "m "),
    MANUSCRIPT("BOOKM", 't', "m "),
    PAMPHLET("BOOKP", 'a', "m "),
    THESIS("BOOKT", 'a', "m "),
    COMPUTER_LEGIBLE("CFILES", 'm', "m "),
    CARTOGRAPHIC("MAPS", 'e', "m "),
    MOVIE("MOVIE", 'p', "m "),
    SCORE("MUSIC", 'c', "m "),
    OBJECT_3D("OBJ3D", 'r', "m "),
    ICONOGRAPHIC("PHOTO", 'k', "m "),
    PERIODIC("SERIAL", 'a', "s "),
    ARTICLES("SERIAR", 'a', "b "),
    MUSIC("SOUND", 'j', "m "),
    AUTHORITIES("AUTHORITIES", 'z', "  "),
    VOCABULARY("VOCABULARY", 'w', "  "),
    HOLDINGS("HOLDINGS", 'u', "  "),
    ALL("ALL", 'a', "m ");
    
    private String code;
    private char typeOfRecord;
    private String implDefined1;

    private MaterialType(String code, char typeOfRecord, String implDef1) {
        this.code = code;
        this.typeOfRecord = typeOfRecord;
        this.implDefined1 = implDef1;
    }
    
    public final String getCode() {
        return this.code;
    }
    
    public final char getTypeOfRecord() {
        return this.typeOfRecord;
    }
    
    public final String getImplDefined1() {
        return this.implDefined1;
    }
    
    public static MaterialType getByCode(final String code) {
        if (code == null) {
            return null;
        }

        for (MaterialType type : values()) {
            if (type.getCode().toUpperCase().equals(code.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    public static MaterialType getByTypeAndImplDef(char typeOfRecord, char[] implDef1) {
        for (MaterialType type : values()) {
            if ( type.getTypeOfRecord() == typeOfRecord && type.getImplDefined1().equals(String.valueOf(implDef1)) ) {
                return type;
            }
        }
        return MaterialType.BOOK;
    }
    
}
