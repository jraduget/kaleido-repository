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
package org.kaleidofoundry.core.util;

import static org.kaleidofoundry.core.util.CollectionsHelper.argsToMap;
import static org.kaleidofoundry.core.util.CollectionsHelper.arrayToString;
import static org.kaleidofoundry.core.util.CollectionsHelper.collectionToString;
import static org.kaleidofoundry.core.util.CollectionsHelper.stringToArray;
import static org.kaleidofoundry.core.util.CollectionsHelper.stringToCollection;
import static org.kaleidofoundry.core.util.CollectionsHelper.stringToSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author jraduget
 */
public class CollectionsHelperTest  {

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

	String result;
	result = arrayToString(new String[] { "a", "bb", "ccc" }, ";");
	assertNotNull(result);
	assertEquals("a;bb;ccc", result);

	result = arrayToString(new String[] { "a", "", "ccc" }, ";");
	assertNotNull(result);
	assertEquals("a;;ccc", result);

	result = arrayToString(new String[] { "a", "bb", "" }, ";");
	assertNotNull(result);
	assertEquals("a;bb;", result);

	result = arrayToString(new String[] { "", "bb", "ccc" }, ";");
	assertNotNull(result);
	assertEquals(";bb;ccc", result);
   }

   @Test
   public void testCollectionToString() {

	assertNull(collectionToString(null, null));
	assertNull(collectionToString(null, ";"));

	String result;
	result = collectionToString(Arrays.asList(new String[] { "a", "bb", "ccc" }), ";");
	assertNotNull(result);
	assertEquals("a;bb;ccc", result);

	result = collectionToString(Arrays.asList(new String[] { "a", "", "ccc" }), ";");
	assertNotNull(result);
	assertEquals("a;;ccc", result);

	result = collectionToString(Arrays.asList(new String[] { "a", "bb", "" }), ";");
	assertNotNull(result);
	assertEquals("a;bb;", result);

	result = collectionToString(Arrays.asList(new String[] { "", "bb", "ccc" }), ";");
	assertNotNull(result);
	assertEquals(";bb;ccc", result);

   }

   @Test
   public void testStringToArray() {
	assertNull(stringToArray(null, null));
	assertNull(stringToArray(null, ";"));

	String[] results;

	results = stringToArray("a;bb;ccc", ";");
	assertNotNull(results);
	assertEquals(3, results.length);
	assertEquals("a", results[0]);
	assertEquals("bb", results[1]);
	assertEquals("ccc", results[2]);

	results = stringToArray("a;;ccc", ";");
	assertNotNull(results);
	assertEquals(2, results.length);
	assertEquals("a", results[0]);
	assertEquals("ccc", results[1]);

	results = stringToArray("a;bb;", ";");
	assertNotNull(results);
	assertEquals(2, results.length);
	assertEquals("a", results[0]);
	assertEquals("bb", results[1]);

	results = stringToArray(";bb;ccc", ";");
	assertNotNull(results);
	assertEquals(2, results.length);
	assertEquals("bb", results[0]);
	assertEquals("ccc", results[1]);
   }

   @Test
   public void testStringToCollection() {
	assertNull(stringToCollection(null, null));
	assertNull(stringToCollection(null, ";"));

	List<String> results;

	results = stringToCollection("a;bb;ccc", ";");
	assertNotNull(results);
	assertEquals(3, results.size());
	assertEquals("a", results.get(0));
	assertEquals("bb", results.get(1));
	assertEquals("ccc", results.get(2));

	results = stringToCollection("a;;ccc", ";");
	assertNotNull(results);
	assertEquals(2, results.size());
	assertEquals("a", results.get(0));

	assertEquals("ccc", results.get(1));

	results = stringToCollection("a;bb;", ";");
	assertNotNull(results);
	assertEquals(2, results.size());
	assertEquals("a", results.get(0));
	assertEquals("bb", results.get(1));

	results = stringToCollection(";bb;ccc", ";");
	assertNotNull(results);
	assertEquals(2, results.size());
	assertEquals("bb", results.get(0));
	assertEquals("ccc", results.get(1));
   }

   @Test
   public void testStringToSet() {
	assertNull(stringToSet(null, null));
	assertNull(stringToSet(null, ";"));

	Set<String> results;

	results = stringToSet("a;a;ccc", ";");
	assertNotNull(results);
	assertEquals(2, results.size());

	Iterator<String> itValues = results.iterator();
	assertEquals("a", itValues.next());
	assertEquals("ccc", itValues.next());
   }

}
