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

package biblivre3.acquisition.order;

import mercury.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

public class BuyOrderDAO extends DAO {

    public boolean insertBuyOrder(BuyOrderDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " INSERT INTO acquisition_order (serial_quotation, order_date, " +
                    " responsable, obs, status, invoice_number, " +
                    " receipt_date, total_value, delivered_quantity, " +
                    " terms_of_payment, deadline_date) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, dto.getSerialQuotation());
            pstInsert.setDate(2, new java.sql.Date(dto.getOrderDate().getTime()));
            pstInsert.setString(3, dto.getResponsible());
            pstInsert.setString(4, dto.getObs());
            pstInsert.setString(5, dto.getStatus());
            pstInsert.setString(6, dto.getInvoiceNumber());

            Date receiptDate = dto.getReceiptDate();
            if (receiptDate != null) {
                pstInsert.setDate(7, new java.sql.Date(receiptDate.getTime()));
            } else {
                pstInsert.setNull(7, java.sql.Types.DATE);
            }

            Float totalValue = dto.getTotalValue();
            if (totalValue != null) {
                pstInsert.setFloat(8, totalValue);
            } else {
                pstInsert.setNull(8, java.sql.Types.FLOAT);
            }

            Integer deliveryQuantity = dto.getDeliveredQuantity();
            if (deliveryQuantity != null) {
                pstInsert.setInt(9, deliveryQuantity);
            } else {
                pstInsert.setNull(9, java.sql.Types.INTEGER);
            }

            pstInsert.setString(10, dto.getTermsOfPayment());
            pstInsert.setDate(11, new java.sql.Date(dto.getDeadlineDate().getTime()));
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public ArrayList<BuyOrderDTO> listBuyOrders(String status, int offset, int limit) {
        ArrayList<BuyOrderDTO> requestList = new ArrayList<BuyOrderDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            boolean setStatus = StringUtils.isNotBlank(status) && (status.equals("0") || status.equals("1"));
            StringBuilder sql = new StringBuilder(" SELECT * FROM acquisition_order ");
            if (setStatus) {
                sql.append(" WHERE status = ? ");
            }
            sql.append(" ORDER BY order_date ASC offset ? limit ?;");

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
                BuyOrderDTO dto = this.populateDto(rs);
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

    public boolean updateBuyOrder(BuyOrderDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " UPDATE acquisition_order " +
                    " SET serial_quotation = ?, order_date = ?, " +
                    " responsable = ?, obs = ?, status = ?, " +
                    " invoice_number = ?, receipt_date = ?, total_value = ?, " +
                    " delivered_quantity = ?, terms_of_payment = ?, deadline_date=? " +
                    " WHERE serial_order = ?;";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, dto.getSerialQuotation());
            pstInsert.setDate(2, new java.sql.Date(dto.getOrderDate().getTime()));
            pstInsert.setString(3, dto.getResponsible());
            pstInsert.setString(4, dto.getObs());
            pstInsert.setString(5, dto.getStatus() != null && dto.getStatus().equals("1") ? "1" : "0");
            pstInsert.setString(6, dto.getInvoiceNumber());

            Date receiptDate = dto.getReceiptDate();
            if (receiptDate != null) {
                pstInsert.setDate(7, new java.sql.Date(receiptDate.getTime()));
            } else {
                pstInsert.setNull(7, java.sql.Types.DATE);
            }

            Float totalValue = dto.getTotalValue();
            if (totalValue != null) {
                pstInsert.setFloat(8, totalValue);
            } else {
                pstInsert.setNull(8, java.sql.Types.FLOAT);
            }

            Integer deliveryQuantity = dto.getDeliveredQuantity();
            if (deliveryQuantity != null) {
                pstInsert.setInt(9, deliveryQuantity);
            } else {
                pstInsert.setNull(9, java.sql.Types.INTEGER);
            }

            pstInsert.setString(10, dto.getTermsOfPayment());
            pstInsert.setDate(11, new java.sql.Date(dto.getDeadlineDate().getTime()));
            pstInsert.setInt(12, dto.getSerial());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean deleteBuyOrder(BuyOrderDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " DELETE FROM acquisition_order " +
                    " WHERE serial_order = ?; ";
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

    public ArrayList<BuyOrderDTO> searchBuyOrder(BuyOrderDTO example, int offset, int limit) {
        ArrayList<BuyOrderDTO> requestList = new ArrayList<BuyOrderDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT * FROM acquisition_order ");

            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_order = ? ");
            } else if (example.getSerialQuotation() != null && example.getSerialQuotation() != 0) {
                sql.append(" WHERE serial_quotation = ? ");
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0) {
                sql.append(" WHERE serial_quotation in ( SELECT serial_quotation FROM acquisition_quotation WHERE serial_supplier = ?) ");
            } else if (StringUtils.isNotBlank(example.getSupplierName())) {
                sql.append(" WHERE serial_quotation in ");
                sql.append(" ( SELECT serial_quotation FROM acquisition_quotation q, acquisition_supplier s ");
                sql.append(" WHERE q.serial_supplier = s.serial_supplier ");
                sql.append(" AND s.trade_mark_name ilike ? ) ");
            }
            if ((example.getSerial() == null || example.getSerial() == 0) && StringUtils.isNotBlank(example.getStatus())) {
                if (example.getStatus().equals("0") || example.getStatus().equals("1")) {
                    if (sql.toString().contains("WHERE")) {
                        sql.append(" AND status = ? ");
                    } else {
                        sql.append(" WHERE status = ? ");
                    }
                }
            }

            sql.append(" ORDER BY order_date ASC offset ? limit ? ");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (example.getSerialQuotation() != null && example.getSerialQuotation() != 0) {
                pst.setInt(i++, example.getSerialQuotation());
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0) {
                pst.setInt(i++, example.getSerialSupplier());
            } else if (StringUtils.isNotBlank(example.getSupplierName())) {
                pst.setString(i++, "%" + example.getSupplierName() + "%");
            }
            if ((example.getSerial() == null || example.getSerial() == 0) && StringUtils.isNotBlank(example.getStatus())) {
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
                BuyOrderDTO dto = this.populateDto(rs);
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

    public int getSearchCount(BuyOrderDTO example) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder(" SELECT * FROM acquisition_order ");
            if (example.getSerial() != null && example.getSerial() != 0) {
                sql.append(" WHERE serial_order = ? ");
            } else if (example.getSerialQuotation() != null && example.getSerialQuotation() != 0) {
                sql.append(" WHERE serial_quotation = ? ");
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0) {
                sql.append(" WHERE serial_quotation in ( SELECT serial_quotation FROM acquisition_quotation WHERE serial_supplier = ?) ");
            }
            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int i = 1;
            if (example.getSerial() != null && example.getSerial() != 0) {
                pst.setInt(i++, example.getSerial());
            } else if (example.getSerialQuotation() != null && example.getSerialQuotation() != 0) {
                pst.setInt(i++, example.getSerialQuotation());
            } else if (example.getSerialSupplier() != null && example.getSerialSupplier() != 0) {
                pst.setInt(i++, example.getSerialSupplier());
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

    public int getTotalNroRecords(String status) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String query = " SELECT count(*) FROM acquisition_order ";
            if (status.equals("0") || status.equals("1")) {
                query += " WHERE status = ? ";
            }
            PreparedStatement pst = con.prepareStatement(query);
            if (status.equals("0") || status.equals("1")) {
                pst.setString(1, status);
            }
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("BUYORDER_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
    }

    private final BuyOrderDTO populateDto(ResultSet rs) throws Exception {
        final BuyOrderDTO dto = new BuyOrderDTO();
        dto.setSerial(rs.getInt("serial_order"));
        dto.setSerialQuotation(rs.getInt("serial_quotation"));
        dto.setOrderDate(rs.getDate("order_date"));
        dto.setResponsible(rs.getString("responsable"));
        dto.setObs(rs.getString("obs"));
        dto.setStatus(rs.getString("status"));
        dto.setInvoiceNumber(rs.getString("invoice_number"));
        dto.setReceiptDate(rs.getDate("receipt_date"));
        dto.setTotalValue(rs.getFloat("total_value"));
        dto.setDeliveredQuantity(rs.getInt("delivered_quantity"));
        dto.setTermsOfPayment(rs.getString("terms_of_payment"));
        dto.setDeadlineDate(rs.getDate("deadline_date"));
        return dto;
    }
}
