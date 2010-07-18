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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.kaleidofoundry.core.lang.annotation.Tested;

/**
 * Helper for url encoding
 * 
 * @author Jerome RADUGET
 */
@Tested
public abstract class URLEncoderHelper {

   /**
    * string decoding
    * 
    * @param s string to encode
    * @return UTF-8 string encoding. (null is string is null or encoding is null)<br/>
    *         <em><strong>Note:</strong> The <a href=
    * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
    * World Wide Web Consortium Recommendation</a> states that
    * UTF-8 should be used. Not doing so may introduce
    * incompatibilites.</em>
    */
   public static String encode(final String s) {
	if (s == null) { return null; }
	try {
	   return java.net.URLEncoder.encode(s, Encoding.UTF_8.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }

   /**
    * @param s string to encode
    * @param enc encoding to use: {@link URLEncoderHelper}.UTF8_ENCODING, {@link URLEncoderHelper}.FR1_ENCODING, ...
    * @return encoded string (null is string is null or encoding is null)
    */
   public static String encode(final String s, final Encoding enc) {
	if (s == null || enc == null) { return null; }
	try {
	   return java.net.URLEncoder.encode(s, enc.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }

   /**
    * @param s string to decode (UTF-8 encoding)
    * @return string decoding (null is string is null)
    */
   public static String decode(final String s) {
	if (s == null) { return null; }
	try {
	   return URLDecoder.decode(s, Encoding.UTF_8.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }

   /**
    * @param s s string to decode
    * @param enc encoding to use: {@link URLEncoderHelper}.UTF8_ENCODING, {@link URLEncoderHelper}.FR1_ENCODING, ....
    * @return string decoding (null is string is null)
    */
   public static String decode(final String s, final Encoding enc) {
	if (s == null || enc == null) { return null; }
	try {
	   return URLDecoder.decode(s, enc.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }
}
