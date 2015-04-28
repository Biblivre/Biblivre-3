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

public enum IndexTable {

    AUTHOR("idx_author"),
    TITLE("idx_title"),
    SUBJECT("idx_subject"),
    ISBN("idx_isbn"),
    ISSN("idx_isbn"),
    YEAR("idx_year"),
    ANY("idx_any"),
    SORT("idx_sort_biblio");

    private IndexTable(String tableName) {
        this.tableName = tableName;
    }
    
    private String tableName;
    
    public final String getTableName() {
        return this.tableName;
    }

}
