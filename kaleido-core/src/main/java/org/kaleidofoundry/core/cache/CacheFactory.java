package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.CACHE_IMPLEMENTATION_ENV;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract cache factory which allow to use different cache implementation with a common interface and behaviors<br/>
 * <br/>
 * Default cache implementation when you call {@link #getCacheFactory()} will use {@link LocalCacheFactoryImpl} (it use an
 * {@link ConcurrentHashMap} internally)<br/>
 * <br/>
 * <b>You can customize default cache implementation, by defining the java env variable :</b>
 * <ul>
 * <li>-Dcache.implementation=cacheImplCode</li>
 * </ul>
 * where cacheImplCode can be :
 * <ul>
 * <li>'local' -> kaleidofoundry local implementation (ConcurrentHashMap)</li>
 * <li>'ehcache-1.x' -> ehcache implementation</li>
 * <li>'jboss-cache-3.x' -> jboss cache implementation</li>
 * <li>'infinispan-4.x' -> infinispan implementation</li>
 * </ul>
 * <b>All cache implementations shared a common test case suite, in order to guarantee the same behavior</b>
 * <b>Example :</b>
 * 
 * <pre>
 * 	// Person is a java classic serializeable POJO
 * 	final CacheFactory<Integer, Person> cacheFactory = CacheFactory.getCacheFactory();
 * 	final Cache<Integer, Person> cache = cacheFactory.getCache(Person.class);
 * 
 * 	// handle cache
 * 	cache.put(1, new Person(...);
 * 	Person p = cache.get(1);
 * 	...
 * </pre>
 * 
 * @author Jerome RADUGET
 * @param <K> Key type of the cache entries
 * @param <V> Entity type of the cache values
 */
public abstract class CacheFactory<K extends Serializable, V extends Serializable> {

   static final Logger LOGGER = LoggerFactory.getLogger(CacheFactory.class);

   // default cache implementation key to use
   private static final CacheEnum DEFAULT_CACHE_IMPLEMENTATION;
   // set of reserved cache name
   private static final Set<String> RESERVED_CACHE_NAME;

   static {
	final String userCacheImpl = System.getProperty(CACHE_IMPLEMENTATION_ENV);
	CacheEnum userDefaultCacheEnum = null;
	boolean userCacheImplNotFound = false;

	if (userCacheImpl != null && !"".equals(userCacheImpl.trim())) {
	   userDefaultCacheEnum = CacheEnum.findByCode(userCacheImpl);
	   if (userDefaultCacheEnum == null) {
		userCacheImplNotFound = true;
	   }
	}
	userDefaultCacheEnum = userDefaultCacheEnum != null ? userDefaultCacheEnum : CacheEnum.LOCAL;

	DEFAULT_CACHE_IMPLEMENTATION = userDefaultCacheEnum;
	RESERVED_CACHE_NAME = Collections.synchronizedSet(new HashSet<String>());

	if (userCacheImplNotFound) {
	   LOGGER.warn(CacheMessageBundle.getMessage("cache.implementation.notfound", userCacheImpl));
	}

	LOGGER.info(CacheMessageBundle.getMessage("cache.implementation.default", userDefaultCacheEnum.toString()));
	LOGGER.info(CacheMessageBundle.getMessage("cache.implementation.customize"));
   }

   /**
    * @return the cache implementation id
    */
   public abstract CacheEnum getImplementation();

   /**
    * @param name
    * @param configurationUri
    * @return Create it and return it at first call, return previously instanced next call
    */
   public abstract Cache<K, V> getCache(@NotNull final String name, @NotNull final String configurationUri);

   /**
    * @return all cache names instantiate by the factory
    */
   public abstract Set<String> getCacheNames();

   /**
    * @param cacheName cache name to free / destroy
    */
   public abstract void destroy(@NotNull final String cacheName);

   /**
    * stop / destroy / shutdown all cache factory instances
    */
   public abstract void destroyAll();

   /**
    * statistic dump of a cache name
    * 
    * @param cacheName
    * @return statistic information of the given cache
    */
   public abstract Map<String, Object> dumpStatistics(@NotNull final String cacheName);

   /**
    * clear cache statistics for given cache name
    * 
    * @param cacheName
    */
   public abstract void clearStatistics(@NotNull final String cacheName);

   /**
    * Default cache implementation configuration will be used
    * 
    * @param name name of the cache you want
    * @return abstract Cache method
    */
   public Cache<K, V> getCache(@NotNull final String name) {
	return getCache(name, null);
   }

   /**
    * Default cache implementation configuration will be used
    * 
    * @param cl class
    * @return abstract Cache method
    */
   public Cache<K, V> getCache(@NotNull final Class<V> cl) {
	return getCache(cl, null);
   }

   /**
    * @param cl
    * @param configurationUri
    * @return Create it and return it at first call, return previously instanced next call
    */
   public Cache<K, V> getCache(@NotNull final Class<V> cl, final String configurationUri) {
	return getCache(cl.getName(), configurationUri);
   }

   /**
    * @param cl cache name to free / destroy
    */
   public void destroy(@NotNull final Class<V> cl) {
	destroy(cl.getName());
   }

   /**
    * @return dump all cache statistics representation<br/>
    *         <ul>
    *         <li>key of the map is cache name
    *         <li>value of a key cache name, is a map of stat. key name / stat. key value
    *         </ul>
    */
   @NotNull
   public Map<String, Map<String, Object>> dumpStatistics() {
	Map<String, Map<String, Object>> dumpStatistics = new LinkedHashMap<String, Map<String, Object>>();
	for (String cacheName : getCacheNames()) {
	   dumpStatistics.put(cacheName, dumpStatistics(cacheName));
	}
	return dumpStatistics;
   }

   /**
    * clear all cache statistics
    */
   public void clearStatistics() {
	for (String cacheName : getCacheNames()) {
	   clearStatistics(cacheName);
	}
   }

   /**
    * print text statistic information to the given {@link OutputStream}
    * 
    * @param out
    * @throws IOException
    */
   public void printStatistics(@NotNull final OutputStream out) throws IOException {
	Writer writer = null;
	writer = new OutputStreamWriter(out);
	printStatistics(writer);
	writer.flush();
   }

   /**
    * print text statistic information to the given {@link Writer}
    * 
    * @param writer
    * @throws IOException
    */
   public void printStatistics(@NotNull final Writer writer) throws IOException {

	Map<String, Map<String, Object>> stats = dumpStatistics();

	if (stats != null && !stats.isEmpty()) {

	   writer.append("\n");

	   // create table width informations
	   Map<String, Integer> tableWidths = new LinkedHashMap<String, Integer>();
	   tableWidths.put("CacheName", 80);

	   // column compute width label and store it in tableWidths
	   Map<String, Object> firstCacheInfo = stats.values().iterator().next();
	   for (String column : firstCacheInfo.keySet()) {
		tableWidths.put(column, column.length() + 4);
	   }

	   // first line of separator
	   for (String column : tableWidths.keySet()) {
		writer.append(StringUtils.rightPad("-", tableWidths.get(column) - 1, "-")).append(" ");
	   }
	   writer.append("\n");

	   // columns name
	   for (String column : tableWidths.keySet()) {
		writer.append(StringUtils.rightPad(StringUtils.abbreviate(column, tableWidths.get(column)), tableWidths.get(column), " "));
	   }
	   writer.append("\n");

	   // second line of separator
	   for (String column : tableWidths.keySet()) {
		writer.append(StringUtils.rightPad("-", tableWidths.get(column) - 1, "-")).append(" ");
	   }
	   writer.append("\n");

	   if (stats != null) {

		// for each cache
		for (String cacheName : stats.keySet()) {

		   // first column is cache name
		   writer.append(StringUtils.rightPad(StringUtils.abbreviate(String.valueOf(cacheName), tableWidths.get("CacheName")),
			   tableWidths.get("CacheName"), " "));

		   // create a column for each stats of the current cache
		   Map<String, Object> statsValue = stats.get(cacheName);

		   // append each column value to the writer
		   for (String statKey : statsValue.keySet()) {
			writer.append(StringUtils.rightPad(String.valueOf(statsValue.get(statKey)), tableWidths.get(statKey), " "));

		   }
		   writer.append("\n");
		}
	   }
	}
   }

   /**
    * @return spring representation of the cache statistics
    * @throws IOException
    */
   @NotNull
   public String printStatistics() throws IOException {
	StringWriter writer = new StringWriter();
	printStatistics(writer);
	return writer.toString();
   }

   /**
    * @return default factory implementation
    * @param <K>
    * @param <V>
    */
   @NotNull
   public static <K extends Serializable, V extends Serializable> CacheFactory<K, V> getCacheFactory() {
	return getCacheFactory(DEFAULT_CACHE_IMPLEMENTATION);
   }

   /**
    * @param <K>
    * @param <V>
    * @param factoryID
    * @return cacheFactory implementation
    */
   @NotNull
   public static <K extends Serializable, V extends Serializable> CacheFactory<K, V> getCacheFactory(@NotNull final CacheEnum factoryID) {

	switch (factoryID) {

	case LOCAL: {
	   return new LocalCacheFactoryImpl<K, V>();
	}
	case EHCACHE_1X: {
	   return new EhCache1xFactoryImpl<K, V>();
	}
	case JBOSS_3X: {
	   return new Jboss32xCacheFactoryImpl<K, V>();
	}
	case COHERENCE_3X: {
	   return new Coherence35xCacheFactoryImpl<K, V>();
	}
	case INFINISPAN_4X: {
	   return new Infinispan4xCacheFactoryImpl<K, V>();
	}
	default: {
	   throw new IllegalArgumentException("Incorrect argument value factoryID:" + factoryID);
	}

	}
   }

   /**
    * @return set of some reserved cache name<br/>
    *         You can add your own as you need
    */
   public static Set<String> getReservedCacheName() {
	return RESERVED_CACHE_NAME;
   }

   /**
    * Search configuration file
    * <ul>
    * <li>first via URI if correct</li>
    * <li>second on file system</li>
    * <li>second on classpath (and jars resources)</li>
    * </ul>
    * 
    * @param configurationUri
    * @return InputStream of the input configuration url, or null if none found
    */
   protected InputStream getConfiguration(final String configurationUri)

   {
	File fconf = null;
	InputStream fconfIn = null;

	// 1. if file exist via URI first
	try {
	   final URI uri = new URI(configurationUri);
	   fconf = new File(uri);
	} catch (final URISyntaxException use) {
	} catch (final IllegalArgumentException iae) {
	} // new File() Exception

	// 2. if file exist on file system
	if (fconf == null || !fconf.exists()) {
	   final URL url = currentClassLoader().getResource(configurationUri);
	   if (url != null) {
		fconf = new File(url.getPath());
	   }
	}

	// create inputStream if file was found
	if (fconf != null && fconf.exists()) {
	   try {
		fconfIn = new FileInputStream(fconf);
	   } catch (final FileNotFoundException fnfe) {
	   }
	}
	// 3. search in classpath (jars) resources
	else {
	   fconfIn = currentClassLoader().getResourceAsStream(configurationUri);
	}

	return fconfIn;
   }

   /**
    * @return current classLoader instance (thread instance, or application instance) (never null will be return)
    */
   protected ClassLoader currentClassLoader() {
	ClassLoader current;

	current = new ThreadLocal<Object>().getClass().getClassLoader();

	if (current == null) {
	   current = this.getClass().getClassLoader();
	}

	return current;
   }

}
