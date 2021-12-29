/*  
 * Copyright 2008-2021 the original author or authors 
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

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.ClasspathFileStore;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreConstants;
import org.kaleidofoundry.core.store.FileSystemStore;
import org.kaleidofoundry.core.store.FtpStore;
import org.kaleidofoundry.core.store.HttpFileStore;
import org.kaleidofoundry.core.store.annotation.Classpath;
import org.kaleidofoundry.core.store.annotation.File;
import org.kaleidofoundry.core.store.annotation.Ftp;
import org.kaleidofoundry.core.store.annotation.Http;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import static org.junit.Assert.*;

/**
 * @author jraduget
 */
public class RawResourceModuleTest {

   private Injector injector;
   private Sample sample;

   @Before
   public void setup() {
	// guice injector
	injector = Guice.createInjector(new org.kaleidofoundry.core.store.module.FileStoreModule());
	// test guice instance
	sample = injector.getInstance(Sample.class);
   }

   @Test
   public void defaultFileStore() {
	assertNotNull(sample);
	assertNotNull(sample.defaultStore);
	assertTrue(sample.defaultStore instanceof FileSystemStore);
   }

   // ** find fileStore by binding annotation **********************************************************************
   @Test
   public void fileStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.fileSystemStore);
	assertTrue(sample.fileSystemStore instanceof FileSystemStore);
   }

   @Test
   public void ftpStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.ftpStore);
	assertTrue(sample.ftpStore instanceof FtpStore);
   }

   @Test
   public void httpFileStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.httpStore);
	assertTrue(sample.httpStore instanceof HttpFileStore);
   }

   @Test
   public void classpathFileStoreByAnnotation() {
	assertNotNull(sample);
	assertNotNull(sample.classpathStore);
	assertTrue(sample.classpathStore instanceof ClasspathFileStore);
   }

   // ** find fileStore by qualifier name **************************************************************************

   @Test
   public void fileFileStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.fileSystemStore);
	assertTrue(sample.fileSystemStore instanceof FileSystemStore);
   }

   @Test
   public void ftpStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.ftpNamedStore);
	assertTrue(sample.ftpNamedStore instanceof FtpStore);
   }

   @Test
   public void httpFileStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.httpNamedStore);
	assertTrue(sample.httpNamedStore instanceof HttpFileStore);
   }

   @Test
   public void classpathFileStoreByName() {
	assertNotNull(sample);
	assertNotNull(sample.classpathNamedStore);
	assertTrue(sample.classpathNamedStore instanceof ClasspathFileStore);
   }

}

/**
 * sample class to test injection without any {@link RuntimeContext} / {@link Context}
 */
class Sample {

   // default file store
   @Inject
   FileStore defaultStore;

   // injection using custom binding guice annotation
   @Inject
   @Ftp
   FileStore ftpStore;
   @Inject
   @Http
   FileStore httpStore;
   @Inject
   @File
   FileStore fileSystemStore;
   @Inject
   @Classpath
   FileStore classpathStore;

   // injection using custom binding guice named
   @Inject
   @Named(FileStoreConstants.FtpStorePluginName)
   FileStore ftpNamedStore;
   @Inject
   @Named(FileStoreConstants.HttpStorePluginName)
   FileStore httpNamedStore;
   @Inject
   @Named(FileStoreConstants.FileSystemStorePluginName)
   FileStore fileNamedSystemStore;
   @Inject
   @Named(FileStoreConstants.ClasspathStorePluginName)
   FileStore classpathNamedStore;

}
