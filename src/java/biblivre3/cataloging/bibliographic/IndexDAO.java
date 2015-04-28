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

package biblivre3.cataloging.bibliographic;

import biblivre3.enums.IndexTable;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import mercury.DAO;
import mercury.ExceptionUser;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since 17/02/2009
 */
public final class IndexDAO extends DAO {

	public final boolean insert(IndexTable table, List<IndexDTO> indexList) {
		if (indexList == null && indexList.isEmpty()) {
			return false;
		}

		Connection con = null;
		try {
			con = getDataSource().getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO ").append(table.getTableName());
			sql.append(" (index_word, record_serial) ");
			sql.append(" VALUES (?, ?);");

			PreparedStatement pst = con.prepareStatement(sql.toString());

			for (IndexDTO index : indexList) {
				pst.setString(1, StringUtils.substring(index.getWord(), 0, 511));
				pst.setInt(2, index.getRecordSerial());
				pst.addBatch();
			}

			pst.executeBatch();
		} catch (BatchUpdateException bue) {
			log.error(bue.getNextException(), bue);
			throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
		} finally {
			closeConnection(con);
		}
		return true;
	}

	public final boolean clearIndex(final IndexTable table) {
		Connection con = null;
		try {
			con = getDataSource().getConnection();
			final String sql = " TRUNCATE TABLE " + table.getTableName() + ";";

			return con.createStatement().execute(sql);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
		} finally {
			closeConnection(con);
		}
	}

	public final boolean delete(final IndexTable table, final IndexDTO index) {
		Connection con = null;
		try {
			con = getDataSource().getConnection();
			final String sql = " DELETE FROM " + table.getTableName()
					+ " WHERE record_serial = ?;";

			final PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, index.getRecordSerial());

			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ExceptionUser("ERROR_BIBLIO_DAO_EXCEPTION");
		} finally {
			closeConnection(con);
		}
	}
}