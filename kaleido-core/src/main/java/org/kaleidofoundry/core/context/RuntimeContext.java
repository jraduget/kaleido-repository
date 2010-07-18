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

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.ConfigurationRegistry;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.plugin.PluginHelper;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * A runtime context represents runtime / environment properties<br/>
 * It will be the ancestor, of all runtime context handled in the framework. The prefixProperty is used to prefix the
 * property names in order to categorize it<br/>
 * You can extend it, and specify your own property prefix<br/>
 * Its data could be load from a {@link Configuration} <br/>
 * <ul>
 * <li>it encapsulates access to runtime env. informations,
 * <li>it is bind to a {@link Configuration} so configuration changes can be managed at runtime,
 * <li>it can be a subset of this {@link Configuration} (the prefixProperty is done for that),
 * <li>it has read only access to the {@link Configuration}
 * </ul>
 * <p>
 * <b>Class is thread safe</b>: it use internally {@link Configuration}, to store context informations in memory.
 * </p>
 * <p>
 * Sample, a file <code>jndi-local.properties</code> contains following :
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
 */
@Review(comment = "review interaction between configuration and runtime context... use case, creation, event handling... have to be thread safe...")
@Immutable(comment = "instance which have been injected using @InjectContext are immutable after injection")
public class RuntimeContext<T> {

   /*
    * Even if the field are not final, the class stay immutable.
    * a class instance which have been injected with @InjectContext, can't be modifiable
    * -> see #copyFrom(...) methods
    */
   private String name;
   private String prefixProperty;
   private Configuration[] configurations;
   private boolean injected;

   /**
    * constructor with an instance of {@link Configuration}, configurations have to be load before
    * 
    * @param configurations configuration instances where to find properties
    */
   public RuntimeContext(final Configuration... configurations) {
	this(null, null, configurations);
   }

   /**
    * constructor with an instance of {@link Configuration}, configurations have to be load before
    * 
    * @param name context name identifier
    * @param configurations configuration instances where to find properties
    */
   public RuntimeContext(final String name, final Configuration... configurations) {
	this(name, null, configurations);
   }

   /**
    * constructor with an instance of {@link Configuration}, configurations have to be load before
    * 
    * @param name context name identifier
    * @param configurations configuration instances where to find properties
    * @param prefixProperty an optional prefix for the properties name (it can be used to categorized your own
    *           RuntimeContext)
    */
   public RuntimeContext(final String name, final String prefixProperty, @NotNull final Configuration... configurations) {
	this(name, prefixProperty, false, configurations);
   }

   /**
    * @param name your context name
    * @param prefix an optional prefix for the properties name (it can be used to categorized your own RuntimeContext)
    * @param context
    */
   public RuntimeContext(final String name, final String prefix, @NotNull final RuntimeContext<?> context) {
	this(name, prefix, false, context.getConfigurations());
   }

   /**
    * @param name your context name
    * @param context
    */
   public RuntimeContext(final String name, @NotNull final RuntimeContext<?> context) {
	this(name, null, false, context.getConfigurations());
   }

   /**
    * @param name
    * @param prefixProperty
    * @param injected
    * @param configurations
    */
   RuntimeContext(final String name, final String prefixProperty, final boolean injected, @NotNull final Configuration... configurations) {
	this.name = name;
	this.prefixProperty = prefixProperty != null ? prefixProperty : PluginHelper.getPluginName(getClassType());
	this.configurations = configurations; // keep original instance, to handle property modification. don't re-copy it
	this.injected = injected;
   }

   /**
    * Internal helper for re-copy a runtime context, which have not been yet injected
    * 
    * @param origin
    * @param target
    */
   static public void copyFrom(final RuntimeContext<?> origin, final RuntimeContext<?> target) {
	if (!target.injected) {
	   target.name = origin.name;
	   target.prefixProperty = origin.prefixProperty;
	   target.configurations = origin.configurations;
	   target.injected = false;
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
    * @return does the current instance have been injected via @{@link InjectContext}
    */
   public boolean isInjected() {
	return injected;
   }

   /**
    * @return class of the generic context annotated
    */
   @SuppressWarnings("unchecked")
   public Class<T> getClassType() {
	return (Class<T>) this.getClass().getTypeParameters()[0].getClass();
   }

   /**
    * @return an optional prefix for the properties name (it can be used to categorized your own RuntimeContext) <br/>
    * <br/>
    *         For example in kaleidofoundry, it could be :
    *         <ul>
    *         <li>resourcestore
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
    * @param property local property name (without prefix and name)
    * @return value of the given property (the first found when more that one configurations is set)
    */
   public String getProperty(final String property) {
	for (final Configuration config : getConfigurations()) {
	   final String propVal = config.getString(getFullPropertyName(property));
	   if (propVal != null) { return propVal; }
	}
	return null;
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
    * @return a clone copy of the properties set names (property name
    */
   public Set<String> keySet() {

	final String prefix = getFullPrefix().toString();

	final Set<String> keys = new LinkedHashSet<String>();
	final Set<String> result = new LinkedHashSet<String>();

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
    *         if several {@link Configuration} are set when create {@link RuntimeContext}, then only the first found property will be return
    *         in the result
    */
   public Properties toProperties() {

	final String prefix = getFullPrefix().toString();
	final Properties props = new Properties();
	for (final String prop : keySet()) {
	   if (prop.startsWith(prefix)) {

		String configValue = null;
		for (final Configuration config : getConfigurations()) {
		   configValue = config.getString(prop);
		   if (configValue != null) {
			break;
		   }
		}

		props.setProperty(prop.substring(prefix.length() <= 0 ? prefix.length() : prefix.length() + 1), configValue);
	   }
	}
	return props;

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