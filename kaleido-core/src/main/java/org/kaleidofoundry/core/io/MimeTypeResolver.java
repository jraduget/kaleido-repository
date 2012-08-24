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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.system.JavaSystemHelper;
import org.kaleidofoundry.core.util.CollectionsHelper;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * This class handle association between mime type / file association <br/>
 * 
 * @author Jerome RADUGET
 */
@ThreadSafe
public class MimeTypeResolver {

   /* Filename containing the mime types to load */
   private static final String FILE_PROPERTIES = "META-INF/mime.types";

   /* Map of mimes properties */
   private final ConcurrentMap<String, String[]> mimeTypesByName;

   /*
    * @throws IOException
    */
   MimeTypeResolver() throws IOException {
	this(FILE_PROPERTIES);
   }

   /**
    * Constructor loading given file resource
    * 
    * @throws IOException
    */
   MimeTypeResolver(final String filename) throws IOException {

	InputStream fin = JavaSystemHelper.getResourceAsStream(filename);

	if (fin == null) { throw new FileNotFoundException(filename); }

	BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

	mimeTypesByName = new ConcurrentHashMap<String, String[]>();

	String line = reader.readLine();

	while ((line = reader.readLine()) != null) {
	   String[] values = CollectionsHelper.stringToArray(line, "	");
	   if (values != null && values.length > 0 && !values[0].startsWith("#")) {
		mimeTypesByName.put(values[0], CollectionsHelper.stringToArray(values[1], " "));
	   }
	}

   }

   /**
    * @return the mime type of a file extension
    * @param fileExtention
    */
   public String getMimeType(String fileExtention) {

	if (StringHelper.isEmpty(fileExtention)) { return null; }

	fileExtention = fileExtention.toLowerCase();

	final Set<String> mimesKey = mimeTypesByName.keySet();

	for (final String string : mimesKey) {

	   final String mime = string;
	   final String[] arrayVal = mimeTypesByName.get(mime);

	   Arrays.sort(arrayVal);

	   if (Arrays.binarySearch(arrayVal, fileExtention) >= 0) { return mime; }
	}

	return null;
   }

   /**
    * @param mimeType
    * @return the array of possible extensions of a mime type
    */
   public String[] getExtentions(final String mimeType) {

	if (StringHelper.isEmpty(mimeType)) { return null; }

	return mimeTypesByName.get(mimeType.toLowerCase());

   }

   /**
    * @param mimeType
    * @return does the mimeType parameter is a binary type ?
    */
   public boolean isMimeTypeBinary(final String mimeType) {
	return !mimeType.startsWith("text/");
   }

   /**
    * @param mimeType
    * @return does the mimeType parameter is a text type ?
    */
   public boolean isMimeTypeAscii(final String mimeType) {
	return !isMimeTypeBinary(mimeType);
   }

}