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
package org.kaleidofoundry.core.store.module;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.FileStore;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Test contextual injection for fileStore Module
 * 
 * @author Jerome RADUGET
 */
public class ContextualResourceModuleTest extends Assert {

   private Injector injector;
   private SampleWithContext sampleWithContext;

   @Before
   public void setup() {
	// guice injector
	injector = Guice.createInjector(new org.kaleidofoundry.core.store.module.FileStoreModule());
	// test guice instance
	sampleWithContext = injector.getInstance(SampleWithContext.class);
   }

   // ** find fileStore using contextual information ***************************************************************

   @Test
   public void contextualInjectionByConstructor() {
	assertNotNull(sampleWithContext);

	// test constructor injection
	assertNotNull(sampleWithContext.getRuntimeContextInjectedByConstructor());
	assertEquals("constructor-injection-name", sampleWithContext.getRuntimeContextInjectedByConstructor().getName());

   }

   @Test
   public void contextualInjectionByField() {
	assertNotNull(sampleWithContext);

	// test field injection
	assertNotNull(sampleWithContext.getRuntimeContextInjectedByField());
	assertEquals("property-injection-name", sampleWithContext.getRuntimeContextInjectedByField().getName());

   }

   @Test
   public void contextualInjectionByMethod() {
	assertNotNull(sampleWithContext);

	// test method injection
	sampleWithContext.injectFileStoreByMethod(sampleWithContext.getRuntimeContextInjectedByMethod());
	assertEquals("method-injection-name", sampleWithContext.getRuntimeContextInjectedByMethod().getName());

   }

   @Test
   public void contextualInjectFileStoreByMethodArg() {
	assertNotNull(sampleWithContext);

	// test method injection
	sampleWithContext.injectFileStoreByMethodArg(sampleWithContext.getRuntimeContextInjectedByMethodArg());
	assertEquals("method-arg-injection-name", sampleWithContext.getRuntimeContextInjectedByMethodArg().getName());
   }

}

/**
 * @author Jerome RADUGET
 */
class SampleWithContext {

   @Context("property-injection-name")
   private RuntimeContext<FileStore> runtimeContextInjectedByField;

   private final RuntimeContext<FileStore> runtimeContextInjectedByConstructor;

   private RuntimeContext<FileStore> runtimeContextInjectedByMethod;
   private RuntimeContext<FileStore> runtimeContextInjectedByMethodArg;

   // contextual injection using guice aop methodInterceptor
   @Inject
   SampleWithContext(@Context("constructor-injection-name") final RuntimeContext<FileStore> runtimeContext) {
	runtimeContextInjectedByConstructor = runtimeContext;
   }

   @Inject
   @Context("method-injection-name")
   public void injectFileStoreByMethod(final RuntimeContext<FileStore> runtimeContext) {
	runtimeContextInjectedByMethod = runtimeContext;
   }

   @Inject
   public void injectFileStoreByMethodArg(@Context("method-arg-injection-name") final RuntimeContext<FileStore> runtimeContext) {
	runtimeContextInjectedByMethodArg = runtimeContext;
   }

   public RuntimeContext<FileStore> getRuntimeContextInjectedByField() {
	return runtimeContextInjectedByField;
   }

   public RuntimeContext<FileStore> getRuntimeContextInjectedByConstructor() {
	return runtimeContextInjectedByConstructor;
   }

   public RuntimeContext<FileStore> getRuntimeContextInjectedByMethod() {
	return runtimeContextInjectedByMethod;
   }

   public RuntimeContext<FileStore> getRuntimeContextInjectedByMethodArg() {
	return runtimeContextInjectedByMethodArg;
   }

}
