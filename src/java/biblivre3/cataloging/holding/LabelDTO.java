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

import biblivre3.utils.NaturalOrderComparator;
import biblivre3.utils.TextUtils;

/**
 * @author 
 */
public class LabelDTO implements Comparable<Object> {

    private int holdingSerial;
    private int recordSerial;
    private String assetHolding;
    private String author;
    private String title;
    private String locationA;
    private String locationB;
    private String locationC;
    private String locationD;


    public int getHoldingSerial() {
        return holdingSerial;
    }

    public void setHoldingSerial(int holdingSerial) {
        this.holdingSerial = holdingSerial;
    }

    public int getRecordSerial() {
        return recordSerial;
    }

    public void setRecordSerial(int recordSerial) {
        this.recordSerial = recordSerial;
    }

    public String getAssetHolding() {
        return TextUtils.sanitize(assetHolding, "");
    }

    public void setAssetHolding(String assetHolding) {
        this.assetHolding = assetHolding;
    }

    public String getAuthor() {
        return TextUtils.sanitize(author, "");
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLocationA() {
        return TextUtils.sanitize(locationA, "");
    }

    public void setLocationA(String locationA) {
        this.locationA = locationA;
    }

    public String getLocationB() {
        return TextUtils.sanitize(locationB, "");
    }

    public void setLocationB(String locationB) {
        this.locationB = locationB;
    }

    public String getLocationC() {
        return TextUtils.sanitize(locationC, "");
    }

    public void setLocationC(String locationC) {
        this.locationC = locationC;
    }

    public String getLocationD() {
        return TextUtils.sanitize(locationD, "");
    }

    public void setLocationD(String locationD) {
        this.locationD = locationD;
    }

    public String getTitle() {
        return TextUtils.sanitize(title, "");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 0;
        }

        if (!(o instanceof LabelDTO)) {
            return 0;
        }

        return NaturalOrderComparator.NUMERICAL_ORDER.compare(this.getAssetHolding(), ((LabelDTO) o).getAssetHolding());
    }
}
