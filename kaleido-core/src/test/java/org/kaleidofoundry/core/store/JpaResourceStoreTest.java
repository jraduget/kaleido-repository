package org.kaleidofoundry.core.store;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;
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
   public void setup() throws URISyntaxException {

	try {
	   // jpa entity manager configuration (default kaleido)
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();
	   // emf = UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-core-derby-oracle");
	   em = UnmanagedEntityManagerFactory.currentEntityManager();

	   // begin transaction
	   transaction = em.getTransaction();
	   transaction.begin();

	   // create mocked entity test
	   ResourceStoreEntity entity = new ResourceStoreEntity();
	   entity.setUri(new URI("jpa://tmp/foo.txt").toString());
	   entity.setName("/tmp/foo.txt");
	   entity.setPath("/tmp/foo.txt");
	   entity.setContent(DEFAULT_RESOURCE_MOCK_TEST.getBytes());

	   em.persist(entity);

	   // resource to test
	   existingResources.put(new URI("jpa://tmp/foo.txt"), DEFAULT_RESOURCE_MOCK_TEST);
	   nonExistingResources.add(new URI("jpa:/foo"));

	   // resource store creation
	   resourceStore = new JpaResourceStore(new RuntimeContext<ResourceStore>());

	} catch (RuntimeException rte) {
	   LOGGER.error("setup error", rte);
	   throw rte;
	}

   }

   @After
   @Override
   public void cleanup() {
	try {
	   em.remove(em.find(ResourceStoreEntity.class, "jpa://tmp/foo.txt"));
	   transaction.commit();
	} finally {
	   UnmanagedEntityManagerFactory.closeItSilently(em);
	   UnmanagedEntityManagerFactory.closeItSilently(emf);
	}
   }
}
