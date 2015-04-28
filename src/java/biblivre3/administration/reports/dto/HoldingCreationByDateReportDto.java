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

package biblivre3.administration.reports.dto;

import java.util.List;

/**
 *
 * @author Danniel
 */
public class HoldingCreationByDateReportDto extends BaseReportDto {

    private String initialDate;
    private String finalDate;
    private String totalBiblioMain;
    private String totalBiblioWork;
    private String totalHoldingMain;
    private String totalHoldingWork;
    private List<String[]> data;

    public List<String[]> getData() {
        return data;
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getTotalBiblioMain() {
        return totalBiblioMain;
    }

    public void setTotalBiblioMain(String totalBiblioMain) {
        this.totalBiblioMain = totalBiblioMain;
    }

    public String getTotalBiblioWork() {
        return totalBiblioWork;
    }

    public void setTotalBiblioWork(String totalBiblioWork) {
        this.totalBiblioWork = totalBiblioWork;
    }

    public String getTotalHoldingMain() {
        return totalHoldingMain;
    }

    public void setTotalHoldingMain(String totalHoldingMain) {
        this.totalHoldingMain = totalHoldingMain;
    }

    public String getTotalHoldingWork() {
        return totalHoldingWork;
    }

    public void setTotalHoldingWork(String totalHoldingWork) {
        this.totalHoldingWork = totalHoldingWork;
    }

}
