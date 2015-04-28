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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biblivre3.z3950;

import org.jzkit.z3950.server.Z3950Listener;

/**
 *
 * @author Danniel
 */
public class Z3950LocalServer {

    Z3950Listener listener;
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }

    public Z3950Listener getListener() {
        return listener;
    }

    public void setListener(Z3950Listener listener) {
        this.listener = listener;
    }

    public void startServer() {
        if (!active) {
            listener.start();
            setActive(!active);
        }
    }

    public void stopServer() {
        if (active) {
            listener.shutdown(0);
            setActive(!active);
        }
    }
    
}