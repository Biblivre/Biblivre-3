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

import com.ibm.icu.text.Transliterator;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import mercury.ExceptionUser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  17/02/2009
 */
public final class TextUtils {

    private static final DecimalFormat decimalFormat;

    static {
        decimalFormat = new DecimalFormat("#,##0.00");
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setMaximumFractionDigits(2);
        final DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator(',');
        s.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(s);
    }

    private TextUtils() {
    }

    public static String encodePassword(String password) {
        if (password == null) {
            throw new ExceptionUser("Password is null");
        }
        if (password.trim().length() == 0) {
            throw new ExceptionUser("Password is empty");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(password.getBytes("UTF-8"));
            byte[] raw = md.digest();
            return new String((new Base64()).encode(raw));
        } catch (Exception e) {
            throw new ExceptionUser(e.getMessage());
        }
    }

    public static String formatCurrency(final Float input) {
        return decimalFormat.format(input);
    }

    public static String removeDiacriticals(final String input) {
        if (input == null) {
            return input;
        }
        final String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);
        String final2 = decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return final2;
    }

    public static String combine(final String input) {
        if (input == null) {
            return input;
        }
        return Normalizer.normalize(input, Normalizer.Form.NFC);
    }

    public static String sanitize(final String s, final String d) {
        if (StringUtils.isBlank(s)) {
            return d;
        }

        return s.trim();
    }

    public static String icu4jNormalize(final String input) {
        if (input == null) {
            return input;
        }
        Transliterator normalizer = Transliterator.getInstance("NFD; [:Nonspacing Mark:] Remove; NFC;");
        String temp = normalizer.transliterate(input);
        return temp;
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static String center(String text, int length) {
        if (text == null) {
            text = "";
        }

        if (text.length() > length) {
            return text.substring(0, length);
        }

        return StringUtils.center(text, length);
    }

    public static String wraptext(String text, int length, String delimiter) {
        return WordUtils.wrap(text, length, delimiter, true);
    }

    public static String formatStringUser(String name) {
        return name.replaceAll("[-'_]", " ");
    }

    public static String incrementLastChar(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        return text + "zzzzzzzzzzzzzzzzz";
    }

    public static boolean endsInValidCharacter(String str) {
        String lastChar = String.valueOf(str.charAt(str.length() - 1));
        
        return TextUtils.removeDiacriticals(lastChar).matches("[0-9a-zA-Z]");
    }
}
