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
package org.kaleidofoundry.core.naming;

import javax.naming.Context;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.util.StringHelper;

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
    * @param config
    * @see Configuration
    */
   public JndiContext(final Configuration... config) {
	super(null, DefaultPrefix, config);
   }

   /**
    * @param name
    * @param defaults
    * @param prefixProperty
    */
   public JndiContext(final String name, final String prefixProperty, final Configuration... defaults) {
	super(name, prefixProperty, defaults);
   }

   /**
    * @param name
    * @param defaults
    */
   public JndiContext(final String name, final Configuration... defaults) {
	super(name, DefaultPrefix, defaults);
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
	super(name, DefaultPrefix, context);
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
    * @return Context URL package prefixes
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