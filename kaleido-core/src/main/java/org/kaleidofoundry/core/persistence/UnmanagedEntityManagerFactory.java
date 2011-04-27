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
package org.kaleidofoundry.core.persistence;

import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory used to manage {@link EntityManagerFactory} and {@link EntityManager} in an <b>unmanaged</b> environment<br/>
 * You can use it in container that does not managed injection via {@link PersistenceContext} or {@link PersistenceUnit} annotation,
 * like :
 * <ul>
 * <li>unit test class
 * <li>main class program
 * <li>...
 * </ul>
 * <b>Warning :</b>
 * <p>
 * If you get (so create) an EntityManager or an EntityManagerFactory with this helper class : <br/>
 * <b>you will have to close EntityManager or EntityManagerFactory by using </b>{@link UnmanagedEntityManagerFactory#close(EntityManager)}
 * or {@link UnmanagedEntityManagerFactory#close(EntityManagerFactory)}
 * </p>
 * 
 * @author Jerome RADUGET
 */
public abstract class UnmanagedEntityManagerFactory {

   private static Logger LOGGER = LoggerFactory.getLogger(UnmanagedEntityManagerFactory.class);

   /**
    * default name for internal kaleidofoundry persistent context unit name (declare into persitence.xml)
    */
   public static final String KaleidoPersistentContextUnitName = "kaleido";

   // default kaleidofoundry EntityManagerFactory
   private static EntityManagerFactory DefaultEmf;

   // default kaleidofoundry threadlocal EntityManager
   private static final ThreadLocal<EntityManager> DefaultEm = new ThreadLocal<EntityManager>();

   // registry of custom entityManager factory
   private static final Registry<String, EntityManagerFactory> CustomEmfRegistry = new Registry<String, EntityManagerFactory>();

   // registry of custom entityManager factory
   private static final ThreadLocal<Registry<String, EntityManager>> CustomEmRegistry = new ThreadLocal<Registry<String, EntityManager>>();

   /**
    * @return default kaleido entity manager of the current user (thread local)<br/>
    *         it will create the EntityManager if needed. If previous entity manager thread was closed, a new one is created.
    */
   @NotNull
   public static final EntityManager currentEntityManager() {
	EntityManager em = DefaultEm.get();
	if (em == null || !em.isOpen()) {
	   EntityManager lem = getEntityManagerFactory().createEntityManager();
	   DefaultEm.set(lem);
	   return lem;
	} else {
	   return em;
	}
   }

   /**
    * @param persistenceUnitName
    * @return entity manager which will map to the persistence unit name declared in META-INF/persistence.xml<br/>
    * <br/>
    *         it will create the EntityManager if needed
    */
   @NotNull
   public static final EntityManager currentEntityManager(@NotNull final String persistenceUnitName) {

	if (KaleidoPersistentContextUnitName.equals(persistenceUnitName)) {
	   return currentEntityManager();
	} else {

	   // if none registry have been created yet
	   if (CustomEmRegistry.get() == null) {
		CustomEmRegistry.set(new Registry<String, EntityManager>());
	   }

	   // get current entityManager thread
	   EntityManager em = CustomEmRegistry.get().get(persistenceUnitName);

	   // create entityManager if needed (if null or closed)
	   if (em == null || !em.isOpen()) {
		em = getEntityManagerFactory(persistenceUnitName).createEntityManager();
		CustomEmRegistry.get().put(persistenceUnitName, em);
	   }

	   return em;
	}
   }

   /**
    * @return default kaleido entity manager factory<br/>
    *         it will use as persistent unit name : {@link #KaleidoPersistentContextUnitName} <br/>
    *         it will create the EntityManagerFactory if needed
    */
   @NotNull
   public static final EntityManagerFactory getEntityManagerFactory() {
	if (DefaultEmf == null) {
	   synchronized (UnmanagedEntityManagerFactory.class) {
		DefaultEmf = Persistence.createEntityManagerFactory(KaleidoPersistentContextUnitName);
	   }
	}
	return DefaultEmf;
   }

   /**
    * @param persistenceUnitName name of the persistence unit declare in persistence.xml
    * @return entity manager factory which will map to the persistence unit name declared in META-INF/persistence.xml<br/>
    *         it will create the EntityManagerFactory if needed
    */
   @NotNull
   public static final EntityManagerFactory getEntityManagerFactory(@NotNull final String persistenceUnitName) {

	if (KaleidoPersistentContextUnitName.equals(persistenceUnitName)) {
	   return getEntityManagerFactory();
	} else {
	   EntityManagerFactory emf = CustomEmfRegistry.get(persistenceUnitName);
	   if (emf == null) {
		emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		CustomEmfRegistry.put(persistenceUnitName, emf);
	   }
	   return emf;
	}
   }

   /**
    * @param em close the given entity manager
    * @throws IllegalArgumentException if the given entity manager have not been create by {@link UnmanagedEntityManagerFactory}
    * @throws IllegalStateException if the entity manager is container-managed
    */
   public static final void close(@NotNull final EntityManager em) throws IllegalArgumentException {

	// if em is the default kaleido one
	if (em == DefaultEm.get()) {
	   em.close();
	   DefaultEm.remove();
	}
	// browse custom threadlocal em to find it
	else {
	   boolean foundEm = false;
	   for (Entry<String, EntityManager> entry : CustomEmRegistry.get().entrySet()) {
		if (entry.getValue() == em) {
		   em.close();
		   CustomEmRegistry.get().remove(entry.getKey());
		   foundEm = true;
		   return;
		}
	   }
	   if (!foundEm) { throw new IllegalArgumentException("entityManager argument is not handle by UnmanagedEntityManagerFactory"); }
	}
   }

   /**
    * @param emf close the given entity manager factory
    * @throws IllegalArgumentException if the given entity manager factory have not been create by {@link UnmanagedEntityManagerFactory}
    * @throws IllegalStateException if the entity manager factory has been closed
    */
   public static final void close(@NotNull final EntityManagerFactory emf) throws IllegalArgumentException {

	// if emf is the default kaleido one
	if (emf == DefaultEmf) {
	   emf.close();
	   DefaultEmf = null;
	}
	// browse custom emf to find it
	else {
	   String emfNameUnitName = null;

	   for (java.util.Map.Entry<String, EntityManagerFactory> lemf : CustomEmfRegistry.entrySet()) {
		if (lemf.getValue() == emf) {
		   emfNameUnitName = lemf.getKey();
		   break;
		}
	   }

	   if (emfNameUnitName != null) {
		emf.close();
		CustomEmfRegistry.remove(emfNameUnitName);
	   } else {
		throw new IllegalArgumentException("entityManagerFactory argument is not handle by UnmanagedEntityManagerFactory");
	   }
	}
   }

   /**
    * close the given entity manager <b>silently</b> : no exception will be throws, only a logged error level message
    * 
    * @param em
    * @see #close(EntityManager)
    */
   public static final void closeItSilently(final EntityManager em) {
	try {
	   close(em);
	} catch (Throwable th) {
	   LOGGER.error("closeItSilently", th);
	}
   }

   /**
    * close the given entity manager factory <b>silently</b> : no exception will be throws, only a logged error level message
    * 
    * @param emf
    * @see #close(EntityManager)
    */
   public static final void closeItSilently(final EntityManagerFactory emf) {
	try {
	   close(emf);
	} catch (Throwable th) {
	   LOGGER.error("closeItSilently", th);
	}
   }
}
