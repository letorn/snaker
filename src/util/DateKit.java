package util;

import static util.Validator.notBlank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateKit {

	public final static String Y = "yyyy";
	public final static String YM = "yyyy-MM";
	public final static String YMD = "yyyy-MM-dd";
	public final static String YMDH = "yyyy-MM-dd HH";
	public final static String YMDHM = "yyyy-MM-dd HH:mm";
	public final static String YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public final static String YMDHMSS = "yyyy-MM-dd HH:mm:ss.SSS";
	
	private final static DateFormat yDateFormat = new SimpleDateFormat(Y);
	private final static DateFormat ymDateFormat = new SimpleDateFormat(YM);
	private final static DateFormat ymdDateFormat = new SimpleDateFormat(YMD);
	private final static DateFormat ymdhDateFormat = new SimpleDateFormat(YMDH);
	private final static DateFormat ymdhmDateFormat = new SimpleDateFormat(YMDHM);
	private final static DateFormat ymdhmsDateFormat = new SimpleDateFormat(YMDHMS);
	private final static DateFormat ymdhmssDateFormat = new SimpleDateFormat(YMDHMSS);
	
	public static Date toDate(String string) {
		return toDate(string, YMDHMS);
	}
	
	public static Date toDate(String string, String format) {
		if (notBlank(string)) {
			try {
				if (Y.equals(format)) return yDateFormat.parse(string);
				else if (YM.equals(format)) return ymDateFormat.parse(string);
				else if (YMD.equals(format)) return ymdDateFormat.parse(string);
				else if (YMDH.equals(format)) return ymdhDateFormat.parse(string);
				else if (YMDHM.equals(format)) return ymdhmDateFormat.parse(string);
				else if (YMDHMS.equals(format)) return ymdhmsDateFormat.parse(string);
				else if (YMDHMSS.equals(format)) return ymdhmssDateFormat.parse(string);
				else return new SimpleDateFormat(format).parse(string);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String toString(Date date) {
		return toString(date, YMDHMS);
	}
	
	public static String toString(Date date, String format) {
		if (notBlank(date)) {
			if (Y.equals(format)) return yDateFormat.format(date);
			else if (YM.equals(format)) return ymDateFormat.format(date);
			else if (YMD.equals(format)) return ymdDateFormat.format(date);
			else if (YMDH.equals(format)) return ymdhDateFormat.format(date);
			else if (YMDHM.equals(format)) return ymdhmDateFormat.format(date);
			else if (YMDHMS.equals(format)) return ymdhmsDateFormat.format(date);
			else if (YMDHMSS.equals(format)) return ymdhmssDateFormat.format(date);
			else return new SimpleDateFormat(format).format(date);
		}
		return null;
	}
	
}
