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
package org.kaleidofoundry.core.config;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.config.model.FireChangesReport;
import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.context.Scope;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.ClasspathFileStore;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileSystemStore;
import org.kaleidofoundry.core.store.FtpStore;
import org.kaleidofoundry.core.store.HttpFileStore;
import org.kaleidofoundry.core.store.JpaFileStore;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * <p>
 * Interface used to access <b>runtime configuration data</b> from your application (this data could be this data could be technical /
 * environmental / application / ...). A {@link Configuration} is like a set of key identifier / value(s) (as {@link Properties}) but with
 * more functionalities ({@link Cache}, typed accessor, changed events...).<br/>
 * <br/>
 * The goal of these interface, is to <b>provide an unique and uniform way to manage for the runtime configuration of your application.</b><br/>
 * Multiple formats and implementations are provided by default, like :
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
 * <b>The configuration resource (usually file)</b> could be accessed through different way, using URI : <br/>
 * <br/>
 * <ul>
 * <li>File system - {@link FileSystemStore},</li>
 * <li>Java classpath - {@link ClasspathFileStore},</li>
 * <li>Http url - {@link HttpFileStore},</li>
 * <li>Ftp url - {@link FtpStore},</li>
 * <li>Jpa custom url - {@link JpaFileStore},</li>
 * <li>...</li>
 * </ul>
 * The type of the uri will determine the sub {@link FileStore} to use <br/>
 * <p>
 * <b>With your configuration instance, you can managed :</b>
 * <ul>
 * <li>load configuration store</li>
 * <li>unload configuration store</li>
 * <li>store (persist) configuration</li>
 * </ul>
 * </p>
 * <br/>
 * <b>Configuration properties can be accessible locally or via a cache cluster (see {@link ConfigurationContextBuilder}) </b> <br/>
 * Each functionalities {@link #load()}, {@link #store()}, {@link #unload()}, {@link #getProperty(String)},
 * {@link #setProperty(String, Serializable)} can use caches functionalities
 * </p>
 * <p>
 * <b>Configuration listener :</b> {@link ConfigurationListener} <br/>
 * To use configuration listener, you can register your own using : {@link #addConfigurationListener(ConfigurationListener)} <br/>
 * This listener enables to trap :
 * <ul>
 * <li>{@link ConfigurationListener#propertyCreate(ConfigurationChangeEvent)}</li>
 * <li>{@link ConfigurationListener#propertyUpdate(ConfigurationChangeEvent)}</li>
 * <li>{@link ConfigurationListener#propertyRemove(ConfigurationChangeEvent)}</li>
 * <li>{@link ConfigurationListener#configurationUnload(Configuration)}</li>
 * </ul>
 * </p>
 * <br/>
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
 * @see FileStore
 * @see NamedConfiguration
 * @see NamedConfigurations
 * @author jraduget
 */
@ThreadSafe
@Declare(ConfigurationConstants.ConfigurationPluginName)
@Provider(value = ConfigurationProvider.class, scope=Scope.singleton)
public interface Configuration {

   /**
    * @return configuration name (have to be unique)
    */
   String getName();

   /**
    * @return configuration resource {@link URI}
    */
   String getResourceUri();

   // **************************************************************************
   // -> Property configuration access management
   // **************************************************************************

   /**
    * @param key key identifier (unique)
    * @return get the raw property value <br>
    */
   Serializable getProperty(@NotNull String key);

   /**
    * @param key property name
    * @param type type of the return value
    * @return value of the property
    * @param <T>
    */
   <T extends Serializable> T getProperty(final String key, final Class<T> type);

   /**
    * @param key property name
    * @param type type of the return value
    * @return values of the property
    * @param <T>
    */
   <T extends Serializable> List<T> getPropertyList(final String key, final Class<T> type);

   /**
    * add or update property value
    * 
    * @param key key identifier (unique)
    * @param value property value to set
    * @throws IllegalStateException if configuration is for read-only use
    * @see #addConfigurationListener(ConfigurationListener) to register a listener on change
    */
   void setProperty(@NotNull String key, @NotNull Serializable value);

   /**
    * Remove given property (if property key does not exist, do nothing)
    * 
    * @param key key identifier (unique)
    * @throws IllegalStateException if configuration is for read-only use
    * @see #addConfigurationListener(ConfigurationListener) to register a listener on remove
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
   void addConfigurationListener(ConfigurationListener listener);

   /**
    * remove a configuration changes listener
    * 
    * @param listener
    */
   void removeConfigurationListener(ConfigurationListener listener);

   /**
    * fire all configuration changes events (create, update, remove) since the last call<br/>
    * events are fired in the order of creation
    * 
    * @return report of configurations changes which have been fired
    */
   FireChangesReport fireConfigurationChangesEvents();

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
    * @return <code>true</code>if key exists, <code>false</code> otherwise
    */
   boolean containsKey(String key);

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
    * Add another configuration content to the current instance.<br/>
    * The configuration argument overrides the current configuration content<br/>
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
    * @throws ConfigurationException if configuration is already loaded
    */
   void load() throws ResourceException, ConfigurationException;

   /**
    * unload configuration content
    * 
    * @throws ResourceException
    * @throws ConfigurationException if configuration is not loaded
    * @see #addConfigurationListener(ConfigurationListener) to register a listener on unload
    */
   void unload() throws ResourceException, ConfigurationException;

   /**
    * reload all configuration content
    * 
    * @throws ConfigurationException if configuration is not loaded
    * @see #addConfigurationListener(ConfigurationListener) to register a listener for changed values
    */
   void reload() throws ResourceException, ConfigurationException;

   /**
    * @return Configuration have been loaded ? <code>true/false</code>
    */
   boolean isLoaded();

   /**
    * Persist all configuration data (with update)
    * 
    * @throws ResourceException
    * @throws ConfigurationException if configuration is not loaded, or is for readonly use
    */
   void store() throws ResourceException, ConfigurationException;

   /**
    * @return does configuration allowed storage
    */
   boolean isStorable();

   /**
    * @return does configuration allowed item updates
    */
   boolean isUpdateable();

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
    * @param defaultValue Default value if property value  is null
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

   /**
    * @param key requested property key
    * @return values of the property requested
    */
   @Nullable
   String[] getStrings(@NotNull String key);
   
   /**
    * @param key requested property key
    * @param defaultValue Default value if property value is null
    * @return values of the property requested
    */
   @Nullable
   String[] getStrings(@NotNull String key, String[] defaultValues);   
   
      
   // **************************************************************************
   // -> Typed property value accessors
   // **************************************************************************

   /**
    * @param key requested property key
    * @return value of the property requested (null if not defined)
    */
   @Nullable
   Character getCharacter(@NotNull String key);

   /**
    * @param key requested property key
    * @param defaultValue Default value if property value is null
    * @return value of the property requested if defined, otherwise defaultValue argument
    */
   @Nullable
   Character getCharacter(@NotNull String key, Character defaultValue);

   /**
    * @param key requested property key
    * @return values of the property requested
    */
   @Nullable
   List<Character> getCharacterList(@NotNull String key);

   /**
    * @param key requested property key
    * @return values of the property requested
    * @throws NumberFormatException
    */
   @Nullable
   BigDecimal getBigDecimal(@NotNull String key) throws NumberFormatException;

   /**
    * @param key requested property key
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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
    * @param defaultValue Default value if property value is null
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