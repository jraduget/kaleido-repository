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
package org.kaleidofoundry.core.i18n;

import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.ClassLoaderClass;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.LocaleCountry;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.LocaleLanguage;

import java.util.Locale;
import java.util.ResourceBundle;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.context.RuntimeContextEmptyParameterException;
import org.kaleidofoundry.core.context.RuntimeContextIllegalParameterException;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.system.JavaSystemHelper;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.core.util.locale.LocaleFactory;

/**
 * @author Jerome RADUGET
 */
public class I18nMessagesProvider extends AbstractProviderService<I18nMessages> {

   /** Enable or disable jpa entity manager resolution for resourceBundle */
   public static final String ENABLE_JPA_PROPERTY = "kaleidofoundry.i18n.jpa.enabled";

   /**
    * @param genericClassInterface
    */
   public I18nMessagesProvider(final Class<I18nMessages> genericClassInterface) {
	super(genericClassInterface);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.ProviderService#_provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public I18nMessages _provides(@NotNull final RuntimeContext<I18nMessages> context) {

	final String baseName = context.getProperty(BaseName);
	final String localeLanguageCode = context.getProperty(LocaleLanguage);
	final String countryLanguageCode = context.getProperty(LocaleCountry);
	final String classLoaderClass = context.getProperty(ClassLoaderClass);

	ClassLoader classLoader = null;
	Locale locale = null;

	// managed baseName
	if (StringHelper.isEmpty(baseName)) { throw new RuntimeContextEmptyParameterException(BaseName, context); }

	// managed classloader context
	try {
	   classLoader = StringHelper.isEmpty(classLoaderClass) ? null : Class.forName(classLoaderClass).getClassLoader();
	} catch (final ClassNotFoundException cnfe) {
	   throw new RuntimeContextIllegalParameterException(ClassLoaderClass, classLoaderClass, context, cnfe);
	}

	// managed locale language & country context
	if (!StringHelper.isEmpty(localeLanguageCode) && !StringHelper.isEmpty(countryLanguageCode)) {
	   locale = new Locale(localeLanguageCode, countryLanguageCode);
	} else if (!StringHelper.isEmpty(localeLanguageCode) && StringHelper.isEmpty(countryLanguageCode)) {
	   locale = new Locale(localeLanguageCode, defaultLocale().getCountry());
	} else if (StringHelper.isEmpty(localeLanguageCode) && !StringHelper.isEmpty(countryLanguageCode)) {
	   locale = new Locale(defaultLocale().getLanguage(), countryLanguageCode);
	}

	return getBundle(baseName, locale, classLoader, null, context);
   }

   /**
    * @param baseName name of the resource
    * @return ResourceBundle with server default locale, and with properties loaded from the same classLoader than ResourceBundle class
    */
   public I18nMessages provides(@NotNull final String baseName) {
	return getBundle(baseName, null, null, null, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param baseName name of the resource
    * @param locale specified locale
    * @return ResourceBundle with specified locale, and with properties loaded from the same classLoader than ResourceBundle class
    */
   public I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale) {
	return getBundle(baseName, locale, null, null, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param baseName name of the resource
    * @param parent parent ResourceBundle
    * @return ResourceBundle with server default locale having a parent resource bundle
    */
   public I18nMessages provides(@NotNull final String baseName, final I18nMessages parent) {
	return getBundle(baseName, null, null, (ResourceBundle) parent, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param baseName name of the resource
    * @param parent parent ResourceBundle
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public I18nMessages provides(@NotNull final String baseName, final ResourceBundle parent) {
	return getBundle(baseName, null, null, parent, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param baseName
    * @param locale
    * @param parent parent ResourceBundle
    * @return ResourceBundle with server default locale having a parent resource bundle
    */
   public I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final ResourceBundle parent) {
	return getBundle(baseName, locale, null, parent, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param baseName
    * @param locale
    * @param parent parent ResourceBundle
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final I18nMessages parent) {
	return getBundle(baseName, locale, null, (ResourceBundle) parent, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param baseName name of the resource
    * @param locale specified locale
    * @param loader target class loader
    * @param parent resourceBundle parent
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final ClassLoader loader, final I18nMessages parent) {
	return getBundle(baseName, locale, loader, (ResourceBundle) parent, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param baseName name of the resource
    * @param locale specified locale
    * @param loader target class loader
    * @return ResourceBundle with specified locale, and with properties loaded from specified classLoader
    */
   public I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final ClassLoader loader) {
	return getBundle(baseName, locale, loader, null, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * it clear internal java resource bundle cache<br/>
    * <br/>
    * when you call multiple times {@link #provides(String)}, jdk use an internal cache to get the current bundle
    * once you call {@link #clearCache()}, then next call to {@link #provides(String)} will instantiate / load ... a new
    * {@link DefaultMessageBundle}
    * 
    * @see ResourceBundle#clearCache()
    */
   public static void clearCache() {
	ResourceBundle.clearCache();
	ResourceBundle.clearCache(I18nMessagesFactory.class.getClassLoader());
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
    * @param baseName name of the resource
    * @param locale specified locale
    * @param loader target class loader
    * @param parent resourceBundle parent
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   static I18nMessages getBundle(@NotNull final String baseName, final Locale locale, final ClassLoader loader, final ResourceBundle parent,
	   @NotNull final RuntimeContext<I18nMessages> context) {

	final DefaultMessageBundle bundle = (DefaultMessageBundle) ResourceBundle.getBundle(baseName, locale != null ? locale : defaultLocale(),
		loader != null ? loader : DefaultMessageBundle.class.getClassLoader(), new MessageBundleControl(context));
	bundle.setParent(parent);
	return bundle;
   }

   /**
    * @return Default user or server local
    */
   @NotNull
   static Locale defaultLocale() {
	return LocaleFactory.getDefaultFactory().getCurrentLocale();
   }

   // **** PRIVATE STATIC PART *************************************************************************************************************
   static boolean JpaIsEnabled;

   static {
	JpaIsEnabled = Boolean.valueOf(JavaSystemHelper.getSystemProperty(ENABLE_JPA_PROPERTY, "true"));
   }
}
