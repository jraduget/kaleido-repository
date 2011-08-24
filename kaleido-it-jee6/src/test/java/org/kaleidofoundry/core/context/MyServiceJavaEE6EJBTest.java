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

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.jee6.MyServiceRemoteBean;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.naming.NamingServiceFactory;
import org.kaleidofoundry.core.naming.NamingServiceNotFoundException;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class MyServiceJavaEE6EJBTest extends AbstractMyServiceTest {

   // the remote naming service use to lookup the remote ejb
   private NamingService myNamingService;
   // the remote ejb
   private MyServiceRemoteBean myRemoteJavaEE6Bean;
   // does profil is jee5 or jee6 ?
   private boolean jee6Profil = false;

   @Before
   public void setup() throws StoreException, Throwable {

	I18nMessagesFactory.disableJpaControl();

	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myNaming=classpath:/naming/myContext.properties
	ConfigurationFactory.provides("myNamingHost", "classpath:/naming/myContext.properties");

	try {
	   myNamingService = NamingServiceFactory.provides(new RuntimeContext<NamingService>("myNamingService"));
	   // lookup the remote ejb
	   myRemoteJavaEE6Bean = myNamingService.locate("ejb/MyServiceBean6", MyServiceRemoteBean.class);
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
   public void cleanupClass() throws StoreException {
	ConfigurationFactory.unregister("myNamingHost");
	I18nMessagesFactory.enableJpaControl();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceInjectionTest#getMyService()
    */
   @Override
   public MyService getMyService() {
	return myRemoteJavaEE6Bean;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.AbstractMyServiceTest#runtimeContextInjectionTest()
    */
   @Override
   public void runtimeContextInjectionTest() {
	if (!jee6Profil) { return; }
	super.runtimeContextInjectionTest();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.AbstractMyServiceTest#configurationInjectionTest()
    */
   @Override
   public void configurationInjectionTest() throws ParseException {
	if (!jee6Profil) { return; }
	super.configurationInjectionTest();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.AbstractMyServiceTest#cacheManagerInjectionTest()
    */
   @Override
   public void cacheManagerInjectionTest() {
	if (!jee6Profil) { return; }
	super.cacheManagerInjectionTest();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.AbstractMyServiceTest#cacheInjectionTest()
    */
   @Override
   public void cacheInjectionTest() {
	if (!jee6Profil) { return; }
	super.cacheInjectionTest();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.AbstractMyServiceTest#i18nMessagesInjectionTest()
    */
   @Override
   public void i18nMessagesInjectionTest() {
	if (!jee6Profil) { return; }
	super.i18nMessagesInjectionTest();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.AbstractMyServiceTest#namingServiceInjectionTest()
    */
   @Override
   public void namingServiceInjectionTest() {
	if (!jee6Profil) { return; }
	super.namingServiceInjectionTest();
   }

}
