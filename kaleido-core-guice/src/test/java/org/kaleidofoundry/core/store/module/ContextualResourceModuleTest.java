package org.kaleidofoundry.core.store.module;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.context.annotation.Context;
import org.kaleidofoundry.core.store.ResourceStore;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Test contextual injection for resourceStore Module
 * 
 * @author Jerome RADUGET
 */
public class ContextualResourceModuleTest extends Assert {

   private Injector injector;
   private SampleWithContext sampleWithContext;

   @Before
   public void setup() {
	// guice injector
	injector = Guice.createInjector(new org.kaleidofoundry.core.store.module.ResourceStoreModule());
	// test guice instance
	sampleWithContext = injector.getInstance(SampleWithContext.class);
   }

   // ** find resourceStore using contextual information ***************************************************************

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
	sampleWithContext.injectResourceStoreByMethod(sampleWithContext.getRuntimeContextInjectedByMethod());
	assertEquals("method-injection-name", sampleWithContext.getRuntimeContextInjectedByMethod().getName());

   }

   @Test
   public void contextualInjectResourceStoreByMethodArg() {
	assertNotNull(sampleWithContext);

	// test method injection
	sampleWithContext.injectResourceStoreByMethodArg(sampleWithContext.getRuntimeContextInjectedByMethodArg());
	assertEquals("method-arg-injection-name", sampleWithContext.getRuntimeContextInjectedByMethodArg().getName());
   }

}

/**
 * @author Jerome RADUGET
 */
class SampleWithContext {

   @Context(name = "property-injection-name")
   private RuntimeContext<ResourceStore> runtimeContextInjectedByField;

   private final RuntimeContext<ResourceStore> runtimeContextInjectedByConstructor;

   private RuntimeContext<ResourceStore> runtimeContextInjectedByMethod;
   private RuntimeContext<ResourceStore> runtimeContextInjectedByMethodArg;

   // contextual injection using guice aop methodInterceptor
   @Inject
   SampleWithContext(@Context(name = "constructor-injection-name") final RuntimeContext<ResourceStore> runtimeContext) {
	runtimeContextInjectedByConstructor = runtimeContext;
   }

   @Inject
   @Context(name = "method-injection-name")
   public void injectResourceStoreByMethod(final RuntimeContext<ResourceStore> runtimeContext) {
	runtimeContextInjectedByMethod = runtimeContext;
   }

   @Inject
   public void injectResourceStoreByMethodArg(
	   @Context(name = "method-arg-injection-name") final RuntimeContext<ResourceStore> runtimeContext) {
	runtimeContextInjectedByMethodArg = runtimeContext;
   }

   public RuntimeContext<ResourceStore> getRuntimeContextInjectedByField() {
	return runtimeContextInjectedByField;
   }

   public RuntimeContext<ResourceStore> getRuntimeContextInjectedByConstructor() {
	return runtimeContextInjectedByConstructor;
   }

   public RuntimeContext<ResourceStore> getRuntimeContextInjectedByMethod() {
	return runtimeContextInjectedByMethod;
   }

   public RuntimeContext<ResourceStore> getRuntimeContextInjectedByMethodArg() {
	return runtimeContextInjectedByMethodArg;
   }

}
