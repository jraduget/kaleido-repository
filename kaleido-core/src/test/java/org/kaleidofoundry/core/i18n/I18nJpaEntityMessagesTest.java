package org.kaleidofoundry.core.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.entity.I18nMessage;
import org.kaleidofoundry.core.i18n.entity.I18nMessageGroup;
import org.kaleidofoundry.core.i18n.entity.I18nMessageLanguage;
import org.kaleidofoundry.core.i18n.entity.I18nMessageService;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class I18nJpaEntityMessagesTest extends AbstractI18nMessagesTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(I18nJpaEntityMessagesTest.class);

   private static final String ResourceRoot = "org/kaleidofoundry/core/i18n/jpa-entity/";

   private static EntityManagerFactory emf;

   /**
    * create a database with mocked data
    */
   @BeforeClass
   public static void setupStatic() {

	EntityManager em = null;
	EntityTransaction et = null;

	try {

	   // memorize entity manager factory in order to clean it up at end of tests
	   emf = UnmanagedEntityManagerFactory.getEntityManagerFactory();

	   // current entity manager
	   em = UnmanagedEntityManagerFactory.currentEntityManager();
	   et = em.getTransaction();

	   et.begin();

	   // feed database with mock entities
	   String resourceName;
	   I18nMessage message;
	   I18nMessageGroup messageGroup;

	   // a. root resourceBundle datas
	   resourceName = ResourceRoot + "root";
	   messageGroup = new I18nMessageGroup(resourceName);

	   message = new I18nMessage("label.simple.test", null, messageGroup);
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "fr test label", Locale.FRENCH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "en test label", Locale.ENGLISH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "de test label", Locale.GERMAN));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "test label", Locale.ROOT));
	   em.merge(message);

	   message = new I18nMessage("label.array.test", null, messageGroup);
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "fr test label {0}", Locale.FRENCH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "en test label {0}", Locale.ENGLISH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "de test label {0}", Locale.GERMAN));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "test label {0}", Locale.ROOT));
	   em.merge(message);

	   message = new I18nMessage("label.array2.test", null, messageGroup);
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "fr test label {0} {1}", Locale.FRENCH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "en test label {0} {1}", Locale.ENGLISH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "de test label {0} {1}", Locale.GERMAN));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "test label {0} {1}", Locale.ROOT));
	   em.merge(message);

	   // b. child of root resourceBundle datas
	   resourceName = ResourceRoot + "child";
	   messageGroup = new I18nMessageGroup(resourceName);

	   // override parent item
	   message = new I18nMessage("label.simple.test", null, messageGroup);
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "fr test override parent label", Locale.FRENCH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "en test override parent label", Locale.ENGLISH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "de test override parent label", Locale.GERMAN));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "test override parent label", Locale.ROOT));
	   em.merge(message);

	   // new item
	   message = new I18nMessage("child.simple.test", null, messageGroup);
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "fr test child label", Locale.FRENCH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "en test child label", Locale.ENGLISH));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "de test child label", Locale.GERMAN));
	   message.getMessageLanguages().add(new I18nMessageLanguage(message, "test child label", Locale.ROOT));
	   em.merge(message);

	   // print mock entities that have been persist
	   I18nMessageService i18nMessageService = new I18nMessageService();

	   for (Locale currentLocale : Arrays.asList(Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN)) {

		List<I18nMessageLanguage> frMessages = i18nMessageService.findMessagesByLocale(ResourceRoot + "root", currentLocale);
		LOGGER.info(StringHelper.replicate("#", 140));
		LOGGER.info("findMessagesByLanguageIso mock database messages for locale '{}' :", currentLocale);
		for (I18nMessageLanguage ml : frMessages) {
		   LOGGER.info("\t{}", ml.toString());
		}
		LOGGER.info(StringHelper.replicate("#", 140));
	   }

	   et.commit();

	   // enable jpa control in case it have been disabled (true by default)
	   I18nMessagesFactory.enableJpaControl();

	} catch (RuntimeException re) {
	   LOGGER.error("static setup", re);
	   throw re;
	} finally {
	   if (em != null) {
		UnmanagedEntityManagerFactory.close(em);
	   }
	}
   }

   @AfterClass
   public static void cleanupStatic() {
	if (emf != null) {
	   UnmanagedEntityManagerFactory.close(emf);
	}
   }

   @After
   @Override
   public void cleanup() {
	super.cleanup();
	// each method will have its own entity manager, we have to clean up after use it
	UnmanagedEntityManagerFactory.closeItSilently(UnmanagedEntityManagerFactory.currentEntityManager());
   }

   @Override
   String getResourceRoot() {
	return ResourceRoot;
   }

   @Test
   public void testResourceBundleCache() {

	MessageBundle messageBundle = (MessageBundle) I18nMessagesFactory.getMessages(ResourceRoot + "root", Locale.FRENCH);
	LOGGER.debug(messageBundle.getMessage("label.array.test", "1"));
	LOGGER.debug(messageBundle.getMessage("label.array.test", "2"));
	messageBundle = (MessageBundle) I18nMessagesFactory.getMessages(ResourceRoot + "root", Locale.FRENCH);
	LOGGER.debug(messageBundle.getMessage("label.array.test", "3"));
	LOGGER.debug(messageBundle.getMessage("label.array.test", "4"));
	LOGGER.debug("clear bundle cache");
	I18nMessagesFactory.clearCache();
	messageBundle = (MessageBundle) I18nMessagesFactory.getMessages(ResourceRoot + "root", Locale.FRENCH);
	LOGGER.debug(messageBundle.getMessage("label.array.test", "1"));
	LOGGER.debug(messageBundle.getMessage("label.array.test", "2"));
	LOGGER.debug(messageBundle.getMessage("label.array.test", "3"));
	LOGGER.debug(messageBundle.getMessage("label.array.test", "4"));
   }
}
