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

package biblivre3.config;

import org.apache.commons.lang.StringUtils;

public class ConfigurationDTO {
    public String library_name = Config.getConfigProperty(ConfigurationEnum.LIBRARY_NAME);
    public String library_subname = Config.getConfigProperty(ConfigurationEnum.LIBRARY_SUBNAME);
    public String money_locale = Config.getConfigProperty(ConfigurationEnum.MONEY_LOCALE);
    public String fine_amount = Config.getConfigProperty(ConfigurationEnum.FINE_AMOUNT);
    public String recordsPerPage = Config.getConfigProperty(ConfigurationEnum.RECORDS_PER_PAGE);
    public String digitalMedia = Config.getConfigProperty(ConfigurationEnum.DIGITAL_MEDIA);
    public String assetPrefix = Config.getConfigProperty(ConfigurationEnum.ASSET_PREFIX);

    private String header_color = Config.getConfigProperty(ConfigurationEnum.HEADER_COLOR);
    private String background_color = Config.getConfigProperty(ConfigurationEnum.BACKGROUND_COLOR);
    private String background_light_color = Config.getConfigProperty(ConfigurationEnum.BACKGROUND_LIGHT_COLOR);
    private String border_color = Config.getConfigProperty(ConfigurationEnum.BORDER_COLOR);
	
	private String welcome_disclaimer_pt_br = Config.getConfigProperty(ConfigurationEnum.WELCOME_DISCLAIMER_PT_BR);
	private String welcome_disclaimer_en_us = Config.getConfigProperty(ConfigurationEnum.WELCOME_DISCLAIMER_EN_US);
	private String welcome_disclaimer_es = Config.getConfigProperty(ConfigurationEnum.WELCOME_DISCLAIMER_ES);

    public final String getDigitalMedia() {
        return digitalMedia;
    }

    public final void setDigitalMedia(String digitalMedia) {
        this.digitalMedia = digitalMedia;
    }

    public final String getRecordsPerPage() {
        return recordsPerPage;
    }

    public final void setRecordsPerPage(String recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public String getFine_amount() {
        return fine_amount;
    }

    public void setFine_amount(String fine_amount) {
        this.fine_amount = fine_amount;
    }

    public String getMoney_locale() {
        return money_locale;
    }

    public void setMoney_locale(String money_locale) {
        this.money_locale = money_locale;
    }

    public String getLibrary_name() {
        return library_name;
    }

    public void setLibrary_name(String library_name) {
        this.library_name = library_name;
    }

    public String getLibrary_subname() {
        return library_subname;
    }

    public void setLibrary_subname(String library_subname) {
        this.library_subname = library_subname;
    }

    public String getHeader_color() {
        return header_color;
    }

    public void setHeader_color(String header_color) {
        this.header_color = header_color;
    }

    public String getBackground_color() {
        return background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public String getBackground_light_color() {
        return background_light_color;
    }

    public void setBackground_light_color(String background_light_color) {
        this.background_light_color = background_light_color;
    }

    public String getBorder_color() {
        return border_color;
    }

    public void setBorder_color(String border_color) {
        this.border_color = border_color;
    }

    public String getAssetPrefix() {
        return assetPrefix;
    }

    public void setAssetPrefix(String assetPrefix) {
        this.assetPrefix = assetPrefix;
    }

	public String getWelcome_disclaimer_pt_br() {
		return this.getWelcome_disclaimer_pt_br(true);
	}

	public String getWelcome_disclaimer_pt_br(boolean replaceLineFeed) {
		if (replaceLineFeed && StringUtils.isNotBlank(this.welcome_disclaimer_pt_br)) {
			return welcome_disclaimer_pt_br.replaceAll("<br>", "\n"); 
		}
		return welcome_disclaimer_pt_br;
	}
	
	public void setWelcome_disclaimer_pt_br(String welcome_disclaimer_pt_br) {
		if (StringUtils.isNotBlank(welcome_disclaimer_pt_br)) {
			welcome_disclaimer_pt_br = welcome_disclaimer_pt_br.replaceAll("\\r?\\n", "<br>"); 
		}
		this.welcome_disclaimer_pt_br = welcome_disclaimer_pt_br;
	}

	public String getWelcome_disclaimer_en_us() {
		return this.getWelcome_disclaimer_en_us(true);
	}
	
	public String getWelcome_disclaimer_en_us(boolean replaceLineFeed) {
		if (replaceLineFeed && StringUtils.isNotBlank(this.welcome_disclaimer_en_us)) {
			return welcome_disclaimer_en_us.replaceAll("<br>", "\n"); 
		}
		return welcome_disclaimer_en_us;
	}	

	public void setWelcome_disclaimer_en_us(String welcome_disclaimer_en_us) {
		if (StringUtils.isNotBlank(welcome_disclaimer_en_us)) {
			welcome_disclaimer_en_us = welcome_disclaimer_en_us.replaceAll("\\r?\\n", "<br>"); 
		}
		this.welcome_disclaimer_en_us = welcome_disclaimer_en_us;
	}

	public String getWelcome_disclaimer_es() {
		return this.getWelcome_disclaimer_es(true);
	}
	
	public String getWelcome_disclaimer_es(boolean replaceLineFeed) {
		if (replaceLineFeed && StringUtils.isNotBlank(this.welcome_disclaimer_es)) {
			return welcome_disclaimer_es.replaceAll("<br>", "\n"); 
		}
		return welcome_disclaimer_es;
	}
	
	public void setWelcome_disclaimer_es(String welcome_disclaimer_es) {
		if (StringUtils.isNotBlank(welcome_disclaimer_es)) {
			welcome_disclaimer_es = welcome_disclaimer_es.replaceAll("\\r?\\n", "<br>"); 
		}
		this.welcome_disclaimer_es = welcome_disclaimer_es;
	}

}
