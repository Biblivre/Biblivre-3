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

import biblivre3.circulation.lending.LendingBO;
import biblivre3.enums.Database;
import biblivre3.enums.MaterialType;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.holding.HoldingDAO;
import biblivre3.cataloging.holding.HoldingDTO;
import biblivre3.cataloging.holding.LabelConfigDTO;
import biblivre3.cataloging.holding.LabelDTO;
import biblivre3.marcutils.MarcUtils;
import biblivre3.utils.ApplicationConstants;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import mercury.BaseBO;
import mercury.MemoryFileDTO;
import org.apache.commons.lang.StringUtils;
import org.marc4j_2_3_1.MarcXmlWriter;
import org.marc4j_2_3_1.marc.Record;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  20/02/2009
 */
public class BiblioBO extends BaseBO {
    
    private static final SimpleDateFormat expectedFormat = new SimpleDateFormat("yyyy-MM-dd");

    public boolean delete(final String[] recordIds) {
        final BiblioDAO dao = new BiblioDAO();
        final HoldingBO holdingBo = new HoldingBO();
        final LendingBO lendingBo = new LendingBO();

        final Collection<RecordDTO> records = new ArrayList<RecordDTO>();

        for (String id : recordIds) {
            final RecordDTO dto = new RecordDTO();
            dto.setRecordSerial(Integer.valueOf(id));
            records.add(dto);
        }

        for (RecordDTO record : records) {
            List<HoldingDTO> holdings = holdingBo.list(record);

            for (HoldingDTO holding : holdings) {
                if (lendingBo.isLent(holding) || lendingBo.wasLent(holding)) {
                    throw new RuntimeException("MESSAGE_DELETE_BIBLIO_ERROR");
                }
            }
        }

        if (dao.delete(records)) {
            final IndexBO bo = new IndexBO();
            for (RecordDTO dto : records) {
                bo.deleteIndexes(dto);
            }
        }

        return true;
    }

    public MemoryFileDTO export(final String[] recordIds) {
        final BiblioDAO dao = new BiblioDAO();
        final Collection<RecordDTO> list = dao.getById(recordIds);
        if (list != null && !list.isEmpty()) {
            return this.createFile(list);
        } else {
            return null;
        }
    }

    private MemoryFileDTO createFile(final Collection<RecordDTO> records) {
        final MemoryFileDTO file = new MemoryFileDTO();
        file.setFileName(new Date().getTime() + ".mrc");
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final OutputStreamWriter writer = new OutputStreamWriter(baos, "UTF-8");
            for (RecordDTO dto : records) {
                writer.write(dto.getIso2709());
                writer.write(ApplicationConstants.LINE_BREAK);
            }
            writer.flush();
            writer.close();
            file.setFileData(baos.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }

    public MemoryFileDTO createFileLabelsTXT(final Collection<LabelDTO> labels) {
        final MemoryFileDTO file = new MemoryFileDTO();
        file.setFileName(new Date().getTime() + ".txt");
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final OutputStreamWriter writer = new OutputStreamWriter(baos, "UTF-8");
            PrintWriter out = new PrintWriter(writer, true);
            for (LabelDTO ldto : labels) {
                out.println("Tombo");
                out.println(ldto.getAssetHolding());
                writer.write(ApplicationConstants.LINE_BREAK);
                out.println("Autor");
                out.println(ldto.getAuthor());
                writer.write(ApplicationConstants.LINE_BREAK);
                out.println("Titulo");
                out.println(ldto.getTitle());
                writer.write(ApplicationConstants.LINE_BREAK);
                out.println("Loc. A");
                out.println(ldto.getLocationA());
                writer.write(ApplicationConstants.LINE_BREAK);
                out.println("Loc. B");
                out.println(ldto.getLocationB());
                writer.write(ApplicationConstants.LINE_BREAK);
                out.println("Loc. C");
                out.println(ldto.getLocationC());
                writer.write(ApplicationConstants.LINE_BREAK);
                out.println("Loc. D");
                out.println(ldto.getLocationD());
                out.println("---------------------------------|");
            }
            writer.flush();
            writer.close();
            file.setFileData(baos.toByteArray());
        } catch (Exception e) {
            e.getMessage();
        }
        return file;
    }

    public MemoryFileDTO createFileLabelsPDF(ArrayList<LabelDTO> labels, LabelConfigDTO labelConfig) {
        Document document = new Document();
        final MemoryFileDTO file = new MemoryFileDTO();
        file.setFileName("biblivre_etiquetas_" + new Date().getTime() + ".pdf");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.setPageSize(PageSize.A4);
            float verticalMargin = (297.0f - (labelConfig.getHeight() * labelConfig.getRows())) / 2;
            document.setMargins(
                    7.15f * ApplicationConstants.MM_UNIT,
                    7.15f * ApplicationConstants.MM_UNIT,
                    verticalMargin * ApplicationConstants.MM_UNIT,
                    verticalMargin * ApplicationConstants.MM_UNIT);
            document.open();

            PdfPTable table = new PdfPTable(labelConfig.getColumns());
            table.setWidthPercentage(100f);
            PdfPCell cell;

            int i = 0;
            for (i = 0; i < labelConfig.getOffset(); i++) {
                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setFixedHeight(labelConfig.getHeight() * ApplicationConstants.MM_UNIT);
                table.addCell(cell);
            }

            for (LabelDTO ldto : labels) {
                PdfContentByte cb = writer.getDirectContent();

                String holdingSerial = String.valueOf(ldto.getHoldingSerial());
                while (holdingSerial.length() < 10) {
                    holdingSerial = "0" + holdingSerial;
                }
                Barcode39 code39 = new Barcode39();
                code39.setExtended(true);
                code39.setCode(holdingSerial);
                code39.setStartStopText(false);

                Image image39 = code39.createImageWithBarcode(cb, null, null);
                if (labelConfig.getHeight() > 30.0f) {
                    image39.scalePercent(110f);
                } else {
                    image39.scalePercent(90f);
                }

                Paragraph para = new Paragraph();
                Phrase p1 = new Phrase(StringUtils.left(ldto.getAuthor(), 28) + "\n");
                Phrase p2 = new Phrase(StringUtils.left(ldto.getTitle(), 28) + "\n\n");
                Phrase p3 = new Phrase(new Chunk(image39, 0, 0));
                para.add(p1);
                para.add(p2);
                para.add(p3);

                cell = new PdfPCell(para);
                i++;
                cell.setNoWrap(true);
                cell.setFixedHeight(labelConfig.getHeight() * ApplicationConstants.MM_UNIT);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                Paragraph para2 = new Paragraph();
                Phrase p5 = new Phrase(ldto.getLocationA() + "\n");
                Phrase p6 = new Phrase(ldto.getLocationB() + "\n");
                Phrase p7 = new Phrase(ldto.getLocationC() + "\n");
                Phrase p8 = new Phrase(ldto.getLocationD() + "\n");
                Phrase p4 = new Phrase(ldto.getAssetHolding() + "\n");
                para2.add(p5);
                para2.add(p6);
                para2.add(p7);
                para2.add(p8);
                para2.add(p4);

                cell = new PdfPCell(para2);
                i++;
                cell.setFixedHeight(labelConfig.getHeight() * ApplicationConstants.MM_UNIT);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }
            if ((i % labelConfig.getColumns()) != 0) {
                while ((i % labelConfig.getColumns()) != 0) {
                    i++;
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
                }
            }
            document.add(table);
            writer.flush();
            document.close();
            file.setFileData(baos.toByteArray());
        } catch (Exception de) {
            System.out.println(de.getMessage());
        }
        return file;
    }

    private boolean insert(final RecordDTO dto, final Database base) {
        final BiblioDAO dao = new BiblioDAO();
        return dao.insert(dto, base);
    }

    public final RecordDTO insert(final Record record, final Database base, MaterialType mt) {
        if (mt == null) {
            mt = MaterialType.getByTypeAndImplDef(record.getLeader().getTypeOfRecord(), record.getLeader().getImplDefined1());
        }

        final Date now = new Date();
        final Integer serial = this.getRecordSerial();

        MarcUtils.setCF001(record, serial);
        MarcUtils.setCF005(record, now);
        MarcUtils.setCF008(record);

        final String iso2709 = MarcUtils.recordToIso2709(record);
        if (iso2709 == null || iso2709.isEmpty()) {
            throw new IllegalArgumentException("freeMarc");
        }

        final RecordDTO dto = new RecordDTO();
        dto.setRecordSerial(serial);
        dto.setCreated(now);
        dto.setModified(now);
        dto.setMaterialType(mt);
        dto.setIso2709(iso2709);

        if (this.insert(dto, base)) {
            final IndexBO idx = new IndexBO();
            idx.updateIndexes(dto);
        }

        return dto;
    }

    private Integer getRecordSerial() {
        return new BiblioDAO().getNextSerial();
    }

    public boolean update(final Record record, final String recordId, MaterialType mt) {
        final Date now = new Date();
        MarcUtils.setCF005(record, now);

        final String iso2709 = MarcUtils.recordToIso2709(record);
        if (iso2709 == null || iso2709.isEmpty()) {
            throw new IllegalArgumentException("freeMarc");
        }

        final BiblioSearchBO bo = new BiblioSearchBO();
        final RecordDTO dto = bo.getById(recordId);

        dto.setModified(now);
        dto.setIso2709(iso2709);
        if (mt != null) {
            dto.setMaterialType(mt);
        }

        if (this.update(dto)) {
            final IndexBO idx = new IndexBO();
            idx.updateIndexes(dto);
        }
        return true;
    }

    public final boolean update(final RecordDTO dto) {
        final BiblioDAO dao = new BiblioDAO();
        return dao.update(dto);
    }

    public int[] generateLabelsByDate(Date startDate, Date endDate, String base) {
        ArrayList<HoldingDTO> list = new HoldingDAO().getHoldingByDate(expectedFormat.format(startDate), expectedFormat.format(endDate), Database.valueOf(base));
        HashMap<Integer, RecordDTO> recordHash = new HashMap<Integer, RecordDTO>();

        int total = list.size();
        int added = 0;

        HoldingBO hbo = new HoldingBO();
        BiblioDAO dao = new BiblioDAO();

        for (int i = 0; i < total; i++) {
            HoldingDTO holdintDto = list.get(i);

            boolean alreadyInList = new HoldingDAO().isLabelPending(holdintDto.getSerial());
            if (alreadyInList) {
                continue;
            }

            RecordDTO record;
            if (recordHash.containsKey(holdintDto.getRecordSerial())) {
                record = recordHash.get(holdintDto.getRecordSerial());
            } else {
                record = dao.getById(holdintDto.getRecordSerial());
                recordHash.put(holdintDto.getRecordSerial(), record);
            }

            if (record != null) {
                if (hbo.generateLabel(holdintDto.getSerial(), holdintDto.getRecordSerial(), record, holdintDto)) {
                    added++;
                }
            }
        }

        int result[] = new int[2];
        result[0] = total;
        result[1] = added;

        return result;
    }

    public Integer getTotalNroRecords(Database base, MaterialType type) {
        return new BiblioDAO().countAll(base, type);
    }

    public Boolean createLinkDatafield(String recordId, String fileName, String tag) {
        final RecordDTO dto = new BiblioSearchBO().getById(recordId);

        final Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
        final Record newRecord = MarcUtils.createLinksDatafield(record, fileName, tag);
        final String iso2709 = MarcUtils.recordToIso2709(newRecord);

        dto.setModified(new Date());
        dto.setIso2709(iso2709);

        new BiblioDAO().update(dto);

        final IndexBO idx = new IndexBO();
        idx.updateIndexes(dto);

        return true;
    }

    public Boolean moveRecords(String[] recordIds, String from) {
        Integer toBase = 0;
        if (StringUtils.isNotBlank(from) && from.equalsIgnoreCase("MAIN")) {
            toBase = 1;
        }

        return new BiblioDAO().moveRecords(recordIds, toBase);
    }

    public Boolean moveAllRecords(MaterialType mt, String from) {
        Integer toBase = 0;
        if (StringUtils.isNotBlank(from) && from.equalsIgnoreCase("MAIN")) {
            toBase = 1;
        }

        return new BiblioDAO().moveAllRecords(mt, toBase);
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
            BiblioDAO biblioDao = new BiblioDAO();
            int limit = 100;
            int recordCount = biblioDao.countAll(database);

            for (int offset = 0; offset < recordCount; offset += limit) {
                ArrayList<RecordDTO> records = biblioDao.list(database, MaterialType.ALL, offset, limit, false);
                for (RecordDTO dto : records) {
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
            BiblioDAO biblioDao = new BiblioDAO();
            int limit = 100;
            int recordCount = biblioDao.countAll(database);

            for (int offset = 0; offset < recordCount; offset += limit) {
                ArrayList<RecordDTO> records = biblioDao.list(database, MaterialType.ALL, offset, limit, false);
                for (RecordDTO dto : records) {
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
