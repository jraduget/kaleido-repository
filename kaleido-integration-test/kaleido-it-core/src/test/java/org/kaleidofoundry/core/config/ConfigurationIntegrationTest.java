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
package org.kaleidofoundry.core.config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.EhCacheImpl;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * Integration test for configuration context injection
 * 
 * @author jraduget
 */
public class ConfigurationIntegrationTest {

   @Before
   public void setup() throws ResourceException {
	// load and register given configuration
	// another way to to this, set following java env variable :
	// -Dkaleido.configurations=myConfigCtx=classpath:/config/myContext.properties
	ConfigurationFactory.provides("myConfigContext", "classpath:/config/myContext.properties");
   }

   @After
   public void cleanup() throws ResourceException {
	// cleanup configuration
	ConfigurationFactory.unregisterAll();
	// cleanup cache manager
	CacheManagerFactory.getRegistry().clear();
   }

   /**
    * {@link ConfigurationSample01}
    * 
    * @throws ParseException
    */
   @Test
   public void testConfigurationSample01() throws ParseException {
	ConfigurationSample01 confSample = new ConfigurationSample01();
	confSample.echo();
	assertions(confSample.getConfiguration());
	assertTrue(confSample.getConfiguration().isStorable());
	assertNull(confSample.getConfiguration().getBoolean("myapp.sample.http"));
   }

   /**
    * {@link ConfigurationSample02}
    * 
    * @throws ParseException
    */
   @Test
   public void testConfigurationSample02() throws ParseException {
	ConfigurationSample02 confSample = new ConfigurationSample02();
	confSample.echo();
	assertions(confSample.getConfiguration());
	assertFalse(confSample.getConfiguration().isStorable());
	assertEquals(Boolean.TRUE, confSample.getConfiguration().getBoolean("myapp.sample.http"));

	// assert that ehcache instance are right been created and feededs
	Cache<String, Serializable> currentConfigurationCache = ((AbstractConfiguration) confSample.getConfiguration()).cacheProperties;
	assertNotNull(currentConfigurationCache);
	assertEquals(EhCacheImpl.class, currentConfigurationCache.getClass());
	assertTrue(currentConfigurationCache.getDelegate() instanceof net.sf.ehcache.Cache);
	net.sf.ehcache.Cache ehCacheConfInstance = (net.sf.ehcache.Cache) currentConfigurationCache.getDelegate();

	assertNotNull(ehCacheConfInstance);
	assertEquals(6, ehCacheConfInstance.getSize());
	assertEquals("my new application", ehCacheConfInstance.get("//myapp/name").getObjectValue());
	assertEquals("myadmin@mysociete.com", ehCacheConfInstance.get("//myapp/admin/email").getObjectValue());
	assertEquals("2010-12-01T02:45:30", ehCacheConfInstance.get("//myapp/sample/date").getObjectValue());
	assertEquals("123.45", ehCacheConfInstance.get("//myapp/sample/float").getObjectValue());
	assertEquals("false", ehCacheConfInstance.get("//myapp/sample/boolean").getObjectValue());
   }

   /**
    * {@link ConfigurationSample03}
    * 
    * @throws ResourceException
    * @throws ParseException
    */
   @Test
   public void testConfigurationSample03() throws ResourceException, ParseException {
	ConfigurationSample03 confSample = new ConfigurationSample03();
	confSample.echo();
	assertions(confSample.getConfiguration());
	assertFalse(confSample.getConfiguration().isStorable());
	assertEquals(Boolean.TRUE, confSample.getConfiguration().getBoolean("myapp.sample.http"));
   }

   /**
    * @param configuration
    * @throws ParseException
    */
   static void assertions(final Configuration configuration) throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	assertNotNull(configuration);
	assertEquals("my new application", configuration.getString("myapp.name"));
	assertEquals("myadmin@mysociete.com", configuration.getString("myapp.admin.email"));
	assertEquals("2010-12-01T02:45:30", configuration.getString("myapp.sample.date"));
	assertEquals(df.parse("2010-12-01T02:45:30"), configuration.getDate("myapp.sample.date"));
	assertEquals("123.45", configuration.getString("myapp.sample.float"));
	assertEquals(new Float(123.45), configuration.getFloat("myapp.sample.float"));
	assertEquals("false", configuration.getString("myapp.sample.boolean"));
	assertEquals(Boolean.FALSE, configuration.getBoolean("myapp.sample.boolean"));

	assertTrue(configuration.keySet().contains("//myapp/name"));
	assertTrue(configuration.keySet().contains("//myapp/admin/email"));
	assertTrue(configuration.keySet().contains("//myapp/sample/date"));
	assertTrue(configuration.keySet().contains("//myapp/sample/float"));
	assertTrue(configuration.keySet().contains("//myapp/sample/boolean"));
   }
}
