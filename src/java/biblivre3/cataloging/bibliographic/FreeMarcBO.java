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

import biblivre3.enums.Database;
import biblivre3.enums.MaterialType;
import biblivre3.enums.RecordStatus;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.enums.Availability;
import biblivre3.marcutils.MarcReader;
import biblivre3.utils.ApplicationConstants;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.marc.Record;

public class FreeMarcBO extends BiblioBO {

    public final RecordDTO insert(final String freeMarc, final Database base, final String materialType) {
        RecordDTO dto = checkAndSave(freeMarc, base, materialType, null);
        return dto;
    }


    private RecordDTO checkAndSave(final String marc, final Database base, final String materialType, final String[] ex_auto) {
        MaterialType mt = MaterialType.getByCode(materialType);

        final Record record = MarcReader.marcToRecord(marc, mt, RecordStatus.NEW);
        RecordDTO dto = super.insert(record, base, mt);

        if (ex_auto != null) {
            new HoldingBO().createAutomaticHolding(record, base, dto.getRecordSerial(), Availability.AVAILABLE, ex_auto);
        }
        
        return dto;
    }

    public final boolean importRecords(final String records, final Database base, final String materialType, final String[] ex_auto) {
        final String[] aRecords = records.split(ApplicationConstants.FREEMARC_RECORD_SEPARATOR);

        for (final String marc : aRecords) {
            if (StringUtils.isNotBlank(marc)) {
                this.checkAndSave(marc, base, materialType, ex_auto);
            }
        }
        return true;
    }


}
