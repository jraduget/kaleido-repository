/*
 * $License$
 */

/*  
 # http://java.sun.com/products/jndi/serviceproviders.html
 # http://www.jmdoudoux.fr/java/dej/chap034.htm

 JBOSS 			
 java.naming.factory.initial		org.jnp.interfaces.NamingContextFactory
 java.naming.provider.url		localhost:1099
 java.naming.factory.url.pkgs	org.jboss.naming:org.jnp.interfaces

 JONAS (RMI OR DAVID)
 java.naming.factory.initial		com.sun.jndi.rmi.registry.RegistryContextFactory
 java.naming.provider.url		rmi://localhost:1099
 java.naming.factory.url.pkgs	org.objectweb.jonas.naming

 SUN
 java.naming.factory.initial
 # CORBA 		com.sun.jndi.cosnaming.CNCtxFactory		iiop://localhost:2001
 # DNS 			com.sun.jndi.dns.DnsContextFactory											
 # LDAP 			com.sun.jndi.ldap.LdapCtxFactory
 # RMI 			com.sun.jndi.rmi.registry.RegistryContextFactory			

 */

package org.kaleidofoundry.core.naming;

import java.util.Properties;

import javax.naming.Context;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * JndiContext used by Jndi client<br/>
 * <p>
 * </p>
 * 
 * @param <T>
 * @author Jerome RADUGET
 */
public class JndiContext<T> extends RuntimeContext<T> {

   private static final long serialVersionUID = 886487888448063622L;

   /** Prefix par défaut nom de properties */
   public static final String DefaultPrefix = "jndi.context";

   /** Nom de la propriété pour spécifier un prefix de nommage pour les ressources Jndi */
   public static final String JavaEnvNamePrefixProperty = "java.naming.env.prefix";

   /** Nom de la propriété pour spécifier un prefix de nommage pour les ressources Jndi */
   public static final String JavaEnvNamePrefixDefaultValue = "java:comp/env";

   /**
    * 
    */
   public JndiContext() {
	super((String) null, DefaultPrefix);
   }

   /**
    * @param config Chargement des informations du contexte à partir d'une configuration
    * @see Configuration
    */
   public JndiContext(final Configuration config) {
	super(null, config, DefaultPrefix);
   }

   /**
    * @param props Chargement des informations du contexte à partir d'un properties
    * @see Properties
    */
   public JndiContext(final Properties props) {
	super(null, props, DefaultPrefix);
   }

   /**
    * @param name
    * @param defaults
    * @param prefixProperty
    */
   public JndiContext(final String name, final Configuration defaults, final String prefixProperty) {
	super(name, defaults, prefixProperty);
   }

   /**
    * @param name
    * @param defaults
    */
   public JndiContext(final String name, final Configuration defaults) {
	super(name, defaults, DefaultPrefix);
   }

   /**
    * @param name
    * @param defaults
    * @param prefixProperty
    */
   public JndiContext(final String name, final Properties defaults, final String prefixProperty) {
	super(name, defaults, prefixProperty);
   }

   /**
    * @param name
    * @param defaults
    */
   public JndiContext(final String name, final Properties defaults) {
	super(name, defaults, DefaultPrefix);
   }

   /**
    * @param name
    */
   public JndiContext(final String name) {
	super(name, DefaultPrefix);
   }

   /**
    * @param name
    * @param context
    */
   public JndiContext(final String name, final RuntimeContext<?> context) {
	super(name, context, DefaultPrefix);
   }

   /**
    * @return Context ProviderUrl
    * @see Context
    */
   public String getProviderUrl() {
	return getProperty(Context.PROVIDER_URL);
   }

   /**
    * @return Context Initial Context Factory
    * @see Context
    */
   public String getInitialContextFactory() {
	return getProperty(Context.INITIAL_CONTEXT_FACTORY);
   }

   /**
    * @return Context Url package prefices
    * @see Context
    */
   public String getUrlPkgPrefixes() {
	return getProperty(Context.URL_PKG_PREFIXES);
   }

   /**
    * @return Context Dns URL
    * @see Context
    */
   public String getDnsUrl() {
	return getProperty(Context.DNS_URL);
   }

   /**
    * @return Prefix eventuel sur les noms de ressources jndi
    *         Rajoute automatiquement "/" à la fin si besoin est
    */
   public String getJavaEnvNamePrefix() {
	String value = getProperty(JavaEnvNamePrefixProperty);
	final String sep = "/";

	if (!StringHelper.isEmpty(value) && !value.endsWith(sep)) {
	   value = value.concat(sep);
	}

	return value;
   }

}