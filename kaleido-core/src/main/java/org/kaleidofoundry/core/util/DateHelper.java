/*
 * Copyright 2008-2010 the original author or authors
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
package org.kaleidofoundry.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.kaleidofoundry.core.util.locale.LocaleFactory;

/**
 * Some helper methods for date
 * 
 * @author Jerome RADUGET
 */
public abstract class DateHelper {

   /**
    * @param birthdate
    * @return Age from the current date
    */
   public static float getAge(final Date birthdate) {
	final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	return getAge(Calendar.getInstance(locale).getTime(), birthdate);
   }

   /**
    * Calculating age from a current date
    * 
    * @param current
    * @param birthdate
    * @return Age from the current (arg) date
    */
   public static float getAge(final Date current, final Date birthdate) {

	if (birthdate == null) { return 0; }
	if (current == null) {
	   return getAge(birthdate);
	} else {
	   final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	   final Calendar calend = new GregorianCalendar(locale);
	   calend.set(Calendar.HOUR_OF_DAY, 0);
	   calend.set(Calendar.MINUTE, 0);
	   calend.set(Calendar.SECOND, 0);
	   calend.set(Calendar.MILLISECOND, 0);

	   calend.setTimeInMillis(current.getTime() - birthdate.getTime());

	   float result = 0;
	   result = calend.get(Calendar.YEAR) - 1970;
	   result += (float) calend.get(Calendar.MONTH) / (float) 12;
	   return result;
	}

   }

   /**
    * @param date
    * @return day month of date
    */
   public static int dayMonthOfDate(final Date date) {
	if (date == null) { return -1; }
	final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	final Calendar calend = new GregorianCalendar(locale);
	calend.setTime(date);
	return calend.get(Calendar.DAY_OF_MONTH);
   }

   /**
    * @param date
    * @return year of date
    */
   public static int yearOfDate(final Date date) {
	if (date == null) { return -1; }
	final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	final Calendar calend = new GregorianCalendar(locale);
	calend.setTime(date);
	return calend.get(Calendar.YEAR);
   }

   /**
    * @param date
    * @return month of date
    */
   public static int monthOfDate(final Date date) {
	if (date == null) { return -1; }
	final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	final Calendar calend = new GregorianCalendar(locale);
	calend.setTime(date);
	return calend.get(Calendar.MONTH) + 1;
   }

   /**
    * @param day
    * @param month between 1 et 12 (french)
    * @param year year
    * @return Date corresponding to data input (hour, minute, second, ms will be set to zero)
    */
   public static Date newDate(final int day, final int month, final int year) {
	final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	final Calendar calend = new GregorianCalendar(locale);
	calend.set(Calendar.DATE, day);
	calend.set(Calendar.MONTH, month - 1);
	calend.set(Calendar.YEAR, year);
	calend.set(Calendar.HOUR_OF_DAY, 0);
	calend.set(Calendar.MINUTE, 0);
	calend.set(Calendar.SECOND, 0);
	calend.set(Calendar.MILLISECOND, 0);
	return calend.getTime();
   }

   /**
    * @param original original date
    * @param field field to add {@link Calendar}.DAY... to the original date arg
    * @param amount value to add
    * @return new instance of date, computed by adding original date, a month, a year, a minute... (the field arg )
    */
   public static Date add(final Date original, final int field, final int amount) {
	final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	final Calendar calendMin = new GregorianCalendar(locale);
	calendMin.setTime(original);
	calendMin.add(field, amount);
	return calendMin.getTime();
   }

}
