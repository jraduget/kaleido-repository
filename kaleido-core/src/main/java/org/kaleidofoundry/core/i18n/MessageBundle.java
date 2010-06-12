package org.kaleidofoundry.core.i18n;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 *         TODO handle RuntimeContext for cache injection (with cache, local ....)
 */
public class MessageBundle extends ResourceBundle implements I18nMessages {

   static final Logger LOGGER = LoggerFactory.getLogger(MessageBundle.class);

   // used for user resource bundle datas
   private final Cache<String, String> resourceBundleCache;
   // used internally for kaleido-core internal resource bundle data
   private final Properties resourceBundleNoCache;

   private final String resourceName;
   private ResourceBundle parent;

   /**
    * @param resourceName
    * @param properties
    */
   public MessageBundle(final String resourceName, final Properties properties) {
	this(resourceName, properties, new RuntimeContext<MessageBundle>());
   }

   /**
    * @param resourceName
    * @param properties
    * @param runtimeContext
    */
   public MessageBundle(final String resourceName, final Properties properties, final RuntimeContext<MessageBundle> runtimeContext) {

	// internal kaleido-foundry resource bundle, does not use internal cache
	if (InternalBundleEnum.isInternalBundle(resourceName)) {
	   LOGGER.debug("create message bundle (without cache) for '{}'", resourceName);
	   resourceBundleNoCache = properties;
	   resourceBundleCache = null;
	}
	// user resource bundle, does use internal cache
	else {
	   LOGGER.debug("create message bundle (with cacheFactory) for '{}'", resourceName);
	   final CacheFactory<String, String> cacheFactory = CacheFactory.getCacheFactory();
	   resourceBundleCache = cacheFactory.getCache("kaleidofoundry/messageBundle/" + resourceName);
	   resourceBundleNoCache = null;
	   // copy common properties to internal Cache<String,String> storage
	   for (String propName : properties.stringPropertyNames()) {
		resourceBundleCache.put(propName, properties.getProperty(propName));
	   }
	}

	this.resourceName = resourceName;

   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
    */
   @Override
   protected Object handleGetObject(final String key) {
	return resourceBundleCache != null ? resourceBundleCache.get(key) : resourceBundleNoCache.getProperty(key);
   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle#getKeys()
    */
   @Override
   public Enumeration<String> getKeys() {
	final Set<String> handleKeys = resourceBundleCache != null ? resourceBundleCache.keys() : resourceBundleNoCache.stringPropertyNames();
	return Collections.enumeration(handleKeys);
   }

   /**
    * @param key message code
    * @return the i18n message mapping to key parameter
    */
   public String getMessage(final String key) throws MissingResourceException {
	return getMessage(key, (String[]) null);
   }

   /**
    * <p>
    * Gets a string message from the resource bundle for the given key. The message may contain variables that will be substituted with the
    * given arguments. Variables have the format:
    * </p>
    * <code> This message has two variables: {0} and {1} </code>
    * 
    * @param key The resource key
    * @param array An array of objects to place in corresponding variables
    * @return the i18n message mapping to key parameter
    */
   public String getMessage(final String key, final String... array) throws MissingResourceException {
	String msg = null;

	msg = getString(key);

	if (msg == null) {
	   // it can't be an i18n message, where are inside the bundle currently instantiate
	   throw new MissingResourceException("Cannot find message key '" + key + "' in resource '" + resourceName + "'", String.class.getName(), key);
	}

	return MessageFormat.format(msg, (Object[]) array);
   }

   /**
    * @return parent resource bundle
    */
   public ResourceBundle getParent() {
	return parent;
   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle#setParent(java.util.ResourceBundle)
    */
   @Override
   public void setParent(final ResourceBundle parent) {
	this.parent = parent;
	super.setParent(parent);
   }

   /**
    * @return resource name of the bundle
    */
   public String getResourceName() {
	return resourceName;
   }

}
