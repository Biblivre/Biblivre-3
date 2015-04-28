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

import biblivre3.config.Config;
import biblivre3.config.ConfigurationEnum;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackupBO {

    public boolean doBackup(String path, String fileSuffix) {
        File pgdump = this.getPgDumpFromFilesystem();

        if (pgdump == null) {
            return false;
        }
        
        String[] commands = new String[]{
            pgdump.getAbsolutePath(),	// 0
            "--ignore-version",		// 1
            "--host",			// 2
            "localhost",		// 3
            "--port",			// 4
            "5432",			// 5
            "--encoding",		// 6
            "UTF-8",			// 7
            "--file",			// 8
            "",                         // 9
            "--format",			// 10
            "p",			// 11
            "--compress",		// 12
            "9",			// 13
            "--blobs",			// 14
            "--create"			// 15
        };

        File backup = new File(path, "backup_" + fileSuffix + ".b3b");
        commands[9] = backup.getAbsolutePath();

        return this.dumpDatabase(commands);
    }

    private boolean dumpDatabase(String[] commands) {
        ProcessBuilder pb = new ProcessBuilder(commands);

        pb.environment().put("PGUSER", "biblivre");
        pb.environment().put("PGPASSWORD", "abracadabra");
        pb.environment().put("PGDATABASE", Config.getConfigProperty(ConfigurationEnum.DATABASE_NAME));

        pb.redirectErrorStream(true);

        try {
            Process p = pb.start();

            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();

            return p.exitValue() == 0;
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }
    
    private File getPgDumpFromFilesystem() {
        String os = System.getProperty("os.name").toUpperCase();

        if (os.contains("WINDOWS")) {
            return getWindowsPgDump();
        } else if (os.contains("LINUX")) {
            return getLinuxPgDump();
        } else if (os.contains("MAC OS X")) {
            return getMacOsPgDump();
        } else {
            return null;
        }
    }
    
    private String getPgDumpFilename() {
        String os = System.getProperty("os.name").toUpperCase();

        if (os.contains("WINDOWS")) {
            return "pg_dump.exe";
        } else {
            return "pg_dump";
        }
    }

    private File getMacOsPgDump() {
        //TODO Implement
        return new File("/usr/bin/pg_dump");
    }

    private File getLinuxPgDump() {
        //TODO Implement
        return new File("/usr/bin/pg_dump");
    }

    private File getWindowsPgDump() {
        String[] commands;

        //Step 1 - Detecting current PostgreSQL service name
        commands = new String[]{
            "tasklist",
            "/nh",
            "/svc",
            "/fi",
            "imagename eq pg_ctl.exe",
            "/fo",
            "csv"
        };

        String postgresServiceName = this.processPatternMatcher(commands, "([^\"]+)\"$", 1);
        if (postgresServiceName == null) {
            return null;
        }

        //Step 2 - Detect PostgreSQL Product Code
        String postgresProductCode = null;
        String[] regkeys = new String[]{
            "HKLM\\SOFTWARE\\PostgreSQL\\Services\\" + postgresServiceName,
            "HKLM\\SOFTWARE\\Wow6432Node\\PostgreSQL\\Services\\" + postgresServiceName
        };
        for (String regkey : regkeys) {
            postgresProductCode = getRegValue(regkey, "Product Code");
            if (postgresProductCode != null) {
                break;
            }
        }
        if (postgresProductCode == null) {
            return null;
        }

        //Step 3 - Detect PostgreSQL Base Directory
        String postgresBaseDirectory = null;
        regkeys = new String[]{
            "HKLM\\SOFTWARE\\PostgreSQL\\Installations\\" + postgresProductCode,
            "HKLM\\SOFTWARE\\Wow6432Node\\PostgreSQL\\Installations\\" + postgresProductCode
        };
        for (String regkey : regkeys) {
            postgresBaseDirectory = getRegValue(regkey, "Base Directory");
            if (postgresBaseDirectory != null) {
                break;
            }
        }
        if (postgresBaseDirectory == null) {
            return null;
        }

        File file = new File(postgresBaseDirectory + File.separator + "bin", this.getPgDumpFilename());
        return file.exists() ? file : null;
    }

    private String getRegValue(String dir, String key) {
        String[] commands = new String[]{
            "reg",
            "query",
            dir,
            "/V",
            key
        };

        return this.processPatternMatcher(commands, "REG_SZ\\s+(.+)$", 1);
    }

    private String processPatternMatcher(String[] commands, String regex, int group) {
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;

            Pattern pattern = Pattern.compile(regex);
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    
    public String getDatabaseVersion() {
        String name = this.getTasklistLine("pg_ctl.exe");
        String service[] = name.split("\"");

        if (service.length < 5) {
            return null;
        }

        return service[5];
    }

    public String getTasklistLine(String executable) {
        String linha = "";
        String service = "";
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("cmd /c tasklist /nh /svc /fi \"imagename eq " + executable + "\" /fo csv");
            if (p != null) {
                InputStreamReader streamReader = new InputStreamReader(p.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);

                while ((linha = reader.readLine()) != null) {
                    service = linha;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return service;
    }
}
