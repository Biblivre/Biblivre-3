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

import biblivre3.cataloging.authorities.AuthoritiesBO;
import biblivre3.cataloging.bibliographic.BiblioBO;
import biblivre3.cataloging.bibliographic.BiblioDAO;
import biblivre3.cataloging.bibliographic.BiblioSearchResultsDTO;
import biblivre3.cataloging.bibliographic.IndexBO;
import biblivre3.cataloging.holding.HoldingBO;
import biblivre3.cataloging.vocabulary.VocabularyBO;
import biblivre3.circulation.UserTypeDTO;
import biblivre3.enums.Database;
import java.io.File;
import java.util.Date;
import java.util.List;
import mercury.BaseBO;

public class AdminBO extends BaseBO {

    private AdminDAO adminDao = new AdminDAO();

    public void addUserType(UserTypeDTO userTypeDTO) {
        adminDao.addUserType(userTypeDTO);
    }

    public boolean deleteUserType(int serial) {
        return adminDao.deleteUserType(serial);
    }

    public void updateUserType(UserTypeDTO userTypeDTO) {
        adminDao.updateUserType(userTypeDTO);
    }

    public UserTypeDTO findUserTypeBySerial(int serial) {
        return adminDao.findUserTypeBySerial(serial);
    }

    public final boolean reindexBiblioBase() {
        return new IndexBO().reindexBase();
    }

    public final boolean reindexAuthoritiesBase() {
        return new AuthoritiesBO().reindexBase();
    }

    public final boolean reindexThesaurusBase() {
        return new VocabularyBO().reindexBase();
    }

    public BiblioSearchResultsDTO totalRecords(String database) {
        int totalrecords = new BiblioDAO().countAll(Database.valueOf(database));

        BiblioSearchResultsDTO biblio = new BiblioSearchResultsDTO();
        biblio.setTotalRecords(totalrecords);
        return biblio;
    }

    public final Date getLastBackupDate() {
        return adminDao.getLastBackupDate();
    }

    public final boolean insertLastBackupDate(Date newDate) {
        return adminDao.insertLastBackupDate(newDate);
    }

    public final List<Date> getLastFiveBackups() {
        return adminDao.getLastFiveBackups();
    }

    public boolean isIndexOutdated() {
        return adminDao.isIndexOutdated();
    }

    public File exportBiblioRecords(String format, Database database) {
        return new BiblioBO().exportRecords(format, database);
    }

    public File exportHoldingRecords(String format, Database database) {
        return new HoldingBO().exportRecords(format, database);
    }
}
