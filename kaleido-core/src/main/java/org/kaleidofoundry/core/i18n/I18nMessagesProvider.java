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
package org.kaleidofoundry.core.i18n;

import static org.kaleidofoundry.core.env.model.EnvironmentConstants.I18N_JPA_ACTIVATION_PROPERTY;
import static org.kaleidofoundry.core.env.model.EnvironmentConstants.STATIC_ENV_PARAMETERS;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.ClassLoaderClass;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.LocaleCountry;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.LocaleLanguage;

import java.util.Locale;
import java.util.ResourceBundle;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.IllegalContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.core.util.locale.LocaleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
public class I18nMessagesProvider extends AbstractProviderService<I18nMessages> {

   private static final Logger LOGGER = LoggerFactory.getLogger(I18nMessagesProvider.class);

   /**
    * @param genericClassInterface
    */
   public I18nMessagesProvider(final Class<I18nMessages> genericClassInterface) {
	super(genericClassInterface);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.AbstractProviderService#getRegistry()
    */
   @Override
   protected Registry<String, I18nMessages> getRegistry() {
	throw new IllegalStateException("I18nMessages registry is managed by java ResourceBundle api");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.ProviderService#_provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public I18nMessages _provides(@NotNull final RuntimeContext<I18nMessages> context) {

	String baseName = context.getString(BaseName);
	final String localeLanguageCode = context.getString(LocaleLanguage);
	final String countryLanguageCode = context.getString(LocaleCountry);
	final String classLoaderClass = context.getString(ClassLoaderClass);

	ClassLoader classLoader = null;
	Locale locale = null;

	// default baseName is not defined
	if (StringHelper.isEmpty(baseName)) {
	   if (StringHelper.isEmpty(context.getName())) { throw new EmptyContextParameterException(BaseName, context); }
	   baseName = "i18n/" + context.getName();
	}

	// managed classloader context
	try {
	   classLoader = StringHelper.isEmpty(classLoaderClass) ? null : Class.forName(classLoaderClass).getClassLoader();
	} catch (final ClassNotFoundException cnfe) {
	   throw new IllegalContextParameterException(ClassLoaderClass, classLoaderClass, context, cnfe);
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
    * @param context runtime context
    * @return ResourceBundle with server default locale, and with properties loaded from the same classLoader than ResourceBundle class
    */
   public I18nMessages provides(@NotNull final String baseName, final RuntimeContext<I18nMessages> context) {
	return getBundle(baseName, null, null, null, context);
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
	LOGGER.info("Clear all resources bundles caches");
	ResourceBundle.clearCache();
	ResourceBundle.clearCache(I18nMessagesFactory.class.getClassLoader());
   }

   /*
    * Build full base name of a class, using its package name and class name
    */
   static String buildBaseName(final Class<?> baseClass) {
	final StringBuilder str = new StringBuilder();

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
    * @param context runtime context
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
    * enable the jpa resource bundle control resolver
    */
   public static void enableJpaControl() {
	STATIC_ENV_PARAMETERS.put(I18N_JPA_ACTIVATION_PROPERTY, Boolean.TRUE.toString());
   }

   /**
    * disable the jpa resource bundle control resolver
    */
   public static void disableJpaControl() {
	STATIC_ENV_PARAMETERS.put(I18N_JPA_ACTIVATION_PROPERTY, Boolean.FALSE.toString());
   }
   
   /**
    * @return do JPA is enable to get and persist i18n messages bundle
    */
   public static boolean isJpaEnabledForI18n() {
	String result = STATIC_ENV_PARAMETERS.get(I18N_JPA_ACTIVATION_PROPERTY);
	if (result == null) {
	   return false;
	} else {
	   return Boolean.valueOf(result);
	}
   }
   
   /**
    * @return Default user or server local
    */
   @NotNull
   static Locale defaultLocale() {
	return LocaleFactory.getDefaultFactory().getCurrentLocale();
   }

}
