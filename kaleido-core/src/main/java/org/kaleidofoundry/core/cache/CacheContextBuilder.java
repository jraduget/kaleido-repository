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

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * {@link Cache} base {@link RuntimeContext} builder & properties.<br/>
 * <b>{@link Cache} commons context properties</b> : <br/>
 * <p>
 * <table border="1">
 * <tr>
 * <td><b>Property name</b></td>
 * <td><b>Property description</b></td>
 * </tr>
 * <tr>
 * <td>cacheName</td>
 * <td>the cache name (unique for a cache provider instance)</td>
 * </tr>
 * <tr>
 * <td>cacheManagerRef</td>
 * <td>reference name of the cache manager context to use</td>
 * </tr>
 * <tr>
 * <td>gaeExpirationDelta</td>
 * <td>Google application engine - expire values the given amount of time relative to when they are put, as an Integer number of
 * milliseconds</td>
 * </tr>
 * <tr>
 * <td>gaeExpirationAt</td>
 * <td>Google application engine - expire values at the given date and time, as a java.util.Date</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author Jerome RADUGET
 * @see CacheManagerContextBuilder
 */
@SuppressWarnings("rawtypes")
public class CacheContextBuilder extends AbstractRuntimeContextBuilder<Cache> {

   /** the cache name (unique for a cache provider instance) */
   public static final String CacheName = "cacheName";
   /** reference name of the cache manager context to use */
   public static final String CacheManagerRef = "cacheManagerRef";

   /**
    * Google application engine - expire values the given amount of time relative to when they are put, as an Integer number of milliseconds
    */
   public static final String GaeCacheExpirationDelta = "gaeExpirationDelta";
   /** Google application engine - expire values at the given date and time, as a java.util.Date */
   public static final String GaeCacheExpiration = "gaeExpirationAt";
   /**
    * Google application engine - Property key that determines whether to throw a {@link GCacheException} if a put method fails.
    * The value should be a boolean value, and defaults to {@code false}.
    * If you set this to true, you should be prepared to catch any {@link GCacheException} thrown from {@code put} or {@code putAll} as this
    * may happen sporadically or during scheduled maintenance.
    */
   public static final String GaeThrowOnPutFailure = "ThrowOnPutFailure";
   
   /**
    * Apache jcs group, to configuration your cache datas
    */
   public static final String JcsGroup = "jcsGroup";

   /**
    * 
    */
   public CacheContextBuilder() {
	super();
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public CacheContextBuilder(final Class<Cache> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    * @param staticParameters
    */
   public CacheContextBuilder(final Class<Cache> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    */
   public CacheContextBuilder(final Class<Cache> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public CacheContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(staticParameters, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public CacheContextBuilder(final String name, final Class<Cache> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public CacheContextBuilder(final String name, final Class<Cache> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public CacheContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public CacheContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public CacheContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public CacheContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public CacheContextBuilder(final String name) {
	super(name);
   }

   /**
    * @param parameter parameter name
    * @param value parameter value
    * @return current builder instance
    */
   public CacheContextBuilder withParameter(final String parameter, final Serializable value) {
	getContextParameters().put(parameter, value);
	return this;
   }

   /**
    * @param cacheName
    * @return current builder instance
    */
   public CacheContextBuilder withCacheName(final String cacheName) {
	getContextParameters().put(CacheName, cacheName);
	return this;
   }

   /**
    * @param cacheManagerRef
    * @return current builder instance
    */
   public CacheContextBuilder withCacheManagerRef(final String cacheManagerRef) {
	getContextParameters().put(CacheManagerRef, cacheManagerRef);
	return this;
   }

   /**
    * @param cacheManagerRef
    * @return current builder instance
    */
   public CacheContextBuilder withGaeCacheExpirationDelta(final String gaeCacheExpirationDelta) {
	getContextParameters().put(GaeCacheExpirationDelta, gaeCacheExpirationDelta);
	return this;
   }

   /**
    * @param cacheManagerRef
    * @return current builder instance
    */
   public CacheContextBuilder withGaeCacheExpiration(final String gaeCacheExpiration) {
	getContextParameters().put(GaeCacheExpiration, gaeCacheExpiration);
	return this;
   }

   /**
    * @param cacheManagerRef
    * @return current builder instance
    */
   public CacheContextBuilder withJcsGroup(final String jcsGroup) {
	getContextParameters().put(JcsGroup, jcsGroup);
	return this;
   }
   
   
}
