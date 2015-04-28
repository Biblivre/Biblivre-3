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

package biblivre3.utils;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Classe que contém métodos utilitários para limpar
 * o registro de elementos HTML que sejam diacríticos,
 * combinando letras para o código UTF-8 referente.
 * 
 * A Classe se baseia na seguinte tabela para realizar
 * a conversão:
 * 
 * 192 (À) "A&#768;"
 * 193 (Á) "A&#769;"
 * 194 (Â) "A&#770;"
 * 195 (Ã) "A&#771;"
 * 196 (Ä) "A&#776;"
 * 197 (Å) "A&#778;"
 * 199 (Ç) "C&#184;"
 * 200 (È) "E&#768;"
 * 201 (É) "E&#769;"
 * 202 (Ê) "E&#770;"
 * 203 (Ë) "E&#776;"
 * 204 (Ì) "I&#768;"
 * 205 (Í) "I&#769;"
 * 206 (Î) "I&#770;"
 * 207 (Ï) "I&#776;"
 * 209 (Ñ) "N&#771;"
 * 210 (Ò) "O&#768;"
 * 211 (Ó) "O&#769;"
 * 212 (Ô) "O&#770;"
 * 213 (Õ) "O&#771;"
 * 214 (Ö) "O&#776;"
 * 217 (Ù) "U&#768;"
 * 218 (Ú) "U&#769;"
 * 219 (Û) "U&#770;"
 * 220 (Ü) "U&#776;"
 * 221 (Ý) "Y&#769;"
 * 224 (à) "a&#768;"
 * 225 (á) "a&#769;"
 * 226 (â) "a&#770;"
 * 227 (ã) "a&#771;"
 * 228 (ä) "a&#776;"
 * 229 (å) "a&#778;"
 * 231 (ç) "c&#184;"
 * 232 (è) "e&#768;"
 * 233 (é) "e&#769;"
 * 234 (ê) "e&#770;"
 * 235 (ë) "e&#776;"
 * 236 (ì) "i&#768;"
 * 237 (í) "i&#769;"
 * 238 (î) "i&#770;"
 * 239 (ï) "i&#776;"
 * 241 (ñ) "n&#771;"
 * 242 (ò) "o&#768;"
 * 243 (ó) "o&#769;"
 * 244 (ô) "o&#770;"
 * 245 (õ) "o&#771;"
 * 246 (ö) "o&#776;"
 * 249 (ù) "u&#768;"
 * 250 (ú) "u&#769;"
 * 251 (û) "u&#770;"
 * 252 (ü) "u&#776;"
 * 253 (ý) "y&#769;"
 * 255 (ÿ) "y&#776;"
 * 
 * @author Danniel Nascimento
 * @version 1.0
 * @since 26/08/2008
 */
public class HtmlEntityEscaper {

    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&#\\d*;");
    private static final TreeMap<String, Integer> entityMap;
    

    static {
        entityMap = new TreeMap<String, Integer>();
        entityMap.put("A&(amp;)?#768;", 192);
        entityMap.put("A&(amp;)?#769;", 193);
        entityMap.put("A&(amp;)?#770;", 194);
        entityMap.put("A&(amp;)?#771;", 195);
        entityMap.put("A&(amp;)?#776;", 196);
        entityMap.put("A&(amp;)?#778;", 197);
        entityMap.put("C&(amp;)?#184;", 199);
        entityMap.put("C&(amp;)?#807;", 199);
        entityMap.put("E&(amp;)?#768;", 200);
        entityMap.put("E&(amp;)?#769;", 201);
        entityMap.put("E&(amp;)?#770;", 202);
        entityMap.put("E&(amp;)?#776;", 203);
        entityMap.put("I&(amp;)?#768;", 204);
        entityMap.put("I&(amp;)?#769;", 205);
        entityMap.put("I&(amp;)?#770;", 206);
        entityMap.put("I&(amp;)?#776;", 207);
        entityMap.put("N&(amp;)?#771;", 209);
        entityMap.put("O&(amp;)?#768;", 210);
        entityMap.put("O&(amp;)?#769;", 211);
        entityMap.put("O&(amp;)?#770;", 212);
        entityMap.put("O&(amp;)?#771;", 213);
        entityMap.put("O&(amp;)?#776;", 214);
        entityMap.put("U&(amp;)?#768;", 217);
        entityMap.put("U&(amp;)?#769;", 218);
        entityMap.put("U&(amp;)?#770;", 219);
        entityMap.put("U&(amp;)?#776;", 220);
        entityMap.put("Y&(amp;)?#769;", 221);
        entityMap.put("a&(amp;)?#768;", 224);
        entityMap.put("a&(amp;)?#769;", 225);
        entityMap.put("a&(amp;)?#770;", 226);
        entityMap.put("a&(amp;)?#771;", 227);
        entityMap.put("a&(amp;)?#776;", 228);
        entityMap.put("a&(amp;)?#778;", 229);
        entityMap.put("c&(amp;)?#184;", 231);
        entityMap.put("c&(amp;)?#807;", 231);
        entityMap.put("e&(amp;)?#768;", 232);
        entityMap.put("e&(amp;)?#769;", 233);
        entityMap.put("e&(amp;)?#770;", 234);
        entityMap.put("e&(amp;)?#776;", 235);
        entityMap.put("i&(amp;)?#768;", 236);
        entityMap.put("i&(amp;)?#769;", 237);
        entityMap.put("i&(amp;)?#770;", 238);
        entityMap.put("i&(amp;)?#776;", 239);
        entityMap.put("n&(amp;)?#771;", 241);
        entityMap.put("o&(amp;)?#768;", 242);
        entityMap.put("o&(amp;)?#769;", 243);
        entityMap.put("o&(amp;)?#770;", 244);
        entityMap.put("o&(amp;)?#771;", 245);
        entityMap.put("o&(amp;)?#776;", 246);
        entityMap.put("u&(amp;)?#768;", 249);
        entityMap.put("u&(amp;)?#769;", 250);
        entityMap.put("u&(amp;)?#770;", 251);
        entityMap.put("u&(amp;)?#776;", 252);
        entityMap.put("y&(amp;)?#769;", 253);
        entityMap.put("y&(amp;)?#776;", 255);
    }

    public static String unescapeHtmlEntities(String html) {
        String unescapedHtml = html;
        Scanner scanner = new Scanner(html);
        String match = scanner.findWithinHorizon(HTML_ENTITY_PATTERN, 0);
        if (match != null) {
            String charCodeString = match.replace("&#", "").replace(";", "");
            char charCode = (char) Integer.parseInt(charCodeString);
            unescapedHtml = html.replaceAll(match, String.valueOf(charCode));
            unescapedHtml = HtmlEntityEscaper.unescapeHtmlEntities(unescapedHtml);
        }
        return unescapedHtml;
    }

    public static String replaceHtmlEntities(String html) {
        for (String entity : entityMap.keySet()) {
            Integer replacement = entityMap.get(entity);
            html = html.replaceAll(entity, String.valueOf((char) replacement.intValue()));
        }
        return html;
    }

}
