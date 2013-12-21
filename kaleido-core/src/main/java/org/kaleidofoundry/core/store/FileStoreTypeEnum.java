/*  
 * Copyright 2008-2014 the original author or authors 
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
package org.kaleidofoundry.core.store;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;

/**
 * Enumeration of default handled uri schemes (default implementations provided by kaleidofoundry )
 * 
 * @author jraduget
 */
public enum FileStoreTypeEnum implements FileStoreType {

   /** classpath resource scheme */
   classpath,

   /** file system resource scheme */
   file,

   /** http resource scheme */
   http,

   /** httpss resource scheme */
   https,

   /** ftp resource scheme */
   ftp,

   /** sftp resource scheme */
   sftp,

   /** webapp resource scheme */
   webapp,

   /** jdbc clob scheme */
   jdbc,

   /** jpa clob scheme */
   jpa,

   /** mock memory scheme */
   memory,

   /** google storage scheme */
   gs,

   /** http servlet request scheme */
   request
   ;

   private final boolean custom = false;

   private final static ConcurrentMap<String, FileStoreType> CustomTypes = new ConcurrentHashMap<String, FileStoreType>();

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStoreType#isCustom()
    */
   public boolean isCustom() {
	return custom;
   }

   /**
    * Try to match the scheme of the uri with a registered {@link FileStoreType}
    * 
    * @param puri
    * @return the matching {@link FileStoreType} or null is scheme is not found
    */
   @Nullable
   public static FileStoreType match(@NotNull final String puri) {
	return match(URI.create(puri));
   }

   /**
    * Try to match the scheme of the uri with a registered {@link FileStoreType}
    * 
    * @param puri
    * @return the matching {@link FileStoreType} or null is scheme is not found
    */
   @Nullable
   public static FileStoreType match(@NotNull final URI puri) {

	// uri check
	if (puri.getScheme() == null) { return null; }

	// search in official schemes
	for (final FileStoreTypeEnum t : values()) {
	   if (t.name().equalsIgnoreCase(puri.getScheme())) { return t; }
	}

	// search in custom schemes
	return CustomTypes.get(puri.getScheme());
   }

   /**
    * @param scheme custom file store uri scheme to register
    */
   public static void registerCustomScheme(@NotNull final String scheme) {
	// check that no scheme is registered in default implementations
	if (valueOf(scheme) == null) {

	   // check that no scheme is ever registered in custom implementations
	   CustomTypes.putIfAbsent(scheme, new FileStoreTypeBean(scheme));

	}
	throw new IllegalArgumentException("scheme '" + scheme + "' argument is already registered");
   }
}
