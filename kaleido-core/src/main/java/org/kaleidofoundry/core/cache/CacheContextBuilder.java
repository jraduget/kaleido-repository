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

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * {@link Cache} base {@link RuntimeContext} builder & properties
 * <p>
 * <b>{@link Cache} commons context properties</b> : <br/>
 * <table border="1">
 * <tr>
 * <th>Property name</th>
 * <th>Property description</th>
 * </tr>
 * <tr>
 * <td>cacheName</td>
 * <td>the cache name (unique for a cache provider instance)</td>
 * </tr>
 * <td>cacheManagerRef</td>
 * <td>reference name of the cache manager context to use</td> </tr>
 * </table>
 * </p>
 * <p>
 * 
 * @author Jerome RADUGET
 * @see CacheManagerContextBuilder
 */
public class CacheContextBuilder extends AbstractRuntimeContextBuilder<CacheManager> {

   /** the cache name (unique for a cache provider instance) */
   public static final String CacheName = "cacheName";
   /** reference name of the cache manager context to use */
   public static final String CacheManagerRef = "cacheManagerRef";

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
   public CacheContextBuilder(final Class<CacheManager> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    */
   public CacheContextBuilder(final Class<CacheManager> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param configurations
    */
   public CacheContextBuilder(final Configuration... configurations) {
	super(configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public CacheContextBuilder(final String name, final Class<CacheManager> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
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
}
