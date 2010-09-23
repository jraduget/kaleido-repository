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

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ClasspathResourceStore;
import org.kaleidofoundry.core.store.FileSystemResourceStore;
import org.kaleidofoundry.core.store.FtpResourceStore;
import org.kaleidofoundry.core.store.HttpResourceStore;
import org.kaleidofoundry.core.store.JpaResourceStore;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceStore;

/**
 * <p>
 * Interface to access runtime configuration data from your application (this data could be this data could be technical / environmental /
 * application / ...). <br/>
 * A {@link Configuration} is like a set of key identifier / value(s) (as {@link Properties}) but with more functionalities ({@link Cache},
 * ...).<br/>
 * <br/>
 * The goal of these interface, is to have unique and uniform to access and managed runtime configuration.<br/>
 * Multiple format implementation are provided by default, an configuration "instance" could be :
 * <ul>
 * <li>Classic java properties file - {@link PropertiesConfiguration},</li>
 * <li>Xml java properties file - {@link XmlPropertiesConfiguration},</li>
 * <li>Xml custom properties file - {@link XmlConfiguration},</li>
 * <li>Java system variables set - {@link JavaSystemConfiguration},</li>
 * <li>OS environment variables set - {@link OsEnvConfiguration},</li>
 * <li>Java main args variables set - {@link MainArgsConfiguration}</li>
 * <li>...</li>
 * </ul>
 * <br/>
 * The configuration resource file could be accessed through different way, using URI (the type of the uri will determine the sub
 * {@link ResourceStore} to use):
 * <ul>
 * <li>File system - {@link FileSystemResourceStore},</li>
 * <li>Java classpath - {@link ClasspathResourceStore},</li>
 * <li>Http url - {@link HttpResourceStore},</li>
 * <li>Ftp url - {@link FtpResourceStore},</li>
 * <li>Jpa custom url - {@link JpaResourceStore},</li>
 * <li>...</li>
 * </ul>
 * <br/>
 * <p>
 * With your configuration instance, you can managed :
 * <ul>
 * <li>load configuration store</li>
 * <li>unload configuration store</li>
 * <li>store (persist) configuration</li>
 * </ul>
 * </p>
 * <br/>
 * <b>Its values can be accessible locally or via a cache cluster (see {@link ConfigurationContextBuilder}) </b> <br/>
 * Each functionalities {@link #load()}, {@link #store()}, {@link #unload()}, {@link #getProperty(String)},
 * {@link #setProperty(String, Serializable)} can use caches functionalities
 * </p>
 * <p>
 * <a href="package-summary.html"/>Package description</a>
 * </p>
 * <p>
 * Implementation have to be thread safe
 * </p>
 * <b>Use case :</b><br/>
 * <br/>
 * For the following properties file (named "appConfig" in the following javadoc) implements by {@link PropertiesConfiguration} :
 * 
 * <pre>
 * application.name=app
 * application.version=1.0.0
 * application.description=description of the application...
 * application.date=2006-09-01T00:00:00
 * application.librairies=dom4j.jar log4j.jar mail.jar
 * 
 * application.modules.sales=Sales
 * application.modules.sales.version=1.1.0
 * application.modules.marketing=Market.
 * application.modules.netbusiness=
 * </pre>
 * <p>
 * Sample of configuration <b>key</b> identifiers:
 * <ul>
 * <li><code>configuration.getString("//application/name") -> "app"</code></li>
 * <li><code>configuration.getString("//application/version") -> "1.0.0"</code></li>
 * <li><code>configuration.getStringList("//application/libraries") -> {"dom4j.jar","log4j.jar","mail.jar"}</code></li>
 * </ul>
 * Informations :
 * <ul>
 * <li>-><code>'//'</code> represents the root</li>
 * <li>-><code>'/'</code> represents the key separator</li>
 * </ul>
 * You can also used standard java properties format :
 * <ul>
 * <li><code>configuration.getString("application.name") -> "app"</code></li>
 * <li><code>configuration.getString("application.version") -> "1.0.0"</code></li>
 * <li><code>configuration.getStringList("application.libraries") -> {"dom4j.jar","log4j.jar","mail.jar"}</code></li>
 * </ul>
 * </ul>
 * </p>
 * 
 * @see ResourceStore
 * @author Jerome RADUGET
 */
@ThreadSafe
@Declare(ConfigurationConstants.ConfigurationPluginName)
@Provider(value = ConfigurationProvider.class, singletons = true)
public interface Configuration {

   /**
    * @return configuration name (have to be unique)
    */
   String getName();

   // **************************************************************************
   // -> Property configuration access management
   // **************************************************************************

   /**
    * @param key key identifier (unique)
    * @return get the raw property value <br>
    */
   Serializable getProperty(@NotNull String key);

   /**
    * add or update property value
    * 
    * @param key key identifier (unique)
    * @param value property value to set
    * @throws IllegalStateException if configuration is for read-only use
    */
   void setProperty(@NotNull String key, @NotNull Serializable value);

   /**
    * Remove given property
    * 
    * @param key key identifier (unique)
    * @throws IllegalStateException if configuration is for read-only use
    */
   void removeProperty(@NotNull String key);

   // **************************************************************************
   // -> changes listener
   // **************************************************************************

   /**
    * add a configuration changes listener
    * 
    * @param listener
    */
   void addConfigurationChangeListener(PropertyChangeListener listener);

   /**
    * remove a configuration changes listener
    * 
    * @param listener
    */
   void removeConfigurationChangeListener(PropertyChangeListener listener);

   // **************************************************************************
   // -> Keys management
   // **************************************************************************

   /**
    * @return Iterator for all declared property keys
    * @see #keySet()
    */
   @NotNull
   Iterator<String> keysIterator();

   /**
    * @param prefix prefix key name filtering
    * @return keys iterator filtered
    * @see #keySet(String)
    */
   @NotNull
   Iterator<String> keysIterator(@NotNull String prefix);

   /**
    * <p>
    * For the top class properties example, implements by{@link PropertiesConfiguration} :
    * 
    * <pre>
    * configuration.keySet()= {&quot;//application/name&quot;, &quot;//application/version&quot;, &quot;//application/description&quot;, &quot;//application/date&quot;, &quot;//application/librairies&quot;, "application.modules.sales", ...}
    * </pre>
    * 
    * </p>
    * 
    * @return a set (clone) of all the declared property keys <br/>
    */
   @NotNull
   Set<String> keySet();

   /**
    * @param prefix prefix key name filtering
    * @return a set (clone) of all declared property keys filtered by prefix argument
    */
   @NotNull
   Set<String> keySet(@NotNull String prefix);

   /**
    * @param key property key to find
    * @param prefixKey property name prefix filtering
    * @return <code>true</code>if key exists, <code>false</code> otherwise
    */
   boolean containsKey(String key, @NotNull String prefixKey);

   // **************************************************************************
   // -> Roots management
   // **************************************************************************

   /**
    * @return Roots iterator
    * @see #roots()
    */
   @NotNull
   Iterator<String> rootsIterator();

   /**
    * @param prefix root prefix filtered
    * @return Roots iterator, which began with prefix arg
    * @see #roots(String)
    */
   @NotNull
   Iterator<String> rootsIterator(@NotNull String prefix);

   /**
    * @return All existing root keys of a configuration file<br/>
    *         <p>
    *         For the following properties file, implements by{@link PropertiesConfiguration} :
    * 
    *         <pre>
    * application.name=app
    * application.version=1.0.0
    * application.description=description of the application...
    * application.date=2006-09-01T00:00:00
    * application.librairies=dom4j.jar log4j.jar mail.jar
    * 
    * application.modules.sales=Sales
    * application.modules.sales.version=1.1.0
    * application.modules.marketing=Market.
    * application.modules.netbusiness=
    * </pre>
    * 
    *         <pre>
    * configuration.roots()= {&quot;application&quot;}
    * </pre>
    * 
    *         </p>
    */
   @NotNull
   Set<String> roots();

   /**
    * @param prefix root prefix filtered
    * @return All existing root keys, that begins by prefix argument <br/>
    *         <p>
    *         For the following properties file (application.properties), implements by{@link PropertiesConfiguration} :
    * 
    *         <pre>
    * application.name=app
    * application.version=1.0.0
    * application.description=description of the application...
    * application.date=2006-09-01T00:00:00
    * application.librairies=dom4j.jar log4j.jar mail.jar
    * 
    * application.modules.sales=Sales
    * application.modules.sales.version=1.1.0
    * application.modules.marketing=Market.
    * application.modules.netbusiness=
    * </pre>
    * 
    *         Java program sample :
    * 
    *         <pre>
    * configuration = ... // create via instance injection, method injection, or manually new ...Configuration() implementation
    * configuration.load();
    * configuration.roots("//application/modules") = {"sales", "marketing", "netbusiness"}
    * </pre>
    * 
    *         </p>
    */
   @NotNull
   Set<String> roots(@NotNull String prefix);

   /**
    * @param rootKey name of the root to find
    * @param prefix Search prefix for the requested root
    * @return <code>true</code>if rootKey exists, <code>false</code> otherwise <br/>
    *         <p>
    *         For the following properties file (application.properties), implements by{@link PropertiesConfiguration} :
    * 
    *         <pre>
    * application.name=app
    * application.version=1.0.0
    * application.description=description of the application...
    * application.date=2006-09-01T00:00:00
    * application.librairies=dom4j.jar log4j.jar mail.jar
    * 
    * application.modules.sales=Sales
    * application.modules.sales.version=1.1.0
    * application.modules.marketing=Market.
    * application.modules.netbusiness=
    * </pre>
    * 
    *         Java program sample :
    * 
    *         <pre>
    * configuration = ... // create via instance injection, method injection, or manually new ...Configuration() implementation
    * configuration.load();
    * configuration.containsRoot("sales", "//application/modules")) -> true
    * configuration.containsRoot("foo", "//application/modules")) -> false
    * </pre>
    * 
    *         </p>
    */
   boolean containsRoot(String rootKey, @NotNull String prefix);

   // **************************************************************************
   // -> Tools
   // **************************************************************************

   /**
    * @return Properties converter (clone copy)
    */
   @NotNull
   Properties toProperties();

   /**
    * @param prefix prefix keys filtered
    * @return Properties converter, filtered by root prefix
    */
   @NotNull
   Properties toProperties(String prefix);

   /**
    * @return String Representation
    */
   String toString();

   /**
    * @param prefix prefix filtered
    * @param outConfiguration configuration identifier
    * @return an extraction a subset of the current configuration instance for keys starting with prefix argument<br/>
    *         it will returns the outConfiguration argument instance, with the addition of the current extracting values <br/>
    *         <p>
    *         For the following properties file (application.properties), implements by{@link PropertiesConfiguration} :
    * 
    *         <pre>
    * application.name=app
    * application.version=1.0.0
    * application.description=description of the application...
    * application.date=2006-09-01T00:00:00
    * application.librairies=dom4j.jar log4j.jar mail.jar
    * 
    * application.modules.sales=Sales
    * application.modules.sales.version=1.1.0
    * application.modules.marketing=Market.
    * application.modules.netbusiness=
    * </pre>
    * 
    *         Java program sample :
    * 
    *         <pre>
    * configuration = ... // create via instance injection, method injection, or manually new ...Configuration() implementation
    * outputConfigration = ...  // create via instance injection, method injection, or manually new ...Configuration() implementation
    * 
    * configuration.load();
    * 
    * configuration.extractConfiguration("//application/modules", emptyConfiguration);
    * outputConfigration.store();
    * 
    * </pre>
    * 
    *         Output configuration will containing :
    * 
    *         <pre>
    * sales=Sales
    * sales.version=1.1.0
    * marketing=Market.
    * netbusiness=
    * </pre>
    */
   Configuration extractConfiguration(@NotNull String prefix, @NotNull Configuration outConfiguration);

   /**
    * Add another configuration content to current instance.<br/>
    * The configuration argument overides the current configuration content<br/>
    * 
    * @param configuration configuration to add
    * @return Merge configuration (configuration argument crushes same existing
    *         properties)
    */
   Configuration addConfiguration(@NotNull Configuration configuration);

   // **************************************************************************
   // -> Load / store management
   // **************************************************************************

   /**
    * load configuration content
    * 
    * @throws ResourceException
    * @throws ConfigurationException
    */
   void load() throws ResourceException, ConfigurationException;

   /**
    * unload configuration content
    * 
    * @throws ResourceException
    */
   void unload() throws ResourceException;

   /**
    * @return Configuration have been loaded ? <code>true/false</code>
    */
   boolean isLoaded();

   /**
    * Persist all configuration datas (with updade)
    * 
    * @throws ResourceException
    * @throws ConfigurationException
    */
   void store() throws ResourceException, ConfigurationException;

   /**
    * @return does configuration allowed the storage
    */
   boolean isStorageAllowed();

   /**
    * @return does configuration allowed item updates
    */
   boolean isUpdateAllowed();

   // **************************************************************************
   // -> Property value accessors
   // **************************************************************************
   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    */
   @Nullable
   String getString(@NotNull String key);

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    */
   @Nullable
   String getString(@NotNull String key, String defaultValue);

   /**
    * @param key requested property key
    * @return values of the property requested
    */
   @Nullable
   List<String> getStringList(@NotNull String key);

   // **************************************************************************
   // -> Typed property value accessors
   // **************************************************************************

   /**
    * @param key requested property key
    * @return values of the property requested
    * @throws NumberFormatException
    */
   @Nullable
   BigDecimal getBigDecimal(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    * @throws NumberFormatException
    */
   @Nullable
   BigDecimal getBigDecimal(@NotNull String key, BigDecimal defaultValue) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   List<BigDecimal> getBigDecimalList(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   BigInteger getBigInteger(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    * @throws NumberFormatException
    */
   @Nullable
   BigInteger getBigInteger(@NotNull String key, BigInteger defaultValue) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   List<BigInteger> getBigIntegerList(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    */
   @Nullable
   Boolean getBoolean(@NotNull String key);

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    */
   @Nullable
   Boolean getBoolean(@NotNull String key, Boolean defaultValue);

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    */
   @Nullable
   List<Boolean> getBooleanList(@NotNull String key);

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    */
   @Nullable
   Byte getByte(@NotNull String key);

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    */
   @Nullable
   Byte getByte(@NotNull String key, Byte defaultValue);

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    */
   @Nullable
   List<Byte> getByteList(@NotNull String key);

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   Double getDouble(@NotNull String key);

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    * @throws NumberFormatException
    */
   @Nullable
   Double getDouble(@NotNull String key, Double defaultValue);

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   List<Double> getDoubleList(@NotNull String key);

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   Float getFloat(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    * @throws NumberFormatException
    */
   @Nullable
   Float getFloat(@NotNull String key, Float defaultValue) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   List<Float> getFloatList(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   Integer getInteger(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    * @throws NumberFormatException
    */
   @Nullable
   Integer getInteger(@NotNull String key, Integer defaultValue) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   List<Integer> getIntegerList(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   Long getLong(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    * @throws NumberFormatException
    */
   @Nullable
   Long getLong(@NotNull String key, Long defaultValue) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   List<Long> getLongList(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   Short getShort(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    * @throws NumberFormatException
    */
   @Nullable
   Short getShort(@NotNull String key, Short defaultValue) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    * @throws NumberFormatException
    */
   @Nullable
   List<Short> getShortList(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    */
   @Nullable
   Date getDate(@NotNull String key);

   /**
    * @param key requested property key
    * @param defaultValue Default value if property content is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    */
   @Nullable
   Date getDate(@NotNull String key, Date defaultValue);

   /**
    * @param key requested property key
    * @return Multiple values of the requested property (null if not defined)
    */
   @Nullable
   List<Date> getDateList(final @NotNull String key);

}