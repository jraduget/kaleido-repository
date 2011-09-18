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

import static org.kaleidofoundry.core.cache.CacheManagerIntegrationTest.assertions;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class CacheIntegrationTest extends Assert {

   @BeforeClass
   public static void setupClass() throws ResourceException {
	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myConfig=classpath:/cache/myContext.properties
	ConfigurationFactory.provides("myConfig", "classpath:/cache/myContext.properties");
   }

   @AfterClass
   public static void cleanupClass() throws ResourceException {
	ConfigurationFactory.unregister("myConfig");
   }

   @Test
   public void testSample01() {
	CacheSample01 cache = new CacheSample01();
	assertNotNull(cache);
	cache.echo();
	assertSame(cache.getMyCache(),cache.getMyCache());
	assertions(cache.getMyCache());
	assertEquals("CacheSample01", cache.getMyCache().getName());
	assertTrue(cache.getMyCache().getDelegate() instanceof net.sf.ehcache.Cache);
   }

   @Test
   public void testSample02() {
	CacheSample02 cache = new CacheSample02();
	assertNotNull(cache);
	cache.echo();
	assertSame(cache.getMyCache(),cache.getMyCache());
	assertions(cache.getMyCache());
	assertEquals("CacheSample02", cache.getMyCache().getName());
	System.out.println(cache.getMyCache().getDelegate().getClass().getName());
	assertTrue(cache.getMyCache().getDelegate() instanceof Map<?, ?>);
   }

   @Test
   public void testSample03() {
	CacheSample03 cache = new CacheSample03();
	assertNotNull(cache);
	cache.echo();
	assertSame(cache.getMyCache(),cache.getMyCache());
	assertions(cache.getMyCache());
	assertEquals("CacheSample03", cache.getMyCache().getName());
	System.out.println(cache.getMyCache().getDelegate().getClass().getName());
	assertTrue(cache.getMyCache().getDelegate() instanceof net.sf.ehcache.Cache);
   }

   @Test
   public void testSample04() {
	CacheSample04 cache = new CacheSample04();
	assertNotNull(cache);
	cache.echo();
	assertSame(cache.getMyCache(),cache.getMyCache());
	assertions(cache.getMyCache());
	assertEquals("CacheSample04", cache.getMyCache().getName());
	System.out.println(cache.getMyCache().getDelegate().getClass().getName());
	assertTrue(cache.getMyCache().getDelegate() instanceof net.sf.ehcache.Cache);
   }
}
