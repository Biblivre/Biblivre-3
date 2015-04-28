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

package biblivre3.administration.permission;

import biblivre3.circulation.UserDTO;
import java.util.List;
import mercury.BaseBO;

public class PermissionBO extends BaseBO {

    private PermissionDAO dao;

    public PermissionBO() {
        try {
            dao = new PermissionDAO();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean save(Integer loginid, String[] permissions) {
        boolean success = dao.deletePermissions(loginid);

        if (permissions != null) {
            for (String permission : permissions) {
                success &= dao.insert(loginid, permission);
            }
        }
        
        return success;
    }

    public List<String> getByLoginId(Integer loginid) {
        return dao.getByLoginId(loginid);
    }

    public final boolean findLoginName(String loginName) {
        return dao.findLoginName(loginName);
    }

    public void createLogin(UserDTO userDTO, String loginName, String password) {
        dao.createLogin(userDTO, loginName, password);
    }

    public void updatePassword(UserDTO userDTO, String loginName, String password) {
        dao.updatePassword(userDTO, loginName, password);
    }

    public void removeLogin(UserDTO userDTO) {
        dao.deletePermissions(userDTO.getLoginid());
        dao.removeLogin(userDTO);
    }
}
