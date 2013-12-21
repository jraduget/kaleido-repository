/*
 *  Copyright 2008-2011 the original author or authors.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

/**
 * @author jraduget
 */
public class ObjectHelperTest {

   @Test
   public void firstNotNull() {

	assertNull(ObjectHelper.firstNonNull((String) null));
	assertEquals("1", ObjectHelper.firstNonNull("1"));

	assertNull(ObjectHelper.firstNonNull(null, null));
	assertEquals("1", ObjectHelper.firstNonNull("1", null));
	assertEquals("1", ObjectHelper.firstNonNull(null, "1"));
	assertEquals("1", ObjectHelper.firstNonNull("1", "2"));

	assertNull(ObjectHelper.firstNonNull(null, null, null));
	assertEquals("1", ObjectHelper.firstNonNull("1", null, null));
	assertEquals("1", ObjectHelper.firstNonNull(null, "1", null));
	assertEquals("1", ObjectHelper.firstNonNull(null, null, "1"));
	assertEquals("1", ObjectHelper.firstNonNull("1", "2", "3"));

	assertNull(ObjectHelper.firstNonNull(null, null, null, null));
	assertEquals("1", ObjectHelper.firstNonNull("1", null, null, null));
	assertEquals("1", ObjectHelper.firstNonNull(null, "1", null, null));
	assertEquals("1", ObjectHelper.firstNonNull(null, null, "1", null));
	assertEquals("1", ObjectHelper.firstNonNull(null, null, null, "1"));
	assertEquals("1", ObjectHelper.firstNonNull("1", "2", "3", "4"));

   }

   @Test
   public void withNoNull() {
	assertNotNull(ObjectHelper.withNoNull((Integer) null));
	assertEquals(0, ObjectHelper.withNoNull((Integer) null).size());
	assertNotNull(ObjectHelper.withNoNull(null, null));
	assertEquals(0, ObjectHelper.withNoNull(null, null).size());

	Integer[] values;
	List<Integer> result;

	values = new Integer[] { 1 };
	result = ObjectHelper.withNoNull(values);
	assertNotNull(result);
	assertEquals(1, result.size());
	assertEquals(new Integer(1), result.get(0));

	values = new Integer[] { 1, 2 };
	result = ObjectHelper.withNoNull(values);
	assertNotNull(result);
	assertEquals(2, result.size());
	assertEquals(new Integer(1), result.get(0));
	assertEquals(new Integer(2), result.get(1));

	values = new Integer[] { null, 2 };
	result = ObjectHelper.withNoNull(values);
	assertNotNull(result);
	assertEquals(1, result.size());
	assertEquals(new Integer(2), result.get(0));

	values = new Integer[] { 1, null };
	result = ObjectHelper.withNoNull(values);
	assertNotNull(result);
	assertEquals(1, result.size());
	assertEquals(new Integer(1), result.get(0));

	values = new Integer[] { 1, null, 2, null, null, 3, null };
	result = ObjectHelper.withNoNull(values);
	assertNotNull(result);
	assertEquals(3, result.size());
	assertEquals(new Integer(1), result.get(0));
	assertEquals(new Integer(2), result.get(1));
	assertEquals(new Integer(3), result.get(2));
   }
}
