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
package org.kaleidofoundry.core.io;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class MimeTypesDefaultServiceTest extends Assert {

   @Test
   public void testMimeExtentions() {

	MimeTypesDefaultService mimes = null;
	String mimeType = null;
	String[] ext = null;
	List<String> resultsValues = null;

	mimes = MimeTypesFactory.getService();
	assertNotNull(mimes);

	mimeType = "application/postscript";
	resultsValues = Arrays.asList(new String[] { "ps", "ai", "eps" });
	ext = mimes.getExtentions(mimeType);
	assertEquals(Arrays.asList(ext), resultsValues);
	assertTrue(mimes.isMimeTypeBinary(mimeType));

	mimeType = "text/plain";
	resultsValues = Arrays.asList(new String[] { "txt", "text", "diff" });
	ext = mimes.getExtentions(mimeType);
	assertEquals(Arrays.asList(ext), resultsValues);
	assertTrue(mimes.isMimeTypeAscii(mimeType));

   }

   @Test
   public void testMimeType() {

	MimeTypesDefaultService mimes = null;
	String extention = null;
	String mimeType = null;

	mimes = MimeTypesFactory.getService();
	assertNotNull(mimes);

	extention = "ps";
	mimeType = mimes.getMimeType(extention);
	assertEquals(mimeType, "application/postscript");

	extention = "txt";
	mimeType = mimes.getMimeType(extention);
	assertEquals(mimeType, "text/plain");

   }

}
