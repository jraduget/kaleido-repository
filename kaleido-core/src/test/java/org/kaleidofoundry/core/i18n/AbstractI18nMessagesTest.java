package org.kaleidofoundry.core.i18n;

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link I18nMessages} and {@link MessageBundle}
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractI18nMessagesTest extends Assert {

   /**
    * @return root resource path containing i18n test mock
    */
   abstract String getResourceRoot();

   /**
    * Clear bundle cache after each test method
    */
   @After
   public void cleanup() {
	I18nMessagesFactory.clearCache();
   }

   @Test(expected = NullPointerException.class)
   public void noParent_nullBasename() {
	I18nMessagesFactory.getMessages(null, Locale.ROOT);
   }

   @Test(expected = NullPointerException.class)
   public void noParent_nullLocale() {
	I18nMessagesFactory.getMessages(getResourceRoot() + "root", (Locale) null);
   }

   @Test(expected = MissingResourceException.class)
   public void noResourceFile() {
	I18nMessagesFactory.getMessages(getResourceRoot() + "nofile");
   }

   @Test(expected = MissingResourceException.class)
   public void noResourceKey() {
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "root", Locale.FRENCH);
	messages.getMessage("noKey");
   }

   @Test
   public void noParent_rootLocale() {
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "root", Locale.ROOT);
	assertValues(messages, null);
   }

   @Test
   public void noParent_French() {
	final Locale locale = Locale.FRENCH;
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "root", locale);
	assertValues(messages, locale);
   }

   @Test
   public void noParent_English() {
	final Locale locale = Locale.ENGLISH;
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "root", locale);
	assertValues(messages, locale);
   }

   @Test
   public void noParent_German() {
	final Locale locale = Locale.GERMAN;
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "root", locale);
	assertValues(messages, locale);
   }

   @Test
   public void noParent_unknownLocale() {
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "root", Locale.TRADITIONAL_CHINESE);
	assertValues(messages, null);
   }

   /**
    * test messageBundle with a parent and its child<br/>
    * if item is not found in children bundle, it have to found it in parent<br/>
    * children item can override parent item
    */
   @Test
   public void withParent_French() {
	final Locale locale = Locale.FRENCH;
	final I18nMessages parent = I18nMessagesFactory.getMessages(getResourceRoot() + "root", locale);
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "child", Locale.FRENCH, parent);
	final String localePrefix = locale != null ? locale.getLanguage() + " " : "";
	assertEquals(localePrefix + "test override parent label", messages.getMessage("label.simple.test")); // override default value
	assertEquals(localePrefix + "test label 01", messages.getMessage("label.array.test", "01")); // parent item value
	assertEquals(localePrefix + "test label 01 02", messages.getMessage("label.array2.test", "01", "02")); // parent item value
	assertEquals(localePrefix + "test child label", messages.getMessage("child.simple.test")); // new child item value
   }

   /**
    * @throws Throwable
    */
   @Test(expected = MissingResourceException.class)
   public void withParent_noResourceKey() throws Throwable {
	final Locale locale = Locale.FRENCH;
	final I18nMessages messages = I18nMessagesFactory.getMessages(getResourceRoot() + "child", locale);
	messages.getMessage("label.array.test");
   }

   /**
    * Global test case
    * 
    * @param messages
    * @param locale
    */
   void assertValues(final I18nMessages messages, final Locale locale) {
	final String localePrefix = locale != null ? locale.getLanguage() + " " : "";
	assertEquals(localePrefix + "test label", messages.getMessage("label.simple.test"));
	assertEquals(localePrefix + "test label 01", messages.getMessage("label.array.test", "01"));
	assertEquals(localePrefix + "test label 01 02", messages.getMessage("label.array2.test", "01", "02"));
   }
}
