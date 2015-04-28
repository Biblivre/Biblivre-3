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

package biblivre3.cataloging;

import biblivre3.cataloging.authorities.AuthoritiesBO;
import biblivre3.cataloging.bibliographic.BiblioBO;
import biblivre3.cataloging.bibliographic.FreeMarcBO;
import biblivre3.cataloging.vocabulary.VocabularyBO;
import biblivre3.enums.Database;
import biblivre3.enums.MaterialType;
import biblivre3.enums.RecordStatus;
import biblivre3.marcutils.MarcReader;
import biblivre3.utils.ApplicationConstants;
import biblivre3.utils.TextUtils;
import java.util.HashMap;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import mercury.BaseHandler;
import mercury.Dialog;
import mercury.I18nUtils;
import mercury.MemoryFileDTO;
import mercury.UploadedFileBean;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j_2_3_1.marc.Record;

public class CatalogingHandler extends BaseHandler {

    @Override
    public String processModule(HttpServletRequest request, HttpServletResponse response, String submitButton, HashMap<String, String> requestParametersHash) {

        if (submitButton.equals("EXPORT_RECORD")) {
            final String recordIds = request.getParameter("serial_list");
            final BiblioBO bo = new BiblioBO();
            final MemoryFileDTO export = bo.export(recordIds.split(","));
            if (export != null) {
                this.returnFile(export, response);
            }
            return "x-download";
        } else if (submitButton.equals("UPLOAD_IMPORT")) {
            String records = "";

            int totalRecords = 0;
            int validRecords = 0;
            final HttpSession session = request.getSession();
            final UploadedFileBean file = (UploadedFileBean) session.getAttribute("UPLOADED_FILE");

            if (file != null) {
                final StringBuilder builder = new StringBuilder();
                final Scanner scanner = new Scanner(file.getInputStream(), "UTF-8");
                while (scanner.hasNextLine()) {
                    String iso2709 = scanner.nextLine();
                    if (StringUtils.isBlank(iso2709)) {
                        continue;
                    }

                    String lines[] = iso2709.replace("\u001E\u001D", "\u001E\u001D\r\n").split("\r\n");

                    for (String line : lines) {
                        if (StringUtils.isNotBlank(line)) {
                            try {
                                String freemarc = MarcReader.iso2709ToMarc(line);
                                freemarc = TextUtils.combine(freemarc);
                                builder.append(freemarc);
                                builder.append(ApplicationConstants.FREEMARC_RECORD_SEPARATOR);
                                builder.append(ApplicationConstants.LINE_BREAK);
                                totalRecords++;
                                validRecords++;
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }
                records = builder.toString();
            }

            JSONObject json = new JSONObject();
            try {
                if (StringUtils.isBlank(records)) {
                    json.put("success", false);
                    json.put("message", I18nUtils.getText(session, "biblivre3", "ERROR_MARC_NOT_FILLED"));
                } else {
                    json.put("success", true);
                    json.put("marc", records);
                    json.put("totalRecords", String.valueOf(totalRecords));
                    json.put("validRecords", String.valueOf(validRecords));
                }
            } catch (JSONException je) {
            }
            this.returnJson(json, response);
            return "x-json";

        } else if (submitButton.equals("SAVE_IMPORT")) {
            final String freemarc = request.getParameter("marc");
            if (StringUtils.isBlank(freemarc)) {
                Dialog.showWarning(request, "ERROR_MARC_NOT_FILLED");
                return "/jsp/cataloging/import.jsp";
            }

            final String importType = request.getParameter("import_type");
            boolean success = false;

            //biblio_MAIN, biblio_WORK, authorities, vocabulary
            if (importType.startsWith("biblio")) {
                String[] ex_auto = null;

                if (request.getParameter("holding") != null) {
                    ex_auto = new String[6];
                    ex_auto[0] = request.getParameter("quant");
                    ex_auto[1] = request.getParameter("nvol");
                    ex_auto[2] = request.getParameter("nvol_obra");
                    ex_auto[3] = request.getParameter("biblio_dep");
                    ex_auto[4] = request.getParameter("aquis");
                    ex_auto[5] = request.getParameter("dt_tomb");
                }

                String base = "WORK";
                if (importType.contains("MAIN")) {
                    base = "MAIN";
                }

                FreeMarcBO fbo = new FreeMarcBO();
                success = fbo.importRecords(freemarc, Database.valueOf(base), null, ex_auto);

            } else {
                boolean authorities = importType.equals("authorities");

                MaterialType mt = authorities ? MaterialType.AUTHORITIES : MaterialType.VOCABULARY;

                AuthoritiesBO abo = new AuthoritiesBO();
                VocabularyBO vbo = new VocabularyBO();

                final String[] records = freemarc.split(ApplicationConstants.FREEMARC_RECORD_SEPARATOR);

                for (final String record : records) {
                    if (StringUtils.isBlank(record)) {
                        continue;
                    }

                    Record recordObj = MarcReader.marcToRecord(record, mt, RecordStatus.NEW);
                    success = authorities ? abo.insert(recordObj) : vbo.insert(recordObj);
                }
            } 

            if (success) {
                Dialog.showNormal(request, "SUCCESS_IMPORT_RECORD");
                return "/jsp/cataloging/import.jsp";
            } else {
                Dialog.showError(request, "ERROR_IMPORT_RECORD");
                return "/jsp/cataloging/import.jsp";
            }
        }

        return "/jsp/cataloging/biblio.jsp";
    }
}
