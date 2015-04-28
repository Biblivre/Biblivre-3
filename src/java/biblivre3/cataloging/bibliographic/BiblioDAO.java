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

package biblivre3.cataloging.bibliographic;

import biblivre3.enums.Database;
import biblivre3.enums.MaterialType;
import biblivre3.utils.TextUtils;
import java.io.UnsupportedEncodingException;
import mercury.ExceptionUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import mercury.DAO;

public class BiblioDAO extends DAO {

    public boolean insert(final RecordDTO record, final Database base) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = " INSERT INTO cataloging_biblio " +
                               " (record_serial, record, created, modified, material_type, database) " +
                               " VALUES (?, ?, ?, ?, ?, ?);";

            final PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, record.getRecordSerial());
            pst.setString(2, record.getIso2709());
            pst.setTimestamp(3, new Timestamp(record.getCreated().getTime()));
            pst.setTimestamp(4, new Timestamp(record.getModified().getTime()));
            pst.setString(5, record.getMaterialType().getCode());
            pst.setInt(6, base.ordinal());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public boolean update(final RecordDTO record) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = 
                    " UPDATE cataloging_biblio SET record = ?, modified = ?, " +
                    " material_type = ? WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, record.getIso2709());
            pst.setTimestamp(2, new Timestamp(record.getModified().getTime()));
            pst.setString(3, record.getMaterialType().getCode());
            pst.setInt(4, record.getRecordSerial());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public boolean delete(final Collection<RecordDTO> records) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            
            final StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM cataloging_biblio WHERE record_serial in ( ");
            for (int i = 0; i < records.size(); i++) {
                if (i != (records.size() - 1)) {
                    sql.append("?,  ");
                } else {
                    sql.append("?");
                }
            }
            sql.append(");");

            final PreparedStatement pst = con.prepareStatement(sql.toString());

            int i = 1;
            for (Iterator<RecordDTO> it = records.iterator(); it.hasNext(); i++) {
                final RecordDTO dto = it.next();
                pst.setInt(i, dto.getRecordSerial());
            }

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public final boolean delete(final RecordDTO record) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM cataloging_biblio WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, record.getRecordSerial());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }



    public Integer count(final Database base, final MaterialType type, final String whereClause, final List<String> values) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final boolean filterMaterialType = type != null && !type.equals(MaterialType.ALL);
            final boolean filterClause = whereClause != null && !whereClause.isEmpty();
            final boolean filterDatabase = base != null;

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(R.*) as total FROM cataloging_biblio R ");
            if (filterDatabase || filterMaterialType || filterClause) {
                sql.append(" WHERE 1 = 1 ");
            }

            if (filterClause) {
                sql.append(" AND ").append(whereClause);
            }

            if (filterMaterialType) {
                sql.append(" AND R.material_type = ? ");
            }

            if (filterDatabase) {
                sql.append(" AND R.database = ? ");
            }

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int index = 1;

            if (filterClause) {
                for (int i = 0; i < values.size(); i++) {
                    pst.setString(index++, values.get(i));
                    pst.setString(index++, TextUtils.incrementLastChar(values.get(i)));
                }
            }

            if (filterMaterialType) {
                pst.setString(index++, type.getCode());
            }

            if (filterDatabase) {
                pst.setInt(index++, base.ordinal());
            }

            final ResultSet result = pst.executeQuery();

            if (result.next()) {
                return result.getInt("total");
            }
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public int countAll(final Database base) {
        return this.count(base, null, null, null);
    }

    public int countAll(final Database base, final MaterialType type) {
        return this.count(base, type, null, null);
    }

    public ArrayList<RecordDTO> search(Database base, MaterialType type, String whereClause, List<String> values, int offset, int limit, boolean ignoreSort) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final boolean filterMaterialType = type != null && !type.equals(MaterialType.ALL);
            final boolean filterClause = whereClause != null && !whereClause.isEmpty();
            final boolean filterDatabase = base != null;

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT R.* FROM cataloging_biblio R ");
            
            if (!ignoreSort) {
                sql.append("LEFT JOIN idx_sort_biblio S ON S.record_serial = R.record_serial ");
            }
            
            if (filterDatabase || filterMaterialType || filterClause) {
                sql.append(" WHERE 1 = 1 ");
            }

            if (filterClause) {
                sql.append(" AND ").append(whereClause);
            }

            if (filterMaterialType) {
                sql.append(" AND R.material_type = ? ");
            }

            if (filterDatabase) {
                sql.append(" AND R.database = ? ");
            }

            if (ignoreSort) {
                sql.append("ORDER BY R.record_serial ASC offset ? limit ?;");
            } else {
                sql.append("ORDER BY S.index_word, R.record_serial ASC offset ? limit ?;");
            }

            final PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;

            if (filterClause) {
                for (int i = 0; i < values.size(); i++) {
                    pst.setString(index++, values.get(i));
                    pst.setString(index++, TextUtils.incrementLastChar(values.get(i)));
                }
            }

            if (filterMaterialType) {
                pst.setString(index++, type.getCode());
            }

            if (filterDatabase) {
                pst.setInt(index++, base.ordinal());
            }

            pst.setInt(index++, offset);
            pst.setInt(index++, limit);

            ResultSet rs = pst.executeQuery();
            ArrayList<RecordDTO> records = new ArrayList<RecordDTO>();

            if (rs != null) {
                while (rs.next()) {
                    records.add(this.populateDTO(rs));
                }
            }

            return records;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
            updateSearchCounter();
        }
    }

    public Object[] searchFromZ3950(String whereClause, List<String> values, int offset, int limit) {
        Connection con = null;
        Object [] cr = new Object[2];
        try {
            con = getDataSource().getConnection();
            final boolean filterClause = whereClause != null && !whereClause.isEmpty();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT record FROM cataloging_biblio R ");
            sql.append("LEFT JOIN idx_sort_biblio S ON S.record_serial = R.record_serial ");
            sql.append(" WHERE R.database = ? ");

            if (filterClause) {
                sql.append(" AND ").append(whereClause);
            }

            sql.append("ORDER BY S.index_word, R.record_serial ASC offset ? limit ?;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;

            pst.setInt(index++, Database.MAIN.ordinal());

            if (filterClause) {
                for (int i = 0; i < values.size(); i++) {
                    pst.setString(index++, values.get(i));
                    pst.setString(index++, TextUtils.incrementLastChar(values.get(i)));
                }
            }

            pst.setInt(index++, offset);
            pst.setInt(index++, limit);

            ResultSet rs = pst.executeQuery();

            cr[0] = con;
            cr[1] = rs;

            return cr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
        }
    }

    public ArrayList<RecordDTO> list(final Database base, final MaterialType type, final int offset, final int limit, final boolean ignoreSort) {
        return this.search(base, type, null, null, offset, limit, ignoreSort);
    }

    public final RecordDTO searchByAssetHolding(final String assetHolding, final Database database) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();

            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT R.* FROM cataloging_biblio R INNER JOIN cataloging_holdings H ");
            sql.append("ON H.record_serial = R.record_serial WHERE H.asset_holding = ? and H.database = ?;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setString(1, assetHolding);
            pst.setInt(2, database.ordinal());

            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return null;
    }

    public final RecordDTO searchByHoldingSerial(final int holdingSerial, final Database database) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();

            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT R.* FROM cataloging_biblio R INNER JOIN cataloging_holdings H ");
            sql.append("ON H.record_serial = R.record_serial WHERE H.holding_serial = ? and H.database = ?;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setInt(1, holdingSerial);
            pst.setInt(2, database.ordinal());

            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return this.populateDTO(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return null;
    }


    public Integer getNextSerial() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            final String sql = "SELECT nextval('cataloging_biblio_record_serial_seq') FROM cataloging_biblio_record_serial_seq;";

            final ResultSet result = con.createStatement().executeQuery(sql);
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public RecordDTO getById(final int serial) {
        Connection con = null;
        RecordDTO dto = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM cataloging_biblio WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, serial);

            final ResultSet result = pst.executeQuery();
            if (result.next()) {
                dto = this.populateDTO(result);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public final Collection<RecordDTO> getById(final String[] ids) {
        Connection con = null;
        final Collection<RecordDTO> list = new ArrayList<RecordDTO>();
        try {
            con = getDataSource().getConnection();

            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM cataloging_biblio WHERE record_serial in ( ");
            for (int i = 0; i < ids.length; i++) {
                if (i != (ids.length - 1)) {
                    sql.append("?,  ");
                } else {
                    sql.append("?");
                }
            }
            sql.append(");");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            for (int i = 1; i <= ids.length; i++) {
                final Integer serial = Integer.valueOf(ids[i - 1]);
                pst.setInt(i, serial);
            }
            final ResultSet result = pst.executeQuery();
            while (result.next()) {
                list.add(this.populateDTO(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return list;
    }

    public final Boolean moveRecords(String[] ids, Integer toDatabase) {
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            StringBuilder sqlBiblio = new StringBuilder("UPDATE cataloging_biblio SET ");
            sqlBiblio.append("database = ? ");
            sqlBiblio.append("WHERE record_serial in ( ");
            for (int i = 0; i < ids.length; i++) {
                if (i != (ids.length - 1)) {
                    sqlBiblio.append("?,  ");
                } else {
                    sqlBiblio.append("?");
                }
            }
            sqlBiblio.append(");");
            final PreparedStatement pstBiblio = con.prepareStatement(sqlBiblio.toString());
            int biblioCounter = 1;
            pstBiblio.setInt(biblioCounter++, toDatabase);
            for (String id : ids) {
                pstBiblio.setInt(biblioCounter++, Integer.valueOf(id));
            }
            pstBiblio.executeUpdate();

            StringBuilder sqlHolding = new StringBuilder();
            sqlHolding.append("UPDATE cataloging_holdings SET database = B.database ");
            sqlHolding.append("FROM cataloging_biblio B ");
            sqlHolding.append("WHERE cataloging_holdings.record_serial = B.record_serial ");
            sqlHolding.append("AND cataloging_holdings.database <> B.database ");

            final PreparedStatement pstHolding = con.prepareStatement(sqlHolding.toString());
            pstHolding.executeUpdate();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return true;
    }

    public final Boolean moveAllRecords(MaterialType mt, Integer toDatabase) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            StringBuilder sqlBiblio = new StringBuilder("UPDATE cataloging_biblio SET database = ? ");
            if (mt != MaterialType.ALL) {
                sqlBiblio.append(" WHERE material_type = ?; ");
            }
            final PreparedStatement pstBiblio = con.prepareStatement(sqlBiblio.toString());
            pstBiblio.setInt(1, toDatabase);

            if (mt != MaterialType.ALL) {
                pstBiblio.setString(2, mt.getCode());
            }

            pstBiblio.executeUpdate();

            StringBuilder sqlHolding = new StringBuilder();
            sqlHolding.append(" UPDATE cataloging_holdings SET database = B.database ");
            sqlHolding.append(" FROM cataloging_biblio B ");
            sqlHolding.append(" WHERE cataloging_holdings.record_serial = B.record_serial ");
            sqlHolding.append(" AND cataloging_holdings.database <> B.database ");

            final PreparedStatement pstHolding = con.prepareStatement(sqlHolding.toString());
            pstHolding.executeUpdate();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return true;
    }


    public final String createIndexClause(final String table) {
        return " R.record_serial in (SELECT record_serial FROM " + table + " WHERE index_word >= ? and index_word < ?) ";
    }

    public final String createSerialClause(String serial) {
        if (!TextUtils.isInteger(serial)) {
            serial = "0";
        }

        return " R.record_serial = " + serial + " ";
    }

    public boolean deleteLabels(String[] labels) {
        boolean deleted = false;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM labels WHERE holding_serial = ?;";
            PreparedStatement pst = con.prepareStatement(sql);

            for (String i : labels) {
                pst.setInt(1, Integer.parseInt(i));
                pst.executeUpdate();

                deleted = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return deleted;
    }

    public boolean deleteAllLabels() {
        boolean deleted = false;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM labels;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.executeUpdate();
            deleted = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return deleted;
    }

    public void updateSearchCounter() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO search_counter (search_date) values (localtimestamp);";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    private RecordDTO populateDTO(final ResultSet rs) throws SQLException, UnsupportedEncodingException {
        final RecordDTO dto = new RecordDTO();
        dto.setRecordSerial(rs.getInt("record_serial"));
        dto.setIso2709(new String(rs.getBytes("record"), "UTF-8"));

        dto.setCreated(rs.getTimestamp("created"));
        dto.setModified(rs.getTimestamp("modified"));

        final String materialType = rs.getString("material_type");
        final MaterialType type = MaterialType.getByCode(materialType);
        dto.setMaterialType(type);
        
        return dto;
    }
}

