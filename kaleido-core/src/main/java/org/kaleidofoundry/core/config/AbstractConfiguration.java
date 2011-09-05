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
package org.kaleidofoundry.core.config;

import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyPropertiesRoot;
import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyPropertiesSeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyRoot;
import static org.kaleidofoundry.core.config.ConfigurationConstants.KeySeparator;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.Name;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.StorageAllowed;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.UpdateAllowed;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ConfigurationMessageBundle;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.event.EventListenerList;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.config.ConfigurationChangeEvent.ConfigurationChangeType;
import org.kaleidofoundry.core.config.entity.FireChangesReport;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.store.FileHandler;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreFactory;
import org.kaleidofoundry.core.store.SingleFileStore;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.AbstractPropertyAccessor;
import org.kaleidofoundry.core.util.ConverterHelper;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Abstract ancestor class of all implementations of Configuration, <br/>
 * It implements the basic and commons functionalities of the interface configuration. <br/>
 * It uses internally a {@link Cache} instance (thread safe) to handle key / property items<br/>
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Immutable
@ThreadSafe
@Task(comment = "bootstrap load for classpath or file uri configuration which can defined a specific cacheManagerRef or resourceStorageRef, ... which is not yet loaded at build time", labels = TaskLabel.Defect)
public abstract class AbstractConfiguration extends AbstractPropertyAccessor implements Configuration {

   protected static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

   // configuration name identifier
   protected final String name;

   // internal properties cache
   protected final Cache<String, Serializable> cacheProperties;

   // external persistent singleFileStore
   protected final SingleFileStore singleFileStore;

   // internal runtime context
   protected final RuntimeContext<Configuration> context;

   // configuration listeners support
   protected final EventListenerList listeners;

   // ordered & thread safe queue of the changes applied on the configuration properties
   private final LinkedBlockingQueue<ConfigurationChangeEvent> changesEvents;

   /**
    * @param context
    * @throws StoreException
    */
   protected AbstractConfiguration(@NotNull final RuntimeContext<Configuration> context) throws StoreException {
	this(null, null, context);
   }

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws StoreException
    * @throws IllegalArgumentException if resourceUri is illegal ({@link URISyntaxException})
    */
   protected AbstractConfiguration(String name, String resourceUri, @NotNull final RuntimeContext<Configuration> context) throws StoreException {

	super(context.getString(ConfigurationContextBuilder.MultiValuesSeparator), context.getString(ConfigurationContextBuilder.DateTimeFormat), context
		.getString(ConfigurationContextBuilder.NumberFormat));

	// argument & context inputs
	name = !StringHelper.isEmpty(name) ? name : (!StringHelper.isEmpty(context.getString(Name)) ? context.getString(Name) : context.getName());
	resourceUri = !StringHelper.isEmpty(resourceUri) ? resourceUri : context.getString(FileStoreUri);

	// context check
	if (StringHelper.isEmpty(name)) { throw new EmptyContextParameterException(Name, context); }
	if (StringHelper.isEmpty(resourceUri)) { throw new EmptyContextParameterException(FileStoreUri, context); }

	this.context = context;
	this.name = name;

	// internal file store instantiation
	final String fileStoreRef = context.getString(FileStoreRef);
	final FileStore fileStore;

	if (!StringHelper.isEmpty(fileStoreRef)) {
	   fileStore = FileStoreFactory.provides(resourceUri, new RuntimeContext<FileStore>(fileStoreRef, FileStore.class, context));
	} else {
	   fileStore = FileStoreFactory.provides(resourceUri);
	}
	singleFileStore = new SingleFileStore(resourceUri, fileStore);

	// internal cache key / value instantiation
	final CacheManager cacheManager;
	final String cacheManagerContextRef = context.getString(CacheManagerRef);

	if (!StringHelper.isEmpty(cacheManagerContextRef)) {
	   cacheManager = CacheManagerFactory.provides(new RuntimeContext<CacheManager>(cacheManagerContextRef, CacheManager.class, context));
	} else {
	   cacheManager = CacheManagerFactory.provides();
	}
	cacheProperties = cacheManager.getCache("kaleidofoundry/configuration/" + name);

	// events listeners
	listeners = new EventListenerList();
	changesEvents = new LinkedBlockingQueue<ConfigurationChangeEvent>();

   }

   /**
    * don't use it,
    * this constructor is only needed and used by some IOC framework like spring.
    */
   AbstractConfiguration() {
	name = null;
	listeners = null;
	cacheProperties = null;
	context = null;
	singleFileStore = null;
	changesEvents = null;
   }

   /**
    * you don't need to release resourceHandler argument, it is done by agregator <br/>
    * <b>be careful, if you use {@link #setProperty(String, Serializable)}, event will be fired...</b> a preferred way is to use
    * properties.put(normalizeKey(key), value)
    * 
    * @param resourceHandler
    * @param properties
    * @return internal cache instance
    * @throws StoreException
    * @throws ConfigurationException
    */
   protected abstract Cache<String, Serializable> loadProperties(FileHandler resourceHandler, Cache<String, Serializable> properties) throws StoreException,
   ConfigurationException;

   /**
    * you don't need to release resourceHandler argument, it is done by agregator
    * 
    * @param fileStore
    * @param properties
    * @return internal cache instance
    * @throws StoreException
    * @throws ConfigurationException
    */
   protected abstract Cache<String, Serializable> storeProperties(Cache<String, Serializable> properties, SingleFileStore fileStore) throws StoreException,
   ConfigurationException;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getName()
    */
   @Override
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getResourceUri()
    */
   @Override
   public String getResourceUri() {
	return singleFileStore.getResourceBinding();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#isStorageAllowed()
    */
   @Override
   public boolean isStorable() {
	if (StringHelper.isEmpty(context.getString(StorageAllowed))) {
	   return true;
	} else {
	   return Boolean.valueOf(context.getString(StorageAllowed));
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#isUpdateAllowed()
    */
   @Override
   public boolean isUpdateable() {
	if (StringHelper.isEmpty(context.getString(UpdateAllowed))) {
	   return true;
	} else {
	   return Boolean.valueOf(context.getString(UpdateAllowed));
	}
   }

   // **************************************************************************
   // -> Load / singleFileStore management
   // **************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#isLoaded()
    */
   @Override
   public boolean isLoaded() {
	return singleFileStore.isLoaded();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#load()
    */
   @Override
   public final synchronized void load() throws StoreException, ConfigurationException {
	if (isLoaded()) { throw new ConfigurationException("config.load.already", name); }

	final FileHandler resourceHandler = singleFileStore.get();
	try {
	   loadProperties(resourceHandler, cacheProperties);
	} finally {
	   resourceHandler.release();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#store()
    */
   @Override
   public final synchronized void store() throws StoreException {
	if (!isLoaded()) { throw new ConfigurationException("config.load.notloaded", name); }
	if (!isStorable()) { throw new ConfigurationException("config.readonly.store", name); }

	singleFileStore.store();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#unload()
    */
   @Override
   public final synchronized void unload() throws StoreException, ConfigurationException {
	if (!isLoaded()) { throw new ConfigurationException("config.load.notloaded", name); }
	// cleanup cache entries
	cacheProperties.removeAll();
	// unload store
	singleFileStore.unload();
	// fire unload event
	fireUnload();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#reload()
    */
   @Override
   public final synchronized void reload() throws StoreException, ConfigurationException {
	if (!isLoaded()) { throw new ConfigurationException("config.load.notloaded", name); }
	// backup old entries
	final Map<String, Serializable> oldItems = new HashMap<String, Serializable>();
	for (final String oldPropName : cacheProperties.keys()) {
	   oldItems.put(oldPropName, cacheProperties.get(oldPropName));
	}
	// cleanup cache entries
	cacheProperties.removeAll();
	// unload store
	singleFileStore.unload();
	// load it
	load();

	// compare each property - if there are different : fire appropriate event
	for (final Entry<String, Serializable> entry : oldItems.entrySet()) {

	   // a property have been removed
	   if (!cacheProperties.keys().contains(entry.getKey())) {
		firePropertyRemove(entry.getKey(), entry.getValue());
		continue;
	   }

	   // a property have been changed
	   final Serializable oldValue = oldItems.get(entry.getKey());
	   final Serializable newValue = cacheProperties.get(entry.getKey());

	   if (oldValue != null && oldValue.equals(newValue)) {
		firePropertyUpdate(entry.getKey(), entry.getValue(), cacheProperties.get(entry.getKey()));
		continue;
	   }
	}
   }

   // ***************************************************************************
   // -> listener used for configuration changes management
   // ***************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#addConfigurationChangeListener(java.beans.PropertyChangeListener)
    */
   @Override
   public void addConfigurationListener(final ConfigurationListener listener) {
	listeners.add(ConfigurationListener.class, listener);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#removeConfigurationChangeListener(java.beans.PropertyChangeListener)
    */
   @Override
   public void removeConfigurationListener(final ConfigurationListener listener) {
	listeners.remove(ConfigurationListener.class, listener);
   }

   /**
    * fire event will be queued until {@link #fireConfigurationChangesEvents} was called
    * 
    * @param propertyName
    * @param newValue
    */
   protected void firePropertyCreate(final String propertyName, final Serializable newValue) {
	final ConfigurationChangeEvent event = ConfigurationChangeEvent.newCreateEvent(this, propertyName, newValue);
	changesEvents.add(event);

   }

   /**
    * fire event will be queued until {@link #fireConfigurationChangesEvents} was called
    * 
    * @param propertyName
    * @param oldValue
    * @param newValue
    */
   protected void firePropertyUpdate(final String propertyName, final Serializable oldValue, final Serializable newValue) {
	final ConfigurationChangeEvent event = ConfigurationChangeEvent.newUpdateEvent(this, propertyName, oldValue, newValue);
	changesEvents.add(event);
   }

   /**
    * fire event will be queued until {@link #fireConfigurationChangesEvents} was called
    * 
    * @param propertyName
    * @param oldValue
    */
   protected void firePropertyRemove(final String propertyName, final Serializable oldValue) {
	final ConfigurationChangeEvent event = ConfigurationChangeEvent.newRemoveEvent(this, propertyName, oldValue);
	changesEvents.add(event);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#fireConfigurationChangesEvents()
    */
   @Override
   public FireChangesReport fireConfigurationChangesEvents() {

	int created = 0;
	int updated = 0;
	int removed = 0;
	int listenerCount = 0;

	// count event by types
	for (final ConfigurationChangeEvent event : changesEvents) {
	   if (event.getConfigurationChangeType() == ConfigurationChangeType.CREATE) {
		created++;
	   }
	   if (event.getConfigurationChangeType() == ConfigurationChangeType.UPDATE) {
		updated++;
	   }
	   if (event.getConfigurationChangeType() == ConfigurationChangeType.REMOVE) {
		removed++;
	   }
	}

	// fire event to registered listener
	for (final ConfigurationListener listener : listeners.getListeners(ConfigurationListener.class)) {

	   for (final ConfigurationChangeEvent event : changesEvents) {
		if (event.getConfigurationChangeType() == ConfigurationChangeType.CREATE) {
		   listener.propertyCreate(event);
		}
		if (event.getConfigurationChangeType() == ConfigurationChangeType.UPDATE) {
		   listener.propertyUpdate(event);
		}
		if (event.getConfigurationChangeType() == ConfigurationChangeType.REMOVE) {
		   listener.propertyRemove(event);
		}
		listenerCount++;
	   }
	}

	// clear past fire events
	changesEvents.clear();

	// created message report
	FireChangesReport fireReport = new FireChangesReport(getName(), singleFileStore.getResourceBinding(), created, updated, removed, listenerCount);

	// log message
	LOGGER.info(ConfigurationMessageBundle.getMessage("config.firechanges.info0", name, singleFileStore.getResourceBinding(), fireReport.getCreated(),
		fireReport.getUpdated(), fireReport.getRemoved(), fireReport.getListernerCount()));
	LOGGER.info(ConfigurationMessageBundle.getMessage("config.firechanges.info1", fireReport.getCreated(), fireReport.getUpdated(), fireReport.getRemoved()));
	LOGGER.info(ConfigurationMessageBundle.getMessage("config.firechanges.info2", fireReport.getListernerCount()));

	return fireReport;
   }

   /**
    */
   protected void fireUnload() {
	for (final ConfigurationListener listener : listeners.getListeners(ConfigurationListener.class)) {
	   listener.configurationUnload(this);
	}
   }

   // ***************************************************************************
   // -> Roots management
   // ***************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getRoots()
    */
   @Override
   public Set<String> roots() {
	return roots(KeyRoot);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getRoots(java.lang.String)
    */
   @Override
   public Set<String> roots(final String prefix) {

	final String fullKey = normalizeKey(prefix);
	final Set<String> roots = new LinkedHashSet<String>();

	for (String pKey : cacheProperties.keys()) {
	   if (pKey.startsWith(fullKey)) {

		pKey = pKey.substring(fullKey.length());
		final StringTokenizer st = new StringTokenizer(pKey, KeySeparator);

		if (st.hasMoreTokens()) {
		   final String root = st.nextToken();
		   if (!roots.contains(root)) {
			roots.add(root);
		   }
		}
	   }
	}

	return roots;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#containsRoot(java.lang. String, java.lang.String)
    */
   @Override
   public boolean containsRoot(final String rootKey, final String prefixRoot) {
	return roots(prefixRoot).contains(rootKey);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getRootsIterator()
    */
   @Override
   public Iterator<String> rootsIterator() {
	return roots().iterator();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getRootsIterator(java.lang .String)
    */
   @Override
   public Iterator<String> rootsIterator(final String prefix) {
	return roots(prefix).iterator();
   }

   // ***************************************************************************
   // -> Keys management
   // ***************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#keySet()
    */
   @Override
   public Set<String> keySet() {
	return keySet(KeyRoot);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#keySet(java.lang.String)
    */
   @Override
   public Set<String> keySet(final String prefix) {
	final String fullKey = normalizeKey(prefix);
	final Set<String> keys = new LinkedHashSet<String>();

	for (final String pKey : cacheProperties.keys()) {
	   if (pKey.startsWith(fullKey)) {
		keys.add(pKey);
	   }
	}
	return keys;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#containsKey(java.lang.String)
    */
   @Override
   public boolean containsKey(final String key) {
	return containsKey(key, "");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#containsKey(java .lang.String)
    */
   @Override
   public boolean containsKey(final String key, final String prefix) {
	return keySet(prefix).contains(normalizeKey(key));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getKeysIterator()
    */
   @Override
   public Iterator<String> keysIterator() {
	return keySet().iterator();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getKeysIterator( java.lang.String)
    */
   @Override
   public Iterator<String> keysIterator(final String prefix) {
	return keySet(prefix).iterator();
   }

   // ***************************************************************************
   // -> Property value accessors
   // ***************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getProperty(java.lang.String)
    */
   @Override
   public Serializable getProperty(final String key) {
	return cacheProperties.get(normalizeKey(key));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#setProperty(java.lang.String, java.io.Serializable)
    */
   @Override
   public void setProperty(@NotNull final String key, @NotNull final Serializable newValue) {
	if (!isUpdateable()) { throw new ConfigurationException("config.readonly.update", name); }
	// normalize the given key
	final String fullKey = normalizeKey(key);
	// is it a new property ?
	final boolean newProperty = !cacheProperties.keys().contains(fullKey);
	// memorize old value for fire event
	final Serializable oldValue = cacheProperties.get(fullKey);
	// update cache data
	cacheProperties.put(fullKey, newValue);
	// fire change event
	if (newProperty) {
	   firePropertyCreate(fullKey, newValue);
	} else {
	   firePropertyUpdate(fullKey, oldValue, newValue);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#removeProperty(java.lang.String)
    */
   @Override
   public void removeProperty(@NotNull final String key) {
	if (!isUpdateable()) { throw new ConfigurationException("config.readonly.update", name); }
	// normalize the given key
	final String fullKey = normalizeKey(key);
	// memorize old value for fire event
	final Serializable oldValue = cacheProperties.get(fullKey);
	// remove it from cache
	cacheProperties.remove(fullKey);
	// fire change event
	firePropertyRemove(fullKey, oldValue);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#toProperties(java.lang.String)
    */
   @Override
   public Properties toProperties(final String rootPrefix) {

	final Properties prop = new Properties();
	final Set<String> keys = keySet(rootPrefix);

	for (final String string : keys) {
	   String propPath = string;
	   final String propValue = getString(propPath);
	   final List<String> propValues = getStringList(propPath);

	   propPath = StringHelper.replaceAll(propPath, KeyRoot, KeyPropertiesRoot);
	   propPath = StringHelper.replaceAll(propPath, KeySeparator, KeyPropertiesSeparator);

	   if (propValues != null) {
		prop.put(propPath, ConverterHelper.collectionToString(propValues, MultiValuesSeparator));
	   } else if (propValue != null) {
		prop.put(propPath, propValue);
	   }
	}

	return prop;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#toProperties()
    */
   @Override
   public Properties toProperties() {
	return toProperties(KeyRoot);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#addConfiguration(org.kaleidofoundry.core.config.Configuration)
    */
   @Override
   public Configuration addConfiguration(final Configuration config) {

	if (config != null) {
	   for (final String propName : config.keySet()) {
		final Serializable propValue = config.getProperty(propName);
		if (propValue != null) {
		   setProperty(propName, propValue);
		}
	   }
	}
	return this;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#extractConfiguration(java.lang.String,
    * org.kaleidofoundry.core.config.Configuration)
    */
   @Override
   public Configuration extractConfiguration(@NotNull String prefix, @NotNull final Configuration outConfiguration) {

	prefix = normalizeKey(prefix);

	for (final String propName : keySet()) {
	   final Serializable propValue = getProperty(propName);

	   if (StringHelper.isEmpty(prefix)) {
		outConfiguration.setProperty(propName, propValue);
	   } else {
		final String newPropName = StringHelper.replaceAll(propName, prefix + KeySeparator, "");
		if (propValue != null && !propName.equals(newPropName)) {
		   outConfiguration.setProperty(newPropName, propValue);
		}
	   }
	}

	return outConfiguration;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	final StringBuilder str = new StringBuilder("{");
	for (final Iterator<String> it = keysIterator(); it.hasNext();) {
	   final String key = it.next();
	   final String value = getString(key);
	   final List<String> values = getStringList(key);
	   if (values != null && values.size() <= 1) {
		str.append(key).append("=").append(value).append(" , ");
	   } else {
		str.append(key).append("=").append(ConverterHelper.collectionToString(values, MultiValuesSeparator)).append(" , ");
	   }
	}
	str.append("}");
	return str.toString();
   }

   /**
    * Normalize property path from argument. If standard property key is used like "application.name", it will be internally convert to
    * "//application/name"
    * 
    * @param propertyPath
    * @return Normalize propertyPath argument
    */
   public static String normalizeKey(@NotNull final String propertyPath) {
	final StringBuilder normalizeKey = new StringBuilder();
	if (!propertyPath.startsWith(KeyRoot)) {
	   normalizeKey.append(KeyRoot);
	}
	normalizeKey.append(StringHelper.replaceAll(propertyPath, KeyPropertiesSeparator, KeySeparator));
	return normalizeKey.toString();
   }

}
