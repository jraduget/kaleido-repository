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
package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.CACHE_PROVIDER_ENV;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.cache.CacheConstants.DefaultCacheProviderEnum;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.util.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract cache factory which allow to use different cache implementation with a common interface and behaviors<br/>
 * <br/>
 * Default cache implementation when you call {@link #getCacheManager()} will use {@link LocalCacheManagerImpl} (it use an
 * {@link ConcurrentHashMap} internally)<br/>
 * <br/>
 * <b>You can customize default cache provider, by defining the java env. variable :</b>
 * <ul>
 * <li>-Dcache.provider=cacheImplCode</li>
 * </ul>
 * where cacheImplCode can be :
 * <ul>
 * <li>'ehCache1x' -> terracotta ehcache (c)</li>
 * <li>'jbossCache3x' -> jboss cache (c)</li>
 * <li>'infinispan4x' -> jboss infinispan (c)</li>
 * <li>'coherence3x' -> oracle coherence (c)</li>
 * <li>'gigaspace7x' -> gigaspace (c)</li>
 * <li>'kaleidoLocalCache' -> kaleidofoundry (c) local (ConcurrentHashMap)</li>
 * </ul>
 * <b>All cache implementations shared a common test case suite, in order to guarantee the same behavior</b>
 * <b>Example :</b>
 * 
 * <pre>
 * 	// Person is a java classic serializeable POJO
 * 	final CacheFactory cacheFactory = CacheFactory.getCacheFactory();
 * 	final Cache<Integer, Person> cache = cacheFactory.getCache(Person.class);
 * 
 * 	// handle cache
 * 	cache.put(1, new Person(...);
 * 	Person p = cache.get(1);
 * 	...
 * </pre>
 * 
 * @author Jerome RADUGET
 * @see DefaultCacheProviderEnum
 */
@ThreadSafe
public abstract class CacheFactory {

   static final Logger LOGGER = LoggerFactory.getLogger(CacheFactory.class);

   // default cache provider code to use
   private static final String DEFAULT_CACHE_PROVIDER;
   // set of reserved cache name
   private static final Set<String> RESERVED_CACHE_NAME;
   // Local registry of cache manager instances */
   static final transient Registry<Integer, CacheManager> CACHEMANAGER_REGISTRY = new Registry<Integer, CacheManager>();

   static {
	final String userCacheImpl = System.getProperty(CACHE_PROVIDER_ENV);
	Plugin<CacheManager> defaultCachePlugin = null;
	boolean userCacheImplNotFound = false;

	if (userCacheImpl != null && !"".equals(userCacheImpl.trim())) {
	   defaultCachePlugin = findCacheManagerByPluginCode(userCacheImpl);
	   if (defaultCachePlugin == null) {
		userCacheImplNotFound = true;
	   }
	}
	DEFAULT_CACHE_PROVIDER = defaultCachePlugin != null ? defaultCachePlugin.getName() : DefaultCacheProviderEnum.localCache.name();
	RESERVED_CACHE_NAME = Collections.synchronizedSet(new HashSet<String>());

	if (userCacheImplNotFound) {
	   LOGGER.warn(CacheMessageBundle.getMessage("cache.provider.notfound", userCacheImpl));
	}

	LOGGER.info(CacheMessageBundle.getMessage("cache.provider.default", DEFAULT_CACHE_PROVIDER));
	LOGGER.info(CacheMessageBundle.getMessage("cache.provider.customize"));
   }

   /**
    * @return default cache manager provider (java system env. will be used, see class javadoc header)
    */
   @NotNull
   public static CacheManager getCacheManager() {
	return getCacheManager(DEFAULT_CACHE_PROVIDER, null, new RuntimeContext<CacheManager>());
   }

   /**
    * @param providerCodeCode
    * @return cache manager using specific providerCodeCode
    */
   @NotNull
   public static CacheManager getCacheManager(@NotNull final String providerCodeCode) {
	return getCacheManager(providerCodeCode, null, new RuntimeContext<CacheManager>());
   }

   /**
    * @param context
    * @return cache manager if context specify a specific provider, it will use it, otherwise default cache manager
    *         provider (java system env. will be used, see class javadoc header)
    */
   @NotNull
   public static CacheManager getCacheManager(final RuntimeContext<CacheManager> context) {
	return getCacheManager(DEFAULT_CACHE_PROVIDER, null, context);
   }

   /**
    * @param providerCode
    * @param configuration
    * @param context
    * @return cacheFactory implementation
    */
   @NotNull
   public static CacheManager getCacheManager(@NotNull final String providerCode, final String configuration,
	   @NotNull final RuntimeContext<CacheManager> context) {

	CacheManager cacheManager = CACHEMANAGER_REGISTRY.get(getCacheManagerId(providerCode, configuration));

	if (cacheManager == null) {
	   cacheManager = createCacheManager(providerCode, configuration, context);
	}

	return cacheManager;
   }

   /**
    * @return set of some reserved cache name<br/>
    *         You can add your own as you need
    */
   public static Set<String> getReservedCacheName() {
	return RESERVED_CACHE_NAME;
   }

   /**
    * @param providerCode
    * @param configuration
    * @return unique cache manager identifier
    */
   static Integer getCacheManagerId(@NotNull final String providerCode, final String configuration) {
	return (providerCode.hashCode() * 128) + (configuration != null ? configuration.hashCode() : 0);

   }

   /**
    * @param providerCode
    * @param configuration
    * @param context
    * @return
    */
   private static synchronized CacheManager createCacheManager(@NotNull final String providerCode, final String configuration,
	   @NotNull final RuntimeContext<CacheManager> context) {

	// only for optimization reasons
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.localCache.name())) { return new LocalCacheManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.ehCache1x.name())) { return new EhCache1xManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.coherence3x.name())) { return new Coherence35xCacheManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.infinispan4x.name())) { return new Infinispan4xCacheManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.jbossCache3x.name())) { return new Jboss32xCacheManagerImpl(configuration, context); }
	// if (providerCode.equalsIgnoreCase(DefaultCacheEnum.gigaspace7x.name())) { return new GigaSpaceManager7xImpl(configuration,
	// context); }

	// dynamic lookup into register plugin
	final Set<Plugin<CacheManager>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(CacheManager.class);

	// scan each @Declare resource store implementation, to get one which handle the given implementation code
	for (final Plugin<CacheManager> pi : pluginImpls) {
	   final Class<? extends CacheManager> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		if (providerCode.equalsIgnoreCase(declarePlugin.value())) {
		   final Constructor<? extends CacheManager> constructor = impl.getConstructor(String.class, RuntimeContext.class);
		   return constructor.newInstance(configuration, context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new CacheException("cache.factory.create.NoSuchConstructorException", impl.getName());
	   } catch (final InstantiationException e) {
		throw new CacheException("cache.factory.create.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new CacheException("cache.factory.create.IllegalAccessException=ResourceStore", impl.getName());
	   } catch (final InvocationTargetException e) {
		throw new CacheException("cache.factory.create.InvocationTargetException", e.getCause(), impl.getName(), e.getMessage());
	   }
	}
	throw new CacheException("cache.provider.illegal", providerCode);

   }

   /*
    * find cache manager by plugin code
    */
   @SuppressWarnings("unchecked")
   private static Plugin<CacheManager> findCacheManagerByPluginCode(@NotNull final String code) {
	final Plugin<?> plugin = PluginFactory.getImplementationRegistry().get(code);
	try {
	   return (Plugin<CacheManager>) plugin;
	} catch (final ClassCastException cce) {
	   throw new CacheException("cache.provider.classcasterror", code, plugin.getClass().getTypeParameters()[0].getClass().getName(), CacheManager.class
		   .getName());
	}
   }
}
