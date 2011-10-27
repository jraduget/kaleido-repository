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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.Infinispan4xCacheImpl;
import org.kaleidofoundry.core.cache.Infinispan4xCacheManagerImpl;
import org.kaleidofoundry.core.cache.LocalCacheImpl;
import org.kaleidofoundry.core.cache.LocalCacheManagerImpl;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.PropertiesConfiguration;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.naming.JndiNamingService;
import org.kaleidofoundry.core.naming.NamingService;

/**
 * @author Jerome RADUGET
 */
public class MyServiceAssertions {

   public static void runtimeContextInjectionAssertions(final RuntimeContext<?> myContext, final RuntimeContext<?> myNamedContext) {
	assertNotNull(myContext);
	assertEquals("myContext", myContext.getName());

	assertNotNull(myNamedContext);
	assertEquals("namedCtx", myNamedContext.getName());
   }

   public static void configurationInjectionAssertions(final Configuration myConfig) throws ParseException {
	assertNotNull(myConfig);
	assertSame(myConfig, myConfig);
	assertEquals("myConfig", myConfig.getName());
	assertEquals(PropertiesConfiguration.class, myConfig.getClass());
	assertEquals("my new application", myConfig.getString("myapp.name"));

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	assertEquals("my new application", myConfig.getString("myapp.name"));
	assertEquals("myadmin@mysociete.com", myConfig.getString("myapp.admin.email"));
	assertEquals("2010-12-01T02:45:30", myConfig.getString("myapp.sample.date"));
	assertEquals(df.parse("2010-12-01T02:45:30"), myConfig.getDate("myapp.sample.date"));
	assertEquals("123.45", myConfig.getString("myapp.sample.float"));
	assertEquals(new Float(123.45), myConfig.getFloat("myapp.sample.float"));
	assertEquals("false", myConfig.getString("myapp.sample.boolean"));
	assertEquals(Boolean.FALSE, myConfig.getBoolean("myapp.sample.boolean"));

	assertTrue(myConfig.keySet().contains("//myapp/name"));
	assertTrue(myConfig.keySet().contains("//myapp/admin/email"));
	assertTrue(myConfig.keySet().contains("//myapp/sample/date"));
	assertTrue(myConfig.keySet().contains("//myapp/sample/float"));
	assertTrue(myConfig.keySet().contains("//myapp/sample/boolean"));
   }

   public static void cacheManagerInjectionAssertions(final CacheManager myDefaultCacheManager, final CacheManager myCustomCacheManager) {
	assertNotNull(myDefaultCacheManager);
	assertEquals(LocalCacheManagerImpl.class, myDefaultCacheManager.getClass());

	assertNotNull(myCustomCacheManager);
	assertEquals(Infinispan4xCacheManagerImpl.class, myCustomCacheManager.getClass());
   }

   @SuppressWarnings("rawtypes")
   public static void cacheInjectionAssertions(final Cache myDefaultCache, final Cache myCustomCache) {
	assertNotNull(myDefaultCache);
	assertEquals(LocalCacheImpl.class, myDefaultCache.getClass());
	assertEquals("myDefaultCache", myDefaultCache.getName());

	assertNotNull(myCustomCache);
	assertEquals(Infinispan4xCacheImpl.class, myCustomCache.getClass());
	assertEquals("myNamedCache", myCustomCache.getName());
   }

   public static void i18nMessagesInjectionAssertions(final I18nMessages myDefaultMessages, final I18nMessages myBaseMessages) {
	assertNotNull(myDefaultMessages);
	assertEquals("Hello world!", myDefaultMessages.getMessage("hello.world"));

	assertNotNull(myBaseMessages);
	assertEquals("Hello world!!", myBaseMessages.getMessage("hello.world"));
   }

   public static void namingServiceInjectionAssertions(final NamingService myNamingService) {
	assertNotNull(myNamingService);
	assertEquals(JndiNamingService.class, myNamingService.getClass());
   }

   public static void entityManagerFactoryInjectionAssertions(final EntityManagerFactory entityManagerFactory) {
	assertNotNull(entityManagerFactory);
	assertTrue(entityManagerFactory.isOpen());
   }

   public static void entityManagerInjectionAssertions(final EntityManager entityManager) {
	assertNotNull(entityManager);
	assertTrue(entityManager.isOpen());
   }
}
