package fate.webapp.blog.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * 日期转换类，默认格式yyyy-MM-dd HH:mm:ss
 * @author Fate
 *
 */
public class DateUtil {

    private DateUtil() {
        super();
        // TODO Auto-generated constructor stub
    }

    // RFC 822 Date Format
    private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    // ISO 8601 format
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // Alternate ISO 8601 format without fractional seconds
    private static final String ALTERNATIVE_ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Formats Date to GMT string.
     */
    public static String formatRfc822Date(Date date) {
        return getRfc822DateFormat().format(date);
    }

    /**
     * Parses a GMT-format string.
     */
    public static Date parseRfc822Date(String dateString) throws ParseException {
        return getRfc822DateFormat().parse(dateString);
    }

    private static DateFormat getRfc822DateFormat() {
        SimpleDateFormat rfc822DateFormat =
                new SimpleDateFormat(RFC822_DATE_FORMAT, Locale.US);
        rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));

        return rfc822DateFormat;
    }

    public static String formatIso8601Date(Date date) {
        return getIso8601DateFormat().format(date);
    }

    public static String formatAlternativeIso8601Date(Date date) {
        return getAlternativeIso8601DateFormat().format(date);
    }
    /**
     * Parse a date string in the format of ISO 8601.
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseIso8601Date(String dateString) throws ParseException {
        try {
            return getIso8601DateFormat().parse(dateString);
        } catch (ParseException e) {
            return getAlternativeIso8601DateFormat().parse(dateString);
        }
    }

    private static DateFormat getIso8601DateFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df;
    }

    private static DateFormat getAlternativeIso8601DateFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ALTERNATIVE_ISO8601_DATE_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df;
    }
    
	public static String format(Date date, String pattern){
		SimpleDateFormat sdf = null;
		if(pattern!=null)
			sdf = new SimpleDateFormat(pattern);
		else
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	public static Date parse(String date, String pattern) throws ParseException{
		SimpleDateFormat sdf = null;
		if(pattern!=null)
			sdf = new SimpleDateFormat(pattern);
		else
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(date);
	}
}
