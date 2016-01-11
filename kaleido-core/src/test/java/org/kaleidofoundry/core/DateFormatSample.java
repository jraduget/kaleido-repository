/*  
 * Copyright 2008-2016 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * @author jraduget
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
