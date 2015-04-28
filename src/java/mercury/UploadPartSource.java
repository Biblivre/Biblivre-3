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

package mercury;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.methods.multipart.PartSource;

public class UploadPartSource implements PartSource {

    long length;
    String fileName;
    InputStream stream;

    public UploadPartSource(String fileName, long length, InputStream stream) {
        this.fileName = fileName;
        this.length = length;
        this.stream = stream;
    }

    public long getLength() {
        return this.length;
    }

    public String getFileName() {
        return this.fileName;
    }

    public InputStream createInputStream() throws IOException {
        return this.stream;
    }
}
