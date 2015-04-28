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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biblivre3.circulation;

import biblivre3.enums.UserStatus;
import java.util.Date;
import java.util.Properties;
import mercury.DTO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class UserDTO extends DTO {

    private String name;
    private UserStatus userStatus;

    private int userid;
    private Date signup_date;
    private Date alter_date;
    private Date renew_date;
    private String social_id_number;
    private String dlicense;
    private String type_id;
    private String birthday;
    private String marital_status;
    private String sex;
    private String occupation;
    private String father_name;
    private String mother_name;
    private String reference_1;
    private String tel_ref_1;
    private String reference_2;
    private String tel_ref_2;
    private String email;
    private int user_type;
    private String obs;
    private String country;
    private String state;
    private String city;
    private String address;
    private String number;
    private String completion;
    private String zip_code;
    private String photo;

    private int whosignup;
    private int loginid;
    private String cellphone;
    private String extension_line;

    private String usernameascii;
    //private String pathPhoto;

    transient private String loginName;
    transient private String cardNumber;
    transient private Float totalFines;

    public UserDTO() {

        this.name = "";
        this.userid = 0;
        this.social_id_number = "";
        this.dlicense = "";
        this.type_id = "";
        this.birthday = "";
        this.marital_status = "";
        this.sex = "";
        this.occupation = "";
        this.father_name = "";
        this.mother_name = "";
        this.reference_1 = "";
        this.tel_ref_1 = "";
        this.reference_2 = "";
        this.tel_ref_2 = "";
        this.email = "";
        this.user_type = 0;
        this.obs = "";
        this.country = "";
        this.state = "";
        this.city = "";
        this.address = "";
        this.number = "";
        this.completion = "";
        this.zip_code = "";
        this.photo = "";
        this.whosignup = 0;
        this.loginid = 0;
        this.cellphone = "";
        this.extension_line = "";
        //this.pathPhoto = null;

        this.loginName = "";
        this.cardNumber = "";
        this.totalFines = 0f;
    }

    public String getUsernameascii() {
        return usernameascii;
    }

    public void setUsernameascii(String usernameascii) {
        this.usernameascii = usernameascii;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getExtension_line() {
        return extension_line;
    }

    public void setExtension_line(String extension_line) {
        this.extension_line = extension_line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final UserStatus getUserStatus() {
        return userStatus;
    }

    public final void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getAlter_date() {
        return alter_date;
    }

    public void setAlter_date(Date alter_date) {
        this.alter_date = alter_date;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDlicense() {
        return dlicense;
    }

    public void setDlicense(String dlicense) {
        this.dlicense = dlicense;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getMother_name() {
        return mother_name;
    }

    public void setMother_name(String mother_name) {
        this.mother_name = mother_name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getReference_1() {
        return reference_1;
    }

    public void setReference_1(String reference_1) {
        this.reference_1 = reference_1;
    }

    public String getReference_2() {
        return reference_2;
    }

    public void setReference_2(String reference_2) {
        this.reference_2 = reference_2;
    }

    public Date getRenew_date() {
        return renew_date;
    }

    public void setRenew_date(Date renew_date) {
        this.renew_date = renew_date;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getSignup_date() {
        return signup_date;
    }

    public void setSignup_date(Date signup_date) {
        this.signup_date = signup_date;
    }

    public String getSocial_id_number() {
        return social_id_number;
    }

    public void setSocial_id_number(String social_id_number) {
        this.social_id_number = social_id_number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTelRef1() {
        return tel_ref_1;
    }

    public void setTelRef1(String telRef1) {
        this.tel_ref_1 = telRef1;
    }

    public String getTelRef2() {
        return tel_ref_2;
    }

    public void setTelRef2(String telRef2) {
        this.tel_ref_2 = telRef2;
    }

    public String getTypeId() {
        return type_id;
    }

    public void setTypeId(String typeId) {
        this.type_id = typeId;
    }

    public int getUserType() {
        return user_type;
    }

    public void setUserType(int userType) {
        this.user_type = userType;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getWhosignup() {
        return whosignup;
    }

    public void setWhosignup(int whosignup) {
        this.whosignup = whosignup;
    }
    public int getLoginid() {
        return loginid;
    }

    public void setLoginid(int loginid) {
        this.loginid = loginid;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Float getTotalFines() {
        return totalFines;
    }

    public void setTotalFines(Float totalFines) {
        this.totalFines = totalFines;
    }

    @Override
    public JSONObject toJSONObject(Properties properties) {
        JSONObject json = new JSONObject();
        try {
            json.putOpt("name", this.getName());
            json.putOpt("userStatus", this.getUserStatus());

            json.putOpt("userid", this.getUserid());
            json.putOpt("signup_date", this.getSignup_date());
            json.putOpt("alter_date", this.getAlter_date());
            json.putOpt("renew_date", this.getRenew_date());
            json.putOpt("social_id_number", this.getSocial_id_number());
            json.putOpt("dlicense", this.getDlicense());
            json.putOpt("type_id", this.getTypeId());
            json.putOpt("birthday", this.getBirthday());
            json.putOpt("marital_status", this.getMarital_status());
            json.putOpt("sex", this.getSex());
            json.putOpt("occupation", this.getOccupation());
            json.putOpt("father_name", this.getFather_name());
            json.putOpt("mother_name", this.getMother_name());
            json.putOpt("reference_1", this.getReference_1());
            json.putOpt("tel_ref_1", this.getTelRef1());
            json.putOpt("reference_2", this.getReference_2());
            json.putOpt("tel_ref_2", this.getTelRef2());
            json.putOpt("email", this.getEmail());
            json.putOpt("user_type", this.getUserType());
            UserTypeDTO type = new CirculationBO().getUserTypeById(this.getUserType());
            json.putOpt("user_type_text", type != null ? type.getName() : "");
            json.putOpt("obs", this.getObs());
            json.putOpt("country", this.getCountry());
            json.putOpt("state", this.getState());
            json.putOpt("city", this.getCity());
            json.putOpt("address", this.getAddress());
            json.putOpt("number", this.getNumber());
            json.putOpt("completion", this.getCompletion());
            json.putOpt("zip_code", this.getZip_code());
            json.putOpt("photo_id", this.getPhoto());
            json.putOpt("whosignup", this.getWhosignup());
            json.putOpt("loginid", this.getLoginid());
            json.putOpt("cellphone", this.getCellphone());
            json.putOpt("extension_line", this.getExtension_line());

            json.putOpt("login", this.getLoginName());

            json.putOpt("card_number", this.getCardNumber());
            json.putOpt("has_card", StringUtils.isNotBlank(this.getCardNumber()));
            if (this.getTotalFines() != null && this.getTotalFines() > 0) {
                json.putOpt("total_fines", this.getTotalFines());
            }

        } catch (JSONException e) {

        }

        return json;
    }
}
