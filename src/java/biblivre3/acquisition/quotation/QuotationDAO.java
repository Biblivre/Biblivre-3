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

package biblivre3.acquisition.quotation;

import mercury.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

public class QuotationDAO extends DAO {

    public boolean insertQuotation(QuotationDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " INSERT INTO acquisition_quotation(quotation_date, serial_supplier, " +
                    " response_date, expiration_date, delivery_time, " +
                    " responsable, obs, serial_quotation)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?); ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setDate(1, new java.sql.Date(dto.getQuotationDate().getTime()));
            pstInsert.setInt(2, dto.getSerialSupplier());
            pstInsert.setDate(3, new java.sql.Date(dto.getResponseDate().getTime()));
            pstInsert.setDate(4, new java.sql.Date(dto.getExpirationDate().getTime()));
            pstInsert.setInt(5, dto.getDeliveryTime());
            pstInsert.setString(6, dto.getResponsible());
            pstInsert.setString(7, dto.getObs());
            pstInsert.setInt(8, dto.getSerial());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public ArrayList<QuotationDTO> listQuotations(int offset, int limit) {
        ArrayList<QuotationDTO> requestList = new ArrayList<QuotationDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = 
                    " SELECT * FROM acquisition_quotation " +
                    " ORDER BY serial_quotation ASC offset ? limit ? ";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, offset);
            pst.setInt(2, limit);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return requestList;
            }
            while (rs.next()) {
                QuotationDTO dto = this.populateDto(rs);
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

    public boolean updateQuotation(QuotationDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = 
                    " UPDATE acquisition_quotation " +
                    " SET " +
                    " serial_supplier=?, response_date=?, " +
                    " expiration_date=?, delivery_time=?, " +
                    " responsable=?, obs=? " +
                    " WHERE serial_quotation = ?; ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, dto.getSerialSupplier());
            pstInsert.setDate(2, new java.sql.Date(dto.getResponseDate().getTime()));
            pstInsert.setDate(3, new java.sql.Date(dto.getExpirationDate().getTime()));
            pstInsert.setInt(4, dto.getDeliveryTime());
            pstInsert.setString(5, dto.getResponsible());
            pstInsert.setString(6, dto.getObs());
            pstInsert.setInt(7, dto.getSerial());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean deleteQuotation(QuotationDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " DELETE FROM acquisition_quotation " +
                    " WHERE serial_quotation = ?; ";
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

    public ArrayList<QuotationDTO> searchQuotation(QuotationDTO example, int offset, int limit) {
        ArrayList<QuotationDTO> requestList = new ArrayList<QuotationDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT * FROM acquisition_quotation ");

            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_quotation = ? ");
            } else if (example.getQuotationDate() != null) {
                sql.append(" WHERE quotation_date = ? ");
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0 ) {
                sql.append(" WHERE serial_supplier = ? ");
            } else if (!StringUtils.isBlank(example.getSupplierName())) {
                sql.append(" WHERE serial_supplier in (select serial_supplier from acquisition_supplier where company_name ilike ?) ");
            }
            sql.append(" ORDER BY serial_quotation ASC offset ? limit ? ");
            final PreparedStatement pst = con.prepareStatement(sql.toString());

            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (example.getQuotationDate() != null) {
                pst.setDate(i++, new java.sql.Date(example.getQuotationDate().getTime()));
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0) {
                pst.setInt(i++, example.getSerialSupplier());
            } else if (!StringUtils.isBlank(example.getSupplierName())) {
                pst.setString(i++, "%" + example.getSupplierName() + "%");
            }
            
            pst.setInt(i++, offset);
            pst.setInt(i++, limit);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return requestList;
            }
            while (rs.next()) {
                QuotationDTO dto = this.populateDto(rs);
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

    public int getSearchCount(QuotationDTO example) {
        int count = 0;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT count(*) FROM acquisition_quotation ");
            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_quotation = ? ");
            } else if (example.getQuotationDate() != null) {
                sql.append(" WHERE quotation_date = ? ");
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0) {
                sql.append(" WHERE serial_supplier = ? ");
            }
            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (example.getQuotationDate() != null) {
                pst.setDate(i++, new java.sql.Date(example.getQuotationDate().getTime()));
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0) {
                pst.setInt(i++, example.getSerialSupplier());
            }
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return count;
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
        return count;
    }

    public int getTotalNroRecords() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final Statement st = con.createStatement();
            final String query = "SELECT count(*) FROM acquisition_quotation;";
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

    private final QuotationDTO populateDto(ResultSet rs) throws Exception {
        final QuotationDTO dto = new QuotationDTO();
        dto.setSerial(rs.getInt("serial_quotation"));
        dto.setQuotationDate(rs.getDate("quotation_date"));
        dto.setSerialSupplier(rs.getInt("serial_supplier"));
        dto.setResponseDate(rs.getDate("response_date"));
        dto.setExpirationDate(rs.getDate("expiration_date"));
        dto.setDeliveryTime(rs.getInt("delivery_time"));
        dto.setResponsible(rs.getString("responsable"));
        dto.setObs(rs.getString("obs"));
        return dto;
    }
}
