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

import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Test ehCache (c) Cache Manager
 * 
 * @author Jerome RADUGET
 */

public class JcsCacheManagerTest extends AbstractCacheManagerTest {

   @Override
   protected String getAvailableConfiguration() {
	return "classpath:/cache/jcs-cache.ccf";
   }

   @Override
   protected String getCacheImplementationCode() {
	return CacheProvidersEnum.jcs.name();
   }

   @Override
   protected RuntimeContext<CacheManager> getCacheManagerContext() {
	return new RuntimeContext<CacheManager>("jcsCacheManager", CacheManager.class);
   }

   @Override
   @Test(expected = CacheConfigurationException.class)
   @Ignore
   public void illegalConfiguration() {
	super.illegalConfiguration();	
	// that's the getCache() which throws the exception
	cacheManager.getCache("testCache1");
   }

   /**
    * not implemented in apache JCS
    */
   @Override
   @Test
   public void defaultConfiguration() {
   }

   @Override
   @Test
   public void legalConfiguration() {
	super.legalConfiguration();
	cacheManager.getCache("testCache1");
   }

   @Override
   @Test(expected = CacheConfigurationException.class)
   @Ignore
   public void invalidConfiguration() {
	super.invalidConfiguration();
	cacheManager.getCache("testCache1");
   }

   @Override
   @Test(expected = CacheConfigurationNotFoundException.class)
   @Ignore
   public void configurationNotFoundException() {
	super.configurationNotFoundException();
	cacheManager.getCache("testCache1");
   }

   @Override
   @Ignore
   @Test(expected = CacheDefinitionNotFoundException.class)
   public void cacheDefinitionNotFoundException() {
	super.cacheDefinitionNotFoundException();
	cacheManager.getCache("testCache1");
   }

   
}
