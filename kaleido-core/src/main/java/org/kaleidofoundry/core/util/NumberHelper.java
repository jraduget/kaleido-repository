/*
 *  Copyright 2008-2021 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Some helper methods for number
 * 
 * @author jraduget
 */
public abstract class NumberHelper {

   /**
    * converter from string to number
    * 
    * <pre>
    * assertNull(toNumber(null, Integer.class));
    * assertEquals((Integer) 1234567, toNumber(&quot;1 234 567€&quot;, Locale.FRANCE, Integer.class));
    * assertEquals((Integer) 1234567, toNumber(&quot;$1,234,567&quot;, Locale.US, Integer.class));
    * assertEquals((Float) 1234567.89f, toNumber(&quot;1 234 567,89€&quot;, Locale.FRANCE, Float.class));
    * assertEquals((Float) 1234567.89f, toNumber(&quot;$1,234,567.89&quot;, Locale.US, Float.class));
    * </pre>
    * 
    * @param <N>
    * @param value value to convert
    * @param locale Locale you want to use
    * @param c Number class you want
    * @return conversion of the string
    * @throws NumberFormatException
    */
   @SuppressWarnings("unchecked")
   public static <N extends Number> N toNumber(String value, final Locale locale, final Class<N> c) throws NumberFormatException {

	if (value == null) { return null; }

	if (locale != null) {
	   DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
	   value = StringHelper.replaceAll(value, String.valueOf(symbols.getGroupingSeparator()), "");
	   value = StringHelper.replaceAll(value, symbols.getCurrencySymbol(), "");
	   value = StringHelper.replaceAll(value, String.valueOf(symbols.getDecimalSeparator()), ".");
	}

	value = StringHelper.replaceAll(value, " ", ""); // space 32
	value = StringHelper.replaceAll(value, " ", ""); // nbsp 160
	value = StringHelper.replaceAll(value, "\n", "");
	value = StringHelper.replaceAll(value, "\t", "");
	value = StringHelper.replaceAll(value, ",", ".");
	value = StringHelper.replaceAll(value, "$", "");
	value = StringHelper.replaceAll(value, "€", "");
	value = StringHelper.replaceAll(value, "£", "");

	if (c.isAssignableFrom(Byte.class)) { return (N) Byte.valueOf(value); }
	if (c.isAssignableFrom(Short.class)) { return (N) Short.valueOf(value); }
	if (c.isAssignableFrom(Integer.class)) { return (N) Integer.valueOf(value); }
	if (c.isAssignableFrom(Long.class)) { return (N) Long.valueOf(value); }
	if (c.isAssignableFrom(BigInteger.class)) { return (N) new BigInteger(value); }
	if (c.isAssignableFrom(Float.class)) { return (N) Float.valueOf(value); }
	if (c.isAssignableFrom(Double.class)) { return (N) Double.valueOf(value); }
	if (c.isAssignableFrom(BigDecimal.class)) { return (N) new BigDecimal(value); }

	throw new IllegalArgumentException(c.getName());
   }

   /**
    * converter from string to number
    * 
    * @param value value to convert
    * @param c Number class you want
    * @return conversion of the string
    * @throws NumberFormatException
    */
   public static <N extends Number> N toNumber(final String value, final Class<N> c) throws NumberFormatException {
	return toNumber(value, null, c);

   }
}
