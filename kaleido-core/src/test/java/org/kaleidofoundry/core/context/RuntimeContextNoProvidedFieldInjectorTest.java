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
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.service_with_no_provider.MyServiceInterface;
import org.kaleidofoundry.core.context.service_with_no_provider.MyServiceWithFinalNullRuntimeContext;
import org.kaleidofoundry.core.context.service_with_no_provider.MyServiceWithFinalRuntimeContext;
import org.kaleidofoundry.core.context.service_with_no_provider.MyServiceWithIllegalRuntimeContext;
import org.kaleidofoundry.core.context.service_with_no_provider.MyServiceWithRuntimeContext;
import org.kaleidofoundry.core.context.service_with_no_provider.MyServiceWithStaticFinalRuntimeContext;
import org.kaleidofoundry.core.context.service_with_no_provider.MyServiceWithStaticRuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Test {@link RuntimeContext} injection using @{@link Context} <br/>
 * No provider is defined on {@link MyServiceInterface}, so the service have to be instantiate into {@link #setup()} <br/>
 * Provider is not tested here
 * 
 * @author Jerome RADUGET
 */
public class RuntimeContextNoProvidedFieldInjectorTest extends Assert {

   // **** RuntimeContext is an instance variable of the following fields
   @Context("myContext")
   private MyServiceInterface myServiceWithRuntimeContext;
   @Context("myContext")
   private MyServiceInterface myServiceWithFinalRuntimeContext;
   @Context("myContext")
   private MyServiceInterface myServiceWithFinalNullRuntimeContext;
   @Context("myContext")
   private MyServiceWithIllegalRuntimeContext myServiceWithIllegalRuntimeContext;

   // **** RuntimeContext is an static variable of the following fields
   @Context("myContext")
   private MyServiceInterface myServiceWithStaticRuntimeContext;
   @Context("myContext")
   private MyServiceInterface myServiceWithStaticFinalRuntimeContext;

   // **** RuntimeContext is an instance variable of the following fields, using only specific configurations
   @Context(value = "myContext", configurations = { "myConf" })
   private MyServiceInterface myServiceWithRuntimeContextFromSpecificConf;
   @Context(value = "myContext", configurations = { "illegalConf" })
   private MyServiceInterface myServiceWithRuntimeContextFromIllegalConf;
   @Context(value = "myContext", configurations = { "anotherConf" })
   private MyServiceInterface myServiceWithRuntimeContextFromWrongConf;

   @Before
   public void setup() throws StoreException {
	// Disable i18n jpa message bundle control
	I18nMessagesFactory.disableJpaControl();
	// Register configurations used for testing
	ConfigurationFactory.provides("myConf", "classpath:/context/application.properties");
	ConfigurationFactory.provides("anotherConf", "classpath:/context/module.properties");
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
	ConfigurationFactory.destroy("myConf");
	ConfigurationFactory.destroy("anotherConf");
	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void testContextInjection() {
	MyServiceInterface initialReference = myServiceWithRuntimeContext;
	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	commonsAssertions(initialReference);
	assertSame(initialReference, myServiceWithRuntimeContext);
	assertSame(context, myServiceWithRuntimeContext.getContext());
   }

   @Test
   public void testFinalContextInjection() {
	MyServiceInterface initialReference = myServiceWithFinalRuntimeContext;
	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	commonsAssertions(initialReference);
	assertSame(initialReference, myServiceWithFinalRuntimeContext);
	assertSame(context, myServiceWithFinalRuntimeContext.getContext());
   }

   @Test
   public void testStaticContextInjection() {
	MyServiceInterface initialReference = myServiceWithStaticRuntimeContext;
	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	commonsAssertions(initialReference);
	assertSame(initialReference, myServiceWithStaticRuntimeContext);
	assertSame(context, myServiceWithStaticRuntimeContext.getContext());
   }

   @Test
   public void testStaticFinalContextInjection() {
	MyServiceInterface initialReference = myServiceWithStaticFinalRuntimeContext;
	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	commonsAssertions(initialReference);
	assertSame(initialReference, myServiceWithStaticFinalRuntimeContext);
	assertSame(context, myServiceWithStaticFinalRuntimeContext.getContext());
   }

   @Test
   public void testFinalNullContextInjection() {
	try {
	   myServiceWithFinalNullRuntimeContext.getContext();
	   fail("RuntimeContextException expected");
	} catch (final RuntimeContextException rce) {
	   assertEquals("context.annotation.illegalfield", rce.getCode());
	}
   }

   @Test
   public void testIllegalContextInjection() {
	try {
	   myServiceWithIllegalRuntimeContext.getCalendar();
	   fail("RuntimeContextException expected");
	} catch (final RuntimeContextException rce) {
	   assertEquals("context.annotation.illegaluse.noRuntimeContextField", rce.getCode());
	}
   }

   @Test
   public void testContextInjectionFromLegalConf() {
	MyServiceInterface initialReference = myServiceWithRuntimeContextFromSpecificConf;
	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	commonsAssertions(initialReference);
	assertSame(initialReference, myServiceWithRuntimeContextFromSpecificConf);
	assertSame(context, myServiceWithRuntimeContextFromSpecificConf.getContext());
   }

   @Test
   public void testContextInjectionFromIllegalConf() {
	try {
	   myServiceWithRuntimeContextFromIllegalConf.getContext();
	   fail("RuntimeContextException expected");
	} catch (final RuntimeContextException rce) {
	   assertEquals("context.annotation.illegalconfig.simple", rce.getCode());
	}
   }

   @Test
   public void testContextInjectionFromWrongConf() {
	final RuntimeContext<MyServiceInterface> context = myServiceWithRuntimeContextFromWrongConf.getContext();
	assertNotNull(context);
	assertNull(context.getProperty("readonly"));
	assertNull(context.getProperty("cache"));
	assertEquals("http://host/module/foo.wsdl", context.getProperty("webservice.url"));
   }

   private void commonsAssertions(final MyServiceInterface myServiceInterface) {
	final RuntimeContext<MyServiceInterface> context = myServiceInterface.getContext();
	assertNotNull(context);
	assertEquals("myContext", context.getName());
	assertEquals("true", context.getProperty("readonly")); // myService.myContext.readonly=true
	assertEquals("kaleido-local", context.getProperty("cache")); // myService.myContext.cache=kaleido-local
	assertSame(context, myServiceInterface.getContext());
   }
}
