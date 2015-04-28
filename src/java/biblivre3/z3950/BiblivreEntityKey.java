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
package biblivre3.z3950;

import com.k_int.sql.data_dictionary.DatabaseColAttribute;
import com.k_int.sql.data_dictionary.EntityKey;
import com.k_int.sql.data_dictionary.EntityTemplate;
import com.k_int.sql.data_dictionary.UnknownAccessPointException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author Danniel
 */
public class BiblivreEntityKey extends EntityKey {

    private java.util.Map key_components = new java.util.HashMap();

    public BiblivreEntityKey(EntityTemplate et, ResultSet rs) {
        try {
            for (Iterator e = et.getKeyAttrs(); e.hasNext();) {
                String key_attr_name = (String) e.next();
                DatabaseColAttribute dca = (DatabaseColAttribute) et.getAttributeDefinition(key_attr_name);
                int colpos = rs.findColumn(dca.getColName());
                Object o = null;
                switch (rs.getMetaData().getColumnType(colpos)) {
                    case 2:
                        o = new Integer(rs.getInt(colpos));
                        break;
                    case java.sql.Types.VARCHAR:
                        try {
                            o = new String(rs.getBytes(colpos), "UTF-8");
                        } catch (Exception ex) {
                            o = rs.getString(colpos);
                        }
                        break;
                    default:
                        o = rs.getObject(colpos);
                        break;
                }
                key_components.put(key_attr_name, o);
            }
        } catch (SQLException sqle) {
            log.error(sqle.getMessage(), sqle);
        } catch (NullPointerException npe) {
            log.error(npe.getMessage(), npe);
        } catch (UnknownAccessPointException uape) {
            log.error(uape.getMessage(), uape);
        }
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        try {
            for (Iterator e = getAttrNames(); e.hasNext();) {
                String attrname = (String) e.next();
                Object attrval = key_components.get(attrname);
                sw.write(attrname);
                sw.write("='");
                sw.write("" + attrval);
                sw.write("'");
                if (e.hasNext()) {
                    sw.write(",");
                }
            }
        } catch (Exception e) {
            log.warn("Unable to assemble key string", e);
        }
        return sw.toString();
    }

    @Override
    public java.util.Map getKeyMap() {
        return key_components;
    }

    @Override
    public void addKeyComponent(String attrname, Object attrval) {
        key_components.put(attrname, attrval);
    }

    @Override
    public int numComponents() {
        return key_components.size();
    }

    @Override
    public Iterator getAttrNames() {
        return key_components.keySet().iterator();
    }

    @Override
    public Object getAttrValue(String attrname) {
        return key_components.get(attrname);
    }
}
