/*
 * $License$
 */
package org.kaleidofoundry.core.config;

import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyPropertiesRoot;
import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyPropertiesSeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.KeyRoot;
import static org.kaleidofoundry.core.config.ConfigurationConstants.KeySeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.LOGGER;
import static org.kaleidofoundry.core.config.ConfigurationConstants.MultiValDefaultSeparator;
import static org.kaleidofoundry.core.config.ConfigurationConstants.StrDateFormat;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ConfigurationMessageBundle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheEnum;
import org.kaleidofoundry.core.cache.CacheFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.ResourceStore;
import org.kaleidofoundry.core.store.ResourceStoreFactory;
import org.kaleidofoundry.core.store.SingleResourceStore;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.ConverterHelper;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * <p>
 * Abstract class, implementing the basic functionality of the interface configuration. <br/>
 * It will be the ancestor of all implementations of Configuration <br/>
 * </p>
 * Use internally a Properties instance (thread safe)<br/>
 * <p>
 * Internally a bridge is implemented between the in memory property,<br>
 * and its storage {@link ResourceStore}
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Immutable
@ThreadSafe
public abstract class AbstractConfiguration implements Configuration {

   /**
    * enumeration of local context property name
    */
   public static enum ContextProperty {
	/** readonly usage */
	readonly,
	/** cache implementation to use */
	cache;
   }

   // configuration identifier
   protected final String identifier;

   // internal properties cache
   protected final Cache<String, String> cacheProperties;

   // external persistent singleStore
   protected final SingleResourceStore singleResourceStore;

   // internal runtime context
   protected final RuntimeContext<Configuration> context;

   /**
    * @param identifier
    * @param resourceUri
    * @param context
    * @throws StoreException
    * @throws IllegalArgumentException if resourceUri is illegal ({@link URISyntaxException})
    */
   protected AbstractConfiguration(@NotNull final String identifier, @NotNull final String resourceUri, @NotNull final RuntimeContext<Configuration> context)
	   throws StoreException {
	this(identifier, URI.create(resourceUri), context);
   }

   /**
    * @param identifier
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   protected AbstractConfiguration(@NotNull final String identifier, @NotNull final URI resourceUri, @NotNull final RuntimeContext<Configuration> context)
	   throws StoreException {
	this.identifier = identifier.toString();
	this.context = context;

	// internal resource store instantiation
	ResourceStore resourceStore = ResourceStoreFactory.createResourceStore(resourceUri);
	singleResourceStore = new SingleResourceStore(resourceUri, resourceStore);

	// internal cache key / value instantiation
	CacheFactory<String, String> cacheFactory;
	String cacheImplKey = context.getProperty(ContextProperty.cache.name());
	CacheEnum cacheEnum = CacheEnum.findByCode(cacheImplKey);

	if (!StringHelper.isEmpty(cacheImplKey) && cacheEnum == null) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage(
		"config.cache.illegal", ContextProperty.cache.name(), cacheImplKey)); }

	if (cacheEnum != null) {
	   cacheFactory = CacheFactory.getCacheFactory(cacheEnum);
	} else {
	   cacheFactory = CacheFactory.getCacheFactory();
	}

	cacheProperties = cacheFactory.getCache("kaleidofoundry/configuration/" + identifier);
   }

   /**
    * you don't need to release resourceHandler argument, it is done by agregator
    * 
    * @param resourceHandler
    * @param properties
    * @return
    * @throws StoreException
    * @throws ConfigurationException
    */
   protected abstract Cache<String, String> loadProperties(ResourceHandler resourceHandler, Cache<String, String> properties) throws StoreException,
	   ConfigurationException;

   /**
    * you don't need to release resourceHandler argument, it is done by agregator
    * 
    * @param resourceStore
    * @param properties
    * @return
    * @throws StoreException
    * @throws ConfigurationException
    */
   protected abstract Cache<String, String> storeProperties(Cache<String, String> properties, SingleResourceStore resourceStore) throws StoreException,
	   ConfigurationException;

   /**
    * Processing on requested property key path
    * 
    * @param propPath
    * @return Normalize property path from argument
    */
   protected String keyPath(final String propPath) {
	return StringHelper.replaceAll(propPath, ".", KeySeparator);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#isReadOnly()
    */
   public boolean isReadOnly() {
	if (StringHelper.isEmpty(context.getProperty(ContextProperty.readonly.name()))) {
	   return false;
	} else {
	   return Boolean.valueOf(context.getProperty(ContextProperty.readonly.name()));
	}
   }

   // **************************************************************************
   // -> Load / singleResourceStore management
   // **************************************************************************

   @Override
   public final void load() throws StoreException, ConfigurationException {
	if (isLoaded()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.illegal01", identifier)); }

	ResourceHandler resourceHandler = singleResourceStore.load();
	try {
	   loadProperties(resourceHandler, cacheProperties);
	} finally {
	   resourceHandler.release();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getIdentifier()
    */
   public String getIdentifier() {
	return identifier;
   }

   @Override
   public final void store() throws StoreException, ConfigurationException {
	if (!isLoaded()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.illegal02", identifier)); }
	if (isReadOnly()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.illegal03", identifier)); }

	singleResourceStore.store();
   }

   @Override
   public final void unload() throws StoreException {
	if (!isLoaded()) { throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.load.illegal02", identifier)); }
	// cleanup cache entries
	cacheProperties.removeAll();
	// unload store
	singleResourceStore.unload();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#isLoaded()
    */
   public boolean isLoaded() {
	return singleResourceStore.isLoaded();
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

	final String fullKey = getValidKey(prefix);
	final Set<String> roots = new LinkedHashSet<String>();

	for (String pKey : cacheProperties.keys()) {
	   if (pKey.startsWith(fullKey)) {

		pKey = pKey.substring(fullKey.length());
		final StringTokenizer st = new StringTokenizer(pKey, KeyPropertiesSeparator);

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
	final String fullKey = getValidKey(prefix);
	final Set<String> keys = new LinkedHashSet<String>();

	for (String pKey : cacheProperties.keys()) {
	   if (pKey.startsWith(fullKey)) {
		pKey = StringHelper.replaceAll(pKey, KeyPropertiesSeparator, KeySeparator);
		keys.add(ConfigurationConstants.KeyRoot + pKey);
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
   public String getRawProperty(final String key) {
	return cacheProperties.get(getValidKey(key));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getString(java.lang.String)
    */
   @Override
   public String getString(final String key) {
	return getRawProperty(getValidKey(key));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getStringList(java.lang.String)
    */
   @Override
   public List<String> getStringList(final String key) {
	String rawValues = getRawProperty(key);
	String[] values = rawValues != null ? StringHelper.split(rawValues, MultiValDefaultSeparator) : null;

	if (values == null) {
	   return null;
	} else {
	   return Arrays.asList(values);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#setProperty(java.lang.String, java.lang.Object)
    */
   @Override
   public void setProperty(@NotNull final String key, @NotNull final Object value) {
	cacheProperties.put(getValidKey(key), objectToString(value));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#removeProperty(java.lang.String)
    */
   @Override
   public void removeProperty(@NotNull final String key) {
	cacheProperties.remove(getValidKey(key));
   }

   /**
    * get a valid / normalize property key
    * 
    * @param key
    * @return
    */
   protected String getValidKey(@NotNull String key) {
	key = key.replaceAll(KeyRoot, KeyPropertiesRoot);
	key = key.replaceAll(KeySeparator, KeyPropertiesSeparator);
	return key;
   }

   // ***************************************************************************
   // -> Typed property value accessors
   // ***************************************************************************

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimal(java .lang.String)
    */
   public BigDecimal getBigDecimal(final String key) {
	return valueOf(getString(key), BigDecimal.class);
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
	return valuesOf(getRawProperty(key), BigDecimal.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigInteger(java .lang.String)
    */
   public BigInteger getBigInteger(final String key) {
	return valueOf(getString(key), BigInteger.class);
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
	return valuesOf(getRawProperty(key), BigInteger.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBoolean(java. lang.String)
    */
   public Boolean getBoolean(final String key) {
	final String value = getString(key);
	return StringHelper.isEmpty(value) ? null : Boolean.valueOf(value);
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
	return valuesOf(getRawProperty(key), Boolean.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByte(java.lang .String)
    */
   public Byte getByte(final String key) {
	final String value = getString(key);
	return StringHelper.isEmpty(value) ? null : Byte.valueOf(value.getBytes()[0]);
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
	return valuesOf(getRawProperty(key), Byte.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDouble(java.lang .String)
    */
   public Double getDouble(final String key) {
	return valueOf(getString(key), Double.class);
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
	return valuesOf(getRawProperty(key), Double.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloat(java.lang .String)
    */
   public Float getFloat(final String key) {
	return valueOf(getString(key), Float.class);
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
	return valuesOf(getRawProperty(key), Float.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getInt(java.lang .String)
    */
   public Integer getInteger(final String key) {
	return valueOf(getString(key), Integer.class);
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
	return valuesOf(getRawProperty(key), Integer.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLong(java.lang .String)
    */
   public Long getLong(final String key) {
	return valueOf(getString(key), Long.class);
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
	return valuesOf(getRawProperty(key), Long.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShort(java.lang .String)
    */
   public Short getShort(final String key) {
	return valueOf(getString(key), Short.class);
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
	return valuesOf(getRawProperty(key), Short.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDate(java.lang .String)
    */
   public Date getDate(final String key) {
	return valueOf(getString(key), Date.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getDateList(java.lang.String)
    */
   public List<Date> getDateList(final String key) {
	return valuesOf(getRawProperty(key), Date.class);
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
	final Set<String> roots = keySet(rootPrefix);

	for (final String string : roots) {
	   String propPath = string;
	   final String propValue = getString(propPath);
	   final List<String> propValues = getStringList(propPath);

	   propPath = StringHelper.replaceAll(propPath, KeyRoot, "");
	   propPath = StringHelper.replaceAll(propPath, KeySeparator, KeyPropertiesSeparator);

	   if (!StringHelper.isEmpty(propValue)) {
		prop.put(propPath, propValue);
	   } else {
		prop.put(propPath, ConverterHelper.collectionToString(propValues, MultiValDefaultSeparator));
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
    * String to Objet Converter
    * 
    * @param strValue string value representation
    * @param cl Target class
    * @return Requested convertion of the string argument. If {@link NumberFormatException} or date {@link ParseException}, silent exception
    *         will be logged, and null return
    * @throws IllegalStateException for date or number parse error
    */
   @SuppressWarnings("unchecked")
   protected <T> T valueOf(final String strValue, final Class<T> cl) throws IllegalStateException {

	if (StringHelper.isEmpty(strValue)) { return null; }

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

	if (cl.isAssignableFrom(Date.class)) {
	   try {
		// TODO - thread local SimpleDateFormat
		return (T) new SimpleDateFormat(StrDateFormat).parse(strValue);
	   } catch (final ParseException pe) {
		LOGGER.error(ConfigurationMessageBundle.getMessage("config.date.format", strValue, StrDateFormat));
		throw new IllegalStateException(ConfigurationMessageBundle.getMessage("config.date.format", strValue, StrDateFormat), pe);
	   }
	}

	if (cl.isAssignableFrom(String.class)) { return (T) (StringHelper.isEmpty(strValue) ? "" : strValue); }

	return null;
   }

   /**
    * @param <T>
    * @param strValue
    * @param cl
    * @return
    */
   protected <T> List<T> valuesOf(final String strValue, final Class<T> cl) {
	List<T> result = new LinkedList<T>();
	if (!StringHelper.isEmpty(strValue)) {
	   StringTokenizer strToken = new StringTokenizer(strValue, MultiValDefaultSeparator);
	   while (strToken.hasMoreTokens()) {
		result.add(valueOf(strToken.nextToken(), cl));
	   }
	}
	return result;
   }

   /**
    * Object to String Converter
    * 
    * @param value instance to convert
    * @return String convertion of the requested objet
    */
   protected String objectToString(final Object value) {

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
		final String propValue = config.getString(propName);
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

	prefix = keyPath(prefix);

	for (final String propName : keySet()) {
	   final String propValue = getString(propName);

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
