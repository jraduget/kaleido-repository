/*  
 * Copyright 2008-2021 the original author or authors 
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

import static org.kaleidofoundry.core.lang.annotation.TaskLabel.Enhancement;

import java.io.IOException;

import org.kaleidofoundry.core.lang.annotation.Task;

/**
 * MimeTypesDefaultService factory
 * 
 * @author jraduget
 */
@Task(comment = "use {@link MimetypesFileTypeMap} instead {@link MimeTypesDefaultService} ?", labels = { Enhancement })
public abstract class MimeTypeResolverFactory {

   /* default unique instance */
   private static MimeTypeResolver DEFAULT_MIMETYPERESSOURCE_INSTANCE;

   static {
	init();
   }

   private synchronized static void init() {
	try {
	   DEFAULT_MIMETYPERESSOURCE_INSTANCE = new MimeTypeResolver();
	} catch (final IOException ioe) {
	   throw new java.lang.IllegalStateException("error loading resource: " + ioe.getMessage(), ioe);
	}

   }

   /**
    * @return default MimeTypesDefaultService instance
    */
   public static MimeTypeResolver getService() {

	if (DEFAULT_MIMETYPERESSOURCE_INSTANCE == null) {
	   init();
	}
	return DEFAULT_MIMETYPERESSOURCE_INSTANCE;
   }

}
