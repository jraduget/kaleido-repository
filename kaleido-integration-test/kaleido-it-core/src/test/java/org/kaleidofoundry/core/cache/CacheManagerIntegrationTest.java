/*
 * Copyright 2008-2021 the original author or authors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;

/**
 * @author jraduget
 */
@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfiguration(name = "myConfig", uri = "classpath:/cache/myContext.properties")
public class CacheManagerIntegrationTest {

   @After
   public void cleanup() {
	// cleanup used cache manager
	if (CacheManagerFactory.getRegistry().containsKey("myCacheManager")) {
	   CacheManagerFactory.getRegistry().get("myCacheManager").destroyAll();
	}
	if (CacheManagerFactory.getRegistry().containsKey("myCacheManager02")) {
	   CacheManagerFactory.getRegistry().get("myCacheManager02").destroyAll();
	}
   }

   @Test
   public void testCacheManagerSample01() {
	CacheManagerSample01 cacheManager = new CacheManagerSample01();
	assertNotNull(cacheManager);
	cacheManager.echo();
	assertions(cacheManager.getMyCache());
   }

   @Test
   public void testCacheManagerSample02() {
	CacheManagerSample02 cacheManager = new CacheManagerSample02();
	assertNotNull(cacheManager);
	cacheManager.echo();
	assertions(cacheManager.getMyCache());
   }

   @Test
   public void testCacheManagerSample03() {
	CacheManagerSample03 cacheManager = new CacheManagerSample03();
	assertNotNull(cacheManager);
	cacheManager.echo();
	assertions(cacheManager.getMyCache());
   }

   @Test
   public void testCacheManagerSample04() {
	CacheManagerSample04 cacheManager = new CacheManagerSample04();
	assertNotNull(cacheManager);
	cacheManager.echo();
	assertions(cacheManager.getMyCache());
   }

   /**
    * junit assertions, used for simple integration tests
    */
   static void assertions(final Cache<String, YourBean> myCache) {
	assertNotNull(myCache);
	assertEquals(2, myCache.size());

	assertNotNull(myCache.get("bean1"));
	assertEquals("name1", myCache.get("bean1").getName());
	assertEquals(Boolean.TRUE, Boolean.valueOf(myCache.get("bean1").isEnabled()));
	assertEquals(2, myCache.get("bean1").getFlag());

	assertNotNull(myCache.get("bean2"));
	assertEquals("name2", myCache.get("bean2").getName());
	assertEquals(Boolean.FALSE, Boolean.valueOf(myCache.get("bean2").isEnabled()));
	assertEquals(15, myCache.get("bean2").getFlag());

	assertSame(myCache.getDelegate(), myCache.getDelegate());
   }
}
