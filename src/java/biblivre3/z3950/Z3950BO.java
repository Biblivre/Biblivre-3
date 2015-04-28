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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mercury.BaseBO;
import org.jzkit.z3950.server.Z3950Listener;
import org.marc4j_2_3_1.marc.Record;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  10/02/2009
 */
public class Z3950BO extends BaseBO {

    private static ApplicationContext context;
    private static Z3950LocalServer server;
    private Z3950DAO dao;

    public Z3950BO() {
        dao = new Z3950DAO();
        this.getContext();
    }

    public final Boolean getServerStatus() {
        return server != null ? server.isActive() : false;
    }

    public final boolean startServer() {
        if (server == null) {
            server = new Z3950LocalServer();
            Z3950Listener listener = new Z3950Listener();
            listener.setBackendBeanName("backend");
            listener.setDefault("default");
            listener.setPort(2200);
            listener.setApplicationContext(this.getContext());
            server.setListener(listener);
        }
        if (!server.isActive()) {
            server.startServer();
        }
        return server.isActive();
    }

    public final boolean stopServer() {
        if (server != null) {
            server.stopServer();
            server = null;
        }
        return false; //False here means that the server is inactive
    }

    public final Map<Z3950ServerDTO, List<Record>> doSearch(final List<Z3950ServerDTO> servers, final Z3950SearchDTO dto) {
        Z3950Client z3950Client = (Z3950Client)getContext().getBean("z3950Client");
        Map<Z3950ServerDTO, List<Record>> recordMap = new HashMap<Z3950ServerDTO, List<Record>>();

        for (Z3950ServerDTO searchServer : servers) {
            try {
                List<Record> recordList = z3950Client.doSearch(searchServer, dto);
                if (recordList != null && !recordList.isEmpty()) {
                    recordMap.put(searchServer, recordList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return recordMap;
    }

    public final List<Z3950ServerDTO> listServers() {
        return dao.listServers();
    }

    public final boolean insert(Z3950ServerDTO dto) {
        return dao.insert(dto);
    }

    public final boolean update(Z3950ServerDTO dto) {
        return dao.update(dto);
    }

    public final boolean delete(Z3950ServerDTO dto) {
        return dao.delete(dto);
    }

    public final Z3950ServerDTO findById(Z3950ServerDTO dto) {
        return dao.findById(dto);
    }

    public final Z3950ServerDTO findById(String id) {
        Z3950ServerDTO dto = new Z3950ServerDTO();
        dto.setServerId(Integer.parseInt(id));
        return dao.findById(dto);
    }

    private ApplicationContext getContext() {
        if (context == null) {
            try {
                context = new ClassPathXmlApplicationContext("applicationContext.xml");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return context;
    }

}
