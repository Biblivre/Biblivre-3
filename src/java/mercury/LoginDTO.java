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

import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

public final class LoginDTO implements Serializable {

    private int loginId;
    private String firstName;
    private String loginName;
    private String encpwd;
    private String plainpwd;

    public String getPlainpwd() {
        return plainpwd;
    }

    public void setPlainpwd(String plainpwd) {
        this.plainpwd = plainpwd;
    }

    public String getEncpwd() {
        return encpwd;
    }

    public void setEncpwd(String encpwd) {
        this.encpwd = encpwd;
    }

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!StringUtils.isBlank(firstName)) {
            String[] split = firstName.split(" ");
            if (split != null && split.length > 0) {
                this.firstName = split[0];
            } else {
                this.firstName = firstName;
            }
        }
    }

}
