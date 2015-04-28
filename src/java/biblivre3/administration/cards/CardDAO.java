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

package biblivre3.administration.cards;

import biblivre3.enums.CardStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import mercury.DAO;
import mercury.ExceptionUser;

public class CardDAO extends DAO {

    public final List<CardDTO> listCards(Integer offset, Integer limit) {
        Connection con = null;
        List<CardDTO> cardList = new ArrayList<CardDTO>();
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT * FROM cards " +
                    " where status <> '" + CardStatus.CANCELLED.ordinal() + "' " +
                    " ORDER BY serial_card ASC offset ? limit ?;";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, offset);
            pst.setInt(2, limit);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                CardDTO dto = new CardDTO();
                dto.setSerialCard(rs.getInt("serial_card"));
                dto.setCardNumber(rs.getString("card_number"));
                dto.setStatus(CardStatus.values()[rs.getInt("status")]);
                dto.setUserid(rs.getInt("userid"));
                dto.setDateTime(rs.getDate("date_time"));
                cardList.add(dto);
            }
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.listCards");
        } finally {
            closeConnection(con);
        }
        return cardList;
    }

    public final List<CardDTO> searchCards(String searchTerms, Integer offset, Integer limit) {
        Connection con = null;
        List<CardDTO> cardList = new ArrayList<CardDTO>();
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT * FROM cards " +
                    " WHERE status <> '" + CardStatus.CANCELLED.ordinal() + "' " +
                    " AND card_number ilike ? " +
                    " ORDER BY serial_card ASC offset ? limit ?; ";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, searchTerms + "%");
            pst.setInt(2, offset);
            pst.setInt(3, limit);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                CardDTO dto = new CardDTO();
                dto.setSerialCard(rs.getInt("serial_card"));
                dto.setCardNumber(rs.getString("card_number"));
                dto.setStatus(CardStatus.values()[rs.getInt("status")]);
                dto.setUserid(rs.getInt("userid"));
                dto.setDateTime(rs.getDate("date_time"));
                cardList.add(dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("Exception at AdminDAO.listCards");
        } finally {
            closeConnection(con);
        }
        return cardList;
    }

    public final int getSearchCardsCount(String searchTerms) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT COUNT(*) FROM cards " +
                    " WHERE status <> '" + CardStatus.CANCELLED.ordinal() + "' " +
                    " AND card_number ilike ?; ";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, searchTerms + "%");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("Exception at AdminDAO.listCards");
        } finally {
            closeConnection(con);
        }
        return 0;
    }


    public final CardDTO getCardById(Integer cardId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT * FROM cards " +
                    " WHERE serial_card = ? AND status <> '" + CardStatus.CANCELLED.ordinal() + "';";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, cardId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                CardDTO dto = new CardDTO();
                dto.setSerialCard(rs.getInt("serial_card"));
                dto.setCardNumber(rs.getString("card_number"));
                dto.setStatus(CardStatus.values()[rs.getInt("status")]);
                dto.setUserid(rs.getInt("userid"));
                dto.setDateTime(rs.getDate("date_time"));
                return dto;
            }
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.listCards");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final CardDTO getCardByNumber(String cardNumber) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = "SELECT C.*, A.entrance_datetime, U.userid as userserial, U.username FROM cards C " +
                         "LEFT JOIN access_control A ON A.serial_card = C.serial_card and A.departure_datetime is null " +
                         "LEFT JOIN users U ON U.userid = A.serial_reader " +
                         "WHERE C.card_number = ? AND C.status <> '" + CardStatus.CANCELLED.ordinal() + "';";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, cardNumber);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                CardDTO dto = new CardDTO();
                dto.setSerialCard(rs.getInt("serial_card"));
                dto.setCardNumber(rs.getString("card_number"));
                dto.setStatus(CardStatus.values()[rs.getInt("status")]);
                dto.setUserid(rs.getInt("userid"));
                dto.setDateTime(rs.getDate("date_time"));
                dto.setEntranceDatetime(rs.getTimestamp("entrance_datetime"));
                dto.setUserSerial(rs.getInt("userserial"));
                dto.setUserName(rs.getString("username"));
                return dto;
            }
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.listCards");
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final boolean addCard(CardDTO dto) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = 
                    " INSERT INTO cards(card_number, status, userid, date_time) " +
                    " VALUES (?, ?, ?, ?);";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, dto.getCardNumber());
            pst.setInt(2, dto.getStatus().ordinal());
            pst.setInt(3, dto.getUserid());
            pst.setDate(4, new java.sql.Date(dto.getDateTime().getTime()));
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.addCard");
        } finally {
            closeConnection(con);
        }
    }

    public final boolean removeCard(Integer cardId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = " DELETE FROM cards WHERE serial_card = ?;";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, cardId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.removeCard");
        } finally {
            closeConnection(con);
        }
    }

    public int getTotalNroRecords() {
        Connection con = null;
        int count = 0;
        try {
            con = getDataSource().getConnection();
            String sql = " SELECT COUNT(*) FROM cards WHERE status <> '" + CardStatus.CANCELLED.ordinal() + "';";
            final PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.listCards");
        } finally {
            closeConnection(con);
        }
        return count;
    }

    public final boolean updateCardStatus(CardStatus newStatus, Integer cardId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = " UPDATE cards SET status = ? WHERE serial_card = ?;";
            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, newStatus.ordinal());
            pst.setInt(2, cardId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e);
            throw new ExceptionUser("Exception at AdminDAO.addCard");
        } finally {
            closeConnection(con);
        }
    }

    
}
