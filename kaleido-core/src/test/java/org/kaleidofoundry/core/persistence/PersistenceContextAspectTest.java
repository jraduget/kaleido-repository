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
package org.kaleidofoundry.core.persistence;

import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * PersistenceContext injection in an unmanaged environment
 * 
 * @author jraduget
 */
public class PersistenceContextAspectTest  {

   // ******* static init part (pre instantiate emf for all test ****************************************************************

   private static EntityManagerFactory emf;

   /**
    * create a database with mocked data
    */
   @BeforeClass
   public static void setupStatic() {
	emf = UnmanagedEntityManagerFactory.getEntityManagerFactory("kaleido-custom");
   }

   /**
    * close emf in order to cleanup internal emf for other test suite that can use it
    */
   @AfterClass
   public static void cleanupStatic() {
	UnmanagedEntityManagerFactory.close(emf);
   }

   // ******* pre init part (begin transaction for current processed test) ******************************************************

   private EntityManager em;
   private EntityTransaction et;

   @Before
   public void setup() {
	em = UnmanagedEntityManagerFactory.currentEntityManager("kaleido-custom");
	et = em.getTransaction();
	et.begin();
   }

   @After
   public void cleanup() {
	try {
	   if (et != null) {
		et.commit();
	   }
	} finally {
	   if (em != null) {
		em.close();
	   }
	}
   }

   // ******* tests part *********************************************************************************************************

   /**
    * test that aspect correctly inject persistence context inside the service
    */
   @Test
   public void legalInjectionTest() {

	// create new service
	PersonService personService = new PersonService();

	// test that entity manager injection has been done by aspect
	assertNotNull("EntityManager have not been injected...", personService.entityManager);

	// create a mocked data to test entity manager functionalities
	PersonEntity entity = new PersonEntity("firstname", "lastname", new GregorianCalendar().getTime());

	personService.create(entity);

	assertNotNull(entity);
	assertNotNull(entity.id);

	PersonEntity entity2 = personService.findById(entity.id);

	assertNotNull(entity2);
	assertEquals("firstname", entity2.getFirstname());
	assertEquals("lastname", entity2.getLastname());

   }

}
