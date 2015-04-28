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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class Config {

    private static Properties properties;
    private static URL ConfigFile;
    private static String ConfigFilePath = "/config.bib";

    public static HashMap<String, String> ht;


    static {
        loadProperties();
    }

    public static String getConfigProperty(final ConfigurationEnum config) {
        return properties.getProperty(config.name());
    }
    
    public static String getConfigProperty(final ConfigurationEnum config, final String def) {
        String ret = properties.getProperty(config.name());
        if (ret == null || ret.isEmpty()) {
            ret = def;
        }

        return ret;
    }

    public static String getConfigProperty(String prop) {
        return properties.getProperty(prop);
    }

    public static void setConfigProperty(String prop, String value) {
        properties.setProperty(prop, value);
    }

    private static void loadProperties() {
        ClassLoader cl = Config.class.getClassLoader();
        ConfigFile = cl.getResource(ConfigFilePath);
        properties = new Properties();
        try {
            properties.load(ConfigFile.openStream());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private static String createFileComment() {
        return " THIS FILE IS AUTOMATICALY GENERATED.\n" +
                "# DO NOT EDIT IT.\n" +
                "# TO CHANGE SYSTEM SETTINGS, USE THE ADMINISTRATION/CONFIGURATION OPTION\n" +
                "# IN SYSTEM MENU.\n" +
                "#";
    }

    public static void saveAllConfig(HashMap<String, String> ht) throws IOException {
        properties = new Properties();
        for (ConfigurationEnum ce : ConfigurationEnum.values()) {
			String value = ht.get(ce.name());
			if (ce.equals(ConfigurationEnum.WELCOME_DISCLAIMER_PT_BR) ||
					ce.equals(ConfigurationEnum.WELCOME_DISCLAIMER_EN_US) ||
					ce.equals(ConfigurationEnum.WELCOME_DISCLAIMER_ES)) {
				value = value.replaceAll("\\r?\\n", "<br>"); 
			}
			properties.setProperty(ce.name(), value);
        }
        FileOutputStream fos = new FileOutputStream(new File(ConfigFile.getFile().replaceAll("%20", " ")));
        properties.store(fos, createFileComment());
        fos.flush();
        fos.close();
    }

}