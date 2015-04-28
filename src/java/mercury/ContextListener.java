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

package mercury;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import org.apache.log4j.Logger;

public final class ContextListener implements ServletContextListener {
    
    protected Logger log = Logger.getLogger(this.getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ServletContext servletContext = servletContextEvent.getServletContext();
            servletContext.setAttribute("FIRST_EXECUTION", "true");

            try {
                // Don't remove. System.err is causing a weird hang bug on Z39.50 server. Blame jzkit.
                PrintStream err = new PrintStream(new File("log" + File.pathSeparator + "err.txt"));
                System.setErr(err);
                //System.setOut(out);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Check and install upgrades
            UpgraderHelper upgrader = new UpgraderHelper();
            upgrader.checkAndInstallUpgrades();

            this.loadLanguages(servletContext);
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
    }

    private void loadLanguages(ServletContext servletContext) throws IOException {
        URL fileURL = null;
        String module = null;
        String i18nFile = null;
        String language = null;
        ClassLoader cl = null;
        Properties i18nProp = null;
        Properties languages = null;

        //--- Loads the mapping of "module name X file path" of all localization files
        ClassLoader cl2 = Controller.class.getClassLoader();
        URL fileURL2 = cl2.getResource("i18nLanguages.properties");
        BufferedReader br = new BufferedReader(new InputStreamReader(fileURL2.openStream(), "UTF-8"));
        Scanner scan = null;

        //--- reads nro languages, locale and label, then continue ...
        int nroLang = (new Integer(br.readLine())).intValue();
        languages = new Properties();

        String locale = null;
        String label = null;
        for (int i = 0; i < nroLang; i++) {
            scan = new Scanner(br.readLine());
            scan.useDelimiter("=");
            locale = scan.next();
            label = scan.next();

            //--- puts locale and label in Properties
            languages.put(locale, label);
        }
        servletContext.setAttribute("LANGUAGES", languages);

        //--- ... to read module x lang. files mappings and ...
        while (br.ready()) {
            scan = new Scanner(br.readLine());
            scan.useDelimiter("=");

            while (scan.hasNext()) {
                module = scan.next();
                if (scan.hasNext()) {
                    i18nFile = scan.next();
                }
                cl = Controller.class.getClassLoader();
                fileURL = cl.getResource(i18nFile);
                i18nProp = new Properties();
                i18nProp.load(new java.io.InputStreamReader(fileURL.openStream(), "UTF-8"));

                //--- ...puts them into an application attribute.
                language = i18nFile.substring(i18nFile.indexOf('.'));
                String attrName = "I18N_" + module + language;
                servletContext.setAttribute(attrName, i18nProp);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
