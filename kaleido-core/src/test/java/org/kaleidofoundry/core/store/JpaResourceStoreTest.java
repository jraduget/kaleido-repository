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
package org.kaleidofoundry.core.store;

import java.net.URI;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.store.entity.ResourceStoreEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class JpaResourceStoreTest extends AbstractResourceStoreTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(JpaResourceStoreTest.class);

   private EntityManagerFactory emf;
   private EntityManager em;
   private EntityTransaction transaction;

   @Before
   @Override
   public void setup() throws Throwable {

	try {
	   // jpa entity manager configuration (default kaleido)
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();
	   // emf = UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-core-derby-oracle");
	   em = UnmanagedEntityManagerFactory.currentEntityManager();

	   // resource store creation
	   final RuntimeContext<ResourceStore> context = new ResourceContextBuilder().withUriRootPath("jpa:/").build();
	   resourceStore = new JpaResourceStore(context);

	   // begin transaction
	   transaction = em.getTransaction();
	   transaction.begin();

	   // 1. existing resources (to get) - create mocked entity for the get test
	   final ResourceStoreEntity entityToGet = new ResourceStoreEntity();
	   final URI resourceUriToGet = URI.create("jpa:/tmp/foo.txt");
	   final String filenameToGet = resourceUriToGet.getPath().substring(1);
	   entityToGet.setUri(resourceUriToGet.toString());
	   entityToGet.setName(FileHelper.getFileName(filenameToGet));
	   entityToGet.setCreationDate(Calendar.getInstance().getTime());
	   entityToGet.setPath(filenameToGet);
	   entityToGet.setContent(DEFAULT_RESOURCE_MOCK_TEST.getBytes());
	   em.persist(entityToGet);
	   em.flush(); // flush to be sure, that entity is right persist
	   existingResources.put(resourceUriToGet.getPath(), DEFAULT_RESOURCE_MOCK_TEST);

	   // 2. resources to get (but which not exists)
	   nonExistingResources.add("foo");

	   // 3. resources to store
	   final String filenameToStore = "tmp/fooToStore.txt";
	   existingResourcesForStore.put(filenameToStore, DEFAULT_RESOURCE_MOCK_TEST);

	   // 4. resources to remove
	   final ResourceStoreEntity entityToRemove = new ResourceStoreEntity();
	   final URI resourceUriToRemove = URI.create("jpa:/tmp/fooToRemove.txt");
	   final String filenameToRemove = resourceUriToRemove.getPath().substring(1);
	   entityToRemove.setUri(resourceUriToRemove.toString());
	   entityToRemove.setName(FileHelper.getFileName(filenameToRemove));
	   entityToRemove.setCreationDate(Calendar.getInstance().getTime());
	   entityToRemove.setPath(filenameToRemove);
	   entityToRemove.setContent(DEFAULT_RESOURCE_MOCK_TEST.getBytes());
	   em.persist(entityToRemove);
	   em.flush(); // flush to be sure, that entity is right persist
	   existingResourcesForRemove.put(filenameToRemove, DEFAULT_RESOURCE_MOCK_TEST);

	} catch (final RuntimeException rte) {
	   LOGGER.error("setup error", rte);
	   throw rte;
	}

   }

   @After
   @Override
   public void cleanup() {
	try {
	   final URI resourceUri = URI.create("jpa:/tmp/foo.txt");
	   final ResourceStoreEntity resource = em.find(ResourceStoreEntity.class, resourceUri.toString());
	   if (resource != null) {
		em.remove(resource);
		transaction.commit();
	   }
	} finally {
	   UnmanagedEntityManagerFactory.closeItSilently(em);
	   UnmanagedEntityManagerFactory.closeItSilently(emf);
	}
   }
}
