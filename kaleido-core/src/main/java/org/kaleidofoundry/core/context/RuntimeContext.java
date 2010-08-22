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
package org.kaleidofoundry.core.context;

import static org.kaleidofoundry.core.config.AbstractConfiguration.valueOf;
import static org.kaleidofoundry.core.config.AbstractConfiguration.valuesOf;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.RuntimeContextMessageBundle;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.ConfigurationRegistry;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.lang.annotation.Reviews;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginHelper;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * A runtime context represents a subset a {@link Configuration} runtime / environment properties, specific to the
 * generic class argument T. It can be used by an instance of class T, which need to access and handle configuration properties changes<br/>
 * <ul>
 * <li>it encapsulates access to runtime environment informations,
 * <li>it is bind to one or more {@link Configuration}, so configurations changes can be managed at runtime,
 * <li>it can be a subset of this {@link Configuration} (the prefixProperty is done for that),
 * <li>it has read only access to the {@link Configuration}
 * </ul>
 * <p>
 * <b>To categorize your properties :</b> you can used {@link #getPrefixProperty()} <br/>
 * <br/>
 * It can be manually set, using constructors :
 * <ul>
 * <li> {@link RuntimeContext#RuntimeContext(String, String, Configuration...)}</li>
 * <li> {@link RuntimeContext#RuntimeContext(String, String, RuntimeContext)}</li>
 * </ul>
 * <br/>
 * Another way to set and bind it to a declared {@link Plugin} (prefix property will be the plugin code), using constructors :
 * <ul>
 * <li> {@link RuntimeContext#RuntimeContext(Class)}</li>
 * <li> {@link RuntimeContext#RuntimeContext(Class, Configuration...)}</li>
 * <li> {@link RuntimeContext#RuntimeContext(String, Class, Configuration...)}</li>
 * </ul>
 * The last way to build it using {@link InjectContext} annotation, with static constructors :
 * <ul>
 * <li>{@link #createFrom(Field)}</li>
 * <li>{@link #createFrom(InjectContext, Class)}</li>
 * <li></li>
 * </ul>
 * </p>
 * <p>
 * <b>This class is thread safe</b>: it use internally {@link Configuration}, to access context informations.
 * </p>
 * <p>
 * Given a file <code>jndi-local.properties</code> contains following :
 * 
 * <pre>
 * 
 * # Tomcat Intial Context Information  [new RuntimeContext("tomcat", configuration, "jndi.context")]
 * jndi.context.tomcat.java.naming.env.prefix=java:comp/env
 * jndi.context.tomcat.java.naming.provider.url=
 * jndi.context.tomcat.java.naming.factory.initial=org.apache.naming.java.javaURLContextFactory
 * jndi.context.tomcat.java.naming.factory.url.pkgs=
 * jndi.context.tomcat.java.naming.dns.url=
 *  
 * # Jboss Intial Context Information [new RuntimeContext("jboss", configuration, "jndi.context")]
 * jndi.context.jboss.java.naming.env.prefix=
 * jndi.context.jboss.java.naming.provider.url=localhost:1099
 * jndi.context.jboss.java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
 * jndi.context.jboss.java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
 * jndi.context.jboss.java.naming.dns.url=
 * 
 * # so :
 * # runtimeContext prefix is: 'jndi.context'
 * # runtimeContext name are: 'tomcat' or 'jboss'
 * 
 * </pre>
 * 
 * </p>
 * Sample java code :
 * <p>
 * 
 * <pre>
 * 	try {
 * 	   // create & load configuration & a extract runtime context
 * 	   Configuration jndiProperties = ConfigurationFactory.provideConfiguration("myJndiConf", "classpath:/jndi-local.properties");
 * 	   RuntimeContext tomcatContext = new RuntimeContext("tomcat", "jndi.context", jndiProperties);
 * 	   
 * 	   // print properties value
 * 	   System.out.println(tomcatContext.getProperty("java.naming.env.prefix"));
 * 	   System.out.println(tomcatContext.getProperty("java.naming.factory.initial"));
 * 
 * 	   // extract current properties to another file
 * 	   Properties tomcatJndiProperties = tomcatContext.toProperties();
 * 	   tomcatJndiProperties.store(new FileOutputStream("jndi-local-tomcat.properties"), "extraction of tomcat jndi properties");
 * 	} catch (final StoreException stoe) {
 * 	   ...
 * 	} catch (final IOException ioe) {
 * 	   ...
 * 	}
 * </pre>
 * 
 * </p>
 * Will result in a <code>jndi-local-tomcat.properties</code>
 * <p>
 * 
 * <pre>
 * # extraction of tomcat jndi properties
 * jndi.context.tomcat.java.naming.env.prefix=java:comp/env
 * jndi.context.tomcat.java.naming.provider.url=
 * jndi.context.tomcat.java.naming.factory.initial=org.apache.naming.java.javaURLContextFactory
 * jndi.context.tomcat.java.naming.factory.url.pkgs=
 * jndi.context.tomcat.java.naming.dns.url=
 * </pre>
 * 
 * </p>
 * 
 * @param <T> service interface / implementation class to use with this context
 * @author Jerome RADUGET
 * @see Declare declaration of a new plugin interface or implemetation class
 * @see InjectContext inject {@link RuntimeContext} to a class field, method argument...
 */
@Reviews(reviews = {
	@Review(comment = "review interaction between configuration and runtime context... use case, creation, event handling... have to be thread safe...", category = ReviewCategoryEnum.Todo),
	@Review(comment = "create or RuntimeContext interface & hierarchy (DefaultRuntimeContext, PluginRuntimeContext)", category = ReviewCategoryEnum.Refactor) })
@Immutable(comment = "instance which have been injected using @InjectContext are immutable after injection")
public class RuntimeContext<T> {

   /*
    * Even if the field are not final, the class stay immutable.
    * a class instance which have been injected with @InjectContext, can't be modifiable
    * -> see #copyFrom(...) methods
    */
   private String name;
   private String prefixProperty;
   private final Class<T> pluginInterface;
   private Configuration[] configurations;

   // used only by {@link AbstractRuntimeContextBuilder}, for static injection
   boolean hasBeenInjectedByAnnotationProcessing;
   // internal properties used by runtime context builder. context internal properties are in this case injected manually by the developer
   // coding (using annotation, or using builder)
   final Map<String, String> parameters;
   // does context has been build by runtime context builder ? if yes, no more updates is possible)
   boolean hasBeenBuildByContextBuilder;

   /**
    * create <b>unnamed</b> {@link RuntimeContext} name, <b>without prefix</b>
    * 
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final Configuration... configurations) {
	this(null, (String) null, configurations);
   }

   /**
    * create <b>named</b> {@link RuntimeContext} name, <b>without prefix</b>
    * 
    * @param name context name
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final String name, final Configuration... configurations) {
	this(name, (String) null, configurations);
   }

   /**
    * create unnamed {@link RuntimeContext}, with the given plugin prefix
    * <p>
    * <b>I would preferred introspection to get generic type T, without giving its Class in constructor argument...</b> <br/>
    * But generic type are erased one compiled (java 4 compatibility)... <br/>
    * <br/>
    * Very interesting topics on the subject :
    * <ul>
    * <li>http://download.oracle.com/docs/cd/E17409_01/javase/tutorial/java/generics/erasure.html
    * <li>http://gafter.blogspot.com/2006/12/super-type-tokens.html
    * </ul>
    * So, I keep simple solution of passing the Class of the generic in constructor. The abstract solution does not satisfy me.
    * </p>
    * 
    * @param pluginInterface {@link Plugin} interface (interface annotated @{@link Declare}), so the {@link Plugin#getName()} will be the
    *           context prefix
    */
   public RuntimeContext(final Class<T> pluginInterface) {
	this(null, pluginInterface, new Configuration[0]);
   }

   /**
    * create unnamed {@link RuntimeContext}, with the given plugin prefix & configurations
    * <p>
    * <b>I would preferred introspection to get generic type T, without giving its Class in constructor argument...</b> <br/>
    * But generic type are erased one compiled (java 4 compatibility)... <br/>
    * <br/>
    * Very interesting topics on the subject :
    * <ul>
    * <li>http://download.oracle.com/docs/cd/E17409_01/javase/tutorial/java/generics/erasure.html
    * <li>http://gafter.blogspot.com/2006/12/super-type-tokens.html
    * </ul>
    * So, I keep simple solution of passing the Class of the generic in constructor. The abstract solution does not satisfy me.
    * </p>
    * 
    * @param pluginInterface {@link Plugin} interface (interface annotated @{@link Declare}), so the {@link Plugin#getName()} will be the
    *           context prefix
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final Class<T> pluginInterface, final Configuration... configurations) {
	this(null, pluginInterface, configurations);
   }

   /**
    * create named {@link RuntimeContext}, with the given plugin prefix
    * <p>
    * <b>I would preferred introspection to get generic type T, without giving its Class in constructor argument...</b> <br/>
    * But generic type are erased one compiled (java 4 compatibility)... <br/>
    * <br/>
    * Very interesting topics on the subject :
    * <ul>
    * <li>http://download.oracle.com/docs/cd/E17409_01/javase/tutorial/java/generics/erasure.html
    * <li>http://gafter.blogspot.com/2006/12/super-type-tokens.html
    * </ul>
    * So, I keep simple solution of passing the Class of the generic in constructor. The abstract solution does not satisfy me.
    * </p>
    * 
    * @param name context name
    * @param pluginInterface {@link Plugin} interface (interface annotated @{@link Declare}), so the {@link Plugin#getName()} will be the
    *           context prefix
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final String name, final Class<T> pluginInterface, final Configuration... configurations) {
	this(name, pluginInterface, false, configurations);
   }

   /**
    * @param name context name
    * @param prefixProperty an optional prefix for the properties name (it can be used to categorized your own RuntimeContext)
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final String name, final String prefixProperty, @NotNull final Configuration... configurations) {
	this(name, prefixProperty, false, configurations);
   }

   /**
    * @param name context name
    * @param prefix an optional prefix for the properties name (it can be used to categorized your own RuntimeContext)
    * @param context
    */
   public RuntimeContext(final String name, final String prefix, @NotNull final RuntimeContext<?> context) {
	this(name, prefix, false, context.getConfigurations());
   }

   /**
    * @param name context name
    * @param context
    */
   public RuntimeContext(final String name, @NotNull final RuntimeContext<?> context) {
	this(name, (String) null, false, context.getConfigurations());
   }

   /**
    * @param name context name
    * @param prefixProperty prefixProperty an optional prefix for the properties name (it can be used to categorized your own
    *           RuntimeContext)
    * @param hasBeenInjectedByAnnotationProcessing does context injection have been done (immutable after injection)
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   RuntimeContext(final String name, final String prefixProperty, final boolean hasBeenInjectedByAnnotationProcessing,
	   @NotNull final Configuration... configurations) {
	this.name = name;
	this.prefixProperty = prefixProperty;
	this.pluginInterface = null;
	this.configurations = configurations; // keep original instance, to handle property modification. don't re-copy it
	this.hasBeenInjectedByAnnotationProcessing = hasBeenInjectedByAnnotationProcessing;
	this.parameters = new ConcurrentHashMap<String, String>();
   }

   /**
    * @param name context name
    * @param pluginInterface
    * @param hasBeenInjectedByAnnotationProcessing does context injection have been done (immutable after injection)
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   RuntimeContext(final String name, final Class<T> pluginInterface, final boolean hasBeenInjectedByAnnotationProcessing,
	   @NotNull final Configuration... configurations) {
	this.name = name;
	this.prefixProperty = PluginHelper.getPluginName(pluginInterface);
	this.pluginInterface = pluginInterface;
	this.configurations = configurations; // keep original instance, to handle property modification. don't re-copy it
	this.hasBeenInjectedByAnnotationProcessing = hasBeenInjectedByAnnotationProcessing;
	this.parameters = new ConcurrentHashMap<String, String>();
   }

   /**
    * @param <T>
    * @param injectContext
    * @param pluginInterface
    * @return new runtime context instance, build from given annotation
    * @throws RuntimeContextException if one of {@link InjectContext#configurations()} is not registered
    */
   static <T> RuntimeContext<T> createFrom(@NotNull final InjectContext injectContext, final Class<T> pluginInterface) {

	if (StringHelper.isEmpty(injectContext.value())) { throw new RuntimeContextIllegalParameterException("context.annotation.value.empty"); }

	final String[] configIds = injectContext.configurations();
	final Configuration[] configs = new Configuration[configIds != null ? configIds.length : 0];
	for (int i = 0; i < configs.length; i++) {
	   configs[i] = ConfigurationFactory.getRegistry().get(configIds[i]);
	   if (configs[i] == null) { throw new RuntimeContextException("context.annotation.illegalconfig.simple", injectContext.value(), configIds[i]); }
	}

	if (pluginInterface != null) {
	   return new RuntimeContext<T>(injectContext.value(), pluginInterface, true, configs);
	} else {
	   return new RuntimeContext<T>(injectContext.value(), (String) null, true, configs);
	}
   }

   /**
    * @param annotatedField
    * @return new runtime context instance, build from given field
    * @throws IllegalArgumentException is the given field is not annotated {@link InjectContext}
    * @throws RuntimeContextException if one of {@link InjectContext#configurations()} is not registered
    */
   static RuntimeContext<?> createFrom(@NotNull final Field annotatedField) {

	final InjectContext injectContext = annotatedField.getAnnotation(InjectContext.class);

	if (injectContext == null) { throw new IllegalArgumentException(RuntimeContextMessageBundle.getMessage("context.annotation.field.illegal",
		InjectContext.class.getName())); }

	Plugin<?> plugin = PluginHelper.getInterfacePlugin(annotatedField.getDeclaringClass());

	return createFrom(injectContext, plugin != null ? plugin.getAnnotatedClass() : null);

   }

   /**
    * feed given context, with annotation meta-data
    * 
    * @param <T>
    * @param injectContext
    * @param contextTarget
    */
   static <T> void createFrom(@NotNull final InjectContext injectContext, @NotNull final RuntimeContext<T> contextTarget) {
	RuntimeContext<T> newOne = createFrom(injectContext, contextTarget.getPluginInterface());
	copyFrom(newOne, contextTarget);
	contextTarget.hasBeenInjectedByAnnotationProcessing = true;
   }

   /**
    * Internal helper for re-copy a runtime context, which have not been yet processing by annotation processor injector
    * 
    * @param origin
    * @param target
    * @throws IllegalStateException is current context have already been injected
    */
   public static void copyFrom(final RuntimeContext<?> origin, final RuntimeContext<?> target) {
	if (!target.hasBeenInjectedByAnnotationProcessing) {
	   target.name = origin.name;
	   target.prefixProperty = origin.prefixProperty;
	   target.configurations = origin.configurations;
	   target.hasBeenInjectedByAnnotationProcessing = false;
	   target.hasBeenBuildByContextBuilder = false;
	   for (String key : origin.parameters.keySet()) {
		// if parameter is already set, we keep original value
		if (target.parameters.get(key) == null) {
		   target.parameters.put(key, origin.parameters.get(key));
		}
	   }

	} else {
	   // RuntimeContext have already been injected
	   throw new IllegalStateException(InternalBundleHelper.RuntimeContextMessageBundle.getMessage("context.annotation.illegalinject", target.name));
	}
   }

   /**
    * @return context name identifier
    */
   public String getName() {
	return name;
   }

   /**
    * @return an optional prefix for the properties name (it can be used to categorized your own RuntimeContext) <br/>
    * <br/>
    *         For example in kaleidofoundry, it could be :
    *         <ul>
    *         <li>resourceStore
    *         <li>jndi.context
    *         <li>messaging.consumer
    *         <li>messaging.transport
    *         <li>mail.session
    *         <li>...
    *         </ul>
    */
   public String getPrefixProperty() {
	return prefixProperty;
   }

   /**
    * @return if {@link RuntimeContext} has been create giving a plugin interface, return it, otherwise return null
    */
   public Class<T> getPluginInterface() {
	return pluginInterface;
   }

   /**
    * @param property local property name (without prefix and name)
    * @return value of the given property <br/>
    *         For a property which is defined both in static parameters, and in configurations: the first parameter occurrence will be
    *         return <br/>
    *         For a property which is defined both in multiple configurations: the first occurrence will be return (configuration array
    *         declaration order) <br/>
    */
   public String getProperty(final String property) {

	// first, search in local static parameters
	String resutlt = parameters.get(property);
	if (resutlt != null) { return resutlt; }

	// if not found, search in configurations
	for (final Configuration config : getConfigurations()) {
	   resutlt = config.getString(getFullPropertyName(property));
	   if (resutlt != null) { return resutlt; }
	}
	return null;
   }

   /**
    * @return does the current instance have been injected via @{@link InjectContext} aop processing
    */
   public boolean hasBeenInjectedByAnnotationProcessing() {
	return hasBeenInjectedByAnnotationProcessing;
   }

   /**
    * @return does the current instance have been build via an @{@link AbstractRuntimeContextBuilder}
    */
   public boolean hasBeenBuildByContextBuilder() {
	return hasBeenBuildByContextBuilder;
   }

   /**
    * @param separator print separator
    * @return string representation of the runtime context
    */
   public String toString(final String separator) {
	final StringBuilder str = new StringBuilder();

	str.append("{context name").append("=").append(name).append(" ");
	str.append("prefix").append("=").append(prefixProperty).append("}").append(separator);

	final Set<String> keys = keySet();
	if (keys != null) {
	   for (final String key : keys) {
		str.append(key).append("=").append(getProperty(key)).append(separator);
	   }
	}
	return str.toString();
   }

   /**
    * @return string representation with ; as separator
    */
   @Override
   public String toString() {
	return toString(" , ");
   }

   /**
    * @return a clone copy of the properties set names (property name without prefix)
    */
   public Set<String> keySet() {

	final String prefix = getFullPrefix().toString();

	final Set<String> keys = new LinkedHashSet<String>();
	final Set<String> result = new LinkedHashSet<String>();

	for (final String key : parameters.keySet()) {
	   keys.add(parameters.get(key));
	}

	for (final Configuration config : getConfigurations()) {
	   keys.addAll(config.toProperties().stringPropertyNames());
	}

	for (final String prop : keys) {
	   if (prop.startsWith(prefix)) {
		result.add(prop.substring(prefix.length() <= 0 ? prefix.length() : prefix.length() + 1));
	   }
	}
	return result;

   }

   /**
    * @return clone properties of the context<br/>
    *         For a property which is defined both in static parameters, and in configurations: the first parameter occurrence will be
    *         return <br/>
    *         For a property which is defined both in multiple configurations: the first occurrence will be return (configuration array
    *         declaration order) <br/>
    */
   public Properties toProperties() {

	final Properties props = new Properties();
	for (final String prop : keySet()) {
	   if (props.get(prop) == null) {
		props.setProperty(prop, getProperty(prop));
	   }
	}

	return props;

   }

   // ***************************************************************************
   // -> Typed property value accessors
   // ***************************************************************************

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
	final String value = getProperty(key);
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
	return valuesOf(getProperty(key), Boolean.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByte(java.lang .String)
    */
   public Byte getByte(final String key) {
	final String value = getProperty(key);
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

   // ***************************************************************************
   // * PROTECTED
   // ***************************************************************************
   @NotNull
   protected Configuration[] getConfigurations() {

	if (configurations == null || configurations.length <= 0) {
	   final ConfigurationRegistry registry = ConfigurationFactory.getRegistry();
	   return registry.values().toArray(new Configuration[registry.size()]);
	} else {
	   return configurations;
	}
   }

   /**
    * @return static parameters set by developer, which will overrides configuration items
    */
   protected Map<String, String> getParameters() {
	return parameters;
   }

   /**
    * @param property simple name of the property
    * @return full property name of the property
    */
   protected String getFullPropertyName(final String property) {

	if (property == null) { return null; }

	final StringBuilder prefix = getFullPrefix();

	if (prefix.length() > 0) {
	   return prefix + "." + property;
	} else {
	   return property;
	}
   }

   /**
    * @return Returns the eventual prefix (prefix + name) of property names
    */
   protected StringBuilder getFullPrefix() {
	final StringBuilder prefix = new StringBuilder();

	if (!StringHelper.isEmpty(prefixProperty)) {
	   prefix.append(prefixProperty);
	}

	if (!StringHelper.isEmpty(getName()) && prefix.length() > 0) {
	   prefix.append(".").append(name);
	}

	if (!StringHelper.isEmpty(getName()) && prefix.length() <= 0) {
	   prefix.append(name);
	}

	return prefix;
   }

}