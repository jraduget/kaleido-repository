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

import org.kaleidofoundry.core.ioc.AbstractModule;
import org.kaleidofoundry.core.store.FileSystemResourceStore;
import org.kaleidofoundry.core.store.GuiceClasspathResourceStore;
import org.kaleidofoundry.core.store.GuiceFileSystemResourceStore;
import org.kaleidofoundry.core.store.GuiceFtpResourceStore;
import org.kaleidofoundry.core.store.GuiceHttpResourceStore;
import org.kaleidofoundry.core.store.GuiceJpaResourceStore;
import org.kaleidofoundry.core.store.GuiceWebappResourceStore;
import org.kaleidofoundry.core.store.ResourceStore;
import org.kaleidofoundry.core.store.annotation.Classpath;
import org.kaleidofoundry.core.store.annotation.File;
import org.kaleidofoundry.core.store.annotation.Ftp;
import org.kaleidofoundry.core.store.annotation.Http;
import org.kaleidofoundry.core.store.annotation.Jpa;
import org.kaleidofoundry.core.store.annotation.Webapp;

/**
 * ResourceStore guice module<br/>
 * Default unnamed {@link ResourceStore} implementation is {@link FileSystemResourceStore}<br/>
 * 
 * @author Jerome RADUGET
 */
public class ResourceStoreModule extends AbstractModule<ResourceStore> {

   @Override
   public Class<? extends ResourceStore> getUnnamedImplementation() {
	return GuiceFileSystemResourceStore.class;
   }

   @Override
   protected void configure() {
	// default module bindings
	super.configure();

	// bind custom annotation with matching implementation
	bind(ResourceStore.class).annotatedWith(Classpath.class).to(GuiceClasspathResourceStore.class).in(scope(GuiceClasspathResourceStore.class));
	bind(ResourceStore.class).annotatedWith(File.class).to(GuiceFileSystemResourceStore.class).in(scope(GuiceFileSystemResourceStore.class));
	bind(ResourceStore.class).annotatedWith(Ftp.class).to(GuiceFtpResourceStore.class).in(scope(GuiceFtpResourceStore.class));
	bind(ResourceStore.class).annotatedWith(Http.class).to(GuiceHttpResourceStore.class).in(scope(GuiceHttpResourceStore.class));
	bind(ResourceStore.class).annotatedWith(Webapp.class).to(GuiceWebappResourceStore.class).in(scope(GuiceWebappResourceStore.class));
	bind(ResourceStore.class).annotatedWith(Jpa.class).to(GuiceJpaResourceStore.class).in(scope(GuiceJpaResourceStore.class));
   }

}
