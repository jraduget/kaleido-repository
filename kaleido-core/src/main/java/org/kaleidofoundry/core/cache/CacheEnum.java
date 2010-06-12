/*
 * $License$
 */
package org.kaleidofoundry.core.cache;

/**
 * <p>
 * Enumeration of cache implementations<br/>
 * Forthcoming :<br/>
 * <ul>
 * <li>GigaSpace
 * <li>Apache JCS
 * <li>MemCache google bridge ?
 * </ul>
 * <p>
 * 
 * @author Jerome RADUGET
 */
public enum CacheEnum {

   /** Default kaleido local cache implementation */
   LOCAL("kaleido-local", "1.0", "1.0", "kaleido-cache.xml", false),
   /** EhCache implementation */
   EHCACHE_1X("ehcache-1.x", "1.2", "1.7.2", "ehcache.xml", false),
   /** JbossCache implementation */
   JBOSS_3X("jboss-cache-3.x", "3.0", "3.2.1", "jboss.xml", false),
   /** Oracle coherence implementation */
   COHERENCE_3X("coherence-3.x", "3.0", "3.5.3", "coherence-cache-config.xml", true),
   /** Jboss infinispan implementation */
   INFINISPAN_4X("infinispan-4.x", "4.0", "4.0.0", "?", true), ;

   private final String code;
   private final String minVersion;
   private final String maxVersion;
   private final String defaultConfiguration;
   private final boolean queryAllowed;

   CacheEnum(final String code, final String minVersion, final String maxVersion, final String configuration, final boolean queryAllowed) {
	this.code = code;
	this.minVersion = minVersion;
	this.maxVersion = maxVersion;
	defaultConfiguration = configuration;
	this.queryAllowed = queryAllowed;
   }

   /**
    * @return unique name of the implementation
    */
   public String getCode() {
	return code;
   }

   public String getMinVersion() {
	return minVersion;
   }

   /**
    * @return default configuration file name
    */
   public String getDefaultConfiguration() {
	return defaultConfiguration;
   }

   /**
    * @return does cache internal implementation allow query or filter
    */
   public boolean isQueryAllowed() {
	return queryAllowed;
   }

   /**
    * @return Cache information
    */
   @Override
   public String toString() {
	return code + " - " + "[" + minVersion + " -> " + maxVersion + "]";
   }

   /**
    * @param code
    * @return CacheEnum matching to the code argument
    */
   public static CacheEnum findByCode(final String code) {
	for (final CacheEnum e : values()) {
	   if (e.code.equals(code)) { return e; }
	}
	return null;
   }
}
