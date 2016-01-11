/*  
 * Copyright 2008-2016 the original author or authors 
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

import java.io.IOException;
import java.util.Date;

import net.sf.jsr107cache.Cache;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import static org.junit.Assert.*;

/**
 * Test ehCache(c) Cache Manager
 * 
 * @author jraduget
 */
public class GaeCacheTest extends AbstractCacheTest {

   protected static LocalServiceTestHelper helper;
   protected static CacheManager cacheManager;

   @BeforeClass
   public static void init() throws IOException {
	helper = new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	helper.setUp();

	cacheManager = CacheManagerFactory.provides(CacheProvidersEnum.gae.name());
   }

   @AfterClass
   public static void tearDown() {
	helper.tearDown();

	if (cacheManager != null) {
	   // destroy all
	   cacheManager.destroyAll();
	}
   }

   @Before
   public void setup() {
	cache = cacheManager.getCache(Person.class.getName());
   }

   @After
   public void cleanup() throws IOException {
	if (cache != null) {
	   cache.clear();
	}
   }

   /**
    * {@link Cache#keySet()} is not supported with google application engine
    */
   @Override
   @Test(expected = UnsupportedOperationException.class)
   public void put() {
	super.put();
   }

   /**
    * specific assertions for gae
    */
   @Test
   public void putForGae() {

	Person mockPerson1 = Person.newMockInstance();
	final Person mockPersonToCompare1 = mockPerson1.clone();
	final Person mockPerson2 = Person.newMockInstance();
	final Person mockPersonToCompare2;

	mockPerson2.setFirstName(mockPerson2.getFirstName() + "-changed");
	mockPerson2.setLastName(mockPerson2.getLastName() + "-changed");
	mockPerson2.setBirthdate(new Date());

	mockPersonToCompare2 = mockPerson2.clone();

	// 0. not same references
	assertNotSame(mockPerson1, mockPerson2);
	assertNotSame(mockPerson1, mockPersonToCompare1);
	assertNotSame(mockPerson2, mockPersonToCompare2);

	// 1. try first put in cache
	cache.put(mockPerson1.getId(), mockPerson1);
	assertNotNull(mockPerson1.getId());
	assertTrue(cache.containsKey(mockPerson1.getId()));
	// assert that first entry put have right properties
	mockPerson1 = cache.get(mockPerson1.getId());
	assertNotNull(mockPerson1);
	assertNotNull(mockPerson1.getId());
	assertEquals(mockPersonToCompare1.getId(), mockPerson1.getId());
	assertEquals(mockPersonToCompare1.getFirstName(), mockPerson1.getFirstName());
	assertEquals(mockPersonToCompare1.getLastName(), mockPerson1.getLastName());
	assertEquals(mockPersonToCompare1.getBirthdate(), mockPerson1.getBirthdate());

	// 2. try second put in cache, replacing old instance
	cache.put(mockPerson2.getId(), mockPerson2);
	assertNotNull(mockPerson1.getId());
	assertTrue(cache.containsKey(mockPerson1.getId()));
	// assert that second entry put have right replace old instance
	mockPerson1 = cache.get(mockPerson2.getId());
	assertNotNull(mockPerson2);
	assertNotNull(mockPerson2.getId());
	assertEquals(mockPersonToCompare2.getId(), mockPerson2.getId());
	assertEquals(mockPersonToCompare2.getFirstName(), mockPerson2.getFirstName());
	assertEquals(mockPersonToCompare2.getLastName(), mockPerson2.getLastName());
	assertEquals(mockPersonToCompare2.getBirthdate(), mockPerson2.getBirthdate());
   }

   /**
    * {@link Cache#keySet()} is not supported with google application engine
    */
   @Override
   @Test(expected = UnsupportedOperationException.class)
   public void keys() {
	super.keys();
   }
}
