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

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public abstract class DAO {

    protected Logger log = Logger.getLogger(this.getClass());
    private DataSource ds;

    protected DAO() {}

    protected final DataSource getDataSource() {
        if (ds == null) {
            try {
                InitialContext cxt = new InitialContext();
                if (cxt == null) {
                    log.error("[DAO.constructor] InitialContext failed.");
                    throw new DAOException("InitialContext failed!");
                }
                ds = (DataSource) cxt.lookup("java:comp/env/jdbc/" + Config.getConfigProperty(ConfigurationEnum.DATABASE_NAME));
                if (ds == null) {
                    log.error("[DAO.constructor] Data Source not found.");
                    throw new DAOException("Data Source not found!");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new DAOException(e.getMessage());
            }
        }

        return ds;
    }

    protected final void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        }
    }

    protected final void rollBack(final Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        }
    }

    protected final void commit(final Connection con) {
        try {
            if (con != null) {
                con.commit();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        }
    }

    protected final java.sql.Date prepareDate(final java.util.Date date) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    protected final String sanitize(String s, String d) {
        if (s == null || s.trim().equals("")) {
            return d;
        }

        return s.trim();
    }

    public final Integer getNextSerial(String sequenceName) {
        String sql = "SELECT nextval('" + sequenceName + "') FROM " + sequenceName;
        Integer serial = 0;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getDataSource().getConnection();
            pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return serial;
        } catch (Exception e) {
            log.error(e);
            return serial;
        } finally {
            closeConnection(con);
        }
    }

}
