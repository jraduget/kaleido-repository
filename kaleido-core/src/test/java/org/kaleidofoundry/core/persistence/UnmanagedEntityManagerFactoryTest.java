package org.kaleidofoundry.core.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jerome RADUGET
 */
public class UnmanagedEntityManagerFactoryTest extends Assert {

   // entity manager part ****************

   @Test
   public void currentEntityManager() {

	EntityManager em = null;
	EntityManager em2 = null;
	try {
	   // first call will create default kaleido EntityManager
	   em = UnmanagedEntityManagerFactory.currentEntityManager();
	   assertNotNull(em);
	   assertTrue(em.isOpen());
	   assertSame(em, UnmanagedEntityManagerFactory.currentEntityManager());
	   assertSame(em, UnmanagedEntityManagerFactory.currentEntityManager());
	   // close and free it
	   UnmanagedEntityManagerFactory.close(em);
	   assertFalse(em.isOpen());
	   // factory have to return a new instance
	   em2 = UnmanagedEntityManagerFactory.currentEntityManager();
	   assertNotSame(em, em2);
	   assertTrue(em2.isOpen());
	   assertSame(em2, UnmanagedEntityManagerFactory.currentEntityManager());
	   UnmanagedEntityManagerFactory.close(em2);
	   assertSame(UnmanagedEntityManagerFactory.currentEntityManager(), UnmanagedEntityManagerFactory.currentEntityManager());
	   // cleanup
	   UnmanagedEntityManagerFactory.close(UnmanagedEntityManagerFactory.currentEntityManager());
	} finally {
	   // clean entity manager factory
	   UnmanagedEntityManagerFactory.closeItSilently(UnmanagedEntityManagerFactory.getEntityManagerFactory());
	}
   }

   @Test
   public void currentEntityManagerNamed() {
	EntityManager em = null;
	EntityManager em2 = null;
	try {
	   // first call will create default kaleido EntityManager
	   em = UnmanagedEntityManagerFactory.currentEntityManager("kaleido-core-custom");
	   assertNotNull(em);
	   assertTrue(em.isOpen());
	   assertSame(em, UnmanagedEntityManagerFactory.currentEntityManager("kaleido-core-custom"));
	   assertSame(em, UnmanagedEntityManagerFactory.currentEntityManager("kaleido-core-custom"));
	   // close and free it
	   UnmanagedEntityManagerFactory.close(em);
	   assertFalse(em.isOpen());
	   // factory have to return a new instance
	   em2 = UnmanagedEntityManagerFactory.currentEntityManager("kaleido-core-custom");
	   assertNotSame(em, em2);
	} finally {
	   if (em2 != null && em2.isOpen()) {
		UnmanagedEntityManagerFactory.close(em2);
	   }
	}
   }

   @Test
   public void closeEntityManager() {
	EntityManager em = UnmanagedEntityManagerFactory.currentEntityManager();
	assertNotNull(em.getTransaction());
	assertTrue(em.isOpen());
	UnmanagedEntityManagerFactory.close(em);
	assertFalse("entity manager must be closed", em.isOpen());
	assertNotSame(em, UnmanagedEntityManagerFactory.currentEntityManager());
   }

   // entity manager factory part ****************

   @Test
   public void getEntityManagerFactory() {
	EntityManagerFactory emf = null;
	try {
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();
	   assertNotNull(emf);

	   assertSame(emf, UnmanagedEntityManagerFactory.getEntityManagerFactory());
	   EntityManager em = emf.createEntityManager();
	   assertNotNull(em);
	   assertTrue(em.isOpen());
	   UnmanagedEntityManagerFactory.close(emf);
	   assertFalse(em.isOpen());
	   assertNotSame(emf, UnmanagedEntityManagerFactory.getEntityManagerFactory());
	} finally {
	   if (emf != null && emf.isOpen()) {
		UnmanagedEntityManagerFactory.close(emf);
	   }
	}
   }

   @Test
   public void getEntityManagerFactoryNamed() {
	EntityManagerFactory emf = null;
	EntityManagerFactory emf2 = null;
	try {
	   // first call will create default kaleido EntityManager
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-core-custom");
	   assertNotNull(emf);
	   assertSame(emf, UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-core-custom"));
	   assertSame(emf, UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-core-custom"));
	   // close and free it
	   UnmanagedEntityManagerFactory.close(emf);
	   // factory have to return a new instance
	   emf2 = UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-core-custom");
	   assertNotSame(emf, emf2);
	} finally {
	   if (emf != null && emf.isOpen()) {
		UnmanagedEntityManagerFactory.close(emf);
	   }
	   if (emf2 != null && emf2.isOpen()) {
		UnmanagedEntityManagerFactory.close(emf2);
	   }
	}
   }

   @Test
   public void closeEntityManagerFactory() {
	EntityManagerFactory emf = null;
	try {
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();
	   assertNotNull(emf);
	   assertSame(emf, UnmanagedEntityManagerFactory.getEntityManagerFactory());
	   UnmanagedEntityManagerFactory.close(emf);
	   assertNotSame(emf, UnmanagedEntityManagerFactory.getEntityManagerFactory());
	} finally {
	   UnmanagedEntityManagerFactory.close(UnmanagedEntityManagerFactory.getEntityManagerFactory());
	}
   }
}
