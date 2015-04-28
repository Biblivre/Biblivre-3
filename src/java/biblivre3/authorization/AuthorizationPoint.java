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

package biblivre3.authorization;

import java.io.Serializable;

public class AuthorizationPoint implements Serializable {
    private String handler;
    private String submitButton;
    private AuthorizationPointTypes authPointType;
    private boolean allowed;

    public AuthorizationPoint(String handler, String submitButton, AuthorizationPointTypes authPointType, boolean allowed) {
        this.handler = handler;
        this.submitButton = submitButton;
        this.authPointType = authPointType;
        this.allowed = allowed;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public AuthorizationPointTypes getAuthPointType() {
        return authPointType;
    }

    public String getHandler() {
        return handler;
    }

    public String getSubmitButton() {
        return submitButton;
    }
}
