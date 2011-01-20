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
package org.kaleidofoundry.core;

import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ProviderCode;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.UriRootPath;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.store.FileStore;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author Jerome RADUGET
 */
@Configurable
public class MySpringBean {

   @Context(value = "myStoreCtx", parameters = { @Parameter(name = UriRootPath, value = "classpath:/store") })
   private FileStore store;

   @Context(value = "myCacheManagerCtx", parameters = { @Parameter(name = ProviderCode, value = "ehCache1x"),
	   @Parameter(name = FileStoreUri, value = "classpath:/cache/ehcache.xml") })
   private CacheManager cacheManager;

   @Context(value = "myCacheCtx", parameters = { @Parameter(name = CacheName, value = "CacheSample01") })
   private Cache<String, String> cache;

   @Context(value = "myConfigCtx")
   private Configuration configuration;

   @Context(value = "myI18nCtx")
   private I18nMessages messages;

   /**
    * @param <T>
    * @param myParam
    * @param mySecondParam
    * @return
    */
   public String myMethodToTestNotNullAnnotation(@NotNull String myParam, String mySecondParam) {
	return myParam;
   }

   public FileStore getStore() {
	return store;
   }

   public CacheManager getCacheManager() {
	return cacheManager;
   }

   public Cache<String, String> getCache() {
	return cache;
   }

   public Configuration getConfiguration() {
	return configuration;
   }

   public I18nMessages getMessages() {
	return messages;
   }

}