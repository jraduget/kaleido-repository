package org.kaleidofoundry.core.store;

import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jerome RADUGET
 */
public class ResourceStoreFactoryTest extends Assert {

   @Test
   public void newClasspathResourceStore() throws StoreException {
	URI resourceUri = URI.create("classpath:/org/kaleidofoundry/core/store/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.createResourceStore(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(ClasspathResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newFileSystemResourceStore() throws StoreException {
	URI resourceUri = URI
		.create("file:/F:/Developments/workspaces/eclipse/jappy/kaleido-core/src/test/resources/org/kaleidofoundry/core/store/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.createResourceStore(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(FileSystemResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newFtpResourceStore() throws StoreException {
	URI resourceUri = URI.create("ftp://localhost/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.createResourceStore(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(FtpResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newHttpResourceStore() throws StoreException {
	URI resourceUri = URI.create("http://localhost/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.createResourceStore(resourceUri);
	assertNotNull(resourceStore);
	assertEquals(HttpResourceStore.class, resourceStore.getClass());
	assertTrue(resourceStore.isUriManageable(resourceUri));
   }

   @Test
   public void newJpaResourceStore() throws StoreException {
	URI resourceUri = URI.create("jpa:/tmp/foo.txt");
	ResourceStore resourceStore = ResourceStoreFactory.createResourceStore(resourceUri);
	assertNotNull(resourceStore);
	assertTrue(resourceStore.isUriManageable(resourceUri));
	assertEquals(JpaResourceStore.class, resourceStore.getClass());
   }

   // @Rule
   @Test
   public void unManagedUri() {
   }

   @Test
   public void illegalImplementation() {
   }

}
