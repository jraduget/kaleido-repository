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

/**
 * Encoding / Charset enum type: <br/>
 * <ul>
 * <li>UTF-8</li>
 * <li>UTF-16</li>
 * <li>ISO-8859-1</li>
 * <li>US-ASCII</li>
 * </ul>
 * Use getCode() to have iso encoding code <br/>
 * <br/>
 * 
 * @author Jerome RADUGET
 */
public enum Encoding {

   /**
    * UTF-8 is an 8-bit encoding scheme. Characters from the English-language alphabet are all encoded using an 8-bit byte. Characters
    * for
    * other languages are encoded using 2, 3, or even 4 bytes. UTF-8 therefore produces compact documents for the English language, but
    * for
    * other languages, documents tend to be half again as large as they would be if they used UTF-16. If the majority of a document's
    * text
    * is
    * in a Western European language, then UTF-8 is generally a good choice because it allows for internationalization while still
    * minimizing
    * the space required for encoding.
    */
   UTF_8("UTF-8"),
   /**
    * UTF-16 is a 16-bit encoding scheme. It is large enough to encode all the characters from all the alphabets in the world. It uses 16
    * bits for most characters but includes 32-bit characters for ideogram-based languages such as Chinese. A Western European-language
    * document that uses UTF-16 will be twice as large as the same document encoded using UTF-8. But documents written in far Eastern
    * languages will be far smaller using UTF-16.
    */
   UTF_16("UTF-16"),
   /**
    * ISO-8859-1 is the character set for Western European languages. It's an 8-bit encoding scheme in which every encoded character
    * takes
    * exactly 8 bits. (With the remaining character sets, on the other hand, some codes are reserved to signal the start of a multibyte
    * character.)
    */
   ISO_8859_1("ISO-8859-1"),
   /**
    * US-ASCII is a 7-bit character set and encoding that covers the English-language alphabet. It is not large enough to cover the
    * characters used in other languages, however, so it is not very useful for internationalization.
    */
   US_ASCII("US-ASCII");

   private final String code;

   Encoding(final String code) {
	this.code = code;
   }

   /**
    * @return iso code of the encoding or charset
    */
   public String getCode() {
	return code;
   }

}
