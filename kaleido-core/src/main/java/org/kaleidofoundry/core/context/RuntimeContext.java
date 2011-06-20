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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ContextMessageBundle;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationChangeEvent;
import org.kaleidofoundry.core.config.ConfigurationChangeHandler;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.ConfigurationRegistry;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginHelper;
import org.kaleidofoundry.core.util.AbstractPropertyAccessor;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * A runtime context represents a subset a {@link Configuration} runtime / environment properties, specific to the
 * generic class argument T. It can be used by an instance of class T, which need to access and handle configuration properties changes<br/>
 * <ul>
 * <li>it encapsulates access to runtime configuration or environment informations,
 * <li>it is bind to one or more {@link Configuration}, so configurations changes can be managed at runtime,
 * <li>it can be a subset of this {@link Configuration} (the prefix is done for that),
 * <li>it has read only access to the {@link Configuration}
 * </ul>
 * <p>
 * <b>To categorize your properties :</b> you can used a prefix : {@link #getPrefix()} <br/>
 * <br/>
 * This prefix can be manually set, using constructors :
 * <ul>
 * <li> {@link RuntimeContext#RuntimeContext(String, Configuration...)}
 * <li> {@link RuntimeContext#RuntimeContext(String, String, Configuration...)}</li>
 * <li> {@link RuntimeContext#RuntimeContext(String, String, ConcurrentMap, Configuration...)}</li>
 * </ul>
 * <br/>
 * Another way to set this prefix is to bind it to a type, using constructors :
 * <ul>
 * <li> {@link RuntimeContext#RuntimeContext(Class)}</li>
 * <li> {@link RuntimeContext#RuntimeContext(Class, Configuration...)}</li>
 * <li> {@link RuntimeContext#RuntimeContext(String, Class, Configuration...)}</li>
 * <li> {@link RuntimeContext#RuntimeContext(String, Class, ConcurrentMap, Configuration...)}</li>
 * </ul>
 * The last way to build it using {@link Context} annotation, with static constructors :
 * <ul>
 * <li>{@link #createFromField(Field)}</li>
 * <li>{@link #createFrom(Context, String, Class)}</li>
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
 * <br/>
 * <p>
 * <b>I would preferred introspection to get generic type T, without giving type in constructor argument...</b> <br/>
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
 * @param <T> service interface / implementation class to use with this context
 * @author Jerome RADUGET
 * @see Context inject {@link RuntimeContext} to a class field, method argument...
 */
@Immutable(comment = "instance which have been injected using @Context are immutable after injection")
@ThreadSafe
@Task(comment = "comment and review interaction between configuration and runtime context... use case, creation, event handling... have to be thread safe...", labels = TaskLabel.Review)
public class RuntimeContext<T> extends AbstractPropertyAccessor {

   /** Context property name, used to enable the component listening of configuration parameters changes */
   public static final String Dynamics = "dynamics";

   /*
    * Even if the field are not final, the class stay immutable.
    * a class instance which have been injected with @Context, can't be modifiable
    * -> see #copyFrom(...) methods
    */
   private String name;
   private String prefix;
   private final Class<T> type;
   private Configuration[] configurations;

   // used only by {@link AbstractRuntimeContextBuilder}, for static injection
   boolean hasBeenInjectedByAnnotationProcessing;
   // does context has been build by runtime context builder ? if yes, no more updates is possible)
   boolean hasBeenBuildByContextBuilder;
   // static parameters (@Contex( parameters = {...})
   // there are injected manually by the developer coding using annotation, or using builder
   final ConcurrentMap<String, Serializable> parameters;
   // an optional configuration change handler
   ConfigurationChangeHandler configurationChangesHandler;

   /**
    * create <b>unnamed</b> {@link RuntimeContext} name, <b>without prefix</b>
    * 
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final Configuration... configurations) {
	this(null, (String) null, configurations);
   }

   /**
    * create <b>unnamed</b> {@link RuntimeContext} name, with static parameters, <b>without prefix</b>
    * 
    * @param parameters Optional static parameters
    * @param configurations
    */
   public RuntimeContext(final ConcurrentMap<String, Serializable> parameters, final Configuration... configurations) {
	this(null, (String) null, parameters, configurations);
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
    * create unnamed {@link RuntimeContext}, with the given type
    * 
    * @param type if type is annotated {@link Plugin}, the current prefix will be the plugin code, otherwise the type package
    */
   public RuntimeContext(final Class<T> type) {
	this(null, type, null, new Configuration[0]);
   }

   /**
    * create unnamed {@link RuntimeContext}, with the given plugin prefix & static parameters
    * 
    * @param type if type is annotated {@link Plugin}, the current prefix will be the plugin code, otherwise the type package
    * @param parameters
    */
   public RuntimeContext(final Class<T> type, final ConcurrentMap<String, Serializable> parameters) {
	this(null, type, parameters, new Configuration[0]);
   }

   /**
    * create unnamed {@link RuntimeContext}, with the given plugin prefix & configurations
    * 
    * @param type if type is annotated {@link Plugin}, the current prefix will be the plugin code, otherwise the type package
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final Class<T> type, final Configuration... configurations) {
	this(null, type, configurations);
   }

   /**
    * create unnamed {@link RuntimeContext}, with the given plugin prefix & static parameters & configurations
    * 
    * @param type if type is annotated {@link Plugin}, the current prefix will be the plugin code, otherwise the type package
    * @param parameters optional static parameters
    * @param configurations
    */
   public RuntimeContext(final Class<T> type, final ConcurrentMap<String, Serializable> parameters, final Configuration... configurations) {
	this(null, type, parameters, configurations);
   }

   /**
    * create named {@link RuntimeContext}, with the given plugin prefix & configurations
    * 
    * @param name context name
    * @param type if type is annotated {@link Plugin}, the current prefix will be the plugin code, otherwise the type package
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final String name, final Class<T> type, final Configuration... configurations) {
	this(name, type, false, null, configurations);
   }

   /**
    * create named {@link RuntimeContext}, with the given prefix & configurations
    * 
    * @param name context name
    * @param prefix an optional prefix for the properties name (it can be used to categorized your own RuntimeContext)
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   public RuntimeContext(final String name, final String prefix, final Configuration... configurations) {
	this(name, prefix, false, null, configurations);
   }

   /**
    * create named {@link RuntimeContext}, with the given type prefix & static parameters & configurations
    * 
    * @param name context name
    * @param type if type is annotated {@link Plugin}, the current prefix will be the plugin code, otherwise the type package
    * @param parameters optional static parameters
    * @param configurations
    */
   public RuntimeContext(final String name, final Class<T> type, final ConcurrentMap<String, Serializable> parameters, final Configuration... configurations) {
	this(name, type, false, parameters, configurations);
   }

   /**
    * create named {@link RuntimeContext}, with the given type prefix & static parameters & configurations
    * 
    * @param name context name
    * @param prefix an optional prefix for the properties name (it can be used to categorized your own RuntimeContext)
    * @param parameters optional static parameters
    * @param configurations
    */
   public RuntimeContext(final String name, final String prefix, final ConcurrentMap<String, Serializable> parameters, final Configuration... configurations) {
	this(name, prefix, false, parameters, configurations);
   }

   /**
    * create named {@link RuntimeContext}, with the given type prefix and using given runtime context configurations
    * 
    * @param name context name
    * @param type if type is annotated {@link Plugin}, the current prefix will be the plugin code, otherwise the type package
    * @param context
    */
   public RuntimeContext(final String name, final Class<T> type, @NotNull final RuntimeContext<?> context) {
	this(name, type, false, null, context.getConfigurations());
   }

   /**
    * @param name context name
    * @param prefix prefix an optional prefix for the properties name (it can be used to categorized your own
    *           RuntimeContext)
    * @param hasBeenInjectedByAnnotationProcessing does context injection have been done (immutable after injection)
    * @param parameters optional static parameters
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   RuntimeContext(final String name, final String prefix, final boolean hasBeenInjectedByAnnotationProcessing,
	   final ConcurrentMap<String, Serializable> parameters, final Configuration... configurations) {
	this.name = name;
	this.prefix = prefix;
	this.type = null;
	this.configurations = configurations; // keep original instance, to handle property modification. don't re-copy it
	this.hasBeenInjectedByAnnotationProcessing = hasBeenInjectedByAnnotationProcessing;
	this.parameters = parameters == null ? new ConcurrentHashMap<String, Serializable>() : parameters;
   }

   /**
    * @param name context name
    * @param type
    * @param hasBeenInjectedByAnnotationProcessing does context injection have been done (immutable after injection)
    * @param parameters optional static parameters
    * @param configurations configuration instances where to find properties, configurations have to be load before
    */
   RuntimeContext(final String name, final Class<T> type, final boolean hasBeenInjectedByAnnotationProcessing,
	   final ConcurrentMap<String, Serializable> parameters, final Configuration... configurations) {
	final String pluginPrefix = PluginHelper.getPluginName(type);
	this.name = name;
	this.prefix = pluginPrefix != null ? pluginPrefix : null;
	this.type = type;
	this.configurations = configurations; // keep original instance, to handle property modification. don't re-copy it
	this.hasBeenInjectedByAnnotationProcessing = hasBeenInjectedByAnnotationProcessing;
	this.parameters = parameters == null ? new ConcurrentHashMap<String, Serializable>() : parameters;
   }

   /**
    * If context annotation contain a name, the created {@link RuntimeContext} will use it, otherwise it will use the defaultName argument
    * coming from field or argument...
    * 
    * @param <T>
    * @param context context annotation
    * @param defaultName default context name if {@link Context} annotation provide no one (field name, argument name, ...)
    * @param type
    * @return new runtime context instance, build from given annotation
    * @throws IllegalContextParameterException if one of {@link Context#configurations()} is not registered
    */
   protected static <T> RuntimeContext<T> createFrom(@NotNull final Context context, @Nullable final String defaultName, final Class<T> type) {

	// name of the context
	final String contextName = !StringHelper.isEmpty(context.value()) ? context.value() : defaultName;

	if (StringHelper.isEmpty(contextName)) { throw new IllegalContextParameterException("context.annotation.value.empty"); }

	final RuntimeContext<T> rc;

	// handle configurations to used in the runtime context
	final String[] configIds = context.configurations();
	final Configuration[] configs = new Configuration[configIds != null ? configIds.length : 0];
	for (int i = 0; i < configs.length; i++) {
	   configs[i] = ConfigurationFactory.getRegistry().get(configIds[i]);
	   if (configs[i] == null) { throw new IllegalContextParameterException("context.annotation.illegalconfig.simple", context.value(), configIds[i]); }
	}

	// copy static annotation parameters
	final ConcurrentMap<String, Serializable> parameters = new ConcurrentHashMap<String, Serializable>();
	// fixed the dynamics status of the context
	parameters.put(Dynamics, String.valueOf(context.dynamics()));

	for (final Parameter p : context.parameters()) {
	   parameters.put(p.name(), p.value());
	}

	// create runtimeContext instance
	if (type != null) {
	   rc = new RuntimeContext<T>(contextName, type, true, parameters, configs);
	} else {
	   rc = new RuntimeContext<T>(contextName, (String) null, true, parameters, configs);
	}

	return rc;
   }

   /**
    * @param annotatedField
    * @return new runtime context instance, build from given field
    * @throws IllegalArgumentException is the given field is not annotated {@link Context}
    * @throws ContextException if one of {@link Context#configurations()} is not registered
    */
   protected static RuntimeContext<?> createFromField(@NotNull final Field annotatedField) {
	final Context context = annotatedField.getAnnotation(Context.class);
	if (context == null) { throw new IllegalArgumentException(ContextMessageBundle.getMessage("context.annotation.field.illegal", Context.class.getName())); }

	final Plugin<?> plugin = PluginHelper.getInterfacePlugin(annotatedField.getDeclaringClass());
	return createFrom(context, annotatedField.getName(), plugin != null ? plugin.getAnnotatedClass() : null);

   }

   /**
    * feed given context, with annotation meta-data
    * 
    * @param <T>
    * @param context
    * @param defaultName default context name if {@link Context} annotation provide no one (field name, argument name, ...)
    * @param contextTarget
    */
   static <T> void createFrom(@NotNull final Context context, @Nullable final String defaultName, @NotNull final RuntimeContext<T> contextTarget) {
	final RuntimeContext<T> newOne = createFrom(context, defaultName, contextTarget.getType());
	copyFrom(newOne, contextTarget);
	contextTarget.hasBeenInjectedByAnnotationProcessing = true;
   }

   /**
    * Internal helper for re-copy a runtime context,
    * which have not been yet processing by annotation processor injector.
    * It is used for final field, which have been manually instantiate
    * 
    * @param origin
    * @param target
    * @throws IllegalStateException is current context have already been injected
    */
   static void copyFrom(final RuntimeContext<?> origin, final RuntimeContext<?> target) {
	if (!target.hasBeenInjectedByAnnotationProcessing) {
	   if (StringHelper.isEmpty(target.name)) {
		target.name = origin.name;
	   }
	   target.prefix = origin.prefix;
	   target.configurations = origin.configurations;
	   target.hasBeenInjectedByAnnotationProcessing = false;
	   target.hasBeenBuildByContextBuilder = false;
	   for (final String key : origin.parameters.keySet()) {
		// if parameter is already set, we keep original value
		if (target.parameters.get(key) == null) {
		   target.parameters.put(key, origin.parameters.get(key));
		}
	   }
	} else {
	   // RuntimeContext have already been injected
	   throw new IllegalStateException(InternalBundleHelper.ContextMessageBundle.getMessage("context.annotation.illegalinject", target.name));
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
    *         <li>store.file
    *         <li>jndi.context
    *         <li>messaging.consumer
    *         <li>messaging.transport
    *         <li>mail.session
    *         <li>...
    *         </ul>
    */
   public String getPrefix() {
	return prefix;
   }

   /**
    * @return if {@link RuntimeContext} has been create giving an type, return it, otherwise return null
    */
   public Class<T> getType() {
	return type;
   }

   /**
    * Does context allow dynamics properties changes ?
    * 
    * @return <code>true / false</code>, if not set return <code>true</code>
    */
   public boolean isDynamics() {
	return getBoolean(Dynamics, true);
   }

   /**
    * @param property local property name (without prefix and name)
    * @return value of the given property <br/>
    *         For a property which is defined both in static parameters, and in configurations: the first parameter occurrence will be
    *         return <br/>
    *         For a property which is defined both in multiple configurations: the first occurrence will be return (configuration array
    *         declaration order) <br/>
    */
   @Override
   public Serializable getProperty(final String property) {

	// first, search in local static parameters
	Serializable result = parameters.get(property);
	if (result != null) { return result; }

	// if not found, search in configurations
	for (final Configuration config : getConfigurations()) {
	   result = config.getProperty(getFullPropertyName(property));
	   if (result != null) { return result; }
	}
	return null;
   }

   /**
    * @return does the current instance have been injected via @{@link Context} aop processing
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
	str.append("prefix").append("=").append(prefix).append("}").append(separator);

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

	if (parameters != null && !parameters.isEmpty()) {
	   keys.addAll(parameters.keySet());
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
		props.setProperty(prop, getProperty(prop).toString());
	   }
	}

	return props;

   }

   // ***************************************************************************
   // -> Configuration changes events methods
   // ***************************************************************************

   /**
    * set here your configuration changes handler
    * 
    * @param handler
    */
   public void setConfigurationChangeHandler(final ConfigurationChangeHandler handler) {
	this.configurationChangesHandler = handler;
   }

   /**
    * if current context is dynamics ({@link #isDynamics()}), and if configuration change handler is defined with
    * {@link #setConfigurationChangeHandler(ConfigurationChangeHandler)},
    * it will triggers the configuration changes given in argument, to the current handler<br/>
    * <br/>
    * 
    * @param events
    */
   final void triggerConfigurationChangeEvents(final LinkedHashSet<ConfigurationChangeEvent> events) {
	if (configurationChangesHandler != null && isDynamics()) {
	   configurationChangesHandler.onConfigurationChanges(events);
	}
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
    * @see Context#parameters()
    */
   protected ConcurrentMap<String, Serializable> getParameters() {
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
	final StringBuilder fullprefix = new StringBuilder();

	if (!StringHelper.isEmpty(prefix)) {
	   fullprefix.append(prefix);
	}

	if (!StringHelper.isEmpty(getName()) && prefix != null && prefix.length() > 0) {
	   fullprefix.append(".").append(name);
	}

	if (!StringHelper.isEmpty(getName()) && (prefix == null || prefix.length() <= 0)) {
	   fullprefix.append(name);
	}

	return fullprefix;
   }

}