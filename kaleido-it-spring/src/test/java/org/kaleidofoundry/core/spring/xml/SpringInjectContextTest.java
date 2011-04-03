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
package org.kaleidofoundry.core.spring.xml;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.cache.CacheDefinitionNotFoundException;
import org.kaleidofoundry.core.cache.CacheException;
import org.kaleidofoundry.core.config.ConfigurationException;
import org.kaleidofoundry.core.naming.NamingServiceException;
import org.kaleidofoundry.core.naming.NamingServiceNotFoundException;
import org.kaleidofoundry.core.store.StoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jerome RADUGET
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/springContext.xml" })
public class SpringInjectContextTest extends Assert {

   @Autowired
   private MySpringService mySpringService;

   @Test
   public void testStore() throws StoreException {
	assertNotNull(mySpringService);
	assertEquals("line1\nline2", mySpringService.getStoreResource("foo.txt"));
   }

   @Test
   public void testConfiguration() throws ConfigurationException {
	assertNotNull(mySpringService);
	assertEquals("myApp", mySpringService.getConfigurationProperty("application.name"));
	assertEquals("1.0", mySpringService.getConfigurationProperty("application.version"));
	assertNull(mySpringService.getConfigurationProperty("?"));
   }

   @Test
   public void testCacheManager() throws CacheException {
	assertNotNull(mySpringService);
	assertNotNull(mySpringService.getCache("CacheSample01"));
	assertEquals(0, mySpringService.getCache("CacheSample01").size());
	try {
	   mySpringService.getCache("UnknownCacheName");
	   fail("CacheDefinitionNotFoundException expected");
	} catch (CacheDefinitionNotFoundException cnfe) {
	}
   }

   @Test
   public void testCache() throws CacheException {
	assertNotNull(mySpringService);
	assertNull(mySpringService.getCacheValue("?"));
	assertEquals("value1", mySpringService.getCacheValue("key1"));
	assertEquals("value2", mySpringService.getCacheValue("key2"));
   }

   @Test
   public void testAnotherCacheManager() throws CacheException {
	assertNotNull(mySpringService);
	assertNotNull(mySpringService.getOtherCache("OtherCacheSample01"));
	assertEquals(0, mySpringService.getOtherCache("OtherCacheSample01").size());
   }

   @Test
   public void testAnotherCache() throws CacheException {
	assertNotNull(mySpringService);
	assertNull(mySpringService.getOtherCacheValue("?"));
	assertEquals("value11", mySpringService.getOtherCacheValue("key11"));
	assertEquals("value21", mySpringService.getOtherCacheValue("key21"));
   }

   @Test
   public void testNamingService() throws NamingServiceException {
	assertNotNull(mySpringService);
	assertNotNull(mySpringService.getNamingResource("jdbc/kaleido", DataSource.class));
	assertNotNull(mySpringService.getNamingResource("jdbc/kaleido", DataSource.class));
	try {
	   assertNotNull(mySpringService.getNamingResource("jdbc/kaleido", DataSource.class));
	   fail("NamingServiceNotFoundException expected");
	} catch (NamingServiceNotFoundException nsnf) {

	}
   }
}
