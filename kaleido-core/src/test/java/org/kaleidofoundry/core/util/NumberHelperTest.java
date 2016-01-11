/*
 *  Copyright 2008-2016 the original author or authors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kaleidofoundry.core.util.NumberHelper.toNumber;

import java.util.Locale;

import org.junit.Test;

/**
 * @author jraduget
 */
public class NumberHelperTest  {

   @Test
   public void integerTest() {
	assertNull(toNumber(null, Integer.class));
	assertEquals((Integer) 1234567, toNumber("1 234 567", Integer.class));
	assertEquals((Integer) 1234567, toNumber("1 234 567€", Locale.FRANCE, Integer.class));
	assertEquals((Integer) 1234567, toNumber("$1,234,567", Locale.US, Integer.class));
   }

   @Test
   public void floatTest() {
	assertNull(toNumber(null, Float.class));
	assertEquals((Float) 1234567.89f, toNumber("1 234 567,89", Float.class));
	assertEquals((Float) 1234567.89f, toNumber("1 234 567,89€", Locale.FRANCE, Float.class));
	assertEquals((Float) 1234567.89f, toNumber("$1,234,567.89", Locale.US, Float.class));
   }

   @Test
   public void doubleTest() {
	assertNull(toNumber(null, Double.class));
	assertEquals((Double) 1234567.89, toNumber("1 234 567,89", Double.class));
	assertEquals((Double) 1234567.89, toNumber("1 234 567,89€", Locale.FRANCE, Double.class));
	assertEquals((Double) 1234567.89, toNumber("$1,234,567.89", Locale.US, Double.class));
   }

}
