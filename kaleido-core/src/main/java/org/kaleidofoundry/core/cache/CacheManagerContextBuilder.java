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
 * {@link CacheManager} base {@link RuntimeContext} builder & properties.<br/>
 * <b>{@link CacheManager} commons context properties</b> : <br/>
 * <p>
 * <table border="1">
 * <tr>
 * <th>
 * <td>Property name</td></th>
 * <th>
 * <td>Property description</td></th>
 * </tr>
 * <tr>
 * <td>providerCode</td>
 * <td>cache provider code to use (see {@link CacheProvidersEnum}):
 * <code>ehCache|jbossCache3x|infinispan4x|coherence3x|gigaspace7x|local</code></td>
 * </tr>
 * <tr>
 * <td>classloader</td>
 * <td>full class name, which will give the class loader to use</td>
 * </tr>
 * <tr>
 * <td>fileStoreUri</td>
 * <td>uri of the external cache configuration file to use</td>
 * </tr>
 * <tr>
 * <td>fileStoreRef</td>
 * <td>name of the file store context to use, in order to load the external cache configuration</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class CacheManagerContextBuilder extends AbstractRuntimeContextBuilder<CacheManager> {

   /**
    * cache provider code to use (see {@link CacheProvidersEnum}):
    * <code>ehCache|jbossCache3x|infinispan4x|coherence3x|gigaspace7x|local</code>
    */
   public static final String ProviderCode = "providerCode";
   /** full class name, which will give the class loader to use */
   public static final String Classloader = "classloader";
   /** uri of the configuration file to use */
   public static final String FileStoreUri = "fileStoreUri";
   /** name of the store context to use, in order to load configuration */
   public static final String FileStoreRef = "fileStoreRef";

   /**
    * 
    */
   public CacheManagerContextBuilder() {
	super();
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public CacheManagerContextBuilder(final Class<CacheManager> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    * @param staticParameters
    */
   public CacheManagerContextBuilder(final Class<CacheManager> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    */
   public CacheManagerContextBuilder(final Class<CacheManager> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public CacheManagerContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(staticParameters, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public CacheManagerContextBuilder(final String name, final Class<CacheManager> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public CacheManagerContextBuilder(final String name, final Class<CacheManager> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public CacheManagerContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public CacheManagerContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public CacheManagerContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public CacheManagerContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public CacheManagerContextBuilder(final String name) {
	super(name);
   }

   /**
    * @param providerCode
    * @return current builder instance
    */
   public CacheManagerContextBuilder withProviderCode(final String providerCode) {
	getContextParameters().put(ProviderCode, providerCode);
	return this;
   }

   /**
    * @param classloader
    * @return current builder instance
    */
   public CacheManagerContextBuilder withClassloader(final String classloader) {
	getContextParameters().put(Classloader, classloader);
	return this;
   }

   /**
    * @param fileStoreUri
    * @return current builder instance
    */
   public CacheManagerContextBuilder withFileStoreUri(final String fileStoreUri) {
	getContextParameters().put(FileStoreUri, fileStoreUri);
	return this;
   }

   /**
    * @param fileStoreRef
    * @return current builder instance
    */
   public CacheManagerContextBuilder withFileStoreRef(final String fileStoreRef) {
	getContextParameters().put(FileStoreRef, fileStoreRef);
	return this;
   }
}
