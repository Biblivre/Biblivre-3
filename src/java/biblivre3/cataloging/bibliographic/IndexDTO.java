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

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  17/02/2009
 */
public class IndexDTO {
    
    private Integer serial;
    private Integer recordSerial;
    private String word;

    public final Integer getRecordSerial() {
        return recordSerial;
    }

    public final void setRecordSerial(Integer recordSerial) {
        this.recordSerial = recordSerial;
    }

    public final Integer getSerial() {
        return serial;
    }

    public final void setSerial(Integer serial) {
        this.serial = serial;
    }

    public final String getWord() {
        return word;
    }

    public final void setWord(String word) {
        this.word = word;
    }
    
}