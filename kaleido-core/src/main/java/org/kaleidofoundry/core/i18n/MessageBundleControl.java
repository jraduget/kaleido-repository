/*  
 * Copyright 2008-2014 the original author or authors 
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.model.I18nMessageLanguage;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.lang.annotation.Tasks;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
@ThreadSafe
public class MessageBundleControl extends Control {

   private static final Logger LOGGER = LoggerFactory.getLogger(MessageBundleControl.class);

   private final RuntimeContext<I18nMessages> context;

   /**
    * @param context
    */
   public MessageBundleControl(final RuntimeContext<I18nMessages> context) {
	this.context = context;
   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle.Control#getFormats(java.lang.String)
    */
   @Override
   public List<String> getFormats(final String baseName) {
	final List<String> lformat = new ArrayList<String>(MessageBundleControlFormat.values().length);
	for (final MessageBundleControlFormat format : MessageBundleControlFormat.values()) {
	   lformat.add(format.name());
	}
	return Collections.unmodifiableList(lformat);
   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle.Control#newBundle(java.lang.String, java.util.Locale, java.lang.String, java.lang.ClassLoader, boolean)
    */
   @Override
   public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload)
	   throws IllegalAccessException, InstantiationException, IOException {
	final MessageBundleControlFormat controlFormat = MessageBundleControlFormat.valueOf(format);

	if (controlFormat == null) { throw new IllegalArgumentException("format: " + format); }
	if (baseName == null || locale == null || format == null || loader == null) { throw new NullPointerException(); }

	return newInputStreamBundle(baseName, locale, controlFormat, loader, reload);
   }

   /**
    * inputStream properties loader
    * 
    * @param baseName
    * @param locale
    * @param format
    * @param loader
    * @param reload
    * @return resource bundle
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws IOException
    */
   @Tasks(tasks = {
	   @Task(comment = "Make messageBundleControl extensible via {@link Plugin} extention. Load can be done via xml, jpa... ?", labels = TaskLabel.Enhancement),
	   @Task(comment = "Use fileStore to load bundle resource", labels = TaskLabel.Enhancement) })
   ResourceBundle newInputStreamBundle(final String baseName, final Locale locale, final MessageBundleControlFormat format, final ClassLoader loader,
	   final boolean reload) throws IllegalAccessException, InstantiationException, IOException {

	final String bundleName = toBundleName(baseName, locale);
	final String resourceName = toResourceName(bundleName, format.getExtention());

	DefaultMessageBundle messageBundle = null;
	final Properties properties = new Properties();
	InputStream inProperties = null;

	boolean foundResource = false;

	if (LOGGER.isDebugEnabled()) {
	   LOGGER.debug("newBundle resolver for");
	   LOGGER.debug("\tbaseName={}", baseName);
	   LOGGER.debug("\tlocale={}", locale.toString());
	   LOGGER.debug("\tformat={}", format.name());
	   LOGGER.debug("\tformat extention={}", format.getExtention());
	   LOGGER.debug("\treload={}", reload);
	   LOGGER.debug("\t-> resourceName={}", resourceName);
	}

	// Loader...
	try {

	   // standard properties datas....
	   if (format == MessageBundleControlFormat.STANDARD_PROPERTIES) {
		inProperties = newUrlInputStream(loader, resourceName, reload);
		if (inProperties != null) {
		   properties.load(inProperties);
		   foundResource = true;
		}
	   }

	   // xml properties datas...
	   if (format == MessageBundleControlFormat.XML_PROPERTIES) {
		inProperties = newUrlInputStream(loader, resourceName, reload);
		if (inProperties != null) {
		   properties.loadFromXML(inProperties);
		   foundResource = true;
		}
	   }

	   // jpa entity datas
	   if (format == MessageBundleControlFormat.JPA_ENTITY_PROPERTIES && I18nMessagesProvider.isJpaEnabledForI18n()) {

		@Task(comment = "create a service injector (local / guice / spring / ejb3 local or remote...)")
		final I18nMessageController messageService = new I18nMessageController();
		final List<I18nMessageLanguage> messagesLanguage = messageService.findMessagesByLocale(baseName, locale);

		if (!messagesLanguage.isEmpty()) {
		   for (final I18nMessageLanguage ml : messagesLanguage) {
			properties.put(ml.getMessage().getCode(), ml.getContent());
		   }
		   foundResource = true;
		}
	   }

	} catch (final RuntimeException rie) {
	   // usefull for jpa entity manager error
	   if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("debug resourceBundle control resolver error: '{}'", rie.getMessage());
	   }
	   throw rie;
	} finally {
	   if (inProperties != null) {
		inProperties.close();
	   }
	}

	if (foundResource) {
	   messageBundle = new DefaultMessageBundle(resourceName, properties, context);
	   LOGGER.debug("\t-> resource found !");
	} else {
	   LOGGER.debug("\t-> resource not found...");
	}

	return messageBundle;
   }

   /**
    * Http file resource loading
    * 
    * @param loader
    * @param resourceName
    * @param reload
    * @return resource input stream
    * @throws IOException
    */
   InputStream newUrlInputStream(final ClassLoader loader, final String resourceName, final boolean reload) throws IOException {

	InputStream stream = null;

	if (reload) {
	   final URL url = loader.getResource(resourceName);
	   if (url != null) {
		final URLConnection connection = url.openConnection();
		if (connection != null) {
		   // Disable caches to get fresh data for reloading.
		   connection.setUseCaches(false);
		   stream = connection.getInputStream();
		}
	   }
	} else {
	   stream = loader.getResourceAsStream(resourceName);
	}
	return stream;

   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle.Control#getFallbackLocale(java.lang.String, java.util.Locale)
    */
   @Override
   public Locale getFallbackLocale(final String baseName, final Locale locale) {
	return locale.equals(Locale.ROOT) ? null : Locale.ROOT;
   }

}
