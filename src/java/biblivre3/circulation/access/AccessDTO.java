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

package biblivre3.circulation.access;

import java.sql.Timestamp;
import mercury.DTO;

public class AccessDTO extends DTO {

    private Integer serial;
    private Integer serialCard;
    private Integer serialStation;
    private Integer serialReader;
    private Timestamp entranceDatetime;
    private Timestamp departureDatetime;

    transient private String userName;
    transient private String stationName;


    public Timestamp getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(Timestamp departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public Timestamp getEntranceDatetime() {
        return entranceDatetime;
    }

    public void setEntranceDatetime(Timestamp entranceDatetime) {
        this.entranceDatetime = entranceDatetime;
    }

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public Integer getSerialCard() {
        return serialCard;
    }

    public void setSerialCard(Integer serialCard) {
        this.serialCard = serialCard;
    }

    public Integer getSerialReader() {
        return serialReader;
    }

    public void setSerialReader(Integer serialReader) {
        this.serialReader = serialReader;
    }

    public Integer getSerialStation() {
        return serialStation;
    }

    public void setSerialStation(Integer serialStation) {
        this.serialStation = serialStation;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



}
