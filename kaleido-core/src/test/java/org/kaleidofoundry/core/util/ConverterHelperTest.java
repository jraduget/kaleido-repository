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

import static org.kaleidofoundry.core.util.ConverterHelper.argsToMap;
import static org.kaleidofoundry.core.util.ConverterHelper.arrayToString;
import static org.kaleidofoundry.core.util.ConverterHelper.collectionToString;
import static org.kaleidofoundry.core.util.ConverterHelper.stringToArray;
import static org.kaleidofoundry.core.util.ConverterHelper.stringToCollection;
import static org.kaleidofoundry.core.util.ConverterHelper.stringToSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jerome RADUGET
 */
public class ConverterHelperTest extends Assert {

   @Test
   public void testArgsToMap() {

	assertNull(argsToMap(null));

	Map<String, String> map = argsToMap(new String[] { "app.version=1.0.0", "app.name=foo" });

	assertNotNull(map);
	assertFalse(map.isEmpty());
	assertTrue(map.keySet().size() == 2);
	assertTrue(map.values().size() == 2);

	assertTrue(map.keySet().contains("app.version"));
	assertTrue(map.keySet().contains("app.name"));

	assertEquals("1.0.0", map.get("app.version"));
	assertEquals("foo", map.get("app.name"));
   }

   @Test
   public void testArgsToMapWithDelim() {

	assertNull(argsToMap(null, null));
	assertNull(argsToMap(null, ";"));

	Map<String, String> map = argsToMap(new String[] { "app.version->1.0.0", "app.name->foo" }, "->");

	assertNotNull(map);
	assertFalse(map.isEmpty());
	assertTrue(map.keySet().size() == 2);
	assertTrue(map.values().size() == 2);

	assertTrue(map.keySet().contains("app.version"));
	assertTrue(map.keySet().contains("app.name"));

	assertEquals("1.0.0", map.get("app.version"));
	assertEquals("foo", map.get("app.name"));
   }

   @Test
   public void testArrayToString() {

	assertNull(arrayToString(null, null));
	assertNull(arrayToString(null, ";"));

	String result = arrayToString(new String[] { "a", "bb", "ccc" }, ";");
	assertNotNull(result);
	assertEquals("a;bb;ccc", result);
   }

   @Test
   public void testCollectionToString() {

	assertNull(collectionToString(null, null));
	assertNull(collectionToString(null, ";"));

	String result = collectionToString(Arrays.asList(new String[] { "a", "bb", "ccc" }), ";");
	assertNotNull(result);
	assertEquals("a;bb;ccc", result);
   }

   @Test
   public void testStringToArray() {
	assertNull(stringToArray(null, null));
	assertNull(stringToArray(null, ";"));

	String[] results = stringToArray("a;bb;ccc", ";");

	assertNotNull(results);
	assertTrue(results.length == 3);
	assertEquals("a", results[0]);
	assertEquals("bb", results[1]);
	assertEquals("ccc", results[2]);
   }

   @Test
   public void testStringToCollection() {
	assertNull(stringToCollection(null, null));
	assertNull(stringToCollection(null, ";"));

	List<String> results = stringToCollection("a;bb;ccc", ";");

	assertNotNull(results);
	assertTrue(results.size() == 3);
	assertEquals("a", results.get(0));
	assertEquals("bb", results.get(1));
	assertEquals("ccc", results.get(2));
   }

   @Test
   public void testStringToSet() {
	assertNull(stringToSet(null, null));
	assertNull(stringToSet(null, ";"));

	Set<String> results = stringToSet("a;a;ccc", ";");

	assertNotNull(results);
	assertTrue(results.size() == 2);

	Iterator<String> itValues = results.iterator();
	assertEquals("a", itValues.next());
	assertEquals("ccc", itValues.next());
   }

}
