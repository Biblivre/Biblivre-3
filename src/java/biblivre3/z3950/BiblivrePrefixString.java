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

import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.commons.lang.StringUtils;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import org.jzkit.search.util.QueryModel.Internal.InternalModelNamespaceNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.InvalidQueryException;
import org.jzkit.search.util.QueryModel.PrefixString.PrefixQueryException;
import org.jzkit.search.util.QueryModel.PrefixString.PrefixString;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Danniel
 */
public class BiblivrePrefixString extends PrefixString {

    private String queryAttr;
    private String queryTerms;
    private InternalModelRootNode internalModel = null;

    private static final String DEFAULT_ATTRSET = "bib-1";
    private static final String DEFAULT_ATTRTYPE = "AccessPoint";

    public BiblivrePrefixString(String queryString) {
        super(queryString);
    }

    @Override
    public InternalModelRootNode toInternalQueryModel(ApplicationContext ctx) throws InvalidQueryException {
        if (StringUtils.isBlank(queryAttr) || StringUtils.isBlank(queryTerms)) {
            throw new InvalidQueryException("Null prefix string");
        }
        try {
            if (internalModel == null) {
                internalModel = new InternalModelRootNode();
                InternalModelNamespaceNode node = new InternalModelNamespaceNode();
                node.setAttrset(DEFAULT_ATTRSET);
                internalModel.setChild(node);
                AttrPlusTermNode attrNode = new AttrPlusTermNode();
                final String attrValue = "1." + queryAttr;
                attrNode.setAttr(DEFAULT_ATTRTYPE, new AttrValue(null, attrValue));
                Vector terms = new Vector();
                StringTokenizer tokenizer = new StringTokenizer(queryTerms);
                while (tokenizer.hasMoreElements()) {
                    terms.add(tokenizer.nextElement());
                }
                if (terms.size() > 1) {
                    attrNode.setTerm(terms);
                } else if (terms.size() == 1) {
                    attrNode.setTerm(terms.get(0));
                } else {
                    throw new PrefixQueryException("No Terms");
                }
                node.setChild(attrNode);
            }
        } catch (Exception e) {
            throw new InvalidQueryException(e.getMessage());
        }
        return internalModel;
    }

    public String getQueryAttr() {
        return queryAttr;
    }

    public void setQueryAttr(String queryAttr) {
        this.queryAttr = queryAttr;
    }

    public String getQueryTerms() {
        return queryTerms;
    }

    public void setQueryTerms(String queryTerms) {
        this.queryTerms = queryTerms;
    }

}
