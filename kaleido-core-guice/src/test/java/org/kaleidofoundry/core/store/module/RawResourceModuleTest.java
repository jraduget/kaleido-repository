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
import org.kaleidofoundry.core.store.ClasspathResourceStore;
import org.kaleidofoundry.core.store.FileSystemResourceStore;
import org.kaleidofoundry.core.store.FtpResourceStore;
import org.kaleidofoundry.core.store.HttpResourceStore;
import org.kaleidofoundry.core.store.ResourceStore;
import org.kaleidofoundry.core.store.ResourceStoreConstants;
import org.kaleidofoundry.core.store.annotation.Classpath;
import org.kaleidofoundry.core.store.annotation.File;
import org.kaleidofoundry.core.store.annotation.Ftp;
import org.kaleidofoundry.core.store.annotation.Http;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * @author Jerome RADUGET
 */
public class RawResourceModuleTest extends Assert {

   private Injector injector;
   private Sample sample;

   @Before
   public void setup() {
	// guice injector
	injector = Guice.createInjector(new org.kaleidofoundry.core.store.module.ResourceStoreModule());
	// test guice instance
	sample = injector.getInstance(Sample.class);
   }

   @Test
   public void defaultResourceStore() {
	assertNotNull(sample);
	assertNotNull(sample.defaultResourceStore);
	assertTrue(sample.defaultResourceStore instanceof FileSystemResourceStore);
   }

   // ** find resourceStore by binding annotation **********************************************************************
   @Test
   public void fileResourceStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.fileSystemResourceStore);
	assertTrue(sample.fileSystemResourceStore instanceof FileSystemResourceStore);
   }

   @Test
   public void ftpResourceStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.ftpResourceStore);
	assertTrue(sample.ftpResourceStore instanceof FtpResourceStore);
   }

   @Test
   public void httpResourceStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.httpResourceStore);
	assertTrue(sample.httpResourceStore instanceof HttpResourceStore);
   }

   @Test
   public void classpathResourceStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.classpathResourceStore);
	assertTrue(sample.classpathResourceStore instanceof ClasspathResourceStore);
   }

   // ** find resourceStore by qualifier name **************************************************************************

   @Test
   public void fileResourceStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.fileSystemResourceStore);
	assertTrue(sample.fileSystemResourceStore instanceof FileSystemResourceStore);
   }

   @Test
   public void ftpResourceStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.ftpNamedResourceStore);
	assertTrue(sample.ftpNamedResourceStore instanceof FtpResourceStore);
   }

   @Test
   public void httpResourceStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.httpNamedResourceStore);
	assertTrue(sample.httpNamedResourceStore instanceof HttpResourceStore);
   }

   @Test
   public void classpathResourceStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.classpathNamedResourceStore);
	assertTrue(sample.classpathNamedResourceStore instanceof ClasspathResourceStore);
   }

}

/**
 * sample class to test injection without any {@link RuntimeContext} / {@link Context}
 */
class Sample {

   // default resource store
   @Inject
   ResourceStore defaultResourceStore;

   // injection using custom binding guice annotation
   @Inject
   @Ftp
   ResourceStore ftpResourceStore;
   @Inject
   @Http
   ResourceStore httpResourceStore;
   @Inject
   @File
   ResourceStore fileSystemResourceStore;
   @Inject
   @Classpath
   ResourceStore classpathResourceStore;

   // injection using custom binding guice named
   @Inject
   @Named(ResourceStoreConstants.FtpStorePluginName)
   ResourceStore ftpNamedResourceStore;
   @Inject
   @Named(ResourceStoreConstants.HttpStorePluginName)
   ResourceStore httpNamedResourceStore;
   @Inject
   @Named(ResourceStoreConstants.FileSystemStorePluginName)
   ResourceStore fileNamedSystemResourceStore;
   @Inject
   @Named(ResourceStoreConstants.ClasspathStorePluginName)
   ResourceStore classpathNamedResourceStore;

}
