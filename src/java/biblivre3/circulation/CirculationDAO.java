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

package biblivre3.circulation;

import biblivre3.enums.UserStatus;
import biblivre3.utils.TextUtils;
import java.sql.SQLException;
import mercury.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class CirculationDAO extends DAO {

    private void populateUserDTO(UserDTO userDTO, final ResultSet rs) throws SQLException {

        userDTO.setName(sanitize(rs.getString("username"), ""));
        userDTO.setUserid(rs.getInt("userid"));
        userDTO.setUserStatus(UserStatus.valueOf(rs.getString("status").trim()));
        userDTO.setTypeId(sanitize(rs.getString("type_id"), ""));
        userDTO.setAddress(sanitize(rs.getString("address"), ""));
        userDTO.setNumber(sanitize(rs.getString("number"), ""));
        userDTO.setCompletion(sanitize(rs.getString("completion"), ""));
        userDTO.setZip_code(sanitize(rs.getString("zip_code"), ""));
        userDTO.setCity(sanitize(rs.getString("city"), ""));
        userDTO.setState(sanitize(rs.getString("state"), ""));
        userDTO.setSocial_id_number(sanitize(rs.getString("social_id_number"), ""));
        userDTO.setDlicense(sanitize(rs.getString("dlicense"), ""));
        userDTO.setEmail(sanitize(rs.getString("email"), ""));
        userDTO.setTelRef1(sanitize(rs.getString("tel_ref_1"), ""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = rs.getDate("birthday");
        userDTO.setBirthday(sdf.format(
                date == null ? new Date(0L) : date //deafault: dec 31st 1969 -> 0L
        ));

        userDTO.setTelRef2(sanitize(rs.getString("tel_ref_2"), ""));
        userDTO.setObs(sanitize(rs.getString("obs"), ""));

        userDTO.setExtension_line(sanitize(rs.getString("extension_line"), ""));
        userDTO.setCellphone(sanitize(rs.getString("cellphone"), ""));

        userDTO.setUsernameascii(sanitize(rs.getString("usernameascii"), ""));
        userDTO.setPhoto(sanitize(rs.getString("photo_id"), ""));

        String userTypeString = sanitize(rs.getString("user_type"), "0");
        userDTO.setUserType((new Integer(userTypeString)).intValue());

        userDTO.setLoginid(rs.getInt("loginid"));
        userDTO.setLoginName(rs.getString("loginname"));

        userDTO.setCardNumber(rs.getString("card_number"));
    }

    public final UserDTO searchByName(String name) {
        UsersDTO usersDTO = this.list(name, 0, 0, 0, 1);
        return usersDTO.getFirst();
    }

    public final UserDTO searchByUserId(int userId) {
        UsersDTO usersDTO = this.list(null, userId, 0, 0, 1);
        return usersDTO.getFirst();
    }

    public final UserDTO searchByLoginId(int loginId) {
        UsersDTO usersDTO = this.list(null, 0, loginId, 0, 1);
        return usersDTO.getFirst();
    }

    public final UsersDTO list(String name, int userId, int loginid, int offset, int recordsPerPage) {
        UsersDTO usersDTO = new UsersDTO();
        Connection con = null;

        if (name != null) {
            name = TextUtils.removeDiacriticals(name);
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sbCount = new StringBuilder();

        try {
            con = getDataSource().getConnection();

            sb.append("SELECT U.username, U.status, U.type_id, U.address, U.number, U.completion, U.zip_code, ");
            sb.append("U.city, U.state, U.social_id_number, U.dlicense, U.email, U.tel_ref_1, U.birthday, U.tel_ref_2, U.obs, U.userid, ");
            sb.append("U.extension_line, U.cellphone, U.usernameascii, U.photo_id, U.user_type, U.loginid, L.loginname, C.card_number ");
            sb.append("FROM users U ");
            sb.append("LEFT JOIN logins L ON L.loginid = U.loginid ");
            sb.append("LEFT JOIN access_control A ON A.serial_reader = U.userid and A.departure_datetime is null ");
            sb.append("LEFT JOIN cards C ON C.serial_card = A.serial_card ");
            sb.append("WHERE U.status <> 'INACTIVE' ");

            sbCount.append("SELECT count(*) as total FROM users U ");
            sbCount.append("WHERE U.status <> 'INACTIVE' ");

            if (name != null && !name.isEmpty()) {
                sb.append("AND U.usernameascii ilike ? ");
                sbCount.append("AND U.usernameascii ilike ? ");
            }
            if (userId != 0) {
                sb.append("AND U.userid = ? ");
                sbCount.append("AND U.userid = ? ");
            }
            if (loginid != 0) {
                sb.append("AND U.loginid = ? ");
                sbCount.append("AND U.loginid = ? ");
            }

            sb.append("ORDER BY UPPER(U.usernameascii) ASC ");
            sb.append("LIMIT ? OFFSET ?;");

            final PreparedStatement pst = con.prepareStatement(sb.toString());
            final PreparedStatement pstCount = con.prepareStatement(sbCount.toString());
            int psIndex = 1;

            if (name != null && !name.isEmpty()) {
                pst.setString(psIndex, "%" + name + "%");
                pstCount.setString(psIndex++, "%" + name + "%");
            }

            if (userId != 0) {
                pst.setInt(psIndex, userId);
                pstCount.setInt(psIndex++, userId);
            }

            if (loginid != 0) {
                pst.setInt(psIndex, loginid);
                pstCount.setInt(psIndex++, loginid);
            }

            pst.setInt(psIndex++, recordsPerPage);
            pst.setInt(psIndex++, offset);

            final ResultSet rs = pst.executeQuery();
            final ResultSet rsCount = pstCount.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    UserDTO userDTO = new UserDTO();
                    this.populateUserDTO(userDTO, rs);

                    usersDTO.add(userDTO);
                }

                if (rsCount != null && rsCount.next()) {
                    int total = rsCount.getInt("total");
                    int nroPages = total / recordsPerPage;
                    int mod = total % recordsPerPage;

                    usersDTO.totalPages = mod == 0 ? nroPages : nroPages + 1;
                    usersDTO.currentPage = (offset / recordsPerPage) + 1;
                    usersDTO.recordsPerPage = recordsPerPage;
                    usersDTO.totalRecords = total;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return usersDTO;
    }

    public final synchronized int addUser(UserDTO userDTO) {
        Connection conInsertUser = null;

        try {

            conInsertUser = getDataSource().getConnection();

            int userCode = this.getNextCodeUser();

            final String sqlInsertUser =
                    " INSERT INTO users(userid, username, type_id, address, number, completion, zip_code, city, state, social_id_number, dlicense, " +
                    " email, tel_ref_1, tel_ref_2, extension_line, cellphone, obs, birthday, signup_date, alter_date, renew_date, whosignup, status, " +
                    " usernameascii, photo_id, user_type ) " + 
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, to_date(?, 'DD/MM/YYYY'), current_date, current_date, current_date, 10001, 'ACTIVE', ?, ?, ?); ";

            PreparedStatement pstInsertUser = conInsertUser.prepareStatement(sqlInsertUser);

            pstInsertUser.setInt(1, userCode);
            pstInsertUser.setString(2, userDTO.getName());
            pstInsertUser.setString(3, userDTO.getTypeId());
            pstInsertUser.setString(4, userDTO.getAddress());
            pstInsertUser.setString(5, userDTO.getNumber());
            pstInsertUser.setString(6, userDTO.getCompletion());
            pstInsertUser.setString(7, userDTO.getZip_code());
            pstInsertUser.setString(8, userDTO.getCity());
            pstInsertUser.setString(9, userDTO.getState());
            pstInsertUser.setString(10, userDTO.getSocial_id_number());
            pstInsertUser.setString(11, userDTO.getDlicense());
            pstInsertUser.setString(12, userDTO.getEmail());
            pstInsertUser.setString(13, userDTO.getTelRef1());
            pstInsertUser.setString(14, userDTO.getTelRef2());
            pstInsertUser.setString(15, userDTO.getExtension_line());
            pstInsertUser.setString(16, userDTO.getCellphone());
            pstInsertUser.setString(17, userDTO.getObs());
            pstInsertUser.setString(18, userDTO.getBirthday());
            pstInsertUser.setString(19, userDTO.getUsernameascii());
            pstInsertUser.setString(20, userDTO.getPhoto());
            pstInsertUser.setInt(21, userDTO.getUserType());

            pstInsertUser.executeUpdate();

            return userCode;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conInsertUser);
        }
    }

    public synchronized int getNextCodeUser() throws Exception {

        int seq = 0;
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            String sql = "SELECT nextval('code_users_seq') as userid;";

            PreparedStatement pst = con.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            if (rs != null && rs.next()) {
                seq = rs.getInt("userid");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return seq;
    }

    public final void deleteUser(int userid) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "UPDATE users SET status = 'INACTIVE', alter_date = current_date WHERE userid = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userid);

            pst.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public final boolean blockUser(int userid, boolean block) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String status = block ? UserStatus.BLOCKED.name() : UserStatus.ACTIVE.name();
            final String sql = "UPDATE users SET status = '" + status + "', alter_date = current_date WHERE userid = ? ; ";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userid);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }


    public final synchronized void updateUser(UserDTO userDTO, int userid) {
        Connection conUpdateUser = null;
        Connection conDeleteUseridx = null;

        try {

            conUpdateUser = getDataSource().getConnection();
            conDeleteUseridx = getDataSource().getConnection();

            final String sqlUpdateUser =
                    " UPDATE users SET " +
                    " username = ?, " +
                    " type_id = ?, " +
                    " address = ?, " +
                    " number = ?, " +
                    " completion = ?, " +
                    " zip_code = ?, " +
                    " city = ?, " +
                    " state = ?,  " +
                    " social_id_number = ?, " +
                    " dlicense = ?, " +
                    " email = ?, " +
                    " tel_ref_1 = ?, " +
                    " birthday = to_date(?, 'DD/MM/YYYY'), " +
                    " tel_ref_2 = ?, " +
                    " obs = ?, " +
                    " alter_date = current_date, " +
                    " whosignup = 10001, " +
                    " extension_line = ?, " +
                    " cellphone = ?, " +
                    " usernameascii = ?," +
                    " photo_id = ?, " +
                    " user_type = ? " +
                    " WHERE userid = ?;";

            PreparedStatement pstUpdateUser = conUpdateUser.prepareStatement(sqlUpdateUser);

            pstUpdateUser.setString(1, userDTO.getName());
            pstUpdateUser.setString(2, userDTO.getTypeId());
            pstUpdateUser.setString(3, userDTO.getAddress());
            pstUpdateUser.setString(4, userDTO.getNumber());
            pstUpdateUser.setString(5, userDTO.getCompletion());
            pstUpdateUser.setString(6, userDTO.getZip_code());
            pstUpdateUser.setString(7, userDTO.getCity());
            pstUpdateUser.setString(8, userDTO.getState());
            pstUpdateUser.setString(9, userDTO.getSocial_id_number());
            pstUpdateUser.setString(10, userDTO.getDlicense());
            pstUpdateUser.setString(11, userDTO.getEmail());
            pstUpdateUser.setString(12, userDTO.getTelRef1());
            pstUpdateUser.setString(13, userDTO.getBirthday());
            pstUpdateUser.setString(14, userDTO.getTelRef2());
            pstUpdateUser.setString(15, userDTO.getObs());
            pstUpdateUser.setString(16, userDTO.getExtension_line());
            pstUpdateUser.setString(17, userDTO.getCellphone());
            pstUpdateUser.setString(18, userDTO.getUsernameascii());
            pstUpdateUser.setString(19, userDTO.getPhoto());
            pstUpdateUser.setInt(20, userDTO.getUserType());
            pstUpdateUser.setInt(21, userid);

            pstUpdateUser.executeUpdate();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(conUpdateUser);
            closeConnection(conDeleteUseridx);
        }
    }

    public Collection<UserTypeDTO> findAllUserType() {
        Collection<UserTypeDTO> listUserType = new ArrayList();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM users_type ORDER BY usertype, serial;";

            final PreparedStatement pst = con.prepareStatement(sql);
            final ResultSet rs = pst.executeQuery();

            while (rs != null && rs.next()) {
                listUserType.add(this.populateUserType(rs));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return listUserType;
    }

    public final UserTypeDTO findUserTypeById(final Integer serial) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM users_type WHERE serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, serial);

            final ResultSet rs = pst.executeQuery();

            while (rs != null && rs.next()) {
                return populateUserType(rs);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return null;
    }

    public final boolean existsUserCard(int userid, String userType) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT serial_card FROM users_cards WHERE user_id = ? AND user_type = ?;";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userid);
            pst.setString(2, userType);

            ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                Integer serial = rs.getInt(1);
                return serial > 0;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
        return false;
    }

    public final boolean insertUserCard(UserCardDTO userCard) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "INSERT INTO users_cards (user_name, user_type, user_id) VALUES (?, ?, ?);";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, userCard.getUserName());
            pst.setString(2, userCard.getUserType());
            pst.setInt(3, userCard.getUserId());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }

    public Integer countUsersByUserType(Integer userTypeId) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT count(userid) from USERS where user_type = ? and status <> 'INACTIVE';";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userTypeId);
            ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
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

    public Integer countUserCards() {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT COUNT(serial_card) FROM users_cards;";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs != null && rs.next()) {
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

    
    public boolean deleteUserCards(String[] cards) {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM users_cards WHERE serial_card IN (");
            for (int i = 0; i < cards.length; i++) {
                if (i != (cards.length - 1)) {
                    sql.append("?,  ");
                } else {
                    sql.append("?");
                }
            }
            sql.append(");");
            PreparedStatement pst = con.prepareStatement(sql.toString());
            int counter = 1;
            for (String label : cards) {
                pst.setInt(counter++, Integer.parseInt(label));
            }
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }
    }


    private UserTypeDTO populateUserType(final ResultSet rs) throws SQLException {
        final UserTypeDTO userTypeDTO = new UserTypeDTO();

        userTypeDTO.setSerial(rs.getInt("serial"));
        userTypeDTO.setDescription(rs.getString("description").trim());
        userTypeDTO.setMaxLendingCount(rs.getInt("number_max_itens"));
        userTypeDTO.setMaxLendingDays(rs.getInt("time_returned"));
        userTypeDTO.setMaxReservationDays(rs.getInt("max_reservation_days"));
        userTypeDTO.setName(rs.getString("usertype").trim());

        return userTypeDTO;
    }

    public final ArrayList<UserDTO> getUsersByDate(final String startDate, final String endDate) {
        ArrayList<UserDTO> listUsers = new ArrayList<UserDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT * FROM users "
                             + "WHERE status = 'ACTIVE' AND alter_date BETWEEN to_date(?, 'YYYY-MM-DD') "
                             + "AND to_date(?, 'YYYY-MM-DD') + 1 ORDER BY username";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, startDate);
            pst.setString(2, endDate);
            ResultSet rs = pst.executeQuery();

            while (rs != null && rs.next()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setName(rs.getString("username"));
                userDTO.setUserid(rs.getInt("userid"));
                userDTO.setUserType(rs.getInt("user_type"));

                listUsers.add(userDTO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return listUsers;
    }

    public final ArrayList<UserCardDTO> listPendingUserCards() {
        ArrayList<UserCardDTO> listCards = new ArrayList<UserCardDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();
            String sql = "SELECT * FROM users_cards ORDER BY user_name ASC;";
            final ResultSet rs = con.createStatement().executeQuery(sql);

            while (rs != null && rs.next()) {
                UserCardDTO udto = new UserCardDTO();
                udto.setSerialCard(rs.getInt("serial_card"));
                udto.setUserId(rs.getInt("user_id"));
                udto.setUserName(rs.getString("user_name"));
                udto.setUserType(rs.getString("user_type"));

                listCards.add(udto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return listCards;
    }

    public final ArrayList<UserCardDTO> listSelectedUserCards(String[] cards) {
        ArrayList<UserCardDTO> listCards = new ArrayList<UserCardDTO>();
        Connection con = null;

        try {
            con = getDataSource().getConnection();

            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM users_cards WHERE serial_card in ( ");

            for (int i = 0; i < cards.length; i++) {
                if (i != (cards.length - 1)) {
                    sql.append("?,  ");
                } else {
                    sql.append("?");
                }
            }

            sql.append(") ORDER BY user_name;");

            final PreparedStatement pst = con.prepareStatement(sql.toString());
            int index = 1;

            for (String serial : cards) {
                pst.setInt(index++, Integer.parseInt(serial));
            }

            final ResultSet rs = pst.executeQuery();

            if (rs != null) {
                while (rs.next()) {
                    UserCardDTO udto = new UserCardDTO();
                    udto.setSerialCard(rs.getInt("serial_card"));
                    udto.setUserId(rs.getInt("user_id"));
                    udto.setUserName(rs.getString("user_name"));
                    udto.setUserType(rs.getString("user_type"));

                    listCards.add(udto);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DAOException(e.getMessage());
        } finally {
            closeConnection(con);
        }

        return listCards;
    }

}
