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

package biblivre3.cataloging.vocabulary;

import biblivre3.utils.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import mercury.DAO;
import mercury.DAOException;

public class VocabularyDAO extends DAO {

    public boolean insert(VocabularyDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = "INSERT INTO cataloging_vocabulary (record_serial, record, created, modified) " +
                                     "VALUES (?, ?, ?, ?);";

            PreparedStatement pst = conInsert.prepareStatement(sqlInsert);
            pst.setInt(1, dto.getSerial());
            pst.setString(2, dto.getIso2709());
            pst.setTimestamp(3, new Timestamp(dto.getCreated().getTime()));
            pst.setTimestamp(4, new Timestamp(dto.getModified().getTime()));

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean update(VocabularyDTO dto) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sqlInsert = "UPDATE cataloging_vocabulary SET record = ?, modified = ? WHERE record_serial = ?; ";

            PreparedStatement pst = con.prepareStatement(sqlInsert);
            pst.setString(1, dto.getIso2709());
            pst.setTimestamp(2, new Timestamp(dto.getModified().getTime()));
            pst.setInt(3, dto.getSerial());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public boolean delete(VocabularyDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = "DELETE FROM cataloging_vocabulary WHERE record_serial = ?; ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, dto.getSerial());

            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public int count(String[] searchTerms, String controlfield) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT count(*) FROM cataloging_vocabulary ");

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    if (i == 0) {
                        sql.append(" WHERE ");
                    } else {
                        sql.append(" AND ");
                    }
                    sql.append(" record_serial in (SELECT record_serial FROM idx_vocabulary WHERE control_field = ? AND index_word >= ? AND index_word < ?) ");
                }
            }

            final PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    pst.setString(index++, controlfield);
                    pst.setString(index++, searchTerms[i]);
                    pst.setString(index++, TextUtils.incrementLastChar(searchTerms[i]));
                }
            }

            final ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }


    public ArrayList<VocabularyDTO> search(String[] searchTerms, String controlfield, int offset, int limit) {
        ArrayList<VocabularyDTO> supplierList = new ArrayList<VocabularyDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();


            StringBuilder sql = new StringBuilder();
            sql.append("SELECT V.* FROM cataloging_vocabulary V ");
            sql.append("LEFT JOIN idx_sort_vocabulary S ON S.record_serial = V.record_serial ");

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    if (i == 0) {
                        sql.append(" WHERE ");
                    } else {
                        sql.append(" AND ");
                    }
                    sql.append(" V.record_serial in (SELECT record_serial FROM idx_vocabulary WHERE control_field = ? AND index_word >= ? AND index_word < ?) ");
                }
            }

            sql.append("ORDER BY S.index_word, V.record_serial ASC offset ? limit ?;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            
            int index = 1;

            if (searchTerms != null) {
                for (int i = 0; i < searchTerms.length; i++) {
                    pst.setString(index++, controlfield);
                    pst.setString(index++, searchTerms[i]);
                    pst.setString(index++, TextUtils.incrementLastChar(searchTerms[i]));
                }
            }

            pst.setInt(index++, offset);
            pst.setInt(index++, limit);

            final ResultSet rs = pst.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    VocabularyDTO dto = this.populateDto(rs);
                    supplierList.add(dto);
                }
            }

            return supplierList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public ArrayList<VocabularyDTO> list(int offset, int limit) {
        return this.search(null, null, offset, limit);
    }

    public int countAll() {
        return this.count(null, null);
    }

    public VocabularyDTO getById(Integer id) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = "SELECT * FROM cataloging_vocabulary WHERE record_serial = ?;" ;

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            pst.setInt(1, id);
            final ResultSet rs = pst.executeQuery();
            
            if (rs != null && rs.next()) {
                return this.populateDto(rs);
            }

            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    private VocabularyDTO populateDto(ResultSet rs) throws Exception {
        final VocabularyDTO dto = new VocabularyDTO();
        dto.setSerial(rs.getInt("record_serial"));
        dto.setIso2709(new String(rs.getBytes("record"), "UTF-8"));
        dto.setCreated(rs.getTimestamp("created"));
        dto.setModified(rs.getTimestamp("modified"));
        return dto;
    }


    public boolean insertIdx(int recordId, String word, String cf) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = "INSERT INTO idx_vocabulary (index_word, record_serial, control_field) VALUES (?, ?, ?);";

            PreparedStatement pst = conInsert.prepareStatement(sqlInsert);
            pst.setString(1, word);
            pst.setInt(2, recordId);
            pst.setString(3, cf);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean deleteIdx(Integer recordId) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = "DELETE FROM idx_vocabulary WHERE record_serial = ?;";

            PreparedStatement pst = conInsert.prepareStatement(sqlInsert);
            pst.setInt(1, recordId);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }

        return true;
    }

    public boolean deleteAllIdx() {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = "DELETE FROM idx_vocabulary;";

            PreparedStatement pst = conInsert.prepareStatement(sqlInsert);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }

        return true;
    }

    public final boolean insertSortIdx(int recordId, String word) {
        boolean success = false;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO idx_sort_vocabulary (record_serial, index_word) VALUES (?, ?); ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);
            pst.setString(2, word);

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return success;
    }

    public final boolean deleteSortIdx(int recordId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM idx_sort_vocabulary WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, recordId);

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return true;
    }

    public final boolean deleteAllSortIdx() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "DELETE FROM idx_sort_vocabulary;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return true;
    }
}