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

import static junit.framework.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationConstants;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class CacheManagerJunitLauncher {

   @BeforeClass
   public static void setupClass() throws ResourceException {
	// -Dkaleido.configurations=myConfig=classpath:/cache/myContext.properties
	System.getProperties().put(ConfigurationConstants.JavaEnvProperties, "myConfig=classpath:/cache/myContext.properties");
	// load given configurations
	ConfigurationFactory.init();
   }

   @AfterClass
   public static void cleanupClass() throws ResourceException {
	ConfigurationFactory.destroyAll();
   }

   @Test
   public void testCacheManagerSample01() {
	CacheManagerSample01 cacheManager = new CacheManagerSample01();
	assertNotNull(cacheManager);
	cacheManager.echo();
	cacheManager.assertions();
   }

   @Test
   public void testCacheManagerSample02() {
	CacheManagerSample02 cacheManager = new CacheManagerSample02();
	assertNotNull(cacheManager);
	cacheManager.echo();
	cacheManager.assertions();
   }

   @Test
   public void testCacheManagerSample03() {
	CacheManagerSample03 cacheManager = new CacheManagerSample03();
	assertNotNull(cacheManager);
	cacheManager.echo();
	cacheManager.assertions();
   }

   @Test
   public void testCacheManagerSample04() {
	CacheManagerSample04 cacheManager = new CacheManagerSample04();
	assertNotNull(cacheManager);
	cacheManager.echo();
	cacheManager.assertions();
   }
}
