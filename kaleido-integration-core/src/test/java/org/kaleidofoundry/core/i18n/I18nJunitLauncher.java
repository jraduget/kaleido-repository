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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class I18nJunitLauncher extends Assert {

   @BeforeClass
   public static void setupClass() throws ResourceException {
	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myConfig=classpath:/i18n/myContext.properties
	ConfigurationFactory.provides("myConfig", "classpath:/i18n/myContext.properties");
   }

   @AfterClass
   public static void cleanupClass() throws ResourceException {
	ConfigurationFactory.destroy("myConfig");
   }

   @Test
   public void testSample01() throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	I18nSample01 sample = new I18nSample01();

	assertNotNull(sample.getMessages());
	sample.echo();

	assertEquals("Bonjour tout le monde!", sample.getMessages().getMessage("label.hello"));
	assertEquals("Bonjour M. Smith", sample.getMessages().getMessage("label.hello.who", "Smith"));
	assertEquals("Bonjour M. Smith, votre derni\u00e8re connexion a \u00e9t\u00e9 le 21/10/2010",
		sample.getMessages().getMessage("label.hello.when", "Smith", df.parse("2010/10/21")));
	assertEquals("Bonjour M. Smith, votre derni\u00e8re connexion a \u00e9t\u00e9 le 21/10/2010 et vous avez gagn\u00e9 12345,67euros", sample.getMessages()
		.getMessage("label.hello.how", "Smith", df.parse("2010/10/21"), 12345.67));

   }

   @Test
   public void testSample02() throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	I18nSample02 sample = new I18nSample02();

	assertNotNull(sample.getMessages());
	sample.echo();

	assertEquals("Hello world!", sample.getMessages().getMessage("label.hello"));
	assertEquals("Hello Mr Smith", sample.getMessages().getMessage("label.hello.who", "Smith"));
	assertEquals("Hello Mr Smith, your last connection was the 2010-10-21",
		sample.getMessages().getMessage("label.hello.when", "Smith", df.parse("2010/10/21")));
	assertEquals("Hello Mr Smith, your last connection was the 2010-10-21 and you have win 12345.67$",
		sample.getMessages().getMessage("label.hello.how", "Smith", df.parse("2010/10/21"), 12345.67));

   }
}
