package org.kaleidofoundry.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Test;

/**
 * Tests for dates with locale / timezone
 * 
 * @author Jerome RADUGET
 */
public class DateFormatSample {

   @Test
   public void testCalendarLocale() {

	final Locale localeFR = Locale.FRENCH;
	final Locale localeUS = Locale.US;
	final Locale localeJP = Locale.JAPAN;

	final DateFormat dfFR = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

	final Calendar currentFR = new GregorianCalendar(localeFR);
	final Calendar currentUS = new GregorianCalendar(localeUS);
	final Calendar currentJP = new GregorianCalendar(localeJP);

	/*
	 * TimeZone timeFR = SimpleTimeZone(3600000,
	 * "Europe/Paris",
	 * Calendar.MARCH, -1, Calendar.SUNDAY,
	 * 3600000, SimpleTimeZone.UTC_TIME,
	 * Calendar.OCTOBER, -1, Calendar.SUNDAY,
	 * 3600000, SimpleTimeZone.UTC_TIME,
	 * 3600000);
	 */

	System.out.println(dfFR.format(currentFR.getTime()));
	System.out.println(dfFR.format(currentUS.getTime()));
	System.out.println(dfFR.format(currentJP.getTime()));
   }
}
