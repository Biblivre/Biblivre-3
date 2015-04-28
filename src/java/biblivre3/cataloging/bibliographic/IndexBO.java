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
import biblivre3.enums.MaterialType;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mercury.BaseBO;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.marc.Record;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since 17/02/2009
 */
public final class IndexBO extends BaseBO {

	private final IndexTable[] INDEXABLE_TABLES = {
		IndexTable.AUTHOR,
		IndexTable.TITLE,
		IndexTable.SUBJECT,
		IndexTable.ISBN,
		IndexTable.ISSN,
		IndexTable.YEAR
	};

	public final boolean clearIndex(final IndexTable table) {
		final IndexDAO dao = new IndexDAO();
		return dao.clearIndex(table);
	}

	public final boolean clearAllIndexes() {
		final IndexDAO dao = new IndexDAO();
		boolean result = true;

		for (IndexTable table : IndexTable.values()) {
			result &= dao.clearIndex(table);
		}

		return result;
	}

	public final boolean reindexBase() {
		this.clearAllIndexes();
		final BiblioDAO biblioDao = new BiblioDAO();
		final int limit = 30;
		final int recordCount = biblioDao.countAll(null);

		for (int offset = 0; offset < recordCount; offset += limit) {
			Map<IndexTable, List<IndexDTO>> indexMap = new HashMap<IndexTable, List<IndexDTO>>();
			ArrayList<RecordDTO> records = biblioDao.list(null, MaterialType.ALL, offset, limit, true);
			this.populateIndexMap(records, indexMap);
			for (IndexTable table : indexMap.keySet()) {
				this.insertIndex(table, indexMap.get(table));
			}
		}
		return true;
	}

	@SuppressWarnings("UnusedAssignment")
	private void populateIndexMap(ArrayList<RecordDTO> records, Map<IndexTable, List<IndexDTO>> indexMap) {
		for (RecordDTO dto : records) {
			Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
			Integer serial = dto.getRecordSerial();
			for (IndexTable table : INDEXABLE_TABLES) {
				List<IndexDTO> indexList = indexMap.get(table);
				if (indexList == null) {
					indexList = new ArrayList<IndexDTO>();
					indexMap.put(table, indexList);
				}
				List<IndexDTO> anyList = indexMap.get(IndexTable.ANY);
				if (anyList == null) {
					anyList = new ArrayList<IndexDTO>();
					indexMap.put(IndexTable.ANY, anyList);
				}
				List<IndexDTO> insertList = this.getIndexList(table, record, serial);
				indexList.addAll(insertList);
				anyList.addAll(insertList);
			}
			this.populateSortIndex(record, serial, indexMap);
		}
	}
	
	private void populateSortIndex(Record record, Integer serial, Map<IndexTable, List<IndexDTO>> indexMap) {
		List<IndexDTO> sortIndex = indexMap.get(IndexTable.SORT);
		if (sortIndex == null) {
			sortIndex = new ArrayList<IndexDTO>();
			indexMap.put(IndexTable.SORT, sortIndex);
		}			
		final StringBuilder sb = new StringBuilder();
		String title = Indexer.listTitle(record);
		sb.append(title).append(" ");
		sb.append(Indexer.listEdition(record)).append(" ");
		String[] location = Indexer.listLocation(record);
		for (String loc : location) {
			sb.append(loc).append(" ");
		}
		IndexDTO index = new IndexDTO();
		index.setRecordSerial(serial);
		index.setWord(TextUtils.removeDiacriticals(sb.toString()));
		sortIndex.add(index);		
	}

	private List<IndexDTO> getIndexList(IndexTable table, Record record, Integer serial) {
		List<IndexDTO> indexList = new ArrayList<IndexDTO>();
		String[] words = this.getIndexValue(table, record);
		for (String word : words) {
			if (StringUtils.isNotBlank(word)) {
				IndexDTO index = new IndexDTO();
				index.setRecordSerial(serial);
				index.setWord(TextUtils.removeDiacriticals(word).toLowerCase());
				indexList.add(index);
			}
		}
		return indexList;
	}

	private String[] getIndexValue(IndexTable table, Record record) {
		String value = "";
		switch (table) {
			case AUTHOR: {
				value = Indexer.listAuthors(record);
				value = value.replaceAll("\\|", " ");
				break;
			}
			case TITLE: {
				value = Indexer.listTitle(record);
				break;
			}
			case SUBJECT: {
				value = Indexer.listSubject(record);
				break;
			}
			case ISBN: {
				value = Indexer.listIsbn(record);
				break;
			}
			case ISSN: {
				value = Indexer.listIssn(record);
				break;
			}
			case YEAR: {
				String year = Indexer.listYearOfPublication(record);
				Pattern p = Pattern.compile("[0-9]{4}");
				Matcher m = p.matcher(year);
				while (m.find()) {
					value += " " + m.group();
				}
				break;
			}
		}
		return value.trim().split("\\s+");
	}

	public final boolean insertIndex(IndexTable table, List<IndexDTO> indexList) {
		final IndexDAO dao = new IndexDAO();
		return dao.insert(table, indexList);
	}

	public final boolean deleteIndexes(final RecordDTO dto) {
		final IndexDAO dao = new IndexDAO();

		for (IndexTable table : IndexTable.values()) {
			final IndexDTO param = new IndexDTO();
			param.setRecordSerial(dto.getRecordSerial());
			dao.delete(table, param);
		}

		return true;
	}

	public final boolean updateIndexes(final RecordDTO dto) {
		
		this.deleteIndexes(dto);
		
		Map<IndexTable, List<IndexDTO>> indexMap = new HashMap<IndexTable, List<IndexDTO>>();
		ArrayList<RecordDTO> records = new ArrayList<RecordDTO>();
		records.add(dto);
		this.populateIndexMap(records, indexMap);
		for (IndexTable table : indexMap.keySet()) {
			this.insertIndex(table, indexMap.get(table));
		}
		
		return true;
	}

}
