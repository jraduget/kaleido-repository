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
package org.kaleidofoundry.core.context;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationConstants;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.test.MyServiceInterface;
import org.kaleidofoundry.core.context.test.MyServiceWithFinalNullRuntimeContext;
import org.kaleidofoundry.core.context.test.MyServiceWithFinalRuntimeContext;
import org.kaleidofoundry.core.context.test.MyServiceWithIllegalRuntimeContext;
import org.kaleidofoundry.core.context.test.MyServiceWithRuntimeContext;
import org.kaleidofoundry.core.context.test.MyServiceWithStaticFinalRuntimeContext;
import org.kaleidofoundry.core.context.test.MyServiceWithStaticRuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Test {@link RuntimeContext} injection using @{@link InjectContext}
 * 
 * @author Jerome RADUGET
 */
public class InjectAgregatedContextAspectTest extends Assert {

   // **** RuntimeContext is an instance variable of the following fields
   @InjectContext("myContext")
   protected MyServiceInterface myServiceWithRuntimeContext;
   @InjectContext("myContext")
   protected MyServiceInterface myServiceWithFinalRuntimeContext;
   @InjectContext("myContext")
   protected MyServiceInterface myServiceWithFinalNullRuntimeContext;
   @InjectContext("myContext")
   protected MyServiceWithIllegalRuntimeContext myServiceWithIllegalRuntimeContext;

   // **** RuntimeContext is an static variable of the following fields
   @InjectContext("myContext")
   private MyServiceInterface myServiceWithStaticRuntimeContext;
   @InjectContext("myContext")
   private MyServiceInterface myServiceWithStaticFinalRuntimeContext;

   // **** RuntimeContext is an instance variable of the following fields, using only specific configurations
   @InjectContext(value = "myContext", configurations = { "myConf" })
   protected MyServiceInterface myServiceWithRuntimeContextFromSpecificConf;
   @InjectContext(value = "myContext", configurations = { "illegalConf" })
   protected MyServiceInterface myServiceWithRuntimeContextFromIllegalConf;
   @InjectContext(value = "myContext", configurations = { "anotherConf" })
   protected MyServiceInterface myServiceWithRuntimeContextFromWrongConf;

   @Before
   public void setup() throws StoreException {
	// Disable i18n jpa message bundle control
	I18nMessagesFactory.disableJpaControl();
	// Register configurations used for testing
	System
		.getProperties()
		.put(ConfigurationConstants.JavaEnvProperties,
			"myConf=classpath:/org/kaleidofoundry/core/context/application.properties;anotherConf=classpath:/org/kaleidofoundry/core/context/module.properties");
	ConfigurationFactory.initConfigurations();

	// Services to test
	myServiceWithRuntimeContext = new MyServiceWithRuntimeContext();
	myServiceWithFinalRuntimeContext = new MyServiceWithFinalRuntimeContext();
	myServiceWithFinalNullRuntimeContext = new MyServiceWithFinalNullRuntimeContext();
	myServiceWithIllegalRuntimeContext = new MyServiceWithIllegalRuntimeContext();
	myServiceWithStaticRuntimeContext = new MyServiceWithStaticRuntimeContext();
	myServiceWithStaticFinalRuntimeContext = new MyServiceWithStaticFinalRuntimeContext();
	myServiceWithRuntimeContextFromSpecificConf = new MyServiceWithRuntimeContext();
	myServiceWithRuntimeContextFromIllegalConf = new MyServiceWithRuntimeContext();
	myServiceWithRuntimeContextFromWrongConf = new MyServiceWithRuntimeContext();
   }

   @After
   public void cleanup() throws StoreException {
	// Remove all configurations
	ConfigurationFactory.destroyConfigurations();
	// Remove configurations env variables used for testing
	System.getProperties().remove(ConfigurationConstants.JavaEnvProperties);
	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void testInjection() {
	legalAssertions(myServiceWithRuntimeContext);
   }

   @Test
   public void testFinalInjection() {
	legalAssertions(myServiceWithFinalRuntimeContext);
   }

   @Test
   public void testStaticInjection() {
	legalAssertions(myServiceWithStaticRuntimeContext);
   }

   @Test
   public void testStaticFinalInjection() {
	legalAssertions(myServiceWithStaticFinalRuntimeContext);
   }

   @Test
   public void testFinalNullInjection() {
	try {
	   myServiceWithFinalNullRuntimeContext.getContext();
	   fail("RuntimeContextException expected");
	} catch (final RuntimeContextException rce) {
	   assertEquals("context.annotation.illegalfield", rce.getCode());
	}
   }

   @Test
   public void testIllegalInjection() {
	try {
	   myServiceWithIllegalRuntimeContext.getCalendar();
	   fail("RuntimeContextException expected");
	} catch (final RuntimeContextException rce) {
	   assertEquals("context.annotation.illegaluse.noRuntimeContextField", rce.getCode());
	}
   }

   @Test
   public void testInjectionFromLegalConf() {
	legalAssertions(myServiceWithRuntimeContextFromSpecificConf);
   }

   @Test
   public void testInjectionFromIllegalConf() {
	try {
	   myServiceWithRuntimeContextFromIllegalConf.getContext();
	   fail("RuntimeContextException expected");
	} catch (final RuntimeContextException rce) {
	   assertEquals("context.annotation.illegalconfig", rce.getCode());
	}
   }

   @Test
   public void testInjectionFromWrongConf() {
	final RuntimeContext<MyServiceInterface> context = myServiceWithRuntimeContextFromWrongConf.getContext();
	assertNotNull(context);
	assertNull(context.getProperty("readonly"));
	assertNull(context.getProperty("cache"));
	assertEquals("http://host/module/foo.wsdl", context.getProperty("webservice.url"));
   }

   private void legalAssertions(final MyServiceInterface myServiceInterface) {
	final RuntimeContext<MyServiceInterface> context = myServiceInterface.getContext();
	assertNotNull(context);
	assertEquals("true", context.getProperty("readonly")); // myService.myContext.readonly=true
	assertEquals("kaleido-local", context.getProperty("cache")); // myService.myContext.cache=kaleido-local
	assertSame(context, myServiceInterface.getContext());
   }
}
