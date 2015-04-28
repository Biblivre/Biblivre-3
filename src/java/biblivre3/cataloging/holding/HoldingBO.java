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

package biblivre3.cataloging.holding;

import biblivre3.cataloging.bibliographic.BiblioDAO;
import biblivre3.cataloging.bibliographic.RecordDTO;
import biblivre3.circulation.UserDTO;
import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import biblivre3.enums.Availability;
import biblivre3.enums.Database;
import biblivre3.enums.HoldingSave;
import biblivre3.enums.MaterialType;
import biblivre3.enums.RecordStatus;
import biblivre3.marcutils.MarcReader;
import biblivre3.marcutils.Indexer;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.ApplicationConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mercury.BaseBO;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.MarcXmlWriter;
import org.marc4j_2_3_1.marc.DataField;
import org.marc4j_2_3_1.marc.MarcFactory;
import org.marc4j_2_3_1.marc.Record;
import org.marc4j_2_3_1.marc.Subfield;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  09/03/2009
 */
public class HoldingBO extends BaseBO {

    private HoldingDAO dao;
    private UserDTO userDto;

    public HoldingBO() {
        dao = new HoldingDAO();
    }

    public HoldingBO(UserDTO userDto) {
        this();
        this.userDto = userDto;
    }

    public final HoldingDTO getById(final int holdingId) {
        return dao.getById(holdingId);
    }

    public final HoldingDTO getByAsset(final String assetHolding) {
        return dao.getByAsset(assetHolding);
    }

    public final List<HoldingDTO> list(final RecordDTO dto) {
        return dao.list(dto);
    }

    public final boolean delete(final HoldingDTO hdto) {
        return dao.delete(hdto);
    }

    private Integer getNextSerial() {
        return dao.getNextSerial();
    }

    public final String getNextAvailableAsset() {
        String prefix = Config.getConfigProperty(ConfigurationEnum.ASSET_PREFIX.name()) + "." + Calendar.getInstance().get(Calendar.YEAR) + ".";
        int nextAsset = dao.getNextAutomaticAsset(prefix);

        return prefix + nextAsset;
    }

    public final String getNextLocationD(final int recordSerial) {
        int nextAsset = dao.getNextLocationD(recordSerial);

        return "ex." + nextAsset;
    }

    public final boolean checkAssetAvailability(final String asset, int holdingSerial) {
        return dao.isAssetAvailable(asset, holdingSerial);
    }

    public final boolean checkAssetAvailability(final String asset) {
        return this.checkAssetAvailability(asset, 0);
    }

    public final HoldingSave insert(final int recordSerial, final Database base, final String marc, final Availability availability) {
        final Record holding = MarcReader.marcToRecord(marc, MaterialType.HOLDINGS, RecordStatus.NEW);

        String assetHolding = Indexer.listAssetHolding(holding);
        boolean emptyAssetHolding = StringUtils.isBlank(assetHolding);

        if (emptyAssetHolding) {
            assetHolding = this.getNextAvailableAsset();
            MarcUtils.setAssetHolding(holding, assetHolding);

        } else if (!this.checkAssetAvailability(assetHolding)) {
            return HoldingSave.ASSET_HOLDING_ALREADY_IN_USE;
        }

        if (this.insert(holding, recordSerial, base, availability)) {
            return emptyAssetHolding ? HoldingSave.SUCCESS_WITH_NEW_ASSET_HOLDING : HoldingSave.SUCCESS;
        } else {
            return HoldingSave.ERROR;
        }
    }

    private boolean insert(final Record holding, final int recordSerial, final Database base, final Availability availability) {
        final Integer holdingSerial = this.getNextSerial();
        final Date now = new Date();

        MarcUtils.setCF001(holding, holdingSerial);
        MarcUtils.setCF004(holding, recordSerial);
        MarcUtils.setCF005(holding, now);

        final String iso2709 = MarcUtils.recordToIso2709(holding);

        final HoldingDTO dto = new HoldingDTO();
        dto.setSerial(holdingSerial);
        dto.setRecordSerial(recordSerial);

        dto.setCreated(now);
        dto.setModified(now);

        dto.setIso2709(iso2709);
        dto.setDatabase(base);

        dto.setAssetHolding(Indexer.listAssetHolding(holding));
        dto.setLocationD(Indexer.listLocation(holding)[3]);
        dto.setAvailability(availability);

        boolean success = dao.insert(dto);

        if (success && this.userDto != null) {
            return dao.updateHoldingCreationCounter(this.userDto);
        }

        return success;
    }

    public final HoldingSave update(final int holdingSerial, final int recordSerial, final String marc, final Availability availability) {
        final Record holding = MarcReader.marcToRecord(marc, MaterialType.HOLDINGS, RecordStatus.CORRECTED);

        String assetHolding = Indexer.listAssetHolding(holding);
        boolean emptyAssetHolding = StringUtils.isBlank(assetHolding);

        if (emptyAssetHolding) {
            assetHolding = this.getNextAvailableAsset();
            MarcUtils.setAssetHolding(holding, assetHolding);

        } else if (!this.checkAssetAvailability(assetHolding, holdingSerial)) {
            return HoldingSave.ASSET_HOLDING_ALREADY_IN_USE;
        }

        if (this.update(holding, holdingSerial, recordSerial, availability)) {
            return emptyAssetHolding ? HoldingSave.SUCCESS_WITH_NEW_ASSET_HOLDING : HoldingSave.SUCCESS;
        } else {
            return HoldingSave.ERROR;
        }
    }

    private boolean update(final Record holding, final int holdingSerial, final int recordSerial, final Availability availability) {
        final Date now = new Date();

        MarcUtils.setCF001(holding, holdingSerial);
        MarcUtils.setCF004(holding, recordSerial);
        MarcUtils.setCF005(holding, now);

        final String iso2709 = MarcUtils.recordToIso2709(holding);

        final HoldingDTO dto = new HoldingDTO();
        dto.setSerial(holdingSerial);
        dto.setIso2709(iso2709);

        dto.setModified(now);

        dto.setAssetHolding(Indexer.listAssetHolding(holding));
        dto.setLocationD(Indexer.listLocation(holding)[3]);
        dto.setAvailability(availability);

        return dao.update(dto);
    }

    public final Integer countAvailableHoldings(final int recordSerial) {
        return dao.countAvailableHoldings(recordSerial);
    }

    public final Integer countAvailableHoldings(final RecordDTO dto) {
        return this.countAvailableHoldings(dto.getRecordSerial());
    }

    public final Integer countHoldings(final int recordSerial) {
        return dao.countHoldings(recordSerial);
    }

    public final Integer countHoldings(final RecordDTO dto) {
        return this.countHoldings(dto.getRecordSerial());
    }

    public boolean generateLabel(final int holdingSerial, final int recordSerial, final RecordDTO record, final HoldingDTO holding) {
        String[] biblioDetails = this.getBibliographicDetails(record);
        String[] locDetails = this.getLocationDetails(holding);

        return this.generateLabel(holdingSerial, recordSerial, biblioDetails, locDetails, holding.getAssetHolding());
    }

    public boolean generateLabel(final int holdingSerial, final int recordSerial, String[] biblioDetails, String[] locDetails, String assetHolding) {
        final LabelDTO ldto = new LabelDTO();
        ldto.setHoldingSerial(holdingSerial);
        ldto.setRecordSerial(recordSerial);
        ldto.setAssetHolding(assetHolding);
        ldto.setAuthor(biblioDetails[0]);
        ldto.setTitle(biblioDetails[1]);
        ldto.setLocationA(locDetails[0]);
        ldto.setLocationB(locDetails[1]);
        ldto.setLocationC(locDetails[2]);
        ldto.setLocationD(locDetails[3]);

        return dao.insertLabel(ldto);
    }

    public String[] getBibliographicDetails(final RecordDTO recordDto) {
        Record record = MarcUtils.iso2709ToRecord(recordDto.getIso2709());

        String[] biblioDetails = new String[3];

        biblioDetails[0] = Indexer.listFirstAuthors(record);
        biblioDetails[1] = Indexer.listOneTitle(record);

        return biblioDetails;
    }

    public String[] getLocationDetails(final HoldingDTO holdingDTO) {
        Record record = MarcUtils.iso2709ToRecord(holdingDTO.getIso2709());

        return Indexer.listLocation(record);
    }

    public boolean createAutomaticHolding(final Record record, final Database base, final int recordSerial, final Availability availability, String[] ex_auto) {
        try {
            int holdingCount = StringUtils.isNumeric(ex_auto[0]) && StringUtils.isNotBlank(ex_auto[0]) ? Integer.parseInt(ex_auto[0]) : 1;
            int volumeNumber = StringUtils.isNumeric(ex_auto[1]) && StringUtils.isNotBlank(ex_auto[1]) ? Integer.parseInt(ex_auto[1]) : 0;
            int volumeCount = StringUtils.isNumeric(ex_auto[2]) && StringUtils.isNotBlank(ex_auto[2]) ? Integer.parseInt(ex_auto[2]) : 1;

            String deposit = ex_auto[3];
            String acquisitionType = ex_auto[4];
            String dt_tomb = ex_auto[5];

            String[] notes = new String[3];
            notes[0] = 'a' + deposit;
            notes[1] = 'c' + acquisitionType;
            notes[2] = 'd' + dt_tomb;

            boolean success = true;

            String[] location = Indexer.listLocation(record);

            for (int j = 0; j < volumeCount; j++) {
                for (int i = 0; i < holdingCount; i++) {
                    String[] vetHolding = new String[4];

                    vetHolding[0] = "a" + location[0];
                    vetHolding[1] = "b" + location[1];
                    vetHolding[2] = "c";

                    if (StringUtils.isNotBlank(location[2])) {
                        vetHolding[2] += location[2];
                    } else {
                        if (volumeNumber != 0) {
                            vetHolding[2] += "v." + volumeNumber;
                        } else if (volumeCount > 1) {
                            vetHolding[2] += "v." + (j + 1);
                        }
                    }

                    vetHolding[3] = 'd' + "ex." + (i + 1);

                    Record holding = this.getRecordHolding(vetHolding, notes);
                    MarcUtils.setAssetHolding(holding, this.getNextAvailableAsset());

                    success &= this.insert(holding, recordSerial, base, availability);
                }
            }

            return success;
        } catch (RuntimeException rex) {
            throw rex;
        }
    }

    public Record getRecordHolding(String[] subfields, String[] notes) {
        MarcFactory factory = MarcFactory.newInstance();
        Record record = factory.newRecord();

        DataField df = factory.newDataField("090", '_', '_');
        record.addVariableField(df);
        for (int i = 0; i < 4; i++) {
            if (StringUtils.isNotBlank(subfields[i])) {
                final char code = subfields[i].charAt(0);
                Subfield subfield = factory.newSubfield(code, subfields[i].substring(1));
                df.addSubfield(subfield);
            }
        }

        DataField df1 = factory.newDataField("541", '_', '_');
        record.addVariableField(df1);
        for (int i = 0; i < 3; i++) {
            if (StringUtils.isNotBlank(notes[i])) {
                final char code = notes[i].charAt(0);
                Subfield subfield = factory.newSubfield(code, notes[i].substring(1));
                df1.addSubfield(subfield);
            }
        }

        record.setLeader(MarcUtils.createBasicLeader(MaterialType.HOLDINGS, RecordStatus.NEW));

        return record;
    }

    public File exportRecords(String format, Database database) {
        if (StringUtils.isBlank(format)) {
            return null;
        } else if (format.equals("iso2709")) {
            return createIsoFile(database);
        } else if (format.equals("xml")) {
            return createXmlFile(database);
        } else {
            return null;
        }
    }

    private File createIsoFile(Database database) {
        try {
            File file = File.createTempFile("bib3_", null);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
            HoldingDAO holdingDao = new HoldingDAO();
            int limit = 100;
            int recordCount = holdingDao.countAll(database);

            for (int offset = 0; offset < recordCount; offset += limit) {
                List<HoldingDTO> records = holdingDao.list(database, offset, limit);
                for (HoldingDTO dto : records) {
                    writer.write(dto.getIso2709());
                    writer.write(ApplicationConstants.LINE_BREAK);
                }
            }
            writer.flush();
            writer.close();
            return file;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private File createXmlFile(Database database) {
        try {
            File file = File.createTempFile("bib3_", null);
            FileOutputStream fos = new FileOutputStream(file);
            MarcXmlWriter writer = new MarcXmlWriter(fos, true);
            HoldingDAO holdingDao = new HoldingDAO();
            int limit = 100;
            int recordCount = holdingDao.countAll(null);

            for (int offset = 0; offset < recordCount; offset += limit) {
                List<HoldingDTO> records = holdingDao.list(database, offset, limit);
                for (HoldingDTO dto : records) {
                    final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
                    if (record != null) {
                        writer.write(record);
                    }
                }
            }
            writer.close();
            return file;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}

