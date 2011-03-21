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
package org.kaleidofoundry.core;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.store.GuiceClasspathFileStore;
import org.kaleidofoundry.core.store.GuiceFtpStore;
import org.kaleidofoundry.core.store.GuiceHttpFileStore;
import org.kaleidofoundry.core.store.FileStore;
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
@Review(comment = "provider , bindConstant")
public class GuiceSample extends Assert {

   private Injector injector;
   private FileStoreSample fileStoreSample;

   @Before
   public void setup() {
	injector = Guice.createInjector(new FileStoreModule());
	fileStoreSample = injector.getInstance(FileStoreSample.class);
   }

   @Test
   public void testClassicInjection() {
	assertNotNull(fileStoreSample);
	assertNotNull(fileStoreSample.getFileStore());
	assertEquals(GuiceClasspathFileStore.class, fileStoreSample.getFileStore().getClass());
   }

   @Test
   public void testNamedInjection() {
	assertNotNull(fileStoreSample);
	assertNotNull(fileStoreSample.getFtpStore());
	assertEquals(GuiceFtpStore.class, fileStoreSample.getFtpStore().getClass());
   }

   @Test
   public void testSingleton() {
	assertNotNull(fileStoreSample);
	assertNotNull(fileStoreSample.getFileStore());
	assertEquals(GuiceClasspathFileStore.class, fileStoreSample.getFileStore().getClass());
	// singleton test - will be true only if it is same injector instance that create the new instance
	final FileStoreSample newOne = injector.getInstance(FileStoreSample.class);
	assertSame(fileStoreSample.getFileStore(), newOne.getFileStore());
   }

   @Test
   public void testGenericInjection() {
	assertNotNull(fileStoreSample);
	assertNotNull(fileStoreSample.getServiceSample());
	assertEquals(ServiceStringSample.class, fileStoreSample.getServiceSample().getClass());
   }
}

/**
 * guice ioc module configuration
 * 
 * @author Jerome RADUGET
 */
class FileStoreModule extends AbstractModule {
   @Override
   public void configure() {
	// bind classic interface to implementation
	bind(FileStore.class).to(GuiceClasspathFileStore.class).in(Scopes.SINGLETON);
	bind(FileStore.class).annotatedWith(Http.class).to(GuiceHttpFileStore.class).in(Scopes.SINGLETON);

	// bind generic interface to implementation
	bind(new TypeLiteral<ServiceSample<String>>() {
	}).to(ServiceStringSample.class);

	// bind interface to implementation by name
	bind(FileStore.class).annotatedWith(Names.named("ftp")).to(GuiceFtpStore.class);

   }
}

/**
 * sample user class
 * 
 * @author Jerome RADUGET
 */
class FileStoreSample {

   private final FileStore fileStore;
   private final FileStore ftpStore;
   private final ServiceSample<String> serviceSample;

   @Inject
   public FileStoreSample(final FileStore fileStore, @Named("ftp") final FileStore ftpStore, final ServiceSample<String> serviceSample) {
	this.fileStore = fileStore;
	this.serviceSample = serviceSample;
	this.ftpStore = ftpStore;
   }

   public FileStore getFileStore() {
	return fileStore;
   }

   public FileStore getFtpStore() {
	return ftpStore;
   }

   public ServiceSample<String> getServiceSample() {
	return serviceSample;
   }

}

interface ServiceSample<T> {
}

class ServiceStringSample implements ServiceSample<String> {
}