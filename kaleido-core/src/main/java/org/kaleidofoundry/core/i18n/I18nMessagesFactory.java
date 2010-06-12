package org.kaleidofoundry.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.kaleidofoundry.core.system.JavaSystemHelper;
import org.kaleidofoundry.core.util.locale.LocaleFactory;

/**
 * I18n messages factory
 * 
 * @author Jerome RADUGET
 */
public class I18nMessagesFactory {

   /** Enable or disable jpa entity manager resolution for resourceBundle */
   public static final String ENABLE_JPA_PROPERTY = "kaleidofoundry.i18n.jpa.enabled";

   /**
    * it clear internal java resource bundle cache<br/>
    * <br/>
    * when you call multiple times {@link #getMessages(String)}, jdk use an internal cache to get the current bundle
    * once you call {@link #clearCache()}, then next call to {@link #getMessages(String)} will instanciate / load ... a new
    * {@link MessageBundle}
    * 
    * @see ResourceBundle#clearCache()
    */
   public static void clearCache() {
	ResourceBundle.clearCache();
	ResourceBundle.clearCache(I18nMessagesFactory.class.getClassLoader());
   }

   /**
    * @param baseName name of the resource
    * @return ResourceBundle with server default locale, and with properties loaded from the same classLoader than ResourceBundle class
    */
   public static I18nMessages getMessages(final String baseName) {
	return (MessageBundle) ResourceBundle.getBundle(baseName, defaultLocale(), MessageBundle.class.getClassLoader(), new MessageBundleControl());
   }

   /**
    * @param baseName name of the resource
    * @param targetLocale specified locale
    * @return ResourceBundle with specified locale, and with properties loaded from the same classLoader than ResourceBundle class
    */
   public static I18nMessages getMessages(final String baseName, final Locale targetLocale) {
	return (MessageBundle) ResourceBundle.getBundle(baseName, targetLocale, MessageBundle.class.getClassLoader(), new MessageBundleControl());
   }

   /**
    * @param baseName name of the resource
    * @param parent parent ResourceBundle
    * @return ResourceBundle with server default locale having a parent resource bundle
    */
   public static I18nMessages getMessages(final String baseName, final I18nMessages parent) {
	final MessageBundle bundle = (MessageBundle) ResourceBundle.getBundle(baseName, defaultLocale(), MessageBundle.class.getClassLoader(),
		new MessageBundleControl());
	bundle.setParent((MessageBundle) parent);
	return bundle;
   }

   /**
    * @param baseName name of the resource
    * @param parent parent ResourceBundle
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public static I18nMessages getMessages(final String baseName, final ResourceBundle parent) {
	final MessageBundle bundle = (MessageBundle) ResourceBundle.getBundle(baseName, defaultLocale(), MessageBundle.class.getClassLoader(),
		new MessageBundleControl());
	bundle.setParent(parent);
	return bundle;
   }

   /**
    * @param baseName
    * @param locale
    * @param parent parent ResourceBundle
    * @return ResourceBundle with server default locale having a parent resource bundle
    */
   public static I18nMessages getMessages(final String baseName, final Locale locale, final ResourceBundle parent) {
	final MessageBundle bundle = (MessageBundle) ResourceBundle.getBundle(baseName, locale, MessageBundle.class.getClassLoader(), new MessageBundleControl());
	bundle.setParent(parent);
	return bundle;
   }

   /**
    * @param baseName
    * @param locale
    * @param parent parent ResourceBundle
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public static I18nMessages getMessages(final String baseName, final Locale locale, final I18nMessages parent) {
	return getMessages(baseName, locale, (ResourceBundle) parent);
   }

   /**
    * @param baseName name of the resource
    * @param targetLocale specified locale
    * @param loader target class loader
    * @param parent resourceBundle parent
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public static I18nMessages getMessages(final String baseName, final Locale targetLocale, final ClassLoader loader, final I18nMessages parent) {
	final MessageBundle bundle = (MessageBundle) ResourceBundle.getBundle(baseName, targetLocale, loader, new MessageBundleControl());
	bundle.setParent((MessageBundle) parent);
	return bundle;
   }

   /**
    * @param baseName name of the resource
    * @param targetLocale specified locale
    * @param loader target class loader
    * @return ResourceBundle with specified locale, and with properties loaded from specified classLoader
    */
   public static I18nMessages getMessages(final String baseName, final Locale targetLocale, final ClassLoader loader) {
	return (MessageBundle) ResourceBundle.getBundle(baseName, targetLocale, loader, new MessageBundleControl());
   }

   /*
    * Build full base name of a class, using its package name and class name
    */
   static String buildBaseName(final Class<?> baseClass) {
	final StringBuffer str = new StringBuffer();

	if (baseClass.getClass().getPackage() != null) {
	   str.append(baseClass.getClass().getPackage().getName()).append(".");
	}

	str.append(baseClass.getCanonicalName());

	return str.toString();
   }

   /**
    * @return Default user or server local
    */
   static Locale defaultLocale() {
	return LocaleFactory.getDefaultFactory().getCurrentLocale();
   }

   /**
    * enable the jpa resource bundle control resolver
    */
   public static void enableJpaControl() {
	JpaIsEnabled = true;
   }

   /**
    * disable the jpa resource bundle control resolver
    */
   public static void disableJpaControl() {
	JpaIsEnabled = false;
   }

   // **** PRIVATE STATIC PART *************************************************************************************************************
   static boolean JpaIsEnabled;

   static {
	JpaIsEnabled = Boolean.valueOf(JavaSystemHelper.getSystemProperty(ENABLE_JPA_PROPERTY, "true"));
   }

}
