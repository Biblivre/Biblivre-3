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

package biblivre3.acquisition.request;

import mercury.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

public class RequestDAO extends DAO {

    public static final int REQUEST_PENDING = 1;
    public static final int REQUEST_CLOSED = 2;
    public static final int REQUEST_BOTH = 3;

    public boolean insertRequest(RequestDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " INSERT INTO acquisition_requisition ( " +
                    " requisition_date, responsable, author_type, " +
                    " author, num_prename, author_title, item_title, item_subtitle, " +
                    " edition_number, publisher, obs, status, requester, quantity) " +
                    " VALUES (localtimestamp, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '0', ?, ?); ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setString(1, dto.getUser());
            pstInsert.setString(2, dto.getAuthorType());
            pstInsert.setString(3, dto.getAuthor());
            pstInsert.setString(4, dto.getAuthorNumeration());
            pstInsert.setString(5, dto.getAuthorTitle());
            pstInsert.setString(6, dto.getTitle());
            pstInsert.setString(7, dto.getSubtitle());
            pstInsert.setString(8, dto.getEditionNumber());
            pstInsert.setString(9, dto.getPublisher());
            pstInsert.setString(10, dto.getObs());
            pstInsert.setString(11, dto.getRequester());
            pstInsert.setInt(12, dto.getQuantity());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public ArrayList<RequestDTO> listRequests(String status, int offset, int limit) {
        ArrayList<RequestDTO> requestList = new ArrayList<RequestDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            boolean setStatus = StringUtils.isNotBlank(status) && (status.equals("0") || status.equals("1"));
            StringBuilder sql = new StringBuilder(" SELECT * FROM acquisition_requisition ");
            if (setStatus) {
                sql.append(" WHERE status = ? ");
            }
            sql.append(" ORDER BY serial_requisition ASC offset ? limit ? ");
            
            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (setStatus) {
                pst.setString(i++, status);
            }
            pst.setInt(i++, offset);
            pst.setInt(i++, limit);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return requestList;
            }
            while (rs.next()) {
                RequestDTO dto = this.populateDto(rs);
                requestList.add(dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return requestList;
    }

    public boolean updateRequest(RequestDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " UPDATE acquisition_requisition " +
                    " SET author_type = ?, " +
                    " author = ?, num_prename = ?, author_title = ?, item_title = ?, " +
                    " item_subtitle = ?, edition_number = ?, publisher = ?, obs = ?, " +
                    " requester = ?, quantity = ? " +
                    " WHERE serial_requisition = ?;";
            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setString(1, dto.getAuthorType());
            pstInsert.setString(2, dto.getAuthor());
            pstInsert.setString(3, dto.getAuthorNumeration());
            pstInsert.setString(4, dto.getAuthorTitle());
            pstInsert.setString(5, dto.getTitle());
            pstInsert.setString(6, dto.getSubtitle());
            pstInsert.setString(7, dto.getEditionNumber());
            pstInsert.setString(8, dto.getPublisher());
            pstInsert.setString(9, dto.getObs());
            pstInsert.setString(10, dto.getRequester());
            pstInsert.setInt(11, dto.getQuantity());
            pstInsert.setInt(12, dto.getSerial());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean updateRequestStatus(Integer buyOrderSerial, String status) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " UPDATE acquisition_requisition SET status = ? " +
                    " WHERE serial_requisition IN (" +
                    " SELECT r.serial_requisition FROM acquisition_requisition r " +
                    " INNER JOIN acquisition_item_quotation i " +
                    " ON i.serial_requisition = r.serial_requisition " +
                    " INNER JOIN acquisition_quotation q " +
                    " ON q.serial_quotation = i.serial_quotation " +
                    " INNER JOIN acquisition_order o " +
                    " ON o.serial_quotation = q.serial_quotation " +
                    " WHERE o.serial_order = ?); ";
            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setString(1, status);
            pstInsert.setInt(2, buyOrderSerial);
            pstInsert.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
        return true;
    }


    public boolean deleteRequest(RequestDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " DELETE FROM acquisition_requisition " +
                    " WHERE serial_requisition = ?; ";
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

    public ArrayList<RequestDTO> searchRequest(RequestDTO example, int offset, int limit) {
        ArrayList<RequestDTO> requestList = new ArrayList<RequestDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT * FROM acquisition_requisition ");
            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_requisition = ? ");
            } else if (StringUtils.isNotBlank(example.getRequester())) {
                sql.append(" WHERE requester ilike ? ");
            } else if (example.getRequestDate() != null) {
                sql.append(" WHERE requisition_date = ? ");
            } else if (StringUtils.isNotBlank(example.getAuthor())) {
                sql.append(" WHERE author ilike ? ");
            } else if (StringUtils.isNotBlank(example.getTitle())) {
                sql.append(" WHERE item_title ilike ? ");
            }
            if (StringUtils.isNotBlank(example.getStatus())) {
                if (example.getStatus().equals("0") || example.getStatus().equals("1")) {
                    if (sql.toString().contains("WHERE")) {
                        sql.append(" AND status = ? ");
                    } else {
                        sql.append(" WHERE status = ? ");
                    }
                }
            }

            sql.append(" ORDER BY serial_requisition ASC offset ? limit ? ");
            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (StringUtils.isNotBlank(example.getRequester())) {
                pst.setString(i++, "%" + example.getRequester() + "%");
            } else if (example.getRequestDate() != null) {
                pst.setDate(i++, new java.sql.Date(example.getRequestDate().getTime()));
            } else if (StringUtils.isNotBlank(example.getAuthor())) {
                pst.setString(i++, "%" + example.getAuthor() + "%");
            } else if (StringUtils.isNotBlank(example.getTitle())) {
                pst.setString(i++, "%" + example.getTitle() + "%");
            }
            if (StringUtils.isNotBlank(example.getStatus())) {
                if (example.getStatus().equals("0") || example.getStatus().equals("1")) {
                    pst.setString(i++, example.getStatus());
                }
            }
            pst.setInt(i++, offset);
            pst.setInt(i++, limit);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return requestList;
            }
            while (rs.next()) {
                RequestDTO dto = this.populateDto(rs);
                requestList.add(dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return requestList;
    }

    public int getSearchCount(RequestDTO example) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT COUNT(*) FROM acquisition_requisition ");
            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_requisition = ? ");
            } else if (StringUtils.isNotBlank(example.getRequester())) {
                sql.append(" WHERE requester ilike ? ");
            } else if (example.getRequestDate() != null) {
                sql.append(" WHERE requisition_date = ? ");
            } else if (StringUtils.isNotBlank(example.getAuthor())) {
                sql.append(" WHERE author ilike ? ");
            } else if (StringUtils.isNotBlank(example.getTitle())) {
                sql.append(" WHERE item_title ilike ? ");
            }
            if (StringUtils.isNotBlank(example.getStatus())) {
                if (example.getStatus().equals("0") || example.getStatus().equals("1")) {
                    if (sql.toString().contains("WHERE")) {
                        sql.append(" AND status = ? ");
                    } else {
                        sql.append(" WHERE status = ? ");
                    }
                }
            }
            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (StringUtils.isNotBlank(example.getRequester())) {
                pst.setString(i++, "%" + example.getRequester() + "%");
            } else if (example.getRequestDate() != null) {
                pst.setDate(i++, new java.sql.Date(example.getRequestDate().getTime()));
            } else if (StringUtils.isNotBlank(example.getAuthor())) {
                pst.setString(i++, "%" + example.getAuthor() + "%");
            } else if (StringUtils.isNotBlank(example.getTitle())) {
                pst.setString(i++, "%" + example.getTitle() + "%");
            }
            if (StringUtils.isNotBlank(example.getStatus())) {
                if (example.getStatus().equals("0") || example.getStatus().equals("1")) {
                    pst.setString(i++, example.getStatus());
                }
            }
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return 0;
            }
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return 0;
    }

    private RequestDTO populateDto(ResultSet rs) throws Exception {
        final RequestDTO dto = new RequestDTO();
        dto.setSerial(rs.getInt("serial_requisition"));
        dto.setRequestDate(rs.getDate("requisition_date"));
        dto.setUser(rs.getString("responsable"));
        dto.setAuthorType(rs.getString("author_type"));
        dto.setAuthor(rs.getString("author"));
        dto.setAuthorNumeration(rs.getString("num_prename"));
        dto.setAuthorTitle(rs.getString("author_title"));
        dto.setTitle(rs.getString("item_title"));
        dto.setSubtitle(rs.getString("item_subtitle"));
        dto.setEditionNumber(rs.getString("edition_number"));
        dto.setPublisher(rs.getString("publisher"));
        dto.setObs(rs.getString("obs"));
        dto.setStatus(rs.getString("status"));
        dto.setRequester(rs.getString("requester"));
        dto.setQuantity(rs.getInt("quantity"));
        return dto;
    }

    public int getTotalNroRecords() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final Statement st = con.createStatement();
            final String query = "SELECT count(*) FROM acquisition_requisition;";
            final ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("REQUEST_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }
}
