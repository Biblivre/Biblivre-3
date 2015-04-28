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

package biblivre3.acquisition.supplier;

import mercury.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

public class SupplierDAO extends DAO {

    public static final int REQUISITION_OPEN = 1;
    public static final int ORDER_STATUS_PENDENT = 1;

    public boolean insertSupplier(SupplierDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " INSERT INTO acquisition_supplier ( " +
                    " trade_mark_name, " +
                    " company_name, " +
                    " company_number, " +
                    " vat_registration_number, " +
                    " address, " +
                    " number_address, " +
                    " complement, " +
                    " area, " +
                    " city, " +
                    " state, " +
                    " country, " +
                    " zip_code, " +
                    " telephone_1, " +
                    " telephone_2, " +
                    " telephone_3, " +
                    " telephone_4, " +
                    " contact_1, " +
                    " contact_2, " +
                    " contact_3, " +
                    " contact_4, " +
                    " obs, " +
                    " created, " +
                    " modified, " +
                    " url, " +
                    " email) " +
                    " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, localtimestamp, localtimestamp, ?, ?); ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setString(1, dto.getTrademarkName());
            pstInsert.setString(2, dto.getCompanyName());
            pstInsert.setString(3, dto.getCompanyNumber());
            pstInsert.setString(4, dto.getVatRegistrationNumber());
            pstInsert.setString(5, dto.getAddress());
            pstInsert.setString(6, dto.getAddressNumber());
            pstInsert.setString(7, dto.getComplement());
            pstInsert.setString(8, dto.getArea());
            pstInsert.setString(9, dto.getCity());
            pstInsert.setString(10, dto.getState());
            pstInsert.setString(11, dto.getCountry());
            pstInsert.setString(12, dto.getZipCode());
            pstInsert.setString(13, dto.getTelephone1());
            pstInsert.setString(14, dto.getTelephone2());
            pstInsert.setString(15, dto.getTelephone3());
            pstInsert.setString(16, dto.getTelephone4());
            pstInsert.setString(17, dto.getContact1());
            pstInsert.setString(18, dto.getContact2());
            pstInsert.setString(19, dto.getContact3());
            pstInsert.setString(20, dto.getContact4());
            pstInsert.setString(21, dto.getObs());
            pstInsert.setString(22, dto.getUrl());
            pstInsert.setString(23, dto.getEmail());
            pstInsert.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
        return true;
    }

    public ArrayList<SupplierDTO> listSuppliers(int offset, int limit) {
        ArrayList<SupplierDTO> supplierList = new ArrayList<SupplierDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = 
                    " SELECT * FROM acquisition_supplier " +
                    " ORDER BY serial_supplier ASC offset ? limit ? ";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, offset);
            pst.setInt(2, limit);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return supplierList;
            }
            while (rs.next()) {
                SupplierDTO dto = this.populateDto(rs);
                supplierList.add(dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return supplierList;
    }

    public boolean updateSupplier(SupplierDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " UPDATE acquisition_supplier SET " +
                    " trade_mark_name = ?, " +
                    " company_name = ?, " +
                    " company_number = ?, " +
                    " vat_registration_number = ?, " +
                    " address = ?, " +
                    " number_address = ?, " +
                    " complement = ?, " +
                    " area = ?, " +
                    " city = ?, " +
                    " state = ?, " +
                    " country = ?, " +
                    " zip_code = ?, " +
                    " telephone_1 = ?, " +
                    " telephone_2 = ?, " +
                    " telephone_3 = ?, " +
                    " telephone_4 = ?, " +
                    " contact_1 = ?, " +
                    " contact_2 = ?, " +
                    " contact_3 = ?, " +
                    " contact_4 = ?, " +
                    " obs = ?, " +
                    " modified = localtimestamp, " +
                    " url = ?, " +
                    " email = ? " +
                    " WHERE serial_supplier = ?; ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setString(1, dto.getTrademarkName());
            pstInsert.setString(2, dto.getCompanyName());
            pstInsert.setString(3, dto.getCompanyNumber());
            pstInsert.setString(4, dto.getVatRegistrationNumber());
            pstInsert.setString(5, dto.getAddress());
            pstInsert.setString(6, dto.getAddressNumber());
            pstInsert.setString(7, dto.getComplement());
            pstInsert.setString(8, dto.getArea());
            pstInsert.setString(9, dto.getCity());
            pstInsert.setString(10, dto.getState());
            pstInsert.setString(11, dto.getCountry());
            pstInsert.setString(12, dto.getZipCode());
            pstInsert.setString(13, dto.getTelephone1());
            pstInsert.setString(14, dto.getTelephone2());
            pstInsert.setString(15, dto.getTelephone3());
            pstInsert.setString(16, dto.getTelephone4());
            pstInsert.setString(17, dto.getContact1());
            pstInsert.setString(18, dto.getContact2());
            pstInsert.setString(19, dto.getContact3());
            pstInsert.setString(20, dto.getContact4());
            pstInsert.setString(21, dto.getObs());
            pstInsert.setString(22, dto.getUrl());
            pstInsert.setString(23, dto.getEmail());
            pstInsert.setInt(24, dto.getSerial());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean deleteSupplier(SupplierDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " DELETE FROM acquisition_supplier " +
                    " WHERE serial_supplier = ?; ";
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

    public ArrayList<SupplierDTO> searchSupplier(SupplierDTO example, int offset, int limit) {
        ArrayList<SupplierDTO> supplierList = new ArrayList<SupplierDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT * FROM acquisition_supplier ");
            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_supplier = ? ");
            } else if (StringUtils.isNotBlank(example.getTrademarkName())) {
                sql.append(" WHERE trade_mark_name ilike ? ");
            } else if (StringUtils.isNotBlank(example.getCompanyName())) {
                sql.append(" WHERE company_name ilike ? ");
            } else if (StringUtils.isNotBlank(example.getCompanyNumber())) {
                sql.append(" WHERE company_number = ? ");
            }
            sql.append(" ORDER BY serial_supplier ASC offset ? limit ? ");
            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (StringUtils.isNotBlank(example.getTrademarkName())) {
                pst.setString(i++, "%" + example.getTrademarkName() + "%");
            } else if (StringUtils.isNotBlank(example.getCompanyName())) {
                pst.setString(i++, "%" + example.getCompanyName() + "%");
            } else if (StringUtils.isNotBlank(example.getCompanyNumber())) {
                pst.setString(i++, example.getCompanyNumber());
            }
            pst.setInt(i++, offset);
            pst.setInt(i++, limit);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return supplierList;
            }
            while (rs.next()) {
                SupplierDTO dto = this.populateDto(rs);
                supplierList.add(dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return supplierList;
    }

    public int getSearchCount(SupplierDTO example) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT count(*) FROM acquisition_supplier ");
            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_supplier = ? ");
            } else if (StringUtils.isNotBlank(example.getTrademarkName())) {
                sql.append(" WHERE trade_mark_name ilike ? ");
            } else if (StringUtils.isNotBlank(example.getCompanyName())) {
                sql.append(" WHERE company_name ilike ? ");
            } else if (StringUtils.isNotBlank(example.getCompanyNumber())) {
                sql.append(" WHERE company_number = ? ");
            }
            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (StringUtils.isNotBlank(example.getTrademarkName())) {
                pst.setString(i++,"%" + example.getTrademarkName() + "%");
            } else if (StringUtils.isNotBlank(example.getCompanyName())) {
                pst.setString(i++,"%" + example.getCompanyName() + "%");
            } else if (StringUtils.isNotBlank(example.getCompanyNumber())) {
                pst.setString(i++, example.getCompanyNumber());
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


    private final SupplierDTO populateDto(ResultSet rs) throws Exception {
        final SupplierDTO dto = new SupplierDTO();
        dto.setSerial(Integer.valueOf(rs.getInt("serial_supplier")));
        dto.setTrademarkName(rs.getString("trade_mark_name"));
        dto.setCompanyName(rs.getString("company_name"));
        dto.setCompanyNumber(rs.getString("company_number"));
        dto.setVatRegistrationNumber(rs.getString("vat_registration_number"));
        dto.setAddress(rs.getString("address"));
        dto.setAddressNumber(rs.getString("number_address"));
        dto.setComplement(rs.getString("complement"));
        dto.setArea(rs.getString("area"));
        dto.setCity(rs.getString("city"));
        dto.setState(rs.getString("state"));
        dto.setCountry(rs.getString("country"));
        dto.setZipCode(rs.getString("zip_code"));
        dto.setTelephone1(rs.getString("telephone_1"));
        dto.setTelephone2(rs.getString("telephone_2"));
        dto.setTelephone3(rs.getString("telephone_3"));
        dto.setTelephone4(rs.getString("telephone_4"));
        dto.setContact1(rs.getString("contact_1"));
        dto.setContact2(rs.getString("contact_2"));
        dto.setContact3(rs.getString("contact_3"));
        dto.setContact4(rs.getString("contact_4"));
        dto.setObs(rs.getString("obs"));
        dto.setCreated(rs.getTimestamp("created"));
        dto.setModified(rs.getTimestamp("modified"));
        dto.setUrl(rs.getString("url"));
        dto.setEmail(rs.getString("email"));
        return dto;
    }

    public int getTotalNroRecords() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final Statement st = con.createStatement();
            final String query = "SELECT count(*) FROM acquisition_supplier;";
            final ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("SUPPLIER_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

}
