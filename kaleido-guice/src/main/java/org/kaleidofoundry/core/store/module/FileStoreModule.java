/*  
 * Copyright 2008-2014 the original author or authors 
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

import org.kaleidofoundry.core.context.AbstractModule;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileSystemStore;
import org.kaleidofoundry.core.store.GuiceClasspathFileStore;
import org.kaleidofoundry.core.store.GuiceFileSystemStore;
import org.kaleidofoundry.core.store.GuiceFtpStore;
import org.kaleidofoundry.core.store.GuiceHttpFileStore;
import org.kaleidofoundry.core.store.GuiceJpaFileStore;
import org.kaleidofoundry.core.store.GuiceWebappFileStore;
import org.kaleidofoundry.core.store.annotation.Classpath;
import org.kaleidofoundry.core.store.annotation.File;
import org.kaleidofoundry.core.store.annotation.Ftp;
import org.kaleidofoundry.core.store.annotation.Http;
import org.kaleidofoundry.core.store.annotation.Jpa;
import org.kaleidofoundry.core.store.annotation.Webapp;

/**
 * FileStore guice module<br/>
 * Default unnamed {@link FileStore} implementation is {@link FileSystemStore}<br/>
 * 
 * @author jraduget
 */
public class FileStoreModule extends AbstractModule<FileStore> {

   @Override
   public Class<? extends FileStore> getUnnamedImplementation() {
	return GuiceFileSystemStore.class;
   }

   @Override
   protected void configure() {
	// default module bindings
	super.configure();

	// bind custom annotation with matching implementation
	bind(FileStore.class).annotatedWith(Classpath.class).to(GuiceClasspathFileStore.class).in(scope(GuiceClasspathFileStore.class));
	bind(FileStore.class).annotatedWith(File.class).to(GuiceFileSystemStore.class).in(scope(GuiceFileSystemStore.class));
	bind(FileStore.class).annotatedWith(Ftp.class).to(GuiceFtpStore.class).in(scope(GuiceFtpStore.class));
	bind(FileStore.class).annotatedWith(Http.class).to(GuiceHttpFileStore.class).in(scope(GuiceHttpFileStore.class));
	bind(FileStore.class).annotatedWith(Webapp.class).to(GuiceWebappFileStore.class).in(scope(GuiceWebappFileStore.class));
	bind(FileStore.class).annotatedWith(Jpa.class).to(GuiceJpaFileStore.class).in(scope(GuiceJpaFileStore.class));
   }

}
