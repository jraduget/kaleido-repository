/*
 * $License$
 */
package org.kaleidofoundry.core.io;

import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;

import org.kaleidofoundry.core.lang.annotation.Tested;

/**
 * MimeTypesDefaultService factory
 * TODO use {@link MimetypesFileTypeMap} instead {@link MimeTypesDefaultService} ?
 * 
 * @author Jerome RADUGET
 */
@Tested
public abstract class MimeTypesFactory {

   /* default unique instance */
   private static MimeTypesDefaultService DEFAULT_MIMETYPERESSOURCE_INSTANCE;

   static {
	init();
   }

   private synchronized static void init() {
	try {
	   DEFAULT_MIMETYPERESSOURCE_INSTANCE = new MimeTypesDefaultService();
	} catch (final IOException ioe) {
	   throw new java.lang.IllegalStateException("error loading resource: " + ioe.getMessage(), ioe);
	}

   }

   /**
    * @return default MimeTypesDefaultService instance
    */
   public static MimeTypesDefaultService getService() {

	if (DEFAULT_MIMETYPERESSOURCE_INSTANCE == null) {
	   init();
	}
	return DEFAULT_MIMETYPERESSOURCE_INSTANCE;
   }

}
