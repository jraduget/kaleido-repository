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
package org.kaleidofoundry.core.config;

import java.net.URISyntaxException;

import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.ConverterHelper;

/**
 * @author Jerome RADUGET
 */
public class MainArgsConfigurationTest extends AbstractConfigurationTest {

   public MainArgsConfigurationTest() throws StoreException, URISyntaxException {
	super();
   }

   @Override
   protected Configuration newInstance() throws StoreException, URISyntaxException {

	final String[] mainArgs = new String[] { "application.name=app", "application.description=description&nbsp;of&nbsp;the&nbsp;application...",
		"application.date=2006-09-01T00:00:00", "application.version=1.0.0", "application.librairies=dom4j.jar|log4j.jar|mail.jar",
		"application.modules.sales.name=Sales", "application.modules.sales.version=1.1.0", "application.modules.marketing.name=Market.",
		"application.modules.netbusiness.name=", "application.array.bigdecimal=987.5|1.123456789", "application.array.boolean=false|true",
		"application.single.boolean=true", "application.single.bigdecimal=1.123456789",
		"application.array.date=2009-01-02T00:00:00|2009-12-31T00:00:00|2012-05-15T00:00:00" };

	RuntimeContext<Configuration> context = new ConfigurationContextBuilder().withMainArgsString(ConverterHelper.arrayToString(mainArgs, "#"))
		.withMainArgsSeparator("#").build();

	return new MainArgsConfiguration("mainArgsConfig", context);

   }

   @Test(expected = IllegalStateException.class)
   @Override
   public void store() throws StoreException {
	assertNotNull(configuration);
	configuration.store();
   }

   @Test
   public void isStorageAllowed() {
	assertNotNull(configuration);
	assertFalse(configuration.isStorageAllowed());
   }
}
