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
import static org.kaleidofoundry.core.config.ConfigurationConstants.LOGGER;
import static org.kaleidofoundry.core.config.ConfigurationConstants.MultiValDefaultSeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.StrDateFormat;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.ResourceStoreRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.StorageAllowed;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.UpdateAllowed;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ConfigurationMessageBundle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.NotYetImplementedException;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.ResourceStore;
import org.kaleidofoundry.core.store.ResourceStoreFactory;
import org.kaleidofoundry.core.store.SingleResourceStore;
import org.kaleidofoundry.core.util.ConverterHelper;
import org.kaleidofoundry.core.util.StringHelper;

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
@Review(comment = "bootstrap load for classpath or file uri configuration which can defined a specific cacheManagerRef or resourceStoreRef, ... which is not yet loaded at build time", category = ReviewCategoryEnum.Fixme)
public abstract class AbstractConfiguration implements Configuration {

   // configuration name identifier
   protected final String name;

   // internal properties cache
   protected final Cache<String, Serializable> cacheProperties;

   // external persistent singleStore
   protected final SingleResourceStore singleResourceStore;

   // internal runtime context
   protected final RuntimeContext<Configuration> context;

   // configuration listeners support
   protected final EventListenerList listeners;

   // ordered & thread safe queue of the changes applied on the configuration properties
   private final LinkedBlockingQueue<ConfigurationChangeEvent> changesEvents;

   /**
    * @param name
    * @param resourceUri
    * @param context
    * @throws ResourceException
    * @throws IllegalArgumentException if resourceUri is illegal ({@link URISyntaxException})
    */
   protected AbstractConfiguration(@NotNull final String name, @NotNull final String resourceUri, @NotNull final RuntimeContext<Configuration> context)
	   throws ResourceException {
	this.name = name.toString();
	this.context = context;

	// internal resource store instantiation
	final String resourceStoreRef = context.getProperty(ResourceStoreRef);
	final ResourceStore resourceStore;

	if (!StringHelper.isEmpty(resourceStoreRef)) {
	   resourceStore = ResourceStoreFactory.provides(resourceUri, new RuntimeContext<ResourceStore>(resourceStoreRef, ResourceStore.class, context));
	} else {
	   resourceStore = ResourceStoreFactory.provides(resourceUri);
	}
	singleResourceStore = new SingleResourceStore(resourceUri, resourceStore);

	// internal cache key / value instantiation
	final CacheManager cacheManager;
	final String cacheManagerContextRef = context.getProperty(CacheManagerRef);

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
    * you don't need to release resourceHandler argument, it is done by agregator <br/>
    * <b>be careful, if you use {@link #setProperty(String, Serializable)}, event will be fired...</b> a preferred way is to use
    * properties.put(normalizeKey(key), value)
    * 
    * @param resourceHandler
    * @param properties
    * @return internal cache instance
    * @throws ResourceException
    * @throws ConfigurationException
    */
   protected abstract Cache<String, Serializable> loadProperties(ResourceHandler resourceHandler, Cache<String, Serializable> properties)
	   throws ResourceException, ConfigurationException;

   /**
    * you don't need to release resourceHandler argument, it is done by agregator
    * 
    * @param resourceStore
    * @param properties
    * @return internal cache instance
    * @throws ResourceException
    * @throws ConfigurationException
    */
   protected abstract Cache<String, Serializable> storeProperties(Cache<String, Serializable> properties, SingleResourceStore resourceStore)
	   throws ResourceException, ConfigurationException;

   /**
    * Normalize property path from argument. If standard property key is used like "application.name", it will be internally convert to
    * "//application/name"
    * 
    * @param propertyPath
    * @return Normalize propertyPath argument
    */
   protected String normalizeKey(@NotNull final String propertyPath) {
	final StringBuilder normalizeKey = new StringBuilder();
	if (!propertyPath.startsWith(KeyRoot)) {
	   normalizeKey.append(KeyRoot);
	}
	normalizeKey.append(StringHelper.replaceAll(propertyPath, KeyPropertiesSeparator, KeySeparator));
	return normalizeKey.toString();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getName()
    */
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#isStorageAllowed()
    */
   public boolean isStorageAllowed() {
	if (StringHelper.isEmpty(context.getProperty(StorageAllowed))) {
	   return true;
	} else {
	   return Boolean.valueOf(context.getProperty(StorageAllowed));
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#isUpdateAllowed()
    */
   public boolean isUpdateAllowed() {
	if (StringHelper.isEmpty(context.getProperty(UpdateAllowed))) {
	   return true;
	} else {
	   return Boolean.valueOf(context.getProperty(UpdateAllowed));
	}
   }

   // **************************************************************************
   // -> Load / singleResourceStore management
   // **************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#isLoaded()
    */
   public boolean isLoaded() {
	return singleResourceStore.isLoaded();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#load()
    */
   @Override
   public final synchronized void load() throws ResourceException, ConfigurationException {
	if (isLoaded()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.already", name)); }

	final ResourceHandler resourceHandler = singleResourceStore.get();
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
   public final synchronized void store() throws ResourceException, ConfigurationException {
	if (!isLoaded()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.notloaded", name)); }
	if (!isStorageAllowed()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.readonly.store", name)); }

	singleResourceStore.store();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#unload()
    */
   @Override
   public final synchronized void unload() throws ResourceException {
	if (!isLoaded()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.notloaded", name)); }
	// cleanup cache entries
	cacheProperties.removeAll();
	// unload store
	singleResourceStore.unload();
	// fire unload event
	fireUnload();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#reload()
    */
   @Override
   public final synchronized void reload() throws ResourceException, ConfigurationException {
	if (!isLoaded()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.notloaded", name)); }
	// backup old entries
	final Map<String, Serializable> oldItems = new HashMap<String, Serializable>();
	for (final String oldPropName : cacheProperties.keys()) {
	   oldItems.put(oldPropName, cacheProperties.get(oldPropName));
	}
	// cleanup cache entries
	cacheProperties.removeAll();
	// unload store
	singleResourceStore.unload();
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
   public void addConfigurationListener(final ConfigurationListener listener) {
	this.listeners.add(ConfigurationListener.class, listener);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#removeConfigurationChangeListener(java.beans.PropertyChangeListener)
    */
   public void removeConfigurationListener(final ConfigurationListener listener) {
	this.listeners.remove(ConfigurationListener.class, listener);
   }

   /**
    * @param propertyName
    * @param newValue
    */
   protected void firePropertyCreate(final String propertyName, final Serializable newValue) {

	final ConfigurationChangeEvent event = ConfigurationChangeEvent.newCreateEvent(this, propertyName, newValue);
	changesEvents.add(event);

	for (final ConfigurationListener listener : listeners.getListeners(ConfigurationListener.class)) {
	   listener.propertyCreate(event);
	}
   }

   /**
    * @param propertyName
    * @param oldValue
    * @param newValue
    */
   protected void firePropertyUpdate(final String propertyName, final Serializable oldValue, final Serializable newValue) {

	final ConfigurationChangeEvent event = ConfigurationChangeEvent.newUpdateEvent(this, propertyName, oldValue, newValue);
	changesEvents.add(event);

	for (final ConfigurationListener listener : listeners.getListeners(ConfigurationListener.class)) {
	   listener.propertyUpdate(event);
	}
   }

   /**
    * @param propertyName
    * @param oldValue
    */
   protected void firePropertyRemove(final String propertyName, final Serializable oldValue) {

	final ConfigurationChangeEvent event = ConfigurationChangeEvent.newRemoveEvent(this, propertyName, oldValue);
	changesEvents.add(event);

	for (final ConfigurationListener listener : listeners.getListeners(ConfigurationListener.class)) {
	   listener.propertyRemove(event);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#fireConfigurationChangesEvents()
    */
   public void fireConfigurationChangesEvents() {

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
	   }
	}

	// clear past fire events
	changesEvents.clear();
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
   public Set<String> roots() {
	return roots(KeyRoot);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getRoots(java.lang.String)
    */
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
   public boolean containsRoot(final String rootKey, final String prefixRoot) {
	return roots(prefixRoot).contains(rootKey);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getRootsIterator()
    */
   public Iterator<String> rootsIterator() {
	return roots().iterator();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getRootsIterator(java.lang .String)
    */
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
    * @see org.kaleidofoundry.core.config.config.Configuration#containsKey(java .lang.String)
    */
   public boolean containsKey(final String key, final String prefix) {
	return keySet(prefix).contains(key);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getKeysIterator()
    */
   public Iterator<String> keysIterator() {
	return keySet().iterator();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getKeysIterator( java.lang.String)
    */
   public Iterator<String> keysIterator(final String prefix) {
	return keySet(prefix).iterator();
   }

   // ***************************************************************************
   // -> Property value accessors
   // ***************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getString(java.lang .String, java.lang.String)
    */
   public String getString(final String key, final String defaultValue) {
	final String s = getString(key);
	return s == null ? defaultValue : s;
   }

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
	if (!isUpdateAllowed()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.readonly.update", name)); }
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
	if (!isUpdateAllowed()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.readonly.update", name)); }
	// normalize the given key
	final String fullKey = normalizeKey(key);
	// memorize old value for fire event
	final Serializable oldValue = cacheProperties.get(fullKey);
	// remove it from cache
	cacheProperties.remove(fullKey);
	// fire change event
	firePropertyRemove(fullKey, oldValue);
   }

   // ***************************************************************************
   // -> Typed property value accessors
   // ***************************************************************************
   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getString(java.lang.String)
    */
   @Override
   public String getString(final String key) {
	return valueOf(getProperty(key), String.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getStringList(java.lang.String)
    */
   @Override
   public List<String> getStringList(final String key) {
	return valuesOf(getProperty(key), String.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimal(java .lang.String)
    */
   public BigDecimal getBigDecimal(final String key) {
	return valueOf(getProperty(key), BigDecimal.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimal(java .lang.String, java.math.BigDecimal)
    */
   public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
	final BigDecimal bd = getBigDecimal(key);
	return bd == null ? defaultValue : bd;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimalList (java.lang.String)
    */
   public List<BigDecimal> getBigDecimalList(final String key) {
	return valuesOf(getProperty(key), BigDecimal.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigInteger(java .lang.String)
    */
   public BigInteger getBigInteger(final String key) {
	return valueOf(getProperty(key), BigInteger.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigInteger(java .lang.String, java.math.BigInteger)
    */
   public BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
	final BigInteger bi = getBigInteger(key);
	return bi == null ? defaultValue : bi;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigIntegerList (java.lang.String)
    */
   public List<BigInteger> getBigIntegerList(final String key) {
	return valuesOf(getProperty(key), BigInteger.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBoolean(java. lang.String)
    */
   public Boolean getBoolean(final String key) {
	return valueOf(getProperty(key), Boolean.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBoolean(java. lang.String, java.lang.Boolean)
    */
   public Boolean getBoolean(final String key, final Boolean defaultValue) {
	final Boolean b = getBoolean(key);
	return b == null ? defaultValue : b;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBooleanList( java.lang.String)
    */
   public List<Boolean> getBooleanList(final String key) {
	return valuesOf(getProperty(key), Boolean.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByte(java.lang .String)
    */
   public Byte getByte(final String key) {
	return valueOf(getProperty(key), Byte.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByte(java.lang .String, java.lang.Byte)
    */
   public Byte getByte(final String key, final Byte defaultValue) {
	final Byte b = getByte(key);
	return b == null ? defaultValue : b;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByteList(java .lang.String)
    */
   public List<Byte> getByteList(final String key) {
	return valuesOf(getProperty(key), Byte.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDouble(java.lang .String)
    */
   public Double getDouble(final String key) {
	return valueOf(getProperty(key), Double.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDouble(java.lang .String, double)
    */
   public Double getDouble(final String key, final Double defaultValue) {
	final Double d = getDouble(key);
	return d == null ? defaultValue : d;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDoubleList(java .lang.String)
    */
   public List<Double> getDoubleList(final String key) {
	return valuesOf(getProperty(key), Double.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloat(java.lang .String)
    */
   public Float getFloat(final String key) {
	return valueOf(getProperty(key), Float.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloat(java.lang .String, java.lang.Float)
    */
   public Float getFloat(final String key, final Float defaultValue) {
	final Float f = getFloat(key);
	return f == null ? defaultValue : f;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloatList(java .lang.String)
    */
   public List<Float> getFloatList(final String key) {
	return valuesOf(getProperty(key), Float.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getInt(java.lang .String)
    */
   public Integer getInteger(final String key) {
	return valueOf(getProperty(key), Integer.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getInteger(java. lang.String, java.lang.Integer)
    */
   public Integer getInteger(final String key, final Integer defaultValue) {
	final Integer i = getInteger(key);
	return i == null ? defaultValue : i;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getIntList(java .lang.String)
    */
   public List<Integer> getIntegerList(final String key) {
	return valuesOf(getProperty(key), Integer.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLong(java.lang .String)
    */
   public Long getLong(final String key) {
	return valueOf(getProperty(key), Long.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLong(java.lang .String, java.lang.Long)
    */
   public Long getLong(final String key, final Long defaultValue) {
	final Long l = getLong(key);
	return l == null ? defaultValue : l;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLongList(java .lang.String)
    */
   public List<Long> getLongList(final String key) {
	return valuesOf(getProperty(key), Long.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShort(java.lang .String)
    */
   public Short getShort(final String key) {
	return valueOf(getProperty(key), Short.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShort(java.lang .String, java.lang.Short)
    */
   public Short getShort(final String key, final Short defaultValue) {
	final Short s = getShort(key);
	return s == null ? defaultValue : s;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShortList(java .lang.String)
    */
   public List<Short> getShortList(final String key) {
	return valuesOf(getProperty(key), Short.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDate(java.lang .String)
    */
   public Date getDate(final String key) {
	return valueOf(getProperty(key), Date.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getDateList(java.lang.String)
    */
   public List<Date> getDateList(final String key) {
	return valuesOf(getProperty(key), Date.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDate(java.lang .String, java.lang.Date)
    */
   public Date getDate(final String key, final Date defaultValue) {
	final Date d = getDate(key);
	return d == null ? defaultValue : d;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#toProperties(java.lang.String)
    */
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
		prop.put(propPath, ConverterHelper.collectionToString(propValues, MultiValDefaultSeparator));
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
   public Properties toProperties() {
	return toProperties(KeyRoot);
   }

   /**
    * String to Object Converter
    * 
    * @param <T>
    * @param value
    * @param cl Target class
    * @return Requested conversion of the string argument. If {@link NumberFormatException} or date {@link ParseException}, silent exception
    *         will be logged, and null return
    * @throws IllegalStateException for date or number parse error
    */
   @SuppressWarnings("unchecked")
   @Review(comment = "use SimpleDateFormat has a thread local", category = ReviewCategoryEnum.Improvement)
   public static <T> T valueOf(final Serializable value, final Class<T> cl) throws IllegalStateException {

	if (value == null) { return null; }

	if (value instanceof String) {

	   final String strValue = (String) value;

	   if (Boolean.class.isAssignableFrom(cl)) { return (T) Boolean.valueOf(strValue); }

	   if (Number.class.isAssignableFrom(cl)) {
		try {
		   if (Byte.class == cl) { return (T) Byte.valueOf(strValue); }
		   if (Short.class == cl) { return (T) Short.valueOf(strValue); }
		   if (Integer.class == cl) { return (T) Integer.valueOf(strValue); }
		   if (Long.class == cl) { return (T) Long.valueOf(strValue); }
		   if (Float.class == cl) { return (T) Float.valueOf(strValue); }
		   if (Double.class == cl) { return (T) Double.valueOf(strValue); }
		   if (BigInteger.class == cl) { return (T) new BigInteger(strValue); }
		   if (BigDecimal.class == cl) { return (T) new BigDecimal(strValue); }
		} catch (final NumberFormatException nfe) {
		   LOGGER.error(ConfigurationMessageBundle.getMessage("config.number.format", strValue));
		   throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.number.format", strValue), nfe);
		}
	   }

	   if (Date.class.isAssignableFrom(cl)) {
		try {
		   return (T) new SimpleDateFormat(StrDateFormat).parse(strValue);
		} catch (final ParseException pe) {
		   LOGGER.error(ConfigurationMessageBundle.getMessage("config.date.format", strValue, StrDateFormat));
		   throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.date.format", strValue, StrDateFormat), pe);
		}
	   }

	   if (String.class.isAssignableFrom(cl)) { return (T) (StringHelper.isEmpty(strValue) ? "" : strValue); }

	   throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.property.illegal.class"));

	} else if (value instanceof Number) {
	   throw new NotYetImplementedException();
	} else if (value instanceof Boolean) {
	   throw new NotYetImplementedException();
	} else if (value instanceof Date) { throw new NotYetImplementedException(); }

	return null;
   }

   /**
    * @param <T>
    * @param values can be a String with multiple values separate by {@link ConfigurationConstants#MultiValDefaultSeparator}, or
    * @param cl
    * @return multiple value
    */
   public static <T> List<T> valuesOf(final Serializable values, final Class<T> cl) {

	if (values == null) { return null; }

	List<T> result = null;
	if (values instanceof String) {
	   final String strValue = (String) values;
	   if (!StringHelper.isEmpty(strValue)) {
		result = new LinkedList<T>();
		final StringTokenizer strToken = new StringTokenizer(strValue, MultiValDefaultSeparator);
		while (strToken.hasMoreTokens()) {
		   result.add(valueOf(strToken.nextToken(), cl));
		}
	   }
	} else {
	   throw new NotYetImplementedException();
	}
	return result;
   }

   /**
    * {@link Serializable} to String Converter
    * 
    * @param value instance to convert
    * @return String conversion of the requested object
    */
   @Review(comment = "use SimpleDateFormat has a thread local", category = ReviewCategoryEnum.Improvement)
   protected String serializableToString(final Serializable value) {

	if (value != null) {

	   if (value instanceof Number) {
		return ((Number) value).toString();
	   } else if (value instanceof Date) {
		return new SimpleDateFormat(StrDateFormat).format(value);
	   } else if (value instanceof String) {
		return (String) value;
	   } else if (value instanceof Boolean) {
		return String.valueOf(value);
	   } else if (value instanceof Byte) {
		return String.valueOf(value);
	   } else {
		return value.toString();
	   }
	} else {
	   return null;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#addConfiguration(org.kaleidofoundry.core.config.Configuration)
    */
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
		str.append(key).append("=").append(ConverterHelper.collectionToString(values, MultiValDefaultSeparator)).append(" , ");
	   }
	}
	str.append("}");
	return str.toString();
   }

}
