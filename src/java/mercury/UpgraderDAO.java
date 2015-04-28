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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class UpgraderDAO extends DAO {

    public HashMap<String, Boolean> getInstalledVersions() {
        Connection con = null;
        HashMap<String, Boolean> versions = new HashMap<String, Boolean>();

        try {
            con = getDataSource().getConnection();

            String sql = "SELECT * FROM versions;";
            PreparedStatement pst = con.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                versions.put(rs.getString("installed_versions"), true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }
        return versions;
    }

    public void insertVersion(String version) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();

            String sql = "INSERT INTO versions (installed_versions) VALUES (?);";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, version);

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }
    }

    public boolean v3_0_1_fixHoldingsDatabase() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            StringBuilder sqlHolding = new StringBuilder();
            sqlHolding.append("UPDATE cataloging_holdings SET database = B.database ");
            sqlHolding.append("FROM cataloging_biblio B ");
            sqlHolding.append("WHERE cataloging_holdings.record_serial = B.record_serial ");
            sqlHolding.append("AND cataloging_holdings.database <> B.database ");

            final PreparedStatement pstHolding = con.prepareStatement(sqlHolding.toString());
            pstHolding.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }
        return false;
    }

    public boolean v3_0_4_fixAuthoritiesSequence() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            final String sql = "SELECT coalesce(max(record_serial), 1) FROM cataloging_authorities;";

            final PreparedStatement pst = con.prepareStatement(sql);
            final ResultSet rs = pst.executeQuery();

            int lastSerial = 0;
            if (rs != null && rs.next()) {
                lastSerial = rs.getInt(1);
            }

            final String sql2 = "SELECT setval('authorities_record_serial_seq', ?, true);";

            final PreparedStatement pst2 = con.prepareStatement(sql2);
            pst2.setInt(1, lastSerial + 1);

            pst2.executeQuery();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }
        return false;
    }

    public boolean v3_0_9_removeInvalidLogins() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            final String sql = "DELETE FROM logins WHERE loginid not in (SELECT loginid FROM users WHERE loginid is not null) and lower(loginname) <> 'admin';";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.executeUpdate();

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }
        return false;
    }
}
