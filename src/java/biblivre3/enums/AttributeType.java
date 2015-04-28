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

public enum AttributeType {
    
    AUTHOR("AUTHOR"),
    TITLE("TITLE"),
    YEAR("YEAR"),
    SUBJECT("SUBJECT"),
    ISBN("ISBN"),
    ISSN("ISSN"),
    SERIAL("SERIAL"),
    ANY("ANY");
    
    private String code;

    private AttributeType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static AttributeType getAttributeTypeByCode(final String code) {
        for (AttributeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
}
