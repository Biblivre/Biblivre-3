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

import java.io.Serializable;
import mercury.DTO;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  10/02/2009
 */
public class Z3950ServerDTO extends DTO implements Serializable {

    private Integer serverId;
    private String name;
    private String url;
    private Integer port;
    private String collection;
    private String charset;

    public Z3950ServerDTO() {
        this.name = "";
        this.url = "";
        this.port = 0;
        this.collection = "default";
        this.charset = "UTF-8";
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final Integer getPort() {
        return port;
    }

    public final void setPort(Integer port) {
        this.port = port;
    }

    public final Integer getServerId() {
        return serverId;
    }

    public final void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public final String getUrl() {
        return url;
    }

    public final void setUrl(String url) {
        this.url = url;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        if (collection != null) {
            this.collection = collection.trim();
        }
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        if (charset != null) {
            this.charset = charset.trim();
        }
    }
}