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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jerome RADUGET
 */
public class StringHelperTest extends Assert {

   @Test
   public void isEmpty() {
	assertTrue(StringHelper.isEmpty(null));
	assertTrue(StringHelper.isEmpty(""));
	assertFalse(StringHelper.isEmpty("   	"));
	assertFalse(StringHelper.isEmpty("foo"));
   }

   @Test
   public void lowerCase() {
	assertNull(StringHelper.lowerCase(null));
	assertEquals("", StringHelper.lowerCase(""));
	assertEquals("aabb", StringHelper.lowerCase("aAbB"));
	assertEquals("aabb", StringHelper.lowerCase("AaBb"));
   }

   @Test
   public void lowerCase1() {
	assertNull(StringHelper.lowerCase1(null));
	assertEquals("", StringHelper.lowerCase1(""));
	assertEquals("aAbB", StringHelper.lowerCase1("aAbB"));
	assertEquals("aaBb", StringHelper.lowerCase1("AaBb"));
   }

   @Test
   public void replaceAll() {
	assertNull(StringHelper.replaceAll(null, null, null));
	assertNull(StringHelper.replaceAll(null, "a", "b"));
	assertEquals("anullc", StringHelper.replaceAll("abc", "b", null));
	assertEquals("", StringHelper.replaceAll("", "", ""));
	assertEquals("foo", StringHelper.replaceAll("a", "a", "foo"));
	assertEquals(".foo", StringHelper.replaceAll(".a", "a", "foo"));
	assertEquals("foo.", StringHelper.replaceAll("a.", "a", "foo"));
	assertEquals("..foo...foo..foo", StringHelper.replaceAll("..a...a..a", "a", "foo"));
	assertEquals("foo...foo..foo.", StringHelper.replaceAll("ab...ab..ab.", "ab", "foo"));
   }

   @Test
   public void replicate() {
	assertEquals("", StringHelper.replicate(null, 0));
	assertEquals("null", StringHelper.replicate(null, 1));
	assertEquals("nullnull", StringHelper.replicate(null, 2));

	assertEquals("", StringHelper.replicate("", 1));
	assertEquals(" ", StringHelper.replicate(" ", 1));
	assertEquals("  ", StringHelper.replicate(" ", 2));

	assertEquals("aBc", StringHelper.replicate("aBc", 1));
	assertEquals("aBcaBc", StringHelper.replicate("aBc", 2));
	assertEquals("aBcaBcaBc", StringHelper.replicate("aBc", 3));
   }

   @Test
   public void split() {
	assertNull(StringHelper.split(null));
	assertNotNull(StringHelper.split(""));
	assertEquals(0, StringHelper.split("").length);
	assertEquals(1, StringHelper.split("aa").length);
	assertEquals("aa", StringHelper.split("aa")[0]);
	assertEquals(2, StringHelper.split("aa bb").length);
	assertEquals("aa", StringHelper.split("aa bb")[0]);
	assertEquals("bb", StringHelper.split("aa bb")[1]);
	assertEquals(2, StringHelper.split("aa bb ").length);
   }

   @Test
   public void splitWithSep() {
	assertNull(StringHelper.split(null, "--"));
	assertNotNull(StringHelper.split("", "--"));
	assertEquals(0, StringHelper.split("", "--").length);
	assertEquals(1, StringHelper.split("aa", "--").length);
	assertEquals("aa", StringHelper.split("aa", "--")[0]);
	assertEquals(2, StringHelper.split("aa--bb", "--").length);
	assertEquals("aa", StringHelper.split("aa--bb", "--")[0]);
	assertEquals("bb", StringHelper.split("aa--bb", "--")[1]);
	assertEquals(2, StringHelper.split("aa--bb--", "--").length);
   }

   @Test
   public void unsplit() {
	assertNull(StringHelper.unsplit("", (String[]) null));
	assertEquals("a", StringHelper.unsplit("-", "a"));
	assertEquals("a-b", StringHelper.unsplit("-", "a", "b"));
	assertEquals("a-b-c", StringHelper.unsplit("-", "a", "b", "c"));
   }

   @Test
   public void tokensBetweenReader() throws IOException {
	assertNull(StringHelper.tokensBetween((Reader) null, "${", "}"));
	final String text = "Hello Mr ${person.name}...\n" + "${person.firstname}...\n" + "happybirtday, today... ${birthday} ....";
	assertEquals(3, StringHelper.tokensBetween(new StringReader(text), "${", "}").size());
	assertEquals("person.name", StringHelper.tokensBetween(new StringReader(text), "${", "}").get(0));
	assertEquals("person.firstname", StringHelper.tokensBetween(new StringReader(text), "${", "}").get(1));
	assertEquals("birthday", StringHelper.tokensBetween(new StringReader(text), "${", "}").get(2));
   }

   @Test
   public void tokensBetweenString() {
	assertNull(StringHelper.tokensBetween((String) null, "${", "}"));
	final String text = "Hello Mr ${person.name}...\n" + "${person.firstname}...\n" + "happybirtday, today... ${birthday} ....";
	assertEquals(3, StringHelper.tokensBetween(text, "${", "}").size());
	assertEquals("person.name", StringHelper.tokensBetween(text, "${", "}").get(0));
	assertEquals("person.firstname", StringHelper.tokensBetween(text, "${", "}").get(1));
	assertEquals("birthday", StringHelper.tokensBetween(text, "${", "}").get(2));
   }

   @Test
   public void toList() {
	final String text = "a ab abc";
	assertNull(StringHelper.toList(null));
	assertNotNull(StringHelper.toList(text));
	assertTrue(StringHelper.toList(text).size() == 3);
	assertEquals("a", StringHelper.toList(text).get(0));
	assertEquals("ab", StringHelper.toList(text).get(1));
	assertEquals("abc", StringHelper.toList(text).get(2));
   }

   @Test
   public void toListWithSep() {
	final String text = "a$$ab$$abc";
	final String sep = "$$";
	assertNull(StringHelper.toList(null, null));
	assertNull(StringHelper.toList(null, sep));
	assertNotNull(StringHelper.toList(text, sep));
	assertTrue(StringHelper.toList(text, sep).size() == 3);
	assertEquals("a", StringHelper.toList(text, sep).get(0));
	assertEquals("ab", StringHelper.toList(text, sep).get(1));
	assertEquals("abc", StringHelper.toList(text, sep).get(2));
   }

   @Test
   public void toStringOfReader() throws IOException {
	assertNull(StringHelper.toString(null));
	final String text = "Hello Mr ${person.name}...\n" + "${person.firstname}...\n" + "happybirtday, today... ${birthday} ....";
	assertEquals(text, StringHelper.toString(new StringReader(text)));
   }

   @Test
   public void toStringBuilderOfReader() throws IOException {
	assertNull(StringHelper.toStringBuilder(null));
	final StringBuilder strBuilder = new StringBuilder();
	final String text = "Hello Mr ${person.name}...\n" + "${person.firstname}...\nhappybirtday, today... ${birthday} ....";

	strBuilder.append(text);

	assertEquals(text, StringHelper.toStringBuilder(new StringReader(strBuilder.toString())).toString());
   }

   @Test
   public void truncate() {
	assertNull(StringHelper.truncate(null, 1));
	assertEquals("", StringHelper.truncate("", 0));
	assertEquals("", StringHelper.truncate("", 1));
	assertEquals("aA", StringHelper.truncate("aAbB", 2));
	assertEquals("AaB", StringHelper.truncate("AaBb", 3));
   }

   @Test
   public void upperCase() {
	assertNull(StringHelper.upperCase(null));
	assertEquals("", StringHelper.upperCase(""));
	assertEquals("AABB", StringHelper.upperCase("aAbB"));
	assertEquals("AABB", StringHelper.upperCase("AaBb"));
   }

   @Test
   public void upperCase1() {
	assertNull(StringHelper.upperCase1(null));
	assertEquals("", StringHelper.upperCase1(""));
	assertEquals("AAbB", StringHelper.upperCase1("aAbB"));
	assertEquals("AaBb", StringHelper.upperCase1("AaBb"));
   }

   @Test
   public void valueOf() {
	assertNotNull(StringHelper.valueOf(""));
	assertEquals("", StringHelper.valueOf(""));
	assertEquals("foo", StringHelper.valueOf("foo"));
   }

   @Test
   public void valueOfWithDefault() {
	assertNull(StringHelper.valueOf(null, null));
	assertEquals("", StringHelper.valueOf(null, ""));
	assertEquals("a", StringHelper.valueOf(null, "a"));
	assertEquals("foo", StringHelper.valueOf("foo", "a"));
   }

}
