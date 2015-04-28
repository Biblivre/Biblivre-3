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
import java.util.ArrayList;

public class ItemQuotationDAO extends DAO {

    public boolean insertItemQuotation(ItemQuotationDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " INSERT INTO acquisition_item_quotation" +
                    " (serial_requisition, serial_quotation, quotation_quantity, unit_value, " +
                    " response_quantity)" +
                    " VALUES (?, ?, ?, ?, ?); ";
            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, dto.getSerialRequisition());
            pstInsert.setInt(2, dto.getSerialQuotation());
            pstInsert.setInt(3, dto.getQuotationQuantity());
            pstInsert.setFloat(4, dto.getUnitValue());
            pstInsert.setInt(5, dto.getResponseQuantity() != null ? dto.getResponseQuantity() : 0);
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public ArrayList<ItemQuotationDTO> listItemQuotations(Integer serialQuotation) {
        ArrayList<ItemQuotationDTO> requestList = new ArrayList<ItemQuotationDTO>();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = 
                    " SELECT i.serial_requisition, i.serial_quotation, i.quotation_quantity, " +
                    " i.unit_value, i.response_quantity, r.item_title, r.author " +
                    " FROM acquisition_item_quotation i, acquisition_requisition r " +
                    " WHERE i.serial_requisition = r.serial_requisition " +
                    " AND serial_quotation = ?; ";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, serialQuotation);
            final ResultSet rs = pst.executeQuery();
            if (rs == null) {
                return requestList;
            }
            while (rs.next()) {
                ItemQuotationDTO dto = this.populateDto(rs);
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

    public boolean updateQuotation(ItemQuotationDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert = 
                    " UPDATE acquisition_item_quotation " +
                    " SET quotation_quantity = ?, " +
                    " unit_value = ?, response_quantity = ? " +
                    " WHERE serial_quotation = ?" +
                    " AND serial_requisition = ?; ";

            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, dto.getQuotationQuantity());
            pstInsert.setFloat(2, dto.getUnitValue());
            pstInsert.setInt(3, dto.getResponseQuantity());
            pstInsert.setInt(4, dto.getSerialQuotation());
            pstInsert.setInt(5, dto.getSerialRequisition());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean deleteItemQuotation(ItemQuotationDTO dto) {
        Connection conInsert = null;
        try {
            conInsert = getDataSource().getConnection();
            final String sqlInsert =
                    " DELETE FROM acquisition_item_quotation " +
                    " WHERE serial_quotation = ? " +
                    " AND serial_requisition = ?; ";
            PreparedStatement pstInsert = conInsert.prepareStatement(sqlInsert);
            pstInsert.setInt(1, dto.getSerialQuotation());
            pstInsert.setInt(2, dto.getSerialRequisition());
            return pstInsert.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsert);
        }
    }

    public boolean deleteAllByQuotationId(Integer quotationId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sqlInsert =
                    " DELETE FROM acquisition_item_quotation " +
                    " WHERE serial_quotation = ?; ";
            PreparedStatement pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, quotationId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    private final ItemQuotationDTO populateDto(ResultSet rs) throws Exception {
        final ItemQuotationDTO dto = new ItemQuotationDTO();
        dto.setSerialRequisition(rs.getInt("serial_requisition"));
        dto.setSerialQuotation(rs.getInt("serial_quotation"));
        dto.setQuotationQuantity(rs.getInt("quotation_quantity"));
        dto.setResponseQuantity(rs.getInt("response_quantity"));
        dto.setUnitValue(rs.getFloat("unit_value"));
        dto.setTitle(rs.getString("item_title"));
        dto.setAuthor(rs.getString("author"));
        return dto;
    }
}
