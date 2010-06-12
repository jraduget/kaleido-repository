/*
 * $License$
 */
package org.kaleidofoundry.core.util;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jerome RADUGET
 */
public class URLEncoderHelperTest extends Assert {

   private static final String TEST_STRING_TO_ENCODE = "123È‡ËabcbÔÓ";
   private static final String TEST_STRING_UTF8_ENCODED = "123%C3%A9%C3%A0%C3%A8abcb%C3%AF%C3%AE";

   @Test
   public void testDefaultEncode() {
	assertNull(URLEncoderHelper.encode(null));
	assertEquals(TEST_STRING_UTF8_ENCODED, URLEncoderHelper.encode(TEST_STRING_TO_ENCODE));
   }

   @Test
   public void testEncode() {
	assertNull(URLEncoderHelper.encode(null, null));

	assertEquals(TEST_STRING_UTF8_ENCODED, URLEncoderHelper.encode(TEST_STRING_TO_ENCODE, Encoding.UTF_8));
	assertEquals("123%FE%FF%00%E9%00%E0%00%E8abcb%FE%FF%00%EF%00%EE", URLEncoderHelper.encode(TEST_STRING_TO_ENCODE,
		Encoding.UTF_16));
	assertEquals("123%E9%E0%E8abcb%EF%EE", URLEncoderHelper.encode(TEST_STRING_TO_ENCODE, Encoding.ISO_8859_1));
	assertEquals("123%3F%3F%3Fabcb%3F%3F", URLEncoderHelper.encode(TEST_STRING_TO_ENCODE, Encoding.US_ASCII));

   }

   @Test
   public void testDecode() {
	assertNull(URLEncoderHelper.decode(null, null));
	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode(TEST_STRING_UTF8_ENCODED));
	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode(URLEncoderHelper.encode(TEST_STRING_TO_ENCODE)));
   }

   @Test
   public void testDefaultDecode() {
	assertNull(URLEncoderHelper.decode(null));
	assertEquals(TEST_STRING_UTF8_ENCODED, URLEncoderHelper.encode(URLEncoderHelper.decode(TEST_STRING_UTF8_ENCODED)));

	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode(TEST_STRING_UTF8_ENCODED, Encoding.UTF_8));
	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode("123%FE%FF%00%E9%00%E0%00%E8abcb%FE%FF%00%EF%00%EE",
		Encoding.UTF_16));
	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode("123%E9%E0%E8abcb%EF%EE", Encoding.ISO_8859_1));
	assertEquals("123???abcb??", URLEncoderHelper.decode("123???abcb??", Encoding.US_ASCII));
   }

}
