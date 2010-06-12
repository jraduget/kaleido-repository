package org.kaleidofoundry.core.store;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;

/**
 * Enumeration of default handled uri schemes (default implementations provided by kaleidofoundry )
 * 
 * @author Jerome RADUGET
 */
public enum ResourceStoreTypeEnum implements ResourceStoreType {

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
   memory

   ;

   private final boolean custom = false;

   private final static ConcurrentMap<String, ResourceStoreType> CustomTypes = new ConcurrentHashMap<String, ResourceStoreType>();


   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceStoreType#isCustom()
    */
   public boolean isCustom() {
	return custom;
   }

   /**
    * Try to match the scheme of the uri with a registered {@link ResourceStoreType}
    * 
    * @param puri
    * @return the matching {@link ResourceStoreType} or null is scheme is not found
    */
   @Nullable
   public static ResourceStoreType match(@NotNull final String puri) {
	return match(URI.create(puri));
   }

   /**
    * Try to match the scheme of the uri with a registered {@link ResourceStoreType}
    * 
    * @param puri
    * @return the matching {@link ResourceStoreType} or null is scheme is not found
    */
   @Nullable
   public static ResourceStoreType match(@NotNull final URI puri) {
	// search in official schemes
	for (ResourceStoreTypeEnum t : values()) {
	   if (t.name().equalsIgnoreCase(puri.getScheme())) { return t; }
	}

	// search in custom schemes
	return CustomTypes.get(puri.getScheme());
   }

   /**
    * @param scheme custom resource store uri scheme to register
    */
   public static void registerCustomScheme(@NotNull final String scheme) {
	// check that no scheme is registered in default implementations
	if (valueOf(scheme) == null) {

	   // check that no scheme is ever registered in custom implementations
	   CustomTypes.putIfAbsent(scheme, new ResourceStoreTypeBean(scheme));

	}
	throw new IllegalArgumentException("scheme '" + scheme + "' argument is already registered");
   }
}
