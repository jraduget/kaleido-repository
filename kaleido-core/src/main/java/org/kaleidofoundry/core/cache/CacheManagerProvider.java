/*
 *  Copyright 2008-2010 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.CACHE_PROVIDER_ENV;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kaleidofoundry.core.cache.CacheConstants.DefaultCacheProviderEnum;
import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry & provider of {@link CacheManager} instances.
 * Please see {@link CacheManagerFactory} javadoc for more details.
 * 
 * @author Jerome RADUGET
 * @see CacheManagerFactory
 */
@ThreadSafe
public class CacheManagerProvider extends AbstractProviderService<CacheManager> {

   static final Logger LOGGER = LoggerFactory.getLogger(CacheManagerProvider.class);

   /**
    * main configuration registry instance (shared between providers instances)
    */
   private static final Registry<Integer, CacheManager> REGISTRY = new Registry<Integer, CacheManager>();

   // default cache provider code to use
   private static final String DEFAULT_CACHE_PROVIDER;
   // set of reserved cache name
   private static final Set<String> RESERVED_CACHE_NAME;

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
	DEFAULT_CACHE_PROVIDER = defaultCachePlugin != null ? defaultCachePlugin.getName() : DefaultCacheProviderEnum.local.name();
	RESERVED_CACHE_NAME = Collections.synchronizedSet(new HashSet<String>());

	if (userCacheImplNotFound) {
	   LOGGER.warn(CacheMessageBundle.getMessage("cache.provider.notfound", userCacheImpl));
	}

	LOGGER.info(CacheMessageBundle.getMessage("cache.provider.default", DEFAULT_CACHE_PROVIDER));
	LOGGER.info(CacheMessageBundle.getMessage("cache.provider.customize"));
   }

   /**
    * @param genericClass
    */
   public CacheManagerProvider(final Class<CacheManager> genericClass) {
	super(genericClass);
   }

   /**
    * @return default cache manager provider (java system env. will be used, see class javadoc header)
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides() throws ProviderException {
	return provides(DEFAULT_CACHE_PROVIDER, null, new RuntimeContext<CacheManager>(CacheManager.class));
   }

   /**
    * @param providerCodeCode
    * @return cache manager using specific providerCodeCode
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides(@NotNull final String providerCodeCode) throws ProviderException {
	return provides(providerCodeCode, null, new RuntimeContext<CacheManager>(CacheManager.class));
   }

   /**
    * @param context
    * @return cache manager if context specify a specific provider, it will use it, otherwise default cache manager
    *         provider (java system env. will be used, see class java-doc header)
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   @Override
   public CacheManager _provides(final RuntimeContext<CacheManager> context) throws ProviderException {
	String providerCode = context.getString(CacheManagerContextBuilder.ProviderCode);
	final String providerConfiguration = context.getString(CacheManagerContextBuilder.FileStoreUri);

	if (StringHelper.isEmpty(providerCode)) {
	   providerCode = DEFAULT_CACHE_PROVIDER;
	}
	return provides(providerCode, providerConfiguration, context);
   }

   /**
    * @param providerCode
    * @param configuration
    * @return cache manager instance
    * @throws CacheConfigurationException cache configuration resource exception
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides(@NotNull final String providerCode, final String configuration) throws ProviderException {
	return provides(providerCode, configuration, new RuntimeContext<CacheManager>(CacheManager.class));
   }

   /**
    * @param providerCode
    * @param configuration
    * @param context
    * @return cache manager instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides(@NotNull final String providerCode, final String configuration, @NotNull final RuntimeContext<CacheManager> context)
	   throws ProviderException {

	final Integer cacheManagerId = getCacheManagerId(providerCode, configuration);
	CacheManager cacheManager = REGISTRY.get(cacheManagerId);

	if (cacheManager == null) {
	   cacheManager = create(providerCode, configuration, context);
	   REGISTRY.put(cacheManagerId, cacheManager);
	}

	return cacheManager;
   }

   /**
    * @return cache manager registry. each instance provided will be registered here
    */
   public static Registry<Integer, CacheManager> getRegistry() {
	return REGISTRY;
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
   public static Integer getCacheManagerId(@NotNull final String providerCode, final String configuration) {
	return (providerCode.hashCode() * 128) + (configuration != null ? configuration.hashCode() : 0);

   }

   /**
    * @param providerCode
    * @param configuration
    * @param context
    * @return new cache manager instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   protected static synchronized CacheManager create(@NotNull final String providerCode, final String configuration,
	   @NotNull final RuntimeContext<CacheManager> context) throws ProviderException {

	// only for optimization reasons
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.local.name())) { return new LocalCacheManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.ehCache1x.name())) { return new EhCache1xManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.coherence3x.name())) { return new Coherence3xCacheManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.infinispan4x.name())) { return new Infinispan4xCacheManagerImpl(configuration, context); }
	if (providerCode.equalsIgnoreCase(DefaultCacheProviderEnum.jbossCache3x.name())) { return new Jboss32xCacheManagerImpl(configuration, context); }
	// if (providerCode.equalsIgnoreCase(DefaultCacheEnum.gigaspace7x.name())) { return new GigaSpaceManager7xImpl(configuration,
	// context); }

	// dynamic lookup into register plugin
	final Set<Plugin<CacheManager>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(CacheManager.class);

	// scan each @Declare file store implementation, to get one which handle the given implementation code
	for (final Plugin<CacheManager> pi : pluginImpls) {
	   final Class<? extends CacheManager> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		if (providerCode.equalsIgnoreCase(declarePlugin.value())) {
		   final Constructor<? extends CacheManager> constructor = impl.getConstructor(String.class, RuntimeContext.class);
		   return constructor.newInstance(configuration, context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(),
			"String configuration, RuntimeContext<CacheManager> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(),
			"String configuration, RuntimeContext<CacheManager> context");
	   } catch (final InvocationTargetException e) {
		if (e.getCause() instanceof CacheConfigurationException) {
		   throw new ProviderException(e.getCause());
		} else {
		   throw new ProviderException("context.provider.error.InvocationTargetException", impl.getName(),
			   "String configuration, RuntimeContext<CacheManager> context", e.getCause().getClass().getName(), e.getMessage());
		}
	   }
	}
	throw new ProviderException(new CacheException("cache.provider.illegal", providerCode));

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
	   throw new CacheException("cache.provider.classcasterror", code, plugin.getClass().getTypeParameters()[0].getClass().getName(),
		   CacheManager.class.getName());
	}
   }

}
