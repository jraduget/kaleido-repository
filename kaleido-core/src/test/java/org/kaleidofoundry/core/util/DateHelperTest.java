/*
 * Copyright 2008-2021 the original author or authors
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

import static org.kaleidofoundry.core.util.DateHelper.add;
import static org.kaleidofoundry.core.util.DateHelper.dayMonthOfDate;
import static org.kaleidofoundry.core.util.DateHelper.getAge;
import static org.kaleidofoundry.core.util.DateHelper.monthOfDate;
import static org.kaleidofoundry.core.util.DateHelper.newDate;
import static org.kaleidofoundry.core.util.DateHelper.yearOfDate;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kaleidofoundry.core.util.locale.LocaleFactory;

/**
 * Date Helper test case
 * 
 * @author jraduget
 */
public class DateHelperTest  {

   @Test
   public void testNewDate() {
	Date date = newDate(1, 1, 2000);
	assertNotNull(date);
	final Locale locale = LocaleFactory.getDefaultFactory().getCurrentLocale();
	Calendar calend = Calendar.getInstance(locale);
	calend.setTime(date);
	assertEquals(1, calend.get(Calendar.DATE));
	assertEquals(1, calend.get(Calendar.MONTH) + 1);
	assertEquals(2000, calend.get(Calendar.YEAR));
	assertEquals(0, calend.get(Calendar.HOUR_OF_DAY));
	assertEquals(0, calend.get(Calendar.MINUTE));
	assertEquals(0, calend.get(Calendar.SECOND));
	assertEquals(0, calend.get(Calendar.MILLISECOND));
   }

   @Test
   public void testGetAgeFromCurrent() {
	double age = getAge(newDate(1, 1, 2000), newDate(16, 11, 1978));
	assertEquals(21.08333396911621d, age, 0d);
   }

   @Test
   public void testDayMonthOfDate() {
	assertEquals(31, dayMonthOfDate(newDate(31, 12, 2000)));
   }

   @Test
   public void testYearOfDate() {
	assertEquals(2000, yearOfDate(newDate(31, 12, 2000)));
   }

   @Test
   public void testMonthOfDate() {
	assertEquals(12, monthOfDate(newDate(31, 12, 2000)));
   }

   @Test
   public void testAdd() {
	int oneDayInMs = 24 * 60 * 60 * 1000;
	Date date = newDate(1, 1, 2000);
	Date newDate = add(date, Calendar.DATE, 1); // add a day
	assertEquals(date.getTime() + oneDayInMs, newDate.getTime());
   }
}
