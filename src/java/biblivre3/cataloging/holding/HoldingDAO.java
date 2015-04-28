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

package biblivre3.cataloging.holding;

import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.circulation.UserDTO;
import biblivre3.enums.Availability;
import biblivre3.enums.Database;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import mercury.DAO;
import mercury.ExceptionUser;
import org.marc4j_2_3_1.marc.Record;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  09/03/2009
 * @maintenance Wilerson Lucas
 * @since 24/11/09
 */
public class HoldingDAO extends DAO {

    public final List<HoldingDTO> list(final RecordDTO record) {
        List<HoldingDTO> list = new ArrayList<HoldingDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder();
            
            sql.append("SELECT * FROM cataloging_holdings ");

            if (record != null && record.getRecordSerial() != null) {
                sql.append("WHERE record_serial = ? ");
            }

            //sql.append("ORDER BY COALESCE(CAST(SUBSTRING(loc_d FROM '([0-9]{1,10})$') AS INTEGER), 0), loc_d;");
            sql.append("ORDER BY asset_holding;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());

            if (record != null && record.getRecordSerial() != null) {
                pst.setInt(1, record.getRecordSerial());
            }

            final ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                try {
                    final HoldingDTO dto = this.populateDTO(rs);
                    list.add(dto);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return list;
    }

    public final List<HoldingDTO> list(Database database, int offset, int limit) {
        List<HoldingDTO> list = new ArrayList<HoldingDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder();
            final boolean filterDatabase = database != null;

            sql.append("SELECT * FROM cataloging_holdings ");

            if (filterDatabase) {
                sql.append(" WHERE database = ? ");
            }

            sql.append("ORDER BY record_serial, holding_serial offset ? limit ?;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;

            if (filterDatabase) {
                pst.setInt(i++, database.ordinal());
            }
            
            pst.setInt(i++, offset);
            pst.setInt(i++, limit);
            
            final ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                try {
                    final HoldingDTO dto = this.populateDTO(rs);
                    list.add(dto);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return list;
    }

    public final HoldingDTO getById(final Integer holdingId) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM cataloging_holdings WHERE holding_serial = ?;";

            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, holdingId);

            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return null;
    }

    public final HoldingDTO getByAsset(final String assetHolding) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM cataloging_holdings WHERE asset_holding = ?;";

            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setString(1, assetHolding);

            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return null;
    }

    public final boolean delete(final HoldingDTO holding) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM cataloging_holdings WHERE holding_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, holding.getSerial());

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return true;
    }

    public final boolean insert(final HoldingDTO holding) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO cataloging_holdings "
                             + "(record_serial, record, created, modified, database, holding_serial, availability, asset_holding, loc_d) "
                             + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, holding.getRecordSerial());
            pst.setString(2, holding.getIso2709());
            pst.setTimestamp(3, new Timestamp(holding.getCreated().getTime()));
            pst.setTimestamp(4, new Timestamp(holding.getModified().getTime()));
            pst.setInt(5, holding.getDatabase().ordinal());
            pst.setInt(6, holding.getSerial());
            pst.setInt(7, holding.getAvailability().ordinal());
            pst.setString(8, holding.getAssetHolding());
            pst.setString(9, holding.getLocationD());
            
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public final boolean update(final HoldingDTO holding) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            final String sql = "UPDATE cataloging_holdings "
                             + "SET record = ?, modified = ?, availability = ?, asset_holding = ?, loc_d = ? "
                             + "WHERE holding_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, holding.getIso2709());
            pst.setTimestamp(2, new Timestamp(holding.getModified().getTime()));
            pst.setInt(3, holding.getAvailability().ordinal());
            pst.setString(4, holding.getAssetHolding());
            pst.setString(5, holding.getLocationD());
            pst.setInt(6, holding.getSerial());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public final boolean isAssetAvailable(final String assetHolding, final int holdingId) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT count(*) FROM cataloging_holdings WHERE asset_holding = ? ");
            if (holdingId != 0) {
                sql.append("AND holding_serial <> ?;");
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setString(1, assetHolding);

            if (holdingId != 0) {
                pst.setInt(2, holdingId);
            }

            ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }

        return false;
    }

    public final int getNextAutomaticAsset(String prefix) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            String sql = "SELECT max(COALESCE(CAST(SUBSTRING(asset_holding FROM '([0-9]{1,10})$') AS INTEGER), 0)) as asset "
                       + "FROM cataloging_holdings WHERE asset_holding > ? and asset_holding < ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, prefix + "0");
            pst.setString(2, prefix + "99999999999999999999");

            ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                return rs.getInt("asset") + 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }

        return 0;
    }

    public final int getNextLocationD(final int recordSerial) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            String sql = "SELECT max(COALESCE(CAST(SUBSTRING(loc_d FROM '([0-9]{1,10})$') AS INTEGER), 0)) as loc "
                       + "FROM cataloging_holdings WHERE record_serial = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, recordSerial);

            ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                return rs.getInt("loc") + 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }

        return 0;
    }

    public final Integer getNextSerial() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT nextval('holdings_holding_serial_seq') FROM holdings_holding_serial_seq;";
            final ResultSet result = con.createStatement().executeQuery(sql);

            if (result.next()) {
                return result.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final Integer countHoldings(final int recordSerial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT COUNT(*) FROM cataloging_holdings WHERE record_serial = ?;";

            final PreparedStatement ppst = con.prepareStatement(sql);
            ppst.setInt(1, recordSerial);

            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final Integer countAll(Database database) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM cataloging_holdings ");

            if (database != null) {
                sql.append(" WHERE database = ? ");
            }

            final PreparedStatement pst = con.prepareStatement(sql.toString());

            if (database != null) {
                pst.setInt(1, database.ordinal());
            }

            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final Integer countAvailableHoldings(final int recordSerial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT COUNT(*) FROM cataloging_holdings "
                             + "WHERE record_serial = ?  AND availability = 0;";
            final PreparedStatement ppst = con.prepareStatement(sql);

            ppst.setInt(1, recordSerial);

            final ResultSet rs = ppst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    private HoldingDTO populateDTO(final ResultSet rs) throws SQLException, UnsupportedEncodingException, ParseException {
        final HoldingDTO dto = new HoldingDTO();

        dto.setSerial(rs.getInt("holding_serial"));
        dto.setRecordSerial(rs.getInt("record_serial"));
        dto.setIso2709(new String(rs.getBytes("record"), "UTF-8"));

        dto.setAssetHolding(rs.getString("asset_holding"));
        dto.setLocationD(rs.getString("loc_d"));

        dto.setCreated(rs.getTimestamp("created"));
        dto.setModified(rs.getTimestamp("modified"));

        final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
        dto.setLocation(Indexer.listLocation(record));

        final String database = rs.getString("database");
        Database db = Database.values()[Integer.valueOf(database)];
        dto.setDatabase(db);

        final Integer availability = rs.getInt("availability");
        final Availability avail = Availability.values()[availability];

        dto.setAvailability(avail);
        return dto;
    }

    public final boolean insertLabel(final LabelDTO ldto) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            String sql = "INSERT INTO labels "
                       + "(holding_serial, record_serial, asset_holding, author, title, location_a, location_b, location_c, location_d) "
                       + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
            final PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, ldto.getHoldingSerial());
            pst.setInt(2, ldto.getRecordSerial());
            pst.setString(3, ldto.getAssetHolding());
            pst.setString(4, ldto.getAuthor());
            pst.setString(5, ldto.getTitle());
            pst.setString(6, ldto.getLocationA());
            pst.setString(7, ldto.getLocationB());
            pst.setString(8, ldto.getLocationC());
            pst.setString(9, ldto.getLocationD());
            
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public final ArrayList<LabelDTO> listPendingLabels() {
        ArrayList<LabelDTO> listholdings = new ArrayList<LabelDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            String sql = "SELECT * FROM labels ORDER BY title, location_d ASC;";
            final ResultSet rs = con.createStatement().executeQuery(sql);

            while (rs != null && rs.next()) {
                LabelDTO ldto = new LabelDTO();
                ldto.setHoldingSerial(rs.getInt("holding_serial"));
                ldto.setRecordSerial(rs.getInt("record_serial"));
                ldto.setAssetHolding(rs.getString("asset_holding"));
                ldto.setAuthor(rs.getString("author"));
                ldto.setTitle(rs.getString("title"));
                ldto.setLocationA(rs.getString("location_a"));
                ldto.setLocationB(rs.getString("location_b"));
                ldto.setLocationC(rs.getString("location_c"));
                ldto.setLocationD(rs.getString("location_d"));

                listholdings.add(ldto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
            closeConnection(con);
        }

        return listholdings;
    }

    public final ArrayList<LabelDTO> listSelectedLabels(String[] labels) {
        ArrayList<LabelDTO> listholdings = new ArrayList<LabelDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();

            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM labels WHERE holding_serial in ( ");
            for (int i = 0; i < labels.length; i++) {
                if (i != (labels.length - 1)) {
                    sql.append("?,  ");
                } else {
                    sql.append("?");
                }
            }
            sql.append(") ORDER BY title, location_d;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int index = 1;

            for (String serial : labels) {
                pst.setInt(index++, Integer.parseInt(serial));
            }

            final ResultSet rs = pst.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    LabelDTO ldto = new LabelDTO();
                    ldto.setHoldingSerial(rs.getInt("holding_serial"));
                    ldto.setRecordSerial(rs.getInt("record_serial"));
                    ldto.setAssetHolding(rs.getString("asset_holding"));
                    ldto.setAuthor(rs.getString("author"));
                    ldto.setTitle(rs.getString("title"));
                    ldto.setLocationA(rs.getString("location_a"));
                    ldto.setLocationB(rs.getString("location_b"));
                    ldto.setLocationC(rs.getString("location_c"));
                    ldto.setLocationD(rs.getString("location_d"));
                    
                    listholdings.add(ldto);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
            closeConnection(con);
        }

        return listholdings;
    }

    public final boolean isLabelPending(int holdingId) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            String sql = "SELECT count(*) FROM labels WHERE holding_serial = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, holdingId);
            ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeConnection(con);
        }

        return false;
    }

    public final boolean updateHoldingCreationCounter(final UserDTO user) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final String sql = " INSERT INTO holding_creation_counter "
                             + " (creation_date, user_name, user_login, user_id) "
                             + " VALUES (localtimestamp, ?, ?, ?);";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, user.getName());
            pst.setString(2, String.valueOf(user.getLoginid()));
            pst.setInt(3, user.getUserid());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("HOLDINGDAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public final ArrayList<HoldingDTO> getHoldingByDate(final String startDate, final String endDate, final Database base) {
        ArrayList<HoldingDTO> listholdings = new ArrayList<HoldingDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM cataloging_holdings "
                             + "WHERE database = ? AND created BETWEEN to_date(?, 'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD') + 1 ORDER BY record_serial";

            PreparedStatement pst = con.prepareStatement(sql);


            pst.setInt(1, base.ordinal());
            pst.setString(2, startDate);
            pst.setString(3, endDate);
            ResultSet rs = pst.executeQuery();

            while (rs != null && rs.next()) {
                listholdings.add(this.populateDTO(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return listholdings;
    }

}
