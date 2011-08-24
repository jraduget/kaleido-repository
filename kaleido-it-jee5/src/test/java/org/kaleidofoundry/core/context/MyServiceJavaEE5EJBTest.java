package org.kaleidofoundry.core.context;

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

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.jee5.MyServiceRemoteBean;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.naming.NamingServiceFactory;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class MyServiceJavaEE5EJBTest implements MyServiceTest {

   // the remote naming service use to lookup the remote ejb
   private NamingService myNamingService;
   // the remote ejb
   private MyServiceRemoteBean myRemoteJavaEE5Bean;

   @Before
   public void setup() throws StoreException, Throwable {

	I18nMessagesFactory.disableJpaControl();

	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myNaming=classpath:/naming/myContext.properties
	ConfigurationFactory.provides("myNamingHost", "classpath:/naming/myContext.properties");

	myNamingService = NamingServiceFactory.provides(new RuntimeContext<NamingService>("myNamingService"));
	// lookup the remote ejb
	myRemoteJavaEE5Bean = myNamingService.locate("ejb/MyServiceBean5", MyServiceRemoteBean.class);

   }

   @After
   public void cleanupClass() throws StoreException {
	ConfigurationFactory.unregister("myNamingHost");
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void runtimeContextInjectionTest() {
	myRemoteJavaEE5Bean.runtimeContextInjectionAssertions();

   }

   @Test
   public void configurationInjectionTest() throws ParseException {
	myRemoteJavaEE5Bean.configurationInjectionAssertions();
   }

   @Test
   public void cacheManagerInjectionTest() {
	myRemoteJavaEE5Bean.cacheManagerInjectionAssertions();
   }

   @Test
   public void cacheInjectionTest() {
	myRemoteJavaEE5Bean.cacheInjectionAssertions();

   }

   @Test
   public void i18nMessagesInjectionTest() {
	myRemoteJavaEE5Bean.i18nMessagesInjectionAssertions();
   }

   @Test
   public void namingServiceInjectionTest() {
	myRemoteJavaEE5Bean.namingServiceInjectionAssertions();
   }

}
