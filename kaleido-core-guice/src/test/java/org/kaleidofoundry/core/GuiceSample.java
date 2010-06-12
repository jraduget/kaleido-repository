package org.kaleidofoundry.core;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.store.GuiceClasspathResourceStore;
import org.kaleidofoundry.core.store.GuiceFtpResourceStore;
import org.kaleidofoundry.core.store.GuiceHttpResourceStore;
import org.kaleidofoundry.core.store.ResourceStore;
import org.kaleidofoundry.core.store.annotation.Http;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * @author Jerome RADUGET
 */
// TODO : provider , bindConstant
public class GuiceSample extends Assert {

   private Injector injector;
   private ResourceStoreSample resourceStoreSample;

   @Before
   public void setup() {
	injector = Guice.createInjector(new ResourceStoreModule());
	resourceStoreSample = injector.getInstance(ResourceStoreSample.class);
   }

   @Test
   public void testClassicInjection() {
	assertNotNull(resourceStoreSample);
	assertNotNull(resourceStoreSample.getResourceStore());
	assertEquals(GuiceClasspathResourceStore.class, resourceStoreSample.getResourceStore().getClass());
   }

   @Test
   public void testNamedInjection() {
	assertNotNull(resourceStoreSample);
	assertNotNull(resourceStoreSample.getFtpResourceStore());
	assertEquals(GuiceFtpResourceStore.class, resourceStoreSample.getFtpResourceStore().getClass());
   }

   @Test
   public void testSingleton() {
	assertNotNull(resourceStoreSample);
	assertNotNull(resourceStoreSample.getResourceStore());
	assertEquals(GuiceClasspathResourceStore.class, resourceStoreSample.getResourceStore().getClass());
	// singleton test - will be true only if it is same injector instance that create the new instance
	final ResourceStoreSample newOne = injector.getInstance(ResourceStoreSample.class);
	assertSame(resourceStoreSample.getResourceStore(), newOne.getResourceStore());
   }

   @Test
   public void testGenericInjection() {
	assertNotNull(resourceStoreSample);
	assertNotNull(resourceStoreSample.getServiceSample());
	assertEquals(ServiceStringSample.class, resourceStoreSample.getServiceSample().getClass());
   }
}

/**
 * guice ioc module configuration
 * 
 * @author Jerome RADUGET
 */
class ResourceStoreModule extends AbstractModule {
   @Override
   public void configure() {
	// bind classic interface to implementation
	bind(ResourceStore.class).to(GuiceClasspathResourceStore.class).in(Scopes.SINGLETON);
	bind(ResourceStore.class).annotatedWith(Http.class).to(GuiceHttpResourceStore.class).in(Scopes.SINGLETON);

	// bind generic interface to implementation
	bind(new TypeLiteral<ServiceSample<String>>() {
	}).to(ServiceStringSample.class);

	// bind interface to implementation by name
	bind(ResourceStore.class).annotatedWith(Names.named("ftp")).to(GuiceFtpResourceStore.class);

   }
}

/**
 * sample user class
 * 
 * @author Jerome RADUGET
 */
class ResourceStoreSample {

   private final ResourceStore resourceStore;
   private final ResourceStore ftpResourceStore;
   private final ServiceSample<String> serviceSample;

   @Inject
   public ResourceStoreSample(final ResourceStore resourceStore, @Named("ftp") final ResourceStore ftpResourceStore, final ServiceSample<String> serviceSample) {
	this.resourceStore = resourceStore;
	this.serviceSample = serviceSample;
	this.ftpResourceStore = ftpResourceStore;
   }

   public ResourceStore getResourceStore() {
	return resourceStore;
   }

   public ResourceStore getFtpResourceStore() {
	return ftpResourceStore;
   }

   public ServiceSample<String> getServiceSample() {
	return serviceSample;
   }

}

interface ServiceSample<T> {
}

class ServiceStringSample implements ServiceSample<String> {
}