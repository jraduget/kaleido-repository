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
