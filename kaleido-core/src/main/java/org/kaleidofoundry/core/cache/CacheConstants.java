package org.kaleidofoundry.core.cache;

/**
 * Cache module constants
 * 
 * @author Jerome RADUGET
 */
public interface CacheConstants {

   /** interface cache declare plugin name */
   public static final String CachePluginName = "cache";

   /** Default local implementation declare plugin name */
   public static final String DefaultLocalPluginName = "local";

   /** EhCache implementation declare plugin name */
   public static final String EhCachePluginName = "ehcache";

   /** JbossCache implementation declare plugin name */
   public static final String JbossCachePluginName = "jboss";

   /** Infinispan implementation declare plugin name */
   public static final String InfinispanCachePluginName = "infinispan";

   /** Coherence implementation declare plugin name */
   public static final String CoherenceCachePluginName = "coherence";

   /** Java system environment variable to set default cache implementation to use */
   public static final String CACHE_IMPLEMENTATION_ENV = "cache.implementation";

}
