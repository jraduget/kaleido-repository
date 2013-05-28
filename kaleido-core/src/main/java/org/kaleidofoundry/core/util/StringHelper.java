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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;

/**
 * Some helper methods for string
 * 
 * @author Jerome RADUGET
 */
public abstract class StringHelper {

   /**
    * Checks if a String is empty "" or null
    * <p>
    * 
    * <pre>
    * <b>Assertions :</b>
    * 	assertTrue(StringHelper.isEmpty(null));
    * 	assertTrue(StringHelper.isEmpty(""));
    * 	assertFalse(StringHelper.isEmpty("   	"));
    * 	assertFalse(StringHelper.isEmpty("foo"));
    * </pre>
    * 
    * </p>
    * 
    * @param str the String to check, it can be null
    * @return <code>true</code> if the String is empty or null, <code>false</code> otherwise
    */
   public static boolean isEmpty(final String str) {
	return str == null || str.length() == 0;
   }

   /**
    * Translate the input String to LowerCase
    * <p>
    * 
    * <pre>
    * <b>Assertions :</b>
    * 	assertNull(StringHelper.lowerCase(null));
    * 	assertEquals("", StringHelper.lowerCase(""));
    * 	assertEquals("aabb", StringHelper.lowerCase("aAbB"));
    * 	assertEquals("aabb", StringHelper.lowerCase("AaBb"));
    * </pre>
    * 
    * </p>
    * 
    * @param str the string to translate
    * @return the modified string
    */
   public static String lowerCase(final String str) {
	if (str != null && str.length() > 0) {
	   return str.toLowerCase();
	} else {
	   return str;
	}
   }

   /**
    * Translate the input String's 1st character to LowerCase,
    * the rest of the string will not be changed
    * <p>
    * 
    * <pre>
    * <b>Assertions :</b>
    * assertNull(StringHelper.lowerCase1(null));
    * assertEquals(&quot;&quot;, StringHelper.lowerCase1(&quot;&quot;));
    * assertEquals(&quot;aAbB&quot;, StringHelper.lowerCase1(&quot;aAbB&quot;));
    * assertEquals(&quot;aaBb&quot;, StringHelper.lowerCase1(&quot;AaBb&quot;));
    * </pre>
    * 
    * </p>
    * 
    * @param str the string to translate
    * @return the modified string
    */
   public static String lowerCase1(final String str) {
	if (str != null && str.length() > 0) {
	   return Character.toLowerCase(str.charAt(0)) + (str.length() == 1 ? "" : str.substring(1));
	} else {
	   return str;
	}
   }

   /**
    * Find and replace all occurrences of the string <br/>
    * 
    * <pre>
    * <b>Assertions :</b>
    * assertNull(StringHelper.replaceAll(null, null, null));
    * assertNull(StringHelper.replaceAll(null, &quot;a&quot;, &quot;b&quot;));
    * assertEquals(&quot;anullc&quot;, StringHelper.replaceAll(&quot;abc&quot;, &quot;b&quot;, null));
    * assertEquals(&quot;&quot;, StringHelper.replaceAll(&quot;&quot;, &quot;&quot;, &quot;&quot;));
    * assertEquals(&quot;foo&quot;, StringHelper.replaceAll(&quot;a&quot;, &quot;a&quot;, &quot;foo&quot;));
    * assertEquals(&quot;.foo&quot;, StringHelper.replaceAll(&quot;.a&quot;, &quot;a&quot;, &quot;foo&quot;));
    * assertEquals(&quot;foo.&quot;, StringHelper.replaceAll(&quot;a.&quot;, &quot;a&quot;, &quot;foo&quot;));
    * assertEquals(&quot;..foo...foo..foo&quot;, StringHelper.replaceAll(&quot;..a...a..a&quot;, &quot;a&quot;, &quot;foo&quot;));
    * assertEquals(&quot;foo...foo..foo.&quot;, StringHelper.replaceAll(&quot;ab...ab..ab.&quot;, &quot;ab&quot;, &quot;foo&quot;));
    * </pre>
    * 
    * @param source source string
    * @param findToken string to search for
    * @param replaceToken string to replace found tokens with
    * @return the modified string
    */
   public static String replaceAll(String source, final String findToken, final String replaceToken) {
	if (source == null) { return null; }
	StringBuilder sb = null;
	int pos;
	do {
	   if ((pos = source.indexOf(findToken)) < 0) {
		break;
	   }
	   if (sb == null) {
		sb = new StringBuilder();
	   }
	   if (pos > 0) {
		sb.append(source.substring(0, pos));
	   }
	   sb.append(replaceToken);
	   source = source.substring(pos + findToken.length());
	} while (source.length() > 0);

	if (sb == null) {
	   return source;
	} else {
	   sb.append(source);
	   return sb.toString();
	}
   }

   /**
    * Left pad the given text parameter
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertNull(StringHelper.leftPad(null, 10));
    * 	assertEquals("", StringHelper.leftPad("", 0));
    * 	assertEquals(" ", StringHelper.leftPad("", 1));
    * 	assertEquals("     ", StringHelper.leftPad("", 5));
    * 	assertEquals("     Hello", StringHelper.leftPad("Hello", 10));
    * 	assertEquals("    ", StringHelper.leftPad("    ", 0));
    * 	assertEquals("    ", StringHelper.leftPad("    ", 1));
    * 	assertEquals("    ", StringHelper.leftPad("    ", 3));
    * </pre>
    * 
    * @param text
    * @param size
    * @return Left pad of the given text parameter
    */
   public static String leftPad(final String text, final int size) {
	return leftPad(text, size, ' ');
   }

   /**
    * Left pad the given text parameter
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertNull(StringHelper.leftPad(null, 10, '*'));
    * 	assertEquals("", StringHelper.leftPad("", 0, '*'));
    * 	assertEquals("*", StringHelper.leftPad("", 1, '*'));
    * 	assertEquals("*****", StringHelper.leftPad("", 5, '*'));
    * 	assertEquals("*****Hello", StringHelper.leftPad("Hello", 10, '*'));
    * 	assertEquals("*****", StringHelper.leftPad("*****", 0, '*'));
    * 	assertEquals("*****", StringHelper.leftPad("*****", 1, '*'));
    * 	assertEquals("*****", StringHelper.leftPad("*****", 3, '*'));
    * </pre>
    * 
    * @param text
    * @param size
    * @param padChar
    * @return Left pad of the given text parameter
    */
   public static String leftPad(final String text, final int size, final char padChar) {
	if (text == null) { return null; }

	final StringBuilder paddedText = new StringBuilder(size);
	while (paddedText.length() < size - text.length()) {
	   paddedText.append(padChar);
	}

	paddedText.append(text);

	return paddedText.toString();
   }

   /**
    * Right pad the given text parameter
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertNull(StringHelper.rightPad(null, 10));
    * 	assertEquals("", StringHelper.rightPad("", 0));
    * 	assertEquals(" ", StringHelper.rightPad("", 1));
    * 	assertEquals("     ", StringHelper.rightPad("", 5));
    * 	assertEquals("Hello     ", StringHelper.rightPad("Hello", 10));
    * 	assertEquals("    ", StringHelper.rightPad("    ", 0));
    * 	assertEquals("    ", StringHelper.rightPad("    ", 1));
    * 	assertEquals("    ", StringHelper.rightPad("    ", 3));
    * </pre>
    * 
    * @param text
    * @param size
    * @return Right pad of the given text parameter
    */
   public static String rightPad(final String text, final int size) {
	return rightPad(text, size, ' ');
   }

   /**
    * Right pad the given text parameter
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertNull(StringHelper.rightPad(null, 10, '*'));
    * 	assertEquals("", StringHelper.rightPad("", 0, '*'));
    * 	assertEquals("*", StringHelper.rightPad("", 1, '*'));
    * 	assertEquals("*****", StringHelper.rightPad("", 5, '*'));
    * 	assertEquals("Hello*****", StringHelper.rightPad("Hello", 10, '*'));
    * 	assertEquals("*****", StringHelper.rightPad("*****", 0, '*'));
    * 	assertEquals("*****", StringHelper.rightPad("*****", 1, '*'));
    * 	assertEquals("*****", StringHelper.rightPad("*****", 3, '*'));
    * </pre>
    * 
    * @param text
    * @param size
    * @param padChar
    * @return Right pad of the given text parameter
    */
   public static String rightPad(final String text, final int size, final char padChar) {
	if (text == null) { return null; }

	final StringBuilder paddedText = new StringBuilder(size);
	paddedText.append(text);

	while (paddedText.length() < size) {
	   paddedText.append(padChar);
	}
	return paddedText.toString();
   }

   /**
    * Create a new String by replicating the given String n (count) times
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertEquals("", StringHelper.replicate(null, 0));
    * 	assertEquals("null", StringHelper.replicate(null, 1));
    * 	assertEquals("nullnull", StringHelper.replicate(null, 2));
    * 
    * 	assertEquals("", StringHelper.replicate("", 1));
    * 	assertEquals(" ", StringHelper.replicate(" ", 1));
    * 	assertEquals("  ", StringHelper.replicate(" ", 2));
    * 
    * 	assertEquals("aBc", StringHelper.replicate("aBc", 1));
    * 	assertEquals("aBcaBc", StringHelper.replicate("aBc", 2));
    * 	assertEquals("aBcaBcaBc", StringHelper.replicate("aBc", 3));
    * </pre>
    * 
    * @param text String to replicate
    * @param count number of times to replicate string
    * @return replicated string
    */
   public static String replicate(final String text, final int count) {
	final StringBuilder sb = new StringBuilder();
	for (int i = 0; i < count; i++) {
	   sb.append(text);
	}
	return sb.toString();
   }

   /**
    * split the Input String with the default tokenizer (whitespace)
    * <p>
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertNull(StringHelper.split(null));
    * 	assertNotNull(StringHelper.split(""));
    * 	assertEquals(0, StringHelper.split("").length);
    * 	assertEquals(1, StringHelper.split("aa").length);
    * 	assertEquals("aa", StringHelper.split("aa")[0]);
    * 	assertEquals(2, StringHelper.split("aa bb").length);
    * 	assertEquals("aa", StringHelper.split("aa bb")[0]);
    * 	assertEquals("bb", StringHelper.split("aa bb")[1]);
    * 	assertEquals(2, StringHelper.split("aa bb ").length);
    * </pre>
    * 
    * </p>
    * 
    * @param str String to tokenize
    * @return string[] tokenized String
    */
   public static String[] split(final String str) {
	return split(str, " ");
   }

    /**
     * split the Input String with the given separator
     * <p>
     *
     * <pre>
     * <b>Assertions:</b>
     * 	assertNull(StringHelper.split(null, '-'));
     * 	assertNotNull(StringHelper.split("", '-'));
     * 	assertEquals(0, StringHelper.split("", '-').length);
     * 	assertEquals(1, StringHelper.split("aa", '-').length);
     * 	assertEquals("aa", StringHelper.split("aa", '-')[0]);
     * 	assertEquals(2, StringHelper.split("aa-bb", '-').length);
     * 	assertEquals("aa", StringHelper.split("aa-bb", '-')[0]);
     * 	assertEquals("bb", StringHelper.split("aa-bb", '-')[1]);
     * 	assertEquals(2, StringHelper.split("aa-bb-", '-').length);
     * </pre>
     *
     * </p>
     *
     * @param str String to tokenize
     * @param separator Deliminator to use
     * @return string[] splitted String
     */
    public static String[] split(final String str, final char separator) {

        return split(str, String.valueOf(separator));
    }

   /**
    * split the Input String with the given separator
    * <p>
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertNull(StringHelper.split(null, "--"));
    * 	assertNotNull(StringHelper.split("", "--"));
    * 	assertEquals(0, StringHelper.split("", "--").length);
    * 	assertEquals(1, StringHelper.split("aa", "--").length);
    * 	assertEquals("aa", StringHelper.split("aa", "--")[0]);
    * 	assertEquals(2, StringHelper.split("aa--bb", "--").length);
    * 	assertEquals("aa", StringHelper.split("aa--bb", "--")[0]);
    * 	assertEquals("bb", StringHelper.split("aa--bb", "--")[1]);
    * 	assertEquals(2, StringHelper.split("aa--bb--", "--").length);
    * </pre>
    * 
    * </p>
    * 
    * @param str String to tokenize
    * @param separator Deliminator to use
    * @return string[] splitted String
    */
   public static String[] split(final String str, final String separator) {
	if (str == null) { return null; }
	final StringTokenizer strTokens = new StringTokenizer(str, separator);
	final String[] result = new String[strTokens.countTokens()];
	int i = 0;
	while (strTokens.hasMoreTokens()) {
	   result[i++] = strTokens.nextToken();
	}
	return result;
   }

   /**
    * unsplit the Input String array using the given separator
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	assertNull(StringHelper.unsplit("", (String[]) null));
    * 	assertEquals("a", StringHelper.unsplit("-", "a"));
    * 	assertEquals("a-b", StringHelper.unsplit("-", "a", "b"));
    * 	assertEquals("a-b-c", StringHelper.unsplit("-", "a", "b", "c"));
    * </pre>
    * 
    * @param separator item separator
    * @param values
    * @return string representation of a String[] using valueSeparator as string separator
    */
   public static String unsplit(final String separator, final Object... values) {
	final StringBuilder buffer = new StringBuilder();
	if (values != null) {
	   for (int i = 0; i < values.length - 1; i++) {
		buffer.append(String.valueOf(values[i]) + separator);
	   }
	   buffer.append(String.valueOf(values[values.length - 1]));

	   return buffer.toString();
	} else {
	   return null;
	}
   }

   /**
    * convert a string with delimiter (whitespace) to a String List
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	final String text = "a ab abc";
    * 	assertNull(StringHelper.toList(null));
    * 	assertNotNull(StringHelper.toList(text));
    * 	assertTrue(StringHelper.toList(text).size() == 3);
    * 	assertEquals("a", StringHelper.toList(text).get(0));
    * 	assertEquals("ab", StringHelper.toList(text).get(1));
    * 	assertEquals("abc", StringHelper.toList(text).get(2));
    * </pre>
    * 
    * </p>
    * 
    * @param str the input String
    * @return List of string
    */
   public static List<String> toList(final String str) {
	if (str == null) { return null; }
	return Arrays.asList(split(str));
   }

   /**
    * convert a string with delimiter (separator) to a String List
    * 
    * <pre>
    * <b>Assertions:</b>
    * 	final String text = "a$$ab$$abc";
    * 	final String sep = "$$";
    * 	assertNull(StringHelper.toList(null, null));
    * 	assertNull(StringHelper.toList(null, sep));
    * 	assertNotNull(StringHelper.toList(text, sep));
    * 	assertTrue(StringHelper.toList(text, sep).size() == 3);
    * 	assertEquals("a", StringHelper.toList(text, sep).get(0));
    * 	assertEquals("ab", StringHelper.toList(text, sep).get(1));
    * 	assertEquals("abc", StringHelper.toList(text, sep).get(2));
    * </pre>
    * 
    * </p>
    * 
    * @param str the input String
    * @param separator delimiter
    * @return List of string
    */
   public static List<String> toList(final String str, final String separator) {
	if (str == null) { return null; }
	return Arrays.asList(split(str, separator));
   }

   /**
    * Search all tokens in a text between: prefixToken and suffixToken
    * 
    * <pre>
    * <b>Assertions & samples :</b>
    * 	assertNull(StringHelper.tokensBetween((Reader) null, "${", "}"));
    * 	final String text = "Hello Mr ${person.name}...\n" + "${person.firstname}...\n"
    * 		+ "happy birthday, today... ${birthday} ....";
    * 	assertEquals(3, StringHelper.tokensBetween(new StringReader(text), "${", "}").size());
    * 	assertEquals("person.name", StringHelper.tokensBetween(new StringReader(text), "${", "}").get(0));
    * 	assertEquals("person.firstname", StringHelper.tokensBetween(new StringReader(text), "${", "}").get(1));
    * 	assertEquals("birthday", StringHelper.tokensBetween(new StringReader(text), "${", "}").get(2));
    * </pre>
    * 
    * </p>
    * 
    * @param reader reader to scan
    * @param prefixToken the String specifying the start of a token
    * @param suffixToken the String specifying the start of a token
    * @return the String specifying the start of a token
    * @throws IOException
    */
   public static LinkedList<String> tokensBetween(final Reader reader, final String prefixToken, final String suffixToken) throws IOException {
	if (reader == null) { return null; }
	final String str = toString(reader);
	return tokensBetween(str, prefixToken, suffixToken);
   }

   /**
    * Search all tokens in a text between: prefixToken and suffixToken
    * 
    * <pre>
    * <b>Assertions & samples :</b>
    * 	assertNull(StringHelper.tokensBetween((String) null, "${", "}"));
    * 	final String text = "Hello Mr ${person.name}...\n" + "${person.firstname}...\n"
    * 		+ "happybirtday, today... ${birthday} ....";
    * 	assertEquals(3, StringHelper.tokensBetween(text, "${", "}").size());
    * 	assertEquals("person.name", StringHelper.tokensBetween(text, "${", "}").get(0));
    * 	assertEquals("person.firstname", StringHelper.tokensBetween(text, "${", "}").get(1));
    * 	assertEquals("birthday", StringHelper.tokensBetween(text, "${", "}").get(2));
    * </pre>
    * 
    * </p>
    * 
    * @param text text to scan
    * @param prefixToken the String specifying the start of a token
    * @param suffixToken the String specifying the start of a token
    * @return the String specifying the start of a token
    */
   public static LinkedList<String> tokensBetween(final String text, final String prefixToken, final String suffixToken) {
	if (text == null) { return null; }

	final LinkedList<String> tokens = new LinkedList<String>();
	int beginSearch = 0;

	while ((beginSearch = text.indexOf(prefixToken, beginSearch)) >= 0) {
	   final int endSearch = text.indexOf(suffixToken, beginSearch);
	   if (endSearch >= 0) {
		final String token = text.substring(beginSearch + prefixToken.length(), endSearch);
		if (token.indexOf(prefixToken) > 0) { throw new IllegalArgumentException(token); }
		tokens.add(token);
		beginSearch = endSearch;
	   } else {
		break;
	   }

	}
	return tokens;
   }

   /**
    * @param reader
    * @return the String content of the given reader
    * @throws IOException
    */
   public static String toString(final Reader reader) throws IOException {
	final StringBuilder buf = toStringBuilder(reader);
	if (buf == null) {
	   return null;
	} else {
	   return buf.toString();
	}
   }

   /**
    * @param reader
    * @return the StringBuilder content of the given reader
    * @throws IOException
    */
   public static StringBuilder toStringBuilder(final Reader reader) throws IOException {
	StringBuilder strBuilder = null;
	if (reader != null) {
	   strBuilder = new StringBuilder();
	   final BufferedReader bReader = new BufferedReader(reader);
	   String str = bReader.readLine();
	   if (str != null) {
		strBuilder.append(str);
		while ((str = bReader.readLine()) != null) {
		   strBuilder.append('\n');
		   strBuilder.append(str);
		}
	   }
	}
	return strBuilder;
   }

   /**
    * truncate the input String, to maxLength
    * 
    * @param str
    * @param maxLength max length of the results String, from the left
    * @return the modified string, and null if arg is null
    */
   public static String truncate(final String str, final int maxLength) {

	if (str == null) { return null; }

	if (str.length() > maxLength) {
	   return str.substring(0, maxLength);
	} else {
	   return str;
	}
   }

   /**
    * Translate the input String to UpperCase
    * <p>
    * 
    * <pre>
    * <b>Assertions :</b>
    * 	assertNull(StringHelper.upperCase(null));
    * 	assertEquals("", StringHelper.upperCase(""));
    * 	assertEquals("AABB", StringHelper.upperCase("aAbB"));
    * 	assertEquals("AABB", StringHelper.upperCase("AaBb"));
    * </pre>
    * 
    * </p>
    * 
    * @param str the string to process
    * @return the modified string
    */
   public static String upperCase(final String str) {
	if (str != null && str.length() > 0) {
	   return str.toUpperCase();
	} else {
	   return str;
	}
   }

   /**
    * Translate the input String's 1st character to UpperCase,
    * the rest of the string will not be changed
    * <p>
    * 
    * <pre>
    * <b>Assertions :</b>
    * 	assertNull(StringHelper.upperCase1(null));
    * 	assertEquals("", StringHelper.upperCase1(""));
    * 	assertEquals("AAbB", StringHelper.upperCase1("aAbB"));
    * 	assertEquals("AaBb", StringHelper.upperCase1("AaBb"));
    * </pre>
    * 
    * </p>
    * 
    * @param str the string to translate
    * @return the modified string
    */
   public static String upperCase1(final String str) {
	if (str != null && str.length() > 0) {
	   return Character.toUpperCase(str.charAt(0)) + (str.length() == 1 ? "" : str.substring(1));
	} else {
	   return str;
	}
   }

   /**
    * <p>
    * 
    * <pre>
    * <b>Assertions :</b>
    * 	assertNotNull(StringHelper.valueOf(""));
    * 	assertEquals("", StringHelper.valueOf(""));
    * 	assertEquals("foo", StringHelper.valueOf("foo"));
    * </pre>
    * 
    * </p>
    * 
    * @param obj
    * @return the String representation of the object instance. <br/>
    *         It uses {@link String#valueOf(Object)}, but if arg is null, it will return empty String ""
    */
   public static String valueOf(final Object obj) {
	return valueOf(obj, "");
   }

   /**
    * <p>
    * 
    * <pre>
    * <b>Assertions :</b>
    * 	assertNull(StringHelper.valueOf(null, null));
    * 	assertEquals("", StringHelper.valueOf(null, ""));
    * 	assertEquals("a", StringHelper.valueOf(null, "a"));
    * 	assertEquals("foo", StringHelper.valueOf("foo", "a"));
    * </pre>
    * 
    * </p>
    * 
    * @param obj
    * @param defaultValue the default value to return if object is null
    * @return the String representation of the object instance. <br/>
    *         It uses {@link String#valueOf(Object)}, but if arg is null, it will return defaultValue
    */
   public static String valueOf(final Object obj, final String defaultValue) {
	if (obj == null) {
	   return defaultValue;
	} else {
	   return String.valueOf(obj);
	}
   }

   /**
    * Returns a list without the null argument
    * 
    * @param values
    * @return list without the null argument
    */
   @NotNull
   public static List<String> withNoNull(final String... values) {
	List<String> list = new ArrayList<String>();
	if (values != null) {
	   for (String value : values) {
		if (!isEmpty(value)) {
		   list.add(value);
		}
	   }
	}
	return list;
   }

   /**
    * Returns the first not empty value in argument
    * 
    * @param values
    * @return first not null value in argument
    */
   @Nullable
   public static String firstNonEmpty(final String... values) {
	for (String value : values) {
	   if (!StringHelper.isEmpty(value)) { return value; }
	}
	return null;
   }

   /**
    * Extract variables ${...} from a string expressions
    * 
    * <pre>
    * <b>Assertions :</b>
    * assertNotNull(StringHelper.extractToken(null));
    * 	assertEquals(0, StringHelper.extractToken(null).size());
    * 
    * 	assertNotNull(StringHelper.extractToken(""));
    * 	assertEquals(0, StringHelper.extractToken("").size());
    * 
    * 	assertNotNull(StringHelper.extractToken("abcdefgh"));
    * 	assertEquals(0, StringHelper.extractToken("abcdefgh").size());
    * 
    * 	assertNotNull(StringHelper.extractToken("abcd${efgh"));
    * 	assertEquals(0, StringHelper.extractToken("abcd${efgh").size());
    * 
    * 	assertNotNull(StringHelper.extractToken("${a}"));
    * 	assertEquals(1, StringHelper.extractToken("${a}").size());
    * 	assertEquals("a", StringHelper.extractToken("${a}").get(0));
    * 
    * 	assertNotNull(StringHelper.extractToken("${ab}"));
    * 	assertEquals(1, StringHelper.extractToken("${ab}").size());
    * 	assertEquals("ab", StringHelper.extractToken("${ab}").iterator().next());
    * 
    * 
    * 	assertNotNull(StringHelper.extractToken("abcd${efgh}"));
    * 	assertEquals(1, StringHelper.extractToken("abcd${efgh}").size());
    * 	assertEquals("efgh", StringHelper.extractToken("abcd${efgh}").get(0));
    * 
    * 	assertNotNull(StringHelper.extractToken("${abcd}efgh"));
    * 	assertEquals(1, StringHelper.extractToken("${abcd}efgh").size());
    * 	assertEquals("abcd", StringHelper.extractToken("${abcd}efgh").get(0));
    * 
    * 	assertNotNull(StringHelper.extractToken("abcd${efgh}ghij"));
    * 	assertEquals(1, StringHelper.extractToken("abcd${efgh}ghij").size());
    * 	assertEquals("efgh", StringHelper.extractToken("abcd${efgh}ghij").get(0));
    * 
    * 	assertNotNull(StringHelper.extractToken("${a}${b}"));
    * 	assertEquals(2, StringHelper.extractToken("${a}${b}").size());
    * 	assertEquals("a", StringHelper.extractToken("${a}${b}").get(0));
    * 	assertEquals("b", StringHelper.extractToken("${a}${b}").get(1));
    * 
    * 	assertNotNull(StringHelper.extractToken("${a}${b}cde${f}${g}"));
    * 	assertEquals(4, StringHelper.extractToken("${a}${b}cde${f}${g}").size());
    * 	assertEquals("a", StringHelper.extractToken("${a}${b}cde${f}${g}").get(0));
    * 	assertEquals("b", StringHelper.extractToken("${a}${b}cde${f}${g}").get(1));
    * 	assertEquals("f", StringHelper.extractToken("${a}${b}cde${f}${g}").get(2));
    * 	assertEquals("g", StringHelper.extractToken("${a}${b}cde${f}${g}").get(3));
    * </pre>
    * 
    * @param expression
    * @return
    */
   @NotNull
   public static List<String> extractToken(final String expression) {

	List<String> tokens = new ArrayList<String>();
	if (expression != null) {
	   for (int cpt = 0; cpt < expression.length(); cpt++) {
		if (expression.charAt(cpt) == '$') {
		   cpt++;
		   int startTokenPos = cpt;
		   int endTokenPos = cpt;
		   if (cpt < expression.length() && expression.charAt(cpt) == '{') {
			while (++cpt < expression.length() && expression.charAt(cpt) != '}') {
			   endTokenPos++;
			}
			if (cpt < expression.length() && expression.charAt(cpt) == '}' && endTokenPos > startTokenPos) {
			   tokens.add(expression.substring(startTokenPos + 1, endTokenPos + 1));
			}
		   }
		}
	   }
	}
	return tokens;
   }

   /**
    * @param expression
    * @param tokensValues
    * @return
    */
   public static String resolveExpression(final String expression, final Map<String, String> tokensValues) {
	if (expression == null) { return null; }
	String result = expression;

	List<String> tokens = extractToken(expression);
	for (String token : tokens) {
	   String tokenValue = tokensValues.get(token);
	   if (tokenValue != null) {
		result = result.replaceAll("[$][{]" + token + "[}]", tokenValue);
	   }
	}
	return result;
   }

}
