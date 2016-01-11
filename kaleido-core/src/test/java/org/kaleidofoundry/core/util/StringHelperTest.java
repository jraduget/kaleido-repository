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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author jraduget
 */
public class StringHelperTest  {

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
	System.out.println((int) " ".charAt(0));
	System.out.println((int) " ".charAt(0));
	assertNull(StringHelper.replaceAll(null, null, null));
	assertNull(StringHelper.replaceAll(null, "a", "b"));
	assertEquals("anullc", StringHelper.replaceAll("abc", "b", null));
	assertEquals("", StringHelper.replaceAll("", "", ""));
	assertEquals("", StringHelper.replaceAll("    ", " ", ""));
	assertEquals("1316,10", StringHelper.replaceAll("1 316,10  ", " ", "")); // nbsp
	assertEquals("12345,67", StringHelper.replaceAll("1 2345,67  ", " ", "")); // space
	assertEquals("foo", StringHelper.replaceAll("a", "a", "foo"));
	assertEquals(".foo", StringHelper.replaceAll(".a", "a", "foo"));
	assertEquals("foo.", StringHelper.replaceAll("a.", "a", "foo"));
	assertEquals("..foo...foo..foo", StringHelper.replaceAll("..a...a..a", "a", "foo"));
	assertEquals("foo...foo..foo.", StringHelper.replaceAll("ab...ab..ab.", "ab", "foo"));
   }

   @Test
   public void leftPad() {
	// default pad is space
	assertNull(StringHelper.leftPad(null, 10));
	assertEquals("", StringHelper.leftPad("", 0));
	assertEquals(" ", StringHelper.leftPad("", 1));
	assertEquals("     ", StringHelper.leftPad("", 5));
	assertEquals("     Hello", StringHelper.leftPad("Hello", 10));
	assertEquals("    ", StringHelper.leftPad("    ", 0));
	assertEquals("    ", StringHelper.leftPad("    ", 1));
	assertEquals("    ", StringHelper.leftPad("    ", 3));
	// default pad is '*'
	assertNull(StringHelper.leftPad(null, 10, '*'));
	assertEquals("", StringHelper.leftPad("", 0, '*'));
	assertEquals("*", StringHelper.leftPad("", 1, '*'));
	assertEquals("*****", StringHelper.leftPad("", 5, '*'));
	assertEquals("*****Hello", StringHelper.leftPad("Hello", 10, '*'));
	assertEquals("*****", StringHelper.leftPad("*****", 0, '*'));
	assertEquals("*****", StringHelper.leftPad("*****", 1, '*'));
	assertEquals("*****", StringHelper.leftPad("*****", 3, '*'));
   }

   @Test
   public void rightPad() {
	// default pad is space
	assertNull(StringHelper.rightPad(null, 10));
	assertEquals("", StringHelper.rightPad("", 0));
	assertEquals(" ", StringHelper.rightPad("", 1));
	assertEquals("     ", StringHelper.rightPad("", 5));
	assertEquals("Hello     ", StringHelper.rightPad("Hello", 10));
	assertEquals("    ", StringHelper.rightPad("    ", 0));
	assertEquals("    ", StringHelper.rightPad("    ", 1));
	assertEquals("    ", StringHelper.rightPad("    ", 3));
	// default pad is '*'
	assertNull(StringHelper.rightPad(null, 10, '*'));
	assertEquals("", StringHelper.rightPad("", 0, '*'));
	assertEquals("*", StringHelper.rightPad("", 1, '*'));
	assertEquals("*****", StringHelper.rightPad("", 5, '*'));
	assertEquals("Hello*****", StringHelper.rightPad("Hello", 10, '*'));
	assertEquals("*****", StringHelper.rightPad("*****", 0, '*'));
	assertEquals("*****", StringHelper.rightPad("*****", 1, '*'));
	assertEquals("*****", StringHelper.rightPad("*****", 3, '*'));
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
	assertNull(StringHelper.unsplit("", (Object[]) null));
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

   @Test
   public void extractToken() {

	assertNotNull(StringHelper.extractToken(null));
	assertEquals(0, StringHelper.extractToken(null).size());

	assertNotNull(StringHelper.extractToken(""));
	assertEquals(0, StringHelper.extractToken("").size());

	assertNotNull(StringHelper.extractToken("abcdefgh"));
	assertEquals(0, StringHelper.extractToken("abcdefgh").size());

	assertNotNull(StringHelper.extractToken("abcd${efgh"));
	assertEquals(0, StringHelper.extractToken("abcd${efgh").size());

	assertNotNull(StringHelper.extractToken("${a}"));
	assertEquals(1, StringHelper.extractToken("${a}").size());
	assertEquals("a", StringHelper.extractToken("${a}").get(0));

	assertNotNull(StringHelper.extractToken("${ab}"));
	assertEquals(1, StringHelper.extractToken("${ab}").size());
	assertEquals("ab", StringHelper.extractToken("${ab}").iterator().next());


	assertNotNull(StringHelper.extractToken("abcd${efgh}"));
	assertEquals(1, StringHelper.extractToken("abcd${efgh}").size());
	assertEquals("efgh", StringHelper.extractToken("abcd${efgh}").get(0));

	assertNotNull(StringHelper.extractToken("${abcd}efgh"));
	assertEquals(1, StringHelper.extractToken("${abcd}efgh").size());
	assertEquals("abcd", StringHelper.extractToken("${abcd}efgh").get(0));

	assertNotNull(StringHelper.extractToken("abcd${efgh}ghij"));
	assertEquals(1, StringHelper.extractToken("abcd${efgh}ghij").size());
	assertEquals("efgh", StringHelper.extractToken("abcd${efgh}ghij").get(0));

	assertNotNull(StringHelper.extractToken("${a}${b}"));
	assertEquals(2, StringHelper.extractToken("${a}${b}").size());
	assertEquals("a", StringHelper.extractToken("${a}${b}").get(0));
	assertEquals("b", StringHelper.extractToken("${a}${b}").get(1));

	assertNotNull(StringHelper.extractToken("${a}${b}cde${f}${g}"));
	assertEquals(4, StringHelper.extractToken("${a}${b}cde${f}${g}").size());
	assertEquals("a", StringHelper.extractToken("${a}${b}cde${f}${g}").get(0));
	assertEquals("b", StringHelper.extractToken("${a}${b}cde${f}${g}").get(1));
	assertEquals("f", StringHelper.extractToken("${a}${b}cde${f}${g}").get(2));
	assertEquals("g", StringHelper.extractToken("${a}${b}cde${f}${g}").get(3));
   }

   @Test
   public void resolveExpression() {
	Map<String, String> tokensValue = new HashMap<String, String>();
	tokensValue.put("a", "b");
	tokensValue.put("b", "c");
	tokensValue.put("f", "g");
	tokensValue.put("g", "h");

	assertNull(StringHelper.resolveExpression(null, tokensValue));

	assertNotNull(StringHelper.resolveExpression("${a", tokensValue));
	assertEquals("${a", StringHelper.resolveExpression("${a", tokensValue));

	assertNotNull(StringHelper.resolveExpression("${a}", tokensValue));
	assertEquals("b", StringHelper.resolveExpression("${a}", tokensValue));

	assertNotNull(StringHelper.resolveExpression("${a}${b}", tokensValue));
	assertEquals("bc", StringHelper.resolveExpression("${a}${b}", tokensValue));

	assertNotNull(StringHelper.resolveExpression("${a}${b}${c}", tokensValue));
	assertEquals("bc${c}", StringHelper.resolveExpression("${a}${b}${c}", tokensValue));

	assertNotNull(StringHelper.resolveExpression("${a}${b}cde${f}${g}", tokensValue));
	assertEquals("bccdegh", StringHelper.resolveExpression("${a}${b}cde${f}${g}", tokensValue));
   }

}
