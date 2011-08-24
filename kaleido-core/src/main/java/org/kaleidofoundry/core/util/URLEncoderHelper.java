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

import org.kaleidofoundry.core.lang.Charsets;

/**
 * Helper for encoding url get parameter
 * 
 * @author Jerome RADUGET
 * @see Charsets
 */
public abstract class URLEncoderHelper {

   /**
    * string UTF-8 encoding
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
	   return java.net.URLEncoder.encode(s, Charsets.UTF_8.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }

   /**
    * string custom encoding
    * 
    * @param s string to encode
    * @param charset charset to use for encoding
    * @return encoded string (null is string is null or encoding is null)
    */
   public static String encode(final String s, final Charsets charset) {
	if (s == null || charset == null) { return null; }
	try {
	   return java.net.URLEncoder.encode(s, charset.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }

   /**
    * string UTF-8 decoding
    * 
    * @param s string to decode (UTF-8 encoding)
    * @return string decoding (null is string is null)
    */
   public static String decode(final String s) {
	if (s == null) { return null; }
	try {
	   return URLDecoder.decode(s, Charsets.UTF_8.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }

   /**
    * string custom decoding
    * 
    * @param s s string to decode
    * @param charset charset to use for decoding
    * @return string decoding (null is string is null)
    */
   public static String decode(final String s, final Charsets charset) {
	if (s == null || charset == null) { return null; }
	try {
	   return URLDecoder.decode(s, charset.getCode());
	} catch (final UnsupportedEncodingException uee) {
	   return s;
	}
   }
}
