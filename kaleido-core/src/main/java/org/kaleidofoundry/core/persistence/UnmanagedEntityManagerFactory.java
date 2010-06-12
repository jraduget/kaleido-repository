package org.kaleidofoundry.core.persistence;

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
   public static final String KaleidoPersistentContextUnitName = "kaleido-core";

   // default kaleidofoundry EntityManagerFactory
   private static EntityManagerFactory DefaultEmf;

   // default kaleidofoundry threadlocal EntityManager
   private static final ThreadLocal<EntityManager> DefaultEm = new ThreadLocal<EntityManager>();

   // registry of custom entityManager factory
   private static final Registry<EntityManagerFactory> CustomEmfRegistry = new Registry<EntityManagerFactory>();

   // registry of custom entityManager factory
   private static final Registry<ThreadLocal<EntityManager>> CustomEmRegistry = new Registry<ThreadLocal<EntityManager>>();

   /**
    * @return default kaleido entity manager of the current user (thread local)<br/>
    *         it will create the EntityManager if needed
    */
   @NotNull
   public static final EntityManager currentEntityManager() {
	if (DefaultEm.get() == null) {
	   EntityManager lem = getEntityManagerFactory().createEntityManager();
	   DefaultEm.set(lem);
	   return lem;
	} else {
	   return DefaultEm.get();
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
	   EntityManager em;
	   ThreadLocal<EntityManager> lem = CustomEmRegistry.get(persistenceUnitName);

	   if (lem == null || lem.get() == null) {
		em = getEntityManagerFactory(persistenceUnitName).createEntityManager();
		lem = lem == null ? new ThreadLocal<EntityManager>() : lem;
		lem.set(em);
		CustomEmRegistry.put(persistenceUnitName, lem);
	   } else {
		em = lem.get();
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
	   for (ThreadLocal<EntityManager> tlem : CustomEmRegistry.values()) {
		if (tlem.get() == em) {
		   em.close();
		   tlem.remove();
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
