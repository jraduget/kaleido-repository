/*
 * $License$
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

import junit.framework.Assert;

import org.junit.Test;

/**
 * Date Helper test case
 * 
 * @author Jerome RADUGET
 */
public class DateHelperTest extends Assert {

   @Test
   public void testNewDate() {
	Date date = newDate(1, 1, 2000);
	assertNotNull(date);
	Calendar calend = Calendar.getInstance();
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
	float age = getAge(newDate(1, 1, 2000), newDate(16, 11, 1978));
	assertEquals(21.083334f, age);
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
