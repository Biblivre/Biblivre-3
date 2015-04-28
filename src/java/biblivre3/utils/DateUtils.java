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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Danniel Nascimento (dannielwillian@biblivre.org.br)
 * @since  23/03/2009
 */
public class DateUtils {

    public static final DateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy");

    public static String getCurrentDateTimeISO8601() {
        DecimalFormat df = new DecimalFormat("00");
        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.AM_PM) == Calendar.AM ? calendar.get(Calendar.HOUR) : calendar.get(Calendar.HOUR) + 12;

        return (new Integer(calendar.get(Calendar.YEAR))).toString() +
                "-" + df.format(calendar.get(Calendar.MONTH) + 1) +
                "-" + df.format(calendar.get(Calendar.DATE)) +
                " " + df.format(hour) +
                ":" + df.format(calendar.get(Calendar.MINUTE)) +
                ":" + df.format(calendar.get(Calendar.SECOND));
    }

    public static int dateDiff(Date date1, Date date2) {
        int count = 0;
        int diff = 0;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        
        cal1.setTime(date1);
        cal2.setTime(date2);
        
        while (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) {
            count = 365 * (cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR));
            diff += count;
            cal1.add(Calendar.DAY_OF_YEAR, count);
        }

        if (cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)) {
            count = cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
            diff += count;
        }
        return diff;
    }
    
    public static Date add(final Date date, final Integer field, final Integer amount) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }
    
    public static String format(final DateFormat f, final Date d) {
        return f.format(d);
    }

    public static Date verifyDate(final String texto, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        sdf.setLenient(false);
        try {
            if (!texto.equals("")) {
                date = sdf.parse(texto);
            }
            return date;
        } catch (ParseException ex) {

        }

        return null;
    }
}
