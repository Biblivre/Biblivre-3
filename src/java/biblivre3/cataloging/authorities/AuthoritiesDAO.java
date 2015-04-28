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

package biblivre3.cataloging.authorities;

import biblivre3.utils.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import mercury.DAO;
import mercury.ExceptionUser;

public class AuthoritiesDAO extends DAO {
    
    public final boolean insert(final AuthorityRecordDTO record) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO cataloging_authorities (record, created, modified) VALUES (?, ?, ?);";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, record.getIso2709());
            pst.setTimestamp(2, new Timestamp(record.getCreated().getTime()));
            pst.setTimestamp(3, new Timestamp(record.getModified().getTime()));
            final boolean result = pst.executeUpdate() > 0;
            if (result) {
                final String newSql = "SELECT record_serial FROM cataloging_authorities ORDER BY record_serial DESC LIMIT 1;";
                final ResultSet rs = con.createStatement().executeQuery(newSql);
                if (rs.next()) {
                    record.setRecordId(rs.getInt("record_serial"));
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public final boolean update(final AuthorityRecordDTO record) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "UPDATE cataloging_authorities SET record = ?, modified = ? WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, record.getIso2709());
            pst.setTimestamp(2, new Timestamp(record.getModified().getTime()));
            pst.setInt(3, record.getRecordId());

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return true;
    }
    
    public final boolean delete(final Collection<AuthorityRecordDTO> records) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM cataloging_authorities WHERE record_serial in ( ");

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

            for (Iterator<AuthorityRecordDTO> it = records.iterator(); it.hasNext(); i++) {
                final AuthorityRecordDTO dto = it.next();
                pst.setInt(i, dto.getRecordId());
            }
            
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return true;
    }

    public int count(String[] searchTerms) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM cataloging_authorities ");

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    if (i == 0) {
                        sql.append(" WHERE ");
                    } else {
                        sql.append(" AND ");
                    }
                    sql.append(" record_serial in (SELECT record_serial FROM idx_authorities WHERE index_word >= ? AND index_word < ?) ");
                }
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());
            int index = 1;

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    pst.setString(index++, searchTerms[i]);
                    pst.setString(index++, TextUtils.incrementLastChar(searchTerms[i]));
                }
            }
            
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    public ArrayList<AuthorityRecordDTO> search(String[] searchTerms, int offset, int limit) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT A.* FROM cataloging_authorities A ");
            sql.append("LEFT JOIN idx_sort_authorities S ON S.record_serial = A.record_serial ");

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    if (i == 0) {
                        sql.append(" WHERE ");
                    } else {
                        sql.append(" AND ");
                    }
                    sql.append(" A.record_serial in (SELECT record_serial FROM idx_authorities WHERE index_word >= ? AND index_word < ?) ");
                }
            }

            sql.append("ORDER BY S.index_word, A.record_serial ASC offset ? limit ?;");


            PreparedStatement pst = con.prepareStatement(sql.toString());
            int index = 1;

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    pst.setString(index++, searchTerms[i]);
                    pst.setString(index++, TextUtils.incrementLastChar(searchTerms[i]));
                }
            }

            pst.setInt(index++, offset);
            pst.setInt(index++, limit);

            final ResultSet rs = pst.executeQuery();
            ArrayList<AuthorityRecordDTO> recordDTOs = new ArrayList<AuthorityRecordDTO>();
            while (rs.next()) {
                AuthorityRecordDTO recordDTO = this.populateDto(rs);
                recordDTOs.add(recordDTO);
            }
            return recordDTOs;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }


    public ArrayList<AuthorityRecordDTO> list(int offset, int limit) {
        return this.search(null, offset, limit);
    }

    public int countAll() {
        return this.count(null);
    }

    public AuthorityRecordDTO getById(int serial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM cataloging_authorities WHERE record_serial = ?;";
            final PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, serial);

            final ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                return this.populateDto(rs);
            }

            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    private AuthorityRecordDTO populateDto(ResultSet rs) throws Exception {
        final AuthorityRecordDTO dto = new AuthorityRecordDTO();
        dto.setRecordId(rs.getInt("record_serial"));
        dto.setIso2709(rs.getString("record"));
        dto.setCreated(rs.getTimestamp("created"));
        dto.setModified(rs.getTimestamp("modified"));
        return dto;
    }

    public final boolean insertIdx(int recordId, String word) {
        boolean success = false;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO idx_authorities (record_serial, index_word) VALUES (?, ?); ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            pst.setString(2, word);

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return success;
    }

    public final boolean deleteIdx(int recordId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM idx_authorities WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return true;
    }

    public final boolean deleteAllIdx() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM idx_authorities;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return true;
    }

    public final boolean insertSortIdx(int recordId, String word) {
        boolean success = false;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO idx_sort_authorities (record_serial, index_word) VALUES (?, ?); ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            pst.setString(2, word);

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return success;
    }

    public final boolean deleteSortIdx(int recordId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM idx_sort_authorities WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return true;
    }

    public final boolean deleteAllSortIdx() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM idx_sort_authorities;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("AUTH_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return true;
    }
}
