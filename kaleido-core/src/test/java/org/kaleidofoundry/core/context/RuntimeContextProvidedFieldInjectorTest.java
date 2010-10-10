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
import org.kaleidofoundry.core.context.service_with_provider.MyServiceInterface;
import org.kaleidofoundry.core.context.service_with_provider.MyServiceWithFinalRuntimeContext;
import org.kaleidofoundry.core.context.service_with_provider.MyServiceWithRuntimeContext;
import org.kaleidofoundry.core.context.service_with_provider.MyServiceWithStaticFinalRuntimeContext;
import org.kaleidofoundry.core.context.service_with_provider.MyServiceWithStaticRuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * Test {@link RuntimeContext} injection using @{@link Context} <br/>
 * A provider is defined on {@link MyServiceInterface}, so the services instance have no need to be instantiate into {@link #setup()} <br/>
 * Provider is tested here
 * 
 * @author Jerome RADUGET
 */
public class RuntimeContextProvidedFieldInjectorTest extends Assert {

   // **** RuntimeContext is an instance variable of the following fields
   @Context("myProvidedContext")
   protected MyServiceInterface myServiceWithRuntimeContext;
   @Context("myProvidedFinalContext")
   protected MyServiceInterface myServiceWithFinalRuntimeContext;
   @Context("myProvidedFinalNullContext")
   protected MyServiceInterface myServiceWithFinalNullRuntimeContext;

   @Context("myProvidedContext")
   protected MyServiceInterface myNullServiceWithRuntimeContext;

   // **** RuntimeContext is an static variable of the following fields
   @Context("myProvidedStaticContext")
   private MyServiceInterface myServiceWithStaticRuntimeContext;
   @Context("myProvidedStaticFinalContext")
   private MyServiceInterface myServiceWithStaticFinalRuntimeContext;

   // **** RuntimeContext is an instance variable of the following fields, using only specific configurations
   @Context(value = "myProvidedContext", configurations = { "myConf" })
   protected MyServiceInterface myServiceWithRuntimeContextFromSpecificConf;
   @Context(value = "myProvidedContext", configurations = { "illegalConf" })
   protected MyServiceInterface myServiceWithRuntimeContextFromIllegalConf;
   @Context(value = "myProvidedContext", configurations = { "anotherConf" })
   protected MyServiceInterface myServiceWithRuntimeContextFromWrongConf;

   @Before
   public void setup() throws ResourceException {
	// Disable i18n jpa message bundle control
	I18nMessagesFactory.disableJpaControl();
	// Register configurations used for testing
	ConfigurationFactory.provides("myConf", "classpath:/context/application.properties");
	ConfigurationFactory.provides("anotherConf", "classpath:/context/module.properties");
   }

   @After
   public void cleanup() throws ResourceException {
	// Remove all configurations
	ConfigurationFactory.destroy("myConf");
	ConfigurationFactory.destroy("anotherConf");
	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void testContextInjection() {

	MyServiceInterface initialReference = myServiceWithRuntimeContext;
	assertNotNull(initialReference);
	assertTrue(initialReference instanceof MyServiceWithRuntimeContext);
	assertSame(initialReference, myServiceWithRuntimeContext);

	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	assertNotNull(context);
	assertSame(context, myServiceWithRuntimeContext.getContext());
	assertEquals("myProvidedContext", context.getName());
	assertEquals("true", context.getProperty("readonly"));
	assertEquals("kaleido-local", context.getProperty("cache"));
	assertEquals("myProvidedService.with.context", context.getProperty("pluginCode"));
   }

   @Test
   public void testFinalContextInjection() {

	MyServiceInterface initialReference = myServiceWithFinalRuntimeContext;
	assertNotNull(initialReference);
	assertTrue(initialReference instanceof MyServiceWithFinalRuntimeContext);
	assertSame(initialReference, myServiceWithFinalRuntimeContext);

	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	assertNotNull(context);
	assertSame(context, myServiceWithFinalRuntimeContext.getContext());
	assertEquals("myProvidedFinalContext", context.getName());
	assertEquals("true", context.getProperty("readonly"));
	assertEquals("kaleido-local", context.getProperty("cache"));
	assertEquals("myProvidedService.with.final.context", context.getProperty("pluginCode"));
   }

   @Test
   public void testStaticContextInjection() {

	MyServiceInterface initialReference = myServiceWithStaticRuntimeContext;
	assertNotNull(initialReference);
	assertTrue(initialReference instanceof MyServiceWithStaticRuntimeContext);
	assertSame(initialReference, myServiceWithStaticRuntimeContext);

	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	assertNotNull(context);
	assertSame(context, myServiceWithStaticRuntimeContext.getContext());
	assertEquals("myProvidedStaticContext", context.getName());
	assertEquals("true", context.getProperty("readonly"));
	assertEquals("kaleido-local", context.getProperty("cache"));
	assertEquals("myProvidedService.with.static.context", context.getProperty("pluginCode"));
   }

   @Test
   public void testStaticFinalContextInjection() {

	MyServiceInterface initialReference = myServiceWithStaticFinalRuntimeContext;
	assertNotNull(initialReference);
	assertTrue(initialReference instanceof MyServiceWithStaticFinalRuntimeContext);
	assertSame(initialReference, myServiceWithStaticFinalRuntimeContext);

	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	assertNotNull(context);
	assertSame(context, myServiceWithStaticFinalRuntimeContext.getContext());
	assertEquals("myProvidedStaticFinalContext", context.getName());
	assertEquals("true", context.getProperty("readonly"));
	assertEquals("kaleido-local", context.getProperty("cache"));
	assertEquals("myProvidedService.with.staticfinal.context", context.getProperty("pluginCode"));
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
   public void testContextInjectionFromLegalConf() {
	MyServiceInterface initialReference = myServiceWithRuntimeContextFromSpecificConf;
	assertNotNull(initialReference);
	assertTrue(initialReference instanceof MyServiceWithRuntimeContext);
	assertSame(initialReference, myServiceWithRuntimeContextFromSpecificConf);

	RuntimeContext<MyServiceInterface> context = initialReference.getContext();
	assertNotNull(context);
	assertSame(context, initialReference.getContext());
	assertEquals("myProvidedContext", context.getName());
	assertEquals("true", context.getProperty("readonly"));
	assertEquals("kaleido-local", context.getProperty("cache"));
	assertEquals("myProvidedService.with.context", context.getProperty("pluginCode"));

   }

   @Test
   public void testContextInjectionFromIllegalConf() {
	try {
	   MyServiceInterface initialReference = myServiceWithRuntimeContextFromIllegalConf;
	   fail("RuntimeContextException expected");
	   initialReference.getContext();
	} catch (final RuntimeContextException rce) {
	   assertEquals("context.annotation.illegalconfig.simple", rce.getCode());
	}
   }

   @Test
   public void testContextInjectionFromWrongConf() {
	try {
	   MyServiceInterface initialReference = myServiceWithRuntimeContextFromWrongConf;
	   fail("IllegalStateException expected (see MyServiceProvider)");
	   initialReference.getContext();
	} catch (final RuntimeContextEmptyParameterException rcpe) {
	   assertEquals("context.parameter.empty", rcpe.getCode());
	   assertEquals("pluginCode", rcpe.getParameter());
	}

   }

}
