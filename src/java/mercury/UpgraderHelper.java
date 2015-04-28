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

import java.util.HashMap;

public class UpgraderHelper {
    private UpgraderDAO dao;

    public UpgraderHelper() {
        this.dao = new UpgraderDAO();
    }

    public void checkAndInstallUpgrades() {
        HashMap<String, Boolean> installedVersions = this.dao.getInstalledVersions();

        if (!installedVersions.containsKey("3.0.1")) {
            this.install_3_0_1_upgrade();
        }

        if (!installedVersions.containsKey("3.0.2")) {
            this.install_3_0_2_upgrade();
        }

        if (!installedVersions.containsKey("3.0.3")) {
            this.install_3_0_3_upgrade();
        }

        if (!installedVersions.containsKey("3.0.4")) {
            this.install_3_0_4_upgrade();
        }

        if (!installedVersions.containsKey("3.0.5")) {
            this.install_3_0_5_upgrade();
        }

        if (!installedVersions.containsKey("3.0.6")) {
            this.install_3_0_6_upgrade();
        }

        if (!installedVersions.containsKey("3.0.7")) {
            this.install_3_0_7_upgrade();
        }

        if (!installedVersions.containsKey("3.0.8")) {
            this.install_3_0_8_upgrade();
        }

        if (!installedVersions.containsKey("3.0.9")) {
            this.install_3_0_9_upgrade();
        }

        if (!installedVersions.containsKey("3.0.10")) {
            this.install_3_0_10_upgrade();
        }

        if (!installedVersions.containsKey("3.0.11")) {
            this.install_3_0_11_upgrade();
        }

        if (!installedVersions.containsKey("3.0.12")) {
            this.install_3_0_12_upgrade();
        }

        if (!installedVersions.containsKey("3.0.13")) {
            this.install_3_0_13_upgrade();
        }
        
        if (!installedVersions.containsKey("3.0.14")) {
            this.install_3_0_14_upgrade();
        }
        
        if (!installedVersions.containsKey("3.0.15")) {
            this.install_3_0_15_upgrade();
        }
        
        if (!installedVersions.containsKey("3.0.16")) {
            this.install_3_0_16_upgrade();
        }
        
        if (!installedVersions.containsKey("3.0.17")) {
            this.install_3_0_17_upgrade();
        }
    }

    private void install_3_0_1_upgrade() {
        if (this.dao.v3_0_1_fixHoldingsDatabase()) {
            this.dao.insertVersion("3.0.1");
        }
    }

    private void install_3_0_2_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.2");
    }

    private void install_3_0_3_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.3");
    }

    private void install_3_0_4_upgrade() {
        if (this.dao.v3_0_4_fixAuthoritiesSequence()) {
            this.dao.insertVersion("3.0.4");
        }
    }

    private void install_3_0_5_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.5");
    }

    private void install_3_0_6_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.6");
    }

    private void install_3_0_7_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.7");
    }
    
    private void install_3_0_8_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.8");
    }

    private void install_3_0_9_upgrade() {
        if (this.dao.v3_0_9_removeInvalidLogins()) {
            this.dao.insertVersion("3.0.9");
        }
    }

    private void install_3_0_10_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.10");
    }

    private void install_3_0_11_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.11");
    }

    private void install_3_0_12_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.12");
    }

    private void install_3_0_13_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.13");
    }

    private void install_3_0_14_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.14");
    }
    
    private void install_3_0_15_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.15");
    }
    
    private void install_3_0_16_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.16");
    }
    
    private void install_3_0_17_upgrade() {
        // No other changes
        this.dao.insertVersion("3.0.17");
    }
}
