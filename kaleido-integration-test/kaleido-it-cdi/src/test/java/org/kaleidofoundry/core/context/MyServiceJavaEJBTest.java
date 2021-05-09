/*
 *  Copyright 2008-2021 the original author or authors.
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.jee6.MyServiceRemoteBean;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.naming.NamingServiceFactory;
import org.kaleidofoundry.core.naming.NamingServiceNotFoundException;
import org.kaleidofoundry.core.store.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
public class MyServiceJavaEJBTest implements MyServiceTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(MyServiceJavaEJBTest.class);

   // the remote naming service use to lookup the remote ejb
   private NamingService myNamingService;
   // the remote ejb
   private MyServiceRemoteBean myRemoteJavaEE6Bean;
   // does profil is jee5 or jee6 ?
   private boolean jee6Profil = false;

   @Before
   public void setup() throws ResourceException, Throwable {

	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myNaming=classpath:/naming/myContext.properties
	ConfigurationFactory.provides("myNamingHost", "classpath:/naming/myContext.properties");

	try {
	   myNamingService = NamingServiceFactory.provides(new RuntimeContext<NamingService>("myNamingService"));
	   // lookup the remote ejb
	   myRemoteJavaEE6Bean = myNamingService.locate("ejb/MyServiceBean", MyServiceRemoteBean.class);
	   jee6Profil = true;
	} catch (NamingServiceNotFoundException nsnf) {
	   jee6Profil = false;
	   nsnf.printStackTrace();
	} catch (Throwable th) {
	   jee6Profil = true;
	   th.printStackTrace();
	   throw th;
	}
   }

   @After
   public void cleanupClass() throws ResourceException {
	ConfigurationFactory.unregister("myNamingHost");
   }

   @Test
   public void runtimeContextInjectionTest() {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.runtimeContextInjectionAssertions();
	}
   }

   @Test
   public void storeInjectionTest() throws ResourceException {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.storeInjectionAssertions();
	}
   }

   @Test
   public void configurationInjectionTest() throws ParseException {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.configurationInjectionAssertions();
	}
   }

   @Test
   public void cacheManagerInjectionTest() {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.cacheManagerInjectionAssertions();
	}
   }

   @Test
   public void cacheInjectionTest() {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.cacheInjectionAssertions();
	}

   }

   @Test
   public void i18nMessagesInjectionTest() {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.i18nMessagesInjectionAssertions();
	}
   }

   @Test
   public void namingServiceInjectionTest() {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.namingServiceInjectionAssertions();
	}
   }

   @Test
   public void entityManagerFactoryInjectionTest() {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.entityManagerFactoryInjectionAssertions();
	}
   }

   @Test
   public void entityManagerInjectionTest() {
	if (isJavaEE6Profil()) {
	   myRemoteJavaEE6Bean.entityManagerInjectionAssertions();
	}
   }

   private boolean isJavaEE6Profil() {
	if (!jee6Profil) {
	   LOGGER.info("JavaEE 6 profile is not enable, skip test");
	}
	return jee6Profil;
   }
}
