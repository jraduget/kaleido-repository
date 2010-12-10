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
package org.kaleidofoundry.core.i18n;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sf.ehcache.Ehcache;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.EhCache1xImpl;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class I18nIntegrationTest extends Assert {

   @Before
   public void setup() throws ResourceException {
	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myConfig=classpath:/i18n/myContext.properties
	ConfigurationFactory.provides("myConfig", "classpath:/i18n/myContext.properties");
   }

   @After
   public void cleanupClass() throws ResourceException {
	I18nMessagesFactory.clearCache();
	ConfigurationFactory.destroy("myConfig");
   }

   /**
    * test with external configuration
    * 
    * @throws ParseException
    */
   @Test
   public void testSample01() throws ParseException {
	I18nSample01 sample = new I18nSample01();
	assertNotNull(sample.getMessages());
	sample.echo();
	assertEn(sample.getMessages());

   }

   /**
    * test with no external configuration
    * 
    * @throws ParseException
    */
   @Test
   public void testSample02() throws ParseException {
	I18nSample02 sample = new I18nSample02();
	assertNotNull(sample.getMessages());
	sample.echo();
	assertFr(sample.getMessages());
   }

   /**
    * test with overriding some external configuration
    * @throws ParseException
    */
   @Test
   public void testSample03() throws ParseException {
	I18nSample03 sample = new I18nSample03();
	assertNotNull(sample.getMessages());
	sample.echo();
	assertEn(sample.getMessages());
	assertTrue(sample.getMessages() instanceof DefaultMessageBundle);
	
	// we have to use ehcache here (see configuration)
	Cache<?,?> internalCache = ((DefaultMessageBundle) sample.getMessages()).resourceBundleCache;
	assertNotNull(internalCache);	
	assertEquals(EhCache1xImpl.class.getName(), internalCache.getClass().getName());
	assertTrue(internalCache.getDelegate() instanceof Ehcache);
	assertEquals(4, internalCache.size());
	assertTrue(internalCache.keys().contains("label.hello"));
	assertTrue(internalCache.keys().contains("label.hello.who"));
	assertTrue(internalCache.keys().contains("label.hello.when"));
	assertTrue(internalCache.keys().contains("label.hello.how"));
   }
   
   /**
    * test with manual context injection (codings)
    * @throws ParseException
    */
   @Test
   public void testSample04() throws ParseException {
	I18nSample04 sample = new I18nSample04();
	assertNotNull(sample.getMessages());
	sample.echo();
	assertFr(sample.getMessages());
   }
   
   void assertFr(I18nMessages messages) throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

	assertEquals("Bonjour tout le monde!", messages.getMessage("label.hello"));
	assertEquals("Bonjour M. Smith", messages.getMessage("label.hello.who", "Smith"));
	assertEquals("Bonjour M. Smith, votre derni\u00e8re connexion a \u00e9t\u00e9 le 21/10/2010",
		messages.getMessage("label.hello.when", "Smith", df.parse("2010/10/21")));
	assertEquals("Bonjour M. Smith, votre derni\u00e8re connexion a \u00e9t\u00e9 le 21/10/2010 et vous avez gagn\u00e9 12345,67euros",
		messages.getMessage("label.hello.how", "Smith", df.parse("2010/10/21"), 12345.67));
   }

   void assertEn(I18nMessages messages) throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	assertEquals("Hello world!", messages.getMessage("label.hello"));
	assertEquals("Hello Mr Smith", messages.getMessage("label.hello.who", "Smith"));
	assertEquals("Hello Mr Smith, your last connection was the 2010-10-21", messages.getMessage("label.hello.when", "Smith", df.parse("2010/10/21")));
	assertEquals("Hello Mr Smith, your last connection was the 2010-10-21 and you have win 12345.67£",
		messages.getMessage("label.hello.how", "Smith", df.parse("2010/10/21"), 12345.67));
   }
}
