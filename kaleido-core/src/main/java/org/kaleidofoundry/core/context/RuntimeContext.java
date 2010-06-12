/* 
 * $License$ 
 */
package org.kaleidofoundry.core.context;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * A runtime context represents runtime / environment properties<br/>
 * It will be the ancestor, of all runtime context handled in the framework. The prefixProperty is used to prefix the
 * property names in order to categorize it<br/>
 * You can extend it, and specify your own property prefix<br/>
 * Its data could be load from a {@link Properties} or from a {@link Configuration} <br/>
 * <ul>
 * <li>it encapsulates access to runtime env. informations,
 * <li>it is bind to a {@link Configuration} or {@link Properties}, so configuration changes can be managed at runtime,
 * <li>it can be a subset of this {@link Configuration} or {@link Properties} (the prefixProperty is done for that),
 * <li>it has read only access to the {@link Configuration} or {@link Properties}
 * </ul>
 * <p>
 * <b>Class is thread safe</b>: it use internally {@link Properties} or {@link Configuration} to store context informations in memory.
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
 * Properties jndiProperties = new Properties();
 * jndiProperties.load(new FileInputStream(&quot;jndi-local.properties&quot;));
 * 
 * RuntimeContext tomcatContext = new RuntimeContext(&quot;tomcat&quot;, jndiProperties, &quot;jndi.context&quot;);
 * Properties tomcatJndiProperties = tomcatContext.toProperties();
 * 
 * System.out.println(tomcatJndiProperties.getProperty(&quot;java.naming.env.prefix&quot;));
 * System.out.println(tomcatJndiProperties.getProperty(&quot;java.naming.factory.initial&quot;));
 * 
 * tomcatJndiProperties.store(new FileOutputStream(&quot;jndi-local-tomcat.properties&quot;), &quot;extraction of tomcat jndi properties&quot;);
 * 
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
public class RuntimeContext<T> {

   private final String name;
   private final String prefixProperty;

   // One of the two can be instantiated!
   private final Properties properties;
   private final Configuration configuration;

   /**
    * default constructor<br/>
    * create context internally with a new {@link Properties}
    */
   public RuntimeContext() {
	this((String) null);
   }

   // Constructor without Properties or Configuration ******************************************************************

   /**
    * create context internally with a new {@link Properties}
    * 
    * @param name context name identifier
    */
   public RuntimeContext(final String name) {
	this(name, new Properties(), null);
   }

   /**
    * create context internally with a new {@link Properties}
    * 
    * @param name context name identifier
    * @param prefix an optional prefix for the properties name (it can be used to categorized your own RuntimeContext)
    */
   public RuntimeContext(final String name, final String prefix) {
	this(name, new Properties(), prefix);
   }

   // Constructors with Properties *************************************************************************************

   /**
    * Default constructor with {@link Properties}, properties have to be load before
    * 
    * @param props intial values of the context
    */
   public RuntimeContext(@NotNull final Properties props) {
	this(null, props, null);
   }

   /**
    * Default constructor with {@link Properties}, properties have to be load before
    * 
    * @param name context name identifier
    * @param defaults initial values of the context
    */
   public RuntimeContext(final String name, @NotNull final Properties defaults) {
	this(name, defaults, null);
   }

   /**
    * Default constructor with {@link Properties}, properties have to be load before
    * 
    * @param properties initial values of the context
    * @param prefixProperty an optional prefix for the properties name (it can be used to categorized your own
    *           RuntimeContext)
    */
   public RuntimeContext(@NotNull final Properties properties, final String prefixProperty) {
	this(null, properties, prefixProperty);
   }

   /**
    * constructor with an instance of {@link Properties} , properties have to be load before
    * 
    * @param name context name identifier
    * @param properties initial values of the context
    * @param prefixProperty an optional prefix for the properties name (it can be used to categorized your own
    *           RuntimeContext)
    */
   public RuntimeContext(final String name, final @NotNull Properties properties, final String prefixProperty) {
	this.name = name;
	this.prefixProperty = prefixProperty;
	this.properties = properties; // keep original instance, to handle property modification. don't re-copy it
	configuration = null;
   }

   // Constructors with Configuration **********************************************************************************

   /**
    * constructor with an instance of {@link Configuration}, configuration have to be load before
    * 
    * @param configuration initial configuration value
    */
   public RuntimeContext(final Configuration configuration) {
	this(null, configuration, null);
   }

   /**
    * constructor with an instance of {@link Configuration}, configuration have to be load before
    * 
    * @param name context name identifier
    * @param defaults initial values of the context
    */
   public RuntimeContext(final String name, final Configuration defaults) {
	this(name, defaults, null);
   }

   /**
    * constructor with an instance of {@link Configuration}, configuration have to be load before
    * 
    * @param defaults initial values of the context
    * @param prefixProperty an optional prefix for the properties name (it can be used to categorized your own
    *           RuntimeContext)
    */
   public RuntimeContext(final Configuration defaults, final String prefixProperty) {
	this(null, defaults, prefixProperty);
   }

   /**
    * constructor with an instance of {@link Configuration}, configuration have to be load before
    * 
    * @param name context name identifier
    * @param defaults initial values of the context
    * @param prefixProperty an optional prefix for the properties name (it can be used to categorized your own
    *           RuntimeContext)
    */
   public RuntimeContext(final String name, @NotNull final Configuration defaults, final String prefixProperty) {
	this.name = name;
	this.prefixProperty = prefixProperty;
	properties = null;
	configuration = defaults;// keep original instance, to handle property modification. don't re-copy it
   }

   /**
    * TODO : to review - clone or not the datas ?
    * 
    * @param name
    * @param context
    * @param prefix an optional prefix for the properties name (it can be used to categorized your own RuntimeContext)
    */
   public RuntimeContext(final String name, @NotNull final RuntimeContext<?> context, final String prefix) {
	this.name = name;
	prefixProperty = prefix;
	configuration = context.getConfiguration();
	properties = context.getProperties();
   }

   /**
    * @return context name identifier
    */
   public String getName() {
	return name;
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
    *         For example in kaleido, it could be :
    *         <ul>
    *         <li>file.store
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
    * @return value of the given property
    */
   public String getProperty(final String property) {
	if (properties != null) { return properties.getProperty(getFullPropertyName(property)); }
	if (configuration != null) { return configuration.getString(getFullPropertyName(property)); }
	throw new IllegalStateException();
   }

   /**
    * @param separator print separator
    * @return string representation of the runtime context
    */
   public String toString(final String separator) {
	final StringBuilder str = new StringBuilder();

	str.append("{context name").append("=").append(name).append(" ");
	str.append("prefix").append("=").append(prefixProperty).append("}").append(separator);

	Set<String> keys = keySet();
	if (keys != null) {
	   for (String key : keys) {
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
	if (properties != null || configuration != null) {
	   final String prefix = getFullPrefix().toString();
	   final Set<String> keys = properties != null ? properties.stringPropertyNames() : configuration.toProperties().stringPropertyNames();
	   final Set<String> result = new LinkedHashSet<String>();
	   for (String prop : keys) {
		if (prop.startsWith(prefix)) {
		   result.add(prop.substring(prefix.length() <= 0 ? prefix.length() : prefix.length() + 1));
		}
	   }
	   return result;
	}
	return null;
   }

   /**
    * @return clone properties of the context
    */
   public Properties toProperties() {

	if (properties != null || configuration != null) {
	   final String prefix = getFullPrefix().toString();
	   final Properties props = new Properties();
	   for (String prop : keySet()) {
		if (prop.startsWith(prefix)) {
		   props.setProperty(prop.substring(prefix.length() <= 0 ? prefix.length() : prefix.length() + 1), properties != null ? properties
			   .getProperty(prop) : configuration.getString(prop));
		}
	   }
	   return props;
	}

	return null;
   }

   // ***************************************************************************
   // * PROTECTED
   // ***************************************************************************
   protected Configuration getConfiguration() {
	return configuration;
   }

   protected Properties getProperties() {
	return properties;
   }

   /**
    * @param property (without prefix and name)
    * @param value value to set to the given property
    */
   protected void setProperty(@NotNull final String property, @NotNull final String value) {
	if (property == null) { throw new NullPointerException(); }

	if (properties != null) {
	   properties.setProperty(getFullPropertyName(property), value);
	}
	if (configuration != null) {
	   configuration.setProperty(getFullPropertyName(property), value);
	}
   }

   /**
    * @param property (without prefix and name)
    */
   protected void removeProperty(@NotNull final String property) {
	if (property == null) { throw new NullPointerException(); }

	if (properties != null) {
	   properties.remove(getFullPropertyName(property));
	}
	if (configuration != null) {
	   configuration.removeProperty(getFullPropertyName(property));
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
	StringBuilder prefix = new StringBuilder();

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