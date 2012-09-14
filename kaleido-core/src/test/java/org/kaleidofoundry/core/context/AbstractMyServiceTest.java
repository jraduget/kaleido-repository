/*
 *  Copyright 2008-2011 the original author or authors.
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
package org.kaleidofoundry.core.context;

import java.text.ParseException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractMyServiceTest extends Assert implements MyServiceTest {

   public abstract MyService getMyService();

   @BeforeClass
   public static void setupStatic() {
   }

   @AfterClass
   public static void cleanupStatic() throws ResourceException {
	if (ConfigurationFactory.isRegistered("myConfig")) {
	   ConfigurationFactory.unregister("myConfig");
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#runtimeContextInjectionTest()
    */
   @Override
   @Test
   public void runtimeContextInjectionTest() {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());

	MyServiceAssertions.runtimeContextInjectionAssertions(myService.getMyContext(), myService.getMyNamedContext());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#storeInjectionTest()
    */
   @Override
   @Test
   public void storeInjectionTest() throws ResourceException {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());
	MyServiceAssertions.storeInjectionAssertions(myService.getMyStore());

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#configurationInjectionTest()
    */
   @Override
   @Test
   public void configurationInjectionTest() throws ParseException {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());

	MyServiceAssertions.configurationInjectionAssertions(myService.getMyConfig());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#cacheManagerInjectionTest()
    */
   @Override
   @Test
   public void cacheManagerInjectionTest() {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());

	MyServiceAssertions.cacheManagerInjectionAssertions(myService.getMyDefaultCacheManager(), myService.getMyCustomCacheManager());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#cacheInjectionTest()
    */
   @Override
   @Test
   public void cacheInjectionTest() {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());

	MyServiceAssertions.cacheInjectionAssertions(myService.getMyDefaultCache(), myService.getMyCustomCache());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#i18nMessagesInjectionTest()
    */
   @Override
   @Test
   public void i18nMessagesInjectionTest() {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());

	MyServiceAssertions.i18nMessagesInjectionAssertions(myService.getMyDefaultMessages(), myService.getMyBaseMessages());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#namingServiceInjectionTest()
    */
   @Override
   @Test
   public void namingServiceInjectionTest() {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());

	MyServiceAssertions.namingServiceInjectionAssertions(myService.getMyNamingService());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#entityManagerFactoryInjectionTest()
    */
   @Override
   @Test
   public void entityManagerFactoryInjectionTest() {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());
	MyServiceAssertions.entityManagerFactoryInjectionAssertions(myService.getEntityManagerFactory());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.MyServiceTest#entityManagerInjectionTest()
    */
   @Override
   @Test
   public void entityManagerInjectionTest() {
	MyService myService = getMyService();
	assertNotNull(myService);
	assertSame(myService, getMyService());
	MyServiceAssertions.entityManagerInjectionAssertions(myService.getEntityManager());
   }

}
