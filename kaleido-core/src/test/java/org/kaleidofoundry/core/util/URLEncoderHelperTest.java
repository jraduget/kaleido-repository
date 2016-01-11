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

import static org.junit.Assert.*;

import org.junit.Test;
import org.kaleidofoundry.core.lang.Charsets;

/**
 * @author jraduget
 */
public class URLEncoderHelperTest  {

   private static final String TEST_STRING_TO_ENCODE = "123éàèabcbïî";
   private static final String TEST_STRING_UTF8_ENCODED = "123%C3%A9%C3%A0%C3%A8abcb%C3%AF%C3%AE";

   @Test
   public void testDefaultEncode() {
	assertNull(URLEncoderHelper.encode(null));
	assertEquals(TEST_STRING_UTF8_ENCODED, URLEncoderHelper.encode(TEST_STRING_TO_ENCODE));
   }

   @Test
   public void testEncode() {
	assertNull(URLEncoderHelper.encode(null, null));

	assertEquals(TEST_STRING_UTF8_ENCODED, URLEncoderHelper.encode(TEST_STRING_TO_ENCODE, Charsets.UTF_8));
	assertEquals("123%FE%FF%00%E9%00%E0%00%E8abcb%FE%FF%00%EF%00%EE", URLEncoderHelper.encode(TEST_STRING_TO_ENCODE, Charsets.UTF_16));
	assertEquals("123%E9%E0%E8abcb%EF%EE", URLEncoderHelper.encode(TEST_STRING_TO_ENCODE, Charsets.ISO_8859_1));
	assertEquals("123%3F%3F%3Fabcb%3F%3F", URLEncoderHelper.encode(TEST_STRING_TO_ENCODE, Charsets.US_ASCII));

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

	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode(TEST_STRING_UTF8_ENCODED, Charsets.UTF_8));
	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode("123%FE%FF%00%E9%00%E0%00%E8abcb%FE%FF%00%EF%00%EE", Charsets.UTF_16));
	assertEquals(TEST_STRING_TO_ENCODE, URLEncoderHelper.decode("123%E9%E0%E8abcb%EF%EE", Charsets.ISO_8859_1));
	assertEquals("123???abcb??", URLEncoderHelper.decode("123???abcb??", Charsets.US_ASCII));
   }

}
