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

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.store.model.ResourceHandlerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * small example of using the EntityManager
 * 
 * @author Jerome RADUGET
 */
public class EntityManagerSample extends Assert {

   private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerSample.class);

   private EntityManagerFactory emf;
   private EntityManager em;
   private EntityTransaction transaction;

   @Before
   public void setup() throws URISyntaxException {

	try {
	   // jpa entity manager configuration
	   emf = Persistence.createEntityManagerFactory("kaleido");
	   em = emf.createEntityManager();

	   // begin transaction
	   transaction = em.getTransaction();
	   transaction.begin();

	   // create mocked entity test
	   ResourceHandlerEntity entity = new ResourceHandlerEntity();
	   entity.setUri(new URI("http://localhost/foo.txt").toString());
	   entity.setName("/tmp/foo.txt");
	   entity.setPath("/tmp/foo.txt");
	   entity.setContent("foo internal text ...".getBytes());

	   em.persist(entity);

	} catch (RuntimeException rte) {
	   LOGGER.error("setup error", rte);
	   throw rte;
	}

   }

   @After
   public void close() {
	try {
	   em.remove(em.find(ResourceHandlerEntity.class, "http://localhost/foo.txt"));
	   transaction.commit();
	} finally {
	   try {
		em.close();
	   } catch (Throwable th) {
		LOGGER.error("close", th);
	   }
	   ;
	   try {
		emf.close();
	   } catch (Throwable th) {
		LOGGER.error("close", th);
	   }
	   ;
	}
   }

   @Test
   public void simpleSample() {

	assertNotNull(em);
	assertNotNull(emf);

	ResourceHandlerEntity entity = em.find(ResourceHandlerEntity.class, "http://localhost/foo.txt");

	assertEquals("http://localhost/foo.txt", entity.getUri());
	assertEquals("/tmp/foo.txt", entity.getName());

   }
}
