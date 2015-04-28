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

package biblivre3.administration;

import biblivre3.administration.reports.ReportUtils;
import biblivre3.administration.reports.dto.AllUsersReportDto;
import biblivre3.administration.reports.dto.AssetHoldingByDateDto;
import biblivre3.administration.reports.dto.AssetHoldingDto;
import biblivre3.administration.reports.dto.BibliographyReportDto;
import biblivre3.administration.reports.dto.DelayedLendingsDto;
import biblivre3.administration.reports.dto.DeweyReportDto;
import biblivre3.administration.reports.dto.LendingsByDateReportDto;
import biblivre3.administration.reports.dto.HoldingCreationByDateReportDto;
import biblivre3.administration.reports.dto.RequestsByDateReportDto;
import biblivre3.administration.reports.dto.ReservationReportDto;
import biblivre3.administration.reports.dto.SearchesByDateReportDto;
import biblivre3.administration.reports.dto.SummaryReportDto;
import biblivre3.cataloging.bibliographic.BiblioDAO;
import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.enums.Database;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.DateUtils;
import biblivre3.utils.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import mercury.DAO;
import mercury.ExceptionUser;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.marc.Record;

public class ReportsDAO extends DAO {

    private BiblioDAO biblioDao;

    public ReportsDAO() {
        biblioDao = new BiblioDAO();
    }

    public SearchesByDateReportDto getSearchesByDateReportData(String initialDate, String finalDate) {
        Connection con = null;
        SearchesByDateReportDto dto = new SearchesByDateReportDto();
        try {
            con = getDataSource().getConnection();
            final String sql =
                    " select count(search_date), to_char(search_date, 'YYYY-MM-DD')" +
                    " from search_counter " +
                    " WHERE search_date >= to_date(?, 'DD-MM-YYYY') " +
                    " and search_date <= to_date(?, 'DD-MM-YYYY') " +
                    " group by to_char(search_date, 'YYYY-MM-DD') " +
                    " order by to_char(search_date, 'YYYY-MM-DD') ASC;";
            final PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            final ResultSet rs = st.executeQuery();
            dto.setInitialDate(initialDate);
            dto.setFinalDate(finalDate);
            List<String[]> data = new ArrayList<String[]>();
            dto.setData(data);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                String[] arrayData = new String[2];
                arrayData[0] = rs.getString(1);
                Date date = format.parse(rs.getString(2));
                arrayData[1] = DateUtils.dd_MM_yyyy.format(date);
                dto.getData().add(arrayData);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public LendingsByDateReportDto getLendingsByDateReportData(String initialDate, String finalDate) {
        Connection con = null;
        PreparedStatement st = null;
        LendingsByDateReportDto dto = new LendingsByDateReportDto();
        dto.setInitialDate(initialDate);
        dto.setFinalDate(finalDate);
        String sqlHistory =
                " select count(*) from lending_history "
                + " WHERE lending_date >= to_date(?, 'DD-MM-YYYY') "
                + " and lending_date <= to_date(?, 'DD-MM-YYYY');";
        String sqlLent =
                " select count(*) from lending "
                + " WHERE lending_date >= to_date(?, 'DD-MM-YYYY') "
                + " and lending_date <= to_date(?, 'DD-MM-YYYY');";
        String sqlLate =
                " select count(*) from lending "
                + " where return_date < to_date(?, 'DD-MM-YYYY') ";
        String sqlTop20 =
                " select b.record_serial, count(b.record_serial) as rec_count "
                + " from lending l, cataloging_biblio b, cataloging_holdings h "
                + " where l.holding_serial = h.holding_serial "
                + " and h.record_serial = b.record_serial "
                + " group by b.record_serial "
                + " order by rec_count desc "
                + " limit 20;";
        int lended = 0, late = 0, total = 0;
        try {
            con = getDataSource().getConnection();
            st = con.prepareStatement(sqlLent);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            ResultSet rs = st.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    lended = rs.getInt(1);
                }
            }
            rs.close();
            st = con.prepareStatement(sqlHistory);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    total = rs.getInt(1) + lended;
                }
            }
            rs.close();
            st = con.prepareStatement(sqlLate);
            st.setString(1, DateUtils.dd_MM_yyyy.format(new Date()));
            rs = st.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    late = rs.getInt(1);
                }
            }
            rs.close();

            String[] totals = {
                String.valueOf(total),
                String.valueOf(lended),
                String.valueOf(late)
            };
            dto.setTotals(totals);

            st = con.prepareStatement(sqlTop20);
            rs = st.executeQuery();
            if (rs != null) {
                List<String[]> data = new ArrayList<String[]>();
                while (rs.next()) {
                    Integer biblioId = rs.getInt(1);
                    Integer count = rs.getInt(2);
                    RecordDTO recordDto = biblioDao.getById(biblioId);
                    Record record = MarcUtils.iso2709ToRecord(recordDto.getIso2709());
                    String[] arrayData = new String[3];
                    arrayData[0] = String.valueOf(count);//count
                    arrayData[1] = Indexer.listOneTitle(record);//title
                    arrayData[2] = Indexer.listPrimaryAuthor(record);//author
                    data.add(arrayData);
                }
                dto.setData(data);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public AllUsersReportDto getAllUsersReportData() {
        String firstSql = "SELECT count(u.user_type) as total, t.description, t.serial "
                        + "FROM users u, users_type t "
                        + "WHERE u.user_type = t.serial "
                        + "GROUP BY u.user_type, t.description, t.serial "
                        + "ORDER BY t.description;";

        Connection con = null;
        Connection con2 = null;

        AllUsersReportDto dto = new AllUsersReportDto();
        dto.setTypesMap(new HashMap<String, Integer>());
        dto.setData(new HashMap<String, List<String>>());
        try {
            con = getDataSource().getConnection();
            con2 = getDataSource().getConnection();
            ResultSet rs = con.createStatement().executeQuery(firstSql);

            if (rs != null) {
                while (rs.next()) {
                    final String description = rs.getString("description");
                    final Integer count = rs.getInt("total");
                    dto.getTypesMap().put(description, count);

                    String secondSql = "SELECT username, userid, signup_date, alter_date from users "
                                     + "WHERE user_type = '" + rs.getString("serial") + "' "
                                     + "ORDER BY username; ";

                    ResultSet rs2 = con.createStatement().executeQuery(secondSql);
                    if (rs2 != null) {
                        List<String> dataList = new ArrayList<String>();
                        while (rs2.next()) {
                            dataList.add(rs2.getString("username") + "\t" + rs2.getInt("userid") + "\t" + rs2.getString("signup_date") + "\t" + rs2.getString("alter_date") + "\n");
                        }
                        dto.getData().put(description, dataList);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
            closeConnection(con2);
        }
        return dto;
    }

    public DeweyReportDto getDeweyReportData(Database db, String datafield, int digits) {
        Connection con = null;
        DeweyReportDto dto = new DeweyReportDto();
        try {
            con = getDataSource().getConnection();
            final String sql =
                    "SELECT b.record, count(h.holding_serial) as holdings FROM cataloging_biblio b "
                    + "LEFT OUTER JOIN cataloging_holdings h "
                    + "ON b.record_serial = h.record_serial "
                    + "WHERE b.database = ? "
                    + "GROUP BY b.record; ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, db.ordinal());

            final ResultSet rs = pst.executeQuery();
            Map<String, Integer[]> acc = new HashMap<String, Integer[]>();

            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getString("record"));
                
                String dewey = "";
                
                if (datafield.equals("082")) {
                    dewey = Indexer.listDDCN(record);
                } else if (datafield.equals("090")) {
                    dewey = Indexer.listLocation(record)[0];
                }
                
                String formattedDewey = ReportUtils.formatDeweyString(dewey, digits);

                Integer numberOfHoldings = rs.getInt("holdings");
                Integer[] totals = acc.get(formattedDewey);

                if (totals == null) {
                    acc.put(formattedDewey, new Integer[]{1, numberOfHoldings});
                } else {
                    Integer[] newTotals = new Integer[]{totals[0] + 1, totals[1] + numberOfHoldings};
                    acc.put(formattedDewey, newTotals);
                }
            }

            List<String[]> data = new ArrayList<String[]>();
            dto.setData(data);
            for (String key : acc.keySet()) {
                String[] arrayData = new String[3];
                arrayData[0] = key;
                Integer[] totals = acc.get(key);
                arrayData[1] = String.valueOf(totals[0]);
                arrayData[2] = String.valueOf(totals[1]);
                dto.getData().add(arrayData);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public DelayedLendingsDto getLateReturnLendingsReportData() {
        DelayedLendingsDto dto = new DelayedLendingsDto();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            final String sql = "SELECT u.userid, u.username, l.return_date, b.record "
                             + "FROM lending l, users u, cataloging_biblio b, cataloging_holdings h "
                             + "WHERE l.return_date < to_date(?, 'DD-MM-YYYY') "
                             + "AND l.user_serial = u.userid "
                             + "AND l.holding_serial = h.holding_serial "
                             + "AND h.record_serial = b.record_serial ";
            final PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, DateUtils.dd_MM_yyyy.format(new Date()));

            final ResultSet rs = st.executeQuery();
            List<String[]> data = new ArrayList<String[]>();

            while (rs.next()) {
                String[] lending = new String[4];
                lending[0] = String.valueOf(rs.getInt("userid")); // matricula do usuario
                lending[1] = rs.getString("username"); //nome do usuario
                lending[2] = Indexer.listOneTitle(MarcUtils.iso2709ToRecord(new String(rs.getBytes("record"), "UTF-8"))); //titulo
                lending[3] = DateUtils.dd_MM_yyyy.format(rs.getDate("return_date"));
                data.add(lending);
            }
            dto.setData(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }

        return dto;
    }

    public BibliographyReportDto getBibliographyReportData(String authorName, Integer[] recordIdArray) {
        BibliographyReportDto dto = new BibliographyReportDto();
        dto.setAuthorName(authorName);
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = " select record from cataloging_biblio where record_serial in (";
            for (int i = 0; i < recordIdArray.length; i++) {
                sql += "?, ";
            }
            sql = sql.substring(0, sql.length() - 2);
            sql += ") order by record_serial asc ";
            PreparedStatement st = con.prepareStatement(sql);
            for (int i = 0; i < recordIdArray.length; i++) {
                st.setInt(i + 1, recordIdArray[i]);
            }
            final ResultSet rs = st.executeQuery();
            List<String[]> data = new ArrayList<String[]>();
            while (rs.next()) {
                String iso2709 = new String(rs.getBytes("record"), "UTF-8");
                Record record = MarcUtils.iso2709ToRecord(iso2709);
                String[] lending = new String[5];
                lending[0] = Indexer.listOneTitle(record);
                lending[1] = Indexer.listEdition(record);
                String[] publication = Indexer.listPublicationFull(record);
                lending[2] = publication[1];
                lending[3] = publication[2];
                String[] location = Indexer.listLocation(record);
                lending[4] = StringUtils.join(location, "\n").replaceAll("\\n+", "\n");
                data.add(lending);
            }
            dto.setData(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public ReservationReportDto getReservationReportData() {
        ReservationReportDto dto = new ReservationReportDto();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            Statement st = con.createStatement();
            String sql =
                    " select u.username, u.userid, b.record, "
                    + " to_char(r.created, 'DD/MM/YYYY') as created "
                    + " from reservation r, users u, cataloging_biblio b "
                    + " where r.userid = u.userid "
                    + " and r.record_serial = b.record_serial  "
                    + " and r.record_serial is not null "
                    + " order by u.username asc;  ";
            ResultSet rs = st.executeQuery(sql);
            List<String[]> biblioReservations = new ArrayList<String[]>();
            while (rs.next()) {
                String[] reservation = new String[5];
                reservation[0] = rs.getString("username");
                reservation[1] = String.valueOf(rs.getInt("userid"));
                String iso2709 = new String(rs.getBytes("record"), "UTF-8");
                Record record = MarcUtils.iso2709ToRecord(iso2709);
                reservation[2] = Indexer.listOneTitle(record);
                reservation[3] = Indexer.listPrimaryAuthor(record);
                reservation[4] = rs.getString("created");
                biblioReservations.add(reservation);
            }
            dto.setBiblioReservations(biblioReservations);

//            String sql2 =
//                    " select u.username, u.userid, b.record, "
//                    + " to_char(r.created, 'DD/MM/YYYY') as created "
//                    + " from reservation r, users u, cataloging_holdings h, cataloging_biblio b "
//                    + " where r.userid = u.userid "
//                    + " and r.holding_serial = h.holding_serial "
//                    + " and h.record_serial = b.record_serial "
//                    + " and r.holding_serial is not null "
//                    + " and h.database = '0' order by u.username asc; ";
//            rs = st.executeQuery(sql2);
//            List<String[]> holdingReservations = new ArrayList<String[]>();
//            while (rs.next()) {
//                String[] reservation = new String[5];
//                reservation[0] = rs.getString("username");
//                reservation[1] = String.valueOf(rs.getInt("userid"));
//                String iso2709 = new String(rs.getBytes("record"), "UTF-8");
//                Record record = MarcUtils.iso2709ToRecord(iso2709);
//                reservation[2] = Indexer.listTitle(record);
//                reservation[3] = Indexer.listPrimaryAuthor(record);
//                reservation[4] = rs.getString("created");
//                holdingReservations.add(reservation);
//            }
//            dto.setHoldingReservations(holdingReservations);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public HoldingCreationByDateReportDto getHoldingCreationByDateReportData(String initialDate, String finalDate) {
        HoldingCreationByDateReportDto dto = new HoldingCreationByDateReportDto();
        dto.setInitialDate(initialDate);
        dto.setFinalDate(finalDate);
        Connection con = null;
        String totalBiblioMain = "0";
        String totalBiblioWork = "0";
        String totalHoldingMain = "0";
        String totalHoldingWork = "0";
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT to_char(creation_date, 'DD/MM/YYYY'), user_name, count(user_id) "
                    + " from holding_creation_counter "
                    + " WHERE creation_date >= to_date(?, 'DD-MM-YYYY') "
                    + " and creation_date <= to_date(?, 'DD-MM-YYYY') "
                    + " group by user_name, to_char(creation_date, 'DD/MM/YYYY');";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            ResultSet rs = st.executeQuery();
            List<String[]> data = new ArrayList<String[]>();
            while (rs.next()) {
                String[] arrayData = new String[4];
                arrayData[0] = rs.getString(1); // data
                arrayData[1] = rs.getString(2); // nome
                arrayData[2] = rs.getString(3); // total
                data.add(arrayData);
            }
            dto.setData(data);

            String biblioMainSql =
                    " SELECT COUNT(record_serial) FROM cataloging_biblio "
                    + " WHERE database = 0 AND created >= to_date(?, 'DD-MM-YYYY') "
                    + " AND created <= to_date(?, 'DD-MM-YYYY'); ";
            st = con.prepareStatement(biblioMainSql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalBiblioMain = rs.getString(1);
            }


            String biblioWorkSql =
                    " SELECT COUNT(record_serial) FROM cataloging_biblio "
                    + " WHERE database = 1 AND created >= to_date(?, 'DD-MM-YYYY') "
                    + " AND created <= to_date(?, 'DD-MM-YYYY'); ";
            st = con.prepareStatement(biblioWorkSql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalBiblioWork = rs.getString(1);
            }

            String holdingMainSql =
                    " SELECT COUNT(*) FROM cataloging_holdings "
                    + " WHERE database = " + Database.MAIN.ordinal()
                    + " AND created >= to_date(?, 'DD-MM-YYYY') "
                    + " AND created <= to_date(?, 'DD-MM-YYYY'); ";
            st = con.prepareStatement(holdingMainSql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalHoldingMain = rs.getString(1);
            }

            String holdingWorkSql =
                    " SELECT COUNT(*) FROM cataloging_holdings "
                    + " WHERE database = " + Database.WORK.ordinal()
                    + " AND created >= to_date(?, 'DD-MM-YYYY') "
                    + " AND created <= to_date(?, 'DD-MM-YYYY'); ";
            st = con.prepareStatement(holdingWorkSql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            rs = st.executeQuery();
            if (rs != null && rs.next()) {
                totalHoldingWork = rs.getString(1);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        dto.setTotalBiblioMain(totalBiblioMain);
        dto.setTotalBiblioWork(totalBiblioWork);
        dto.setTotalHoldingMain(totalHoldingMain);
        dto.setTotalHoldingWork(totalHoldingWork);
        return dto;
    }

    public RequestsByDateReportDto getRequestsByDateReportData(String initialDate, String finalDate) {
        RequestsByDateReportDto dto = new RequestsByDateReportDto();
        dto.setInitialDate(initialDate);
        dto.setFinalDate(finalDate);
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = "SELECT DISTINCT b.serial_order, r.requester, r.item_title, r.quantity, i.unit_value, b.total_value "
                    + " FROM acquisition_order b, acquisition_requisition r, acquisition_item_quotation i "
                    + " WHERE b.serial_quotation = i.serial_quotation "
                    + " AND r.serial_requisition = i.serial_requisition "
                    + " AND r.requisition_date >= to_date(?, 'DD-MM-YYYY') "
                    + " AND r.requisition_date <= to_date(?, 'DD-MM-YYYY') "
                    + " ORDER BY b.serial_order;";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, initialDate);
            st.setString(2, finalDate);
            final ResultSet rs = st.executeQuery();
            List<String[]> dataList = new ArrayList<String[]>();
            dto.setData(dataList);
            while (rs.next()) {
                String[] data = new String[6];
                data[0] = rs.getString("serial_order");
                data[1] = rs.getString("requester");
                data[2] = rs.getString("item_title");
                data[3] = rs.getString("quantity");
                data[4] = rs.getString("unit_value");
                data[5] = rs.getString("total_value");
                dataList.add(data);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public SummaryReportDto getSummaryReportData(Database database) {
        SummaryReportDto dto = new SummaryReportDto();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = "SELECT record, record_serial FROM cataloging_biblio WHERE database = ?;";
            String countSql = "SELECT count(holding_serial) FROM cataloging_holdings WHERE record_serial = ?;";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setFetchSize(100);

            final PreparedStatement count = con.prepareStatement(countSql);

            pst.setInt(1, database.ordinal());
            
            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<String[]>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes("record"));
                String[] data = new String[8];
                data[0] = Indexer.listOneTitle(record);
                data[1] = Indexer.listAuthors(record);
                data[2] = Indexer.listIsbn(record);
                data[3] = Indexer.listEditor(record);// editora(50)
                data[4] = Indexer.listYearOfPublication(record);// ano(20)
                data[5] = Indexer.listEdition(record);
                data[6] = Indexer.listLocation(record)[0];

                count.setInt(1, rs.getInt("record_serial"));
                ResultSet countRs = count.executeQuery();
                countRs.next();
                data[7] = countRs.getString(1);
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public AssetHoldingDto getAssetHoldingReportData() {
        AssetHoldingDto dto = new AssetHoldingDto();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql = 
                    " SELECT H.asset_holding, R.record FROM cataloging_holdings H INNER JOIN cataloging_biblio R " +
                    " ON R.record_serial = H.record_serial WHERE H.database = 0 " +
                    " ORDER BY H.asset_holding ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setFetchSize(100);

            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<String[]>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes("record"));
                String assetHolding = rs.getString("asset_holding");
                String[] data = new String[5];
                data[0] = assetHolding;
                data[1] = Indexer.listPrimaryAuthor(record);
                data[2] = Indexer.listOneTitle(record);
                data[3] = Indexer.listEdition(record);
                data[4] = Indexer.listYearOfPublication(record);
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public AssetHoldingByDateDto getAssetHoldingByDateReportData(String initialDate, String finalDate) {
        AssetHoldingByDateDto dto = new AssetHoldingByDateDto();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT H.asset_holding, to_char(H.created, 'DD/MM/YYYY'), R.record, H.record " +
                    " FROM cataloging_holdings H INNER JOIN cataloging_biblio R " +
                    " ON R.record_serial = H.record_serial WHERE H.database = 0 " +
                    " AND H.created >= to_date(?, 'DD-MM-YYYY') " +
                    " AND H.created <= to_date(?, 'DD-MM-YYYY') " +
                    " ORDER BY H.created, H.asset_holding ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, initialDate);
            pst.setString(2, finalDate);
            pst.setFetchSize(100);

            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<String[]>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes(3));
                Record holding = MarcUtils.iso2709ToRecord(rs.getBytes(4));
                String assetHolding = rs.getString("asset_holding");
                String creationDate = rs.getString(2);
                String[] data = new String[6];
                data[0] = creationDate;
                data[1] = assetHolding;
                data[2] = Indexer.listOneTitle(record);
                data[3] = Indexer.listPrimaryAuthor(record);
                data[4] = Indexer.listYearOfPublication(record);
                data[5] = Indexer.listSourceAcquisitionDate(holding);
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public AssetHoldingDto getAssetHoldingFullReportData() {
        AssetHoldingDto dto = new AssetHoldingDto();
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            String sql =
                    " SELECT H.holding_serial, H.asset_holding, R.record FROM cataloging_holdings H INNER JOIN cataloging_biblio R " +
                    " ON R.record_serial = H.record_serial WHERE H.database = 0 " +
                    " ORDER BY H.asset_holding ";

            final PreparedStatement pst = con.prepareStatement(sql);
            pst.setFetchSize(100);

            final ResultSet rs = pst.executeQuery();
            List<String[]> dataList = new ArrayList<String[]>();
            while (rs.next()) {
                Record record = MarcUtils.iso2709ToRecord(rs.getBytes("record"));
                String assetHolding = rs.getString("asset_holding");
                String serial = rs.getString("holding_serial");
                String[] data = new String[7];
                data[0] = serial;
                data[1] = assetHolding;
                data[2] = Indexer.listOneTitle(record);
                data[3] = Indexer.listPrimaryAuthor(record);
                data[4] = Indexer.listFormattedLocation(record);
                data[5] = Indexer.listEdition(record);
                data[6] = Indexer.listYearOfPublication(record);
                dataList.add(data);
            }
            dto.setData(dataList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return dto;
    }

    public TreeMap<String, Set<Integer>> searchAuthors(String authorName) {
        TreeMap<String, Set<Integer>> results = new TreeMap<String, Set<Integer>>();

        String[] terms = authorName.split(" ");
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT B.record_serial, B.record FROM cataloging_biblio B ");
            sql.append("INNER JOIN idx_author I ON I.record_serial = B.record_serial WHERE B.database = 0 ");

            for (int i = 0; i < terms.length; i++) {
                sql.append("AND B.record_serial in (SELECT record_serial FROM idx_author WHERE index_word >= ? and index_word < ?) ");
            }

            PreparedStatement st = con.prepareStatement(sql.toString());
            int index = 1;
            for (int i = 0; i < terms.length; i++) {
                st.setString(index++, terms[i]);
                st.setString(index++, TextUtils.incrementLastChar(terms[i]));
            }

            ResultSet rs = st.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    Integer id = rs.getInt("record_serial");
                    String iso2709 = new String(rs.getBytes("record"), "UTF-8");
                    Record record = MarcUtils.iso2709ToRecord(iso2709);
                    String name = Indexer.listPrimaryAuthor(record);
                    if (results.containsKey(name)) {
                        Set<Integer> ids = results.get(name);
                        ids.add(id);
                    } else {
                        Set<Integer> ids = new HashSet<Integer>();
                        ids.add(id);
                        results.put(name, ids);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExceptionUser("ERROR_REPORT_DAO_EXCEPTION");
        } finally {
            closeConnection(con);
        }
        return results;
    }
}
