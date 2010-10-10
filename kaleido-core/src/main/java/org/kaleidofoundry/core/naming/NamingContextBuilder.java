/*
 *  Copyright 2008-2010 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.naming;

import javax.naming.Context;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;

/**
 * <h2>Some jndi configuration</h2>
 * <hr/>
 * <table>
 * <tr>
 * <th>Target</th>
 * <th>java.naming.factory.initial</th>
 * <th>java.naming.provider.url</th>
 * <th>java.naming.factory.url.pkgs</th>
 * <th>env prefix</th>
 * <th>other properties</th>
 * <tr>
 * <td>TOMCAT 5/6</td>
 * <td>org.apache.naming.java.javaURLContextFactory</td>
 * <td></td>
 * <td></td>
 * <td>java:comp/env</td>
 * <td></td>
 * </tr>
 * </tr>
 * <tr>
 * <td>GLASSFISH 3</td>
 * <td>com.sun.enterprise.naming.SerialInitContextFactory</td>
 * <td></td>
 * <td>com.sun.enterprise.naming</td>
 * <td>java:comp/env</td>
 * <td>java.naming.factory.state=com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl<br/>
 * org.omg.CORBA.ORBInitialHost=127.0.0.1<br/>
 * org.omg.CORBA.ORBInitialPort=3700</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>JBOSS</td>
 * <td>org.jnp.interfaces.NamingContextFactory</td>
 * <td>localhost:1099</td>
 * <td>org.jboss.naming:org.jnp.interfaces</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>JONAS</td>
 * <td>com.sun.jndi.rmi.registry.RegistryContextFactory</td>
 * <td>rmi://localhost:1099</td>
 * <td>org.objectweb.jonas.naming</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>WEBSPHERE 5</td>
 * <td>com.ibm.ejs.ns.jndi.CNInitialContextFactory</td>
 * <td>iiop://localhost:2809</td>
 * <td></td>
 * <td>java:comp/env</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>WEBSPHERE 6</td>
 * <td>com.ibm.websphere.naming.WsnInitialContextFactory</td>
 * <td>iiop://localhost:900</td>
 * <td>com.ibm.ws.naming</td>
 * <td>java:comp/env</td>
 * </tr>
 * <tr>
 * <td>WEBLOGIC 8 / 9 / 10</td>
 * <td>weblogic.jndi.WLInitialContextFactory</td>
 * <td>t3://localhost:7001</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>CORBA SUN</td>
 * <td>com.sun.jndi.cosnaming.CNCtxFactory</td>
 * <td>iiop://localhost:2001</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>DNS SUN</td>
 * <td>com.sun.jndi.dns.DnsContextFactory</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>LDAP SUN</td>
 * <td>com.sun.jndi.ldap.LdapCtxFactory</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>RMI SUN</td>
 * <td>com.sun.jndi.rmi.registry.RegistryContextFactory</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td></td>
 * </tr>
 * </table>
 * 
 * @author Jerome RADUGET
 */
public class NamingContextBuilder extends AbstractRuntimeContextBuilder<NamingService> {

   /** jndi optional prefix name for resource name (no need with jee6) */
   public static String EnvPrefixName = "java.naming.env.prefix";

   /**
    * @see Context#PROVIDER_URL
    */
   public static String ProviderUrl = Context.PROVIDER_URL;
   /**
    * @see Context#INITIAL_CONTEXT_FACTORY
    */
   public static String InitialContextFactory = Context.INITIAL_CONTEXT_FACTORY;
   /**
    * @see Context#URL_PKG_PREFIXES
    */
   public static String UrlPkgPrefixes = Context.URL_PKG_PREFIXES;
   /**
    * @see Context#DNS_URL
    */
   public static String DnsUrl = Context.DNS_URL;
   /**
    * @see Context#AUTHORITATIVE
    */
   public static String Authoritative = Context.AUTHORITATIVE;
   /**
    * @see Context#BATCHSIZE
    */
   public static String BatchSize = Context.BATCHSIZE;
   /**
    * @see Context#LANGUAGE
    */
   public static String Language = Context.LANGUAGE;
   /**
    * @see Context#REFERRAL
    */
   public static String Referral = Context.REFERRAL;
   /**
    * @see Context#SECURITY_AUTHENTICATION
    */
   public static String SecurityAuthentication = Context.SECURITY_AUTHENTICATION;
   /**
    * @see Context#SECURITY_PRINCIPAL
    */
   public static String SecurityPrincipal = Context.SECURITY_PRINCIPAL;
   /**
    * @see Context#SECURITY_CREDENTIALS
    */
   public static String SecurityCredentials = Context.SECURITY_CREDENTIALS;
   /**
    * @see Context#SECURITY_PROTOCOL
    */
   public static String SecurityProtocol = Context.SECURITY_PROTOCOL;
   /**
    * @see Context#STATE_FACTORIES
    */
   public static String StateFactories = Context.STATE_FACTORIES;
   /**
    * 
    */
   public static String CorbaORBInitialHost = "org.omg.CORBA.ORBInitialHost";
   /**
    * 
    */
   public static String CorbaORBInitialPort = "org.omg.CORBA.ORBInitialPort";

   /**
    * 
    */
   public NamingContextBuilder() {
	super();
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public NamingContextBuilder(final Class<NamingService> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    */
   public NamingContextBuilder(final Class<NamingService> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param configurations
    */
   public NamingContextBuilder(final Configuration... configurations) {
	super(configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public NamingContextBuilder(final String name, final Class<NamingService> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public NamingContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public NamingContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public NamingContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public NamingContextBuilder(final String name) {
	super(name);
   }

   /**
    * @param envprefixname the env prefix name to set
    */
   public NamingContextBuilder withEnvprefixname(final String envprefixname) {
	getContextParameters().put(EnvPrefixName, envprefixname);
	return this;
   }

   /**
    * @param providerurl the provider url to set
    */
   public NamingContextBuilder withProviderUrl(final String providerurl) {
	getContextParameters().put(ProviderUrl, providerurl);
	return this;
   }

   /**
    * @param initialcontextfactory the initial context factory to set
    */
   public NamingContextBuilder withInitialContextFactory(final String initialcontextfactory) {
	getContextParameters().put(InitialContextFactory, initialcontextfactory);
	return this;
   }

   /**
    * @param urlpkgprefixes the urlpkgprefixes to set
    */
   public NamingContextBuilder withUrlpkgPrefixes(final String urlpkgprefixes) {
	getContextParameters().put(UrlPkgPrefixes, urlpkgprefixes);
	return this;
   }

   /**
    * @param dnsurl the dnsurl to set
    */
   public NamingContextBuilder withDnsUrl(final String dnsurl) {
	getContextParameters().put(DnsUrl, dnsurl);
	return this;
   }

   /**
    * @param authoritative the authoritative to set
    */
   public NamingContextBuilder withAuthoritative(final String authoritative) {
	getContextParameters().put(Authoritative, authoritative);
	return this;
   }

   /**
    * @param batchsize the batchsize to set
    */
   public NamingContextBuilder withBatchSize(final String batchsize) {
	getContextParameters().put(BatchSize, batchsize);
	return this;
   }

   /**
    * @param language the language to set
    */
   public NamingContextBuilder withLanguage(final String language) {
	getContextParameters().put(Language, language);
	return this;
   }

   /**
    * @param referral the referral to set
    */
   public NamingContextBuilder withReferral(final String referral) {
	getContextParameters().put(Referral, referral);
	return this;
   }

   /**
    * @param securityauthentication the security authentication to set
    */
   public NamingContextBuilder withSecurityAuthentication(final String securityauthentication) {
	getContextParameters().put(SecurityAuthentication, securityauthentication);
	return this;
   }

   /**
    * @param securitycredentials the security credentials to set
    */
   public NamingContextBuilder withSecuritycredentials(final String securitycredentials) {
	getContextParameters().put(SecurityCredentials, securitycredentials);
	return this;
   }

   /**
    * @param securityprincipal the security principal to set
    */
   public NamingContextBuilder withSecurityPrincipal(final String securityprincipal) {
	getContextParameters().put(SecurityPrincipal, securityprincipal);
	return this;
   }

   /**
    * @param securityprotocol the security protocol to set
    */
   public NamingContextBuilder withSecurityProtocol(final String securityprotocol) {
	getContextParameters().put(SecurityProtocol, securityprotocol);
	return this;
   }

   /**
    * @param stateFactories the state factories to set
    */
   public NamingContextBuilder withStateFactories(final String stateFactories) {
	getContextParameters().put(StateFactories, stateFactories);
	return this;
   }

   /**
    * @param corbaORBInitialHost
    */
   public NamingContextBuilder withCorbaORBInitialHost(final String corbaORBInitialHost) {
	getContextParameters().put(CorbaORBInitialHost, corbaORBInitialHost);
	return this;
   }

   /**
    * @param corbaORBInitialPort
    */
   public NamingContextBuilder withCorbaORBInitialPort(final String corbaORBInitialPort) {
	getContextParameters().put(CorbaORBInitialPort, corbaORBInitialPort);
	return this;
   }

}
