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

import javax.ejb.EJBHome;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Naming service base {@link RuntimeContext} builder & properties. <br/>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Type</th>
 * <th>Property</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.factory.initial</td>
 * <td>see {@link Context#INITIAL_CONTEXT_FACTORY}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.factory.state</td>
 * <td>see {@link Context#STATE_FACTORIES}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.factory.url.pkgs</td>
 * <td>see {@link Context#URL_PKG_PREFIXES}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.provider.url</td>
 * <td>see {@link Context#PROVIDER_URL}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.dns.url</td>
 * <td>see {@link Context#DNS_URL}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.authoritative</td>
 * <td>see {@link Context#AUTHORITATIVE}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.authentication</td>
 * <td>see {@link Context#SECURITY_AUTHENTICATION}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.principal</td>
 * <td>see {@link Context#SECURITY_PRINCIPAL}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.credentials</td>
 * <td>see {@link Context#SECURITY_CREDENTIALS}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.language</td>
 * <td>see {@link Context#LANGUAGE}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.batchsize</td>
 * <td>see {@link Context#BATCHSIZE}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.protocol</td>
 * <td>see {@link Context#SECURITY_PROTOCOL}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>org.omg.CORBA.ORBInitialHost</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>org.omg.CORBA.ORBInitialPort</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <th>Type</th>
 * <th>Property</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.env.prefix</td>
 * <td>jndi optional prefix name for resource name like java:comp/env for tomcat (no more need with jee6)</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>caching</td>
 * <td>
 * <p>
 * This feature allow you to cache your resource lookup (like initial context or ejb home). Be careful, in a clustered environment, your
 * resource can be bound to a specific server. Failover policies might be implemented in the EJB homes... So it depends on the vendor's
 * implementation. However, if your application server and your environment allows it, you will have great benefit to cache it. For example,
 * for weblogic, weblogic-ejb-jar.xml descriptor allow you to specify home-is-clusterable for your ejb home, and it is activated by default.
 * If caching is enabled, when a lookup failed on a cached resource, throwing {@link NamingException}, {@link CommunicationException}...
 * Resource will be remove from cache, and a new one will be lookup.
 * </p>
 * <ul>
 * <li><code>none</code> -> no caching resource (default usage)</li>
 * <li><code>context</code> -> caching {@link InitialContext}</li>
 * <li><code>home</code> -> caching {@link EJBHome}</li>
 * <li><code>all</code> -> caching all</li>
 * </ul>
 * Values : <code>none |  all | context | home</code></td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>caching.strategy</td>
 * <td>if {@link #Caching} property is enabled, you can define your resource cache lookup policy :
 * <ul>
 * <li><code>thread-local</code> -> resource cache in thread local</li>
 * <li><code>global</code> -> resource cache static way in a Concurrent HashMap</li>
 * </ul>
 * Values : <code>threadlocal | global</code></td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>failover.enabled</td>
 * <td>Use it if you want to enable <b>failover</b> for resource context creation or resource lookup.<br/>
 * <br/>
 * The following exceptions will be handled :
 * <table>
 * <tr>
 * <td><b>naming type</b></td>
 * <td><b>context</b></td>
 * <td><b>handle exceptions</b></td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>initial context creation</td>
 * <td>javax.naming.NamingException<br/>
 * javax.naming.CommunicationException</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>lookup and remote call</td>
 * <td>java.rmi.RemoteException<br/>
 * <- java.rmi.ConnectException<br/>
 * <- java.rmi.NoSuchObjectException<br/>
 * <- java.rmi.AccessException<br/>
 * <- java.rmi.ConnectIOException<br/>
 * <- java.rmi.UnknownHostException<br/>
 * <- java.rmi.UnmarshalException</td>
 * </tr>
 * </table>
 * <br/>
 * <b>Values :</b> <code>true|false</code></td>
 * </tr>
 * </table>
 * </td> </tr>
 * <tr>
 * <td>jndi</td>
 * <td>failover.wait</td>
 * <td>if failover is enabled ({@link #FailoverEnabled}), it defines the time in <b>millisecond</b> to sleep after the last fail. It could
 * be set to 0 in order to not sleep</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>failover.maxretry</td>
 * <td>if failover is enabled ({@link #FailoverEnabled}), it defines the maximum number of time to attempt a retry</td>
 * </tr>
 * </table> <br/>
 * <br/>
 * Some JNDI application server configurations :
 * <p>
 * <table border="1" width="100%">
 * <tr>
 * <td><b>Application server</b></td>
 * <td><b>Initial factory - java.naming.factory.initial</b></td>
 * <td><b>Url - java.naming.provider.url</b></td>
 * <td><b>Packages prefix - java.naming.factory.url.pkgs</b></td>
 * <td><b>Env resource prefix</b></td>
 * <td><b>Other properties</b></td>
 * </tr>
 * <tr>
 * <td>TOMCAT 5/6</td>
 * <td>org.apache.naming.java.javaURLContextFactory</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>java:comp/env</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>GLASSFISH 3</td>
 * <td>com.sun.enterprise.naming.SerialInitContextFactory</td>
 * <td>&nbsp;</td>
 * <td>com.sun.enterprise.naming</td>
 * <td>java:comp/env</td>
 * <td>java.naming.factory.state=com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl<br/>
 * org.omg.CORBA.ORBInitialHost=127.0.0.1<br/>
 * org.omg.CORBA.ORBInitialPort=3700</td>
 * </tr>
 * <tr>
 * <td>JBOSS</td>
 * <td>org.jnp.interfaces.NamingContextFactory</td>
 * <td>localhost:1099</td>
 * <td>org.jboss.naming:org.jnp.interfaces</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>JONAS</td>
 * <td>com.sun.jndi.rmi.registry.RegistryContextFactory</td>
 * <td>rmi://localhost:1099</td>
 * <td>org.objectweb.jonas.naming</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>WEBSPHERE 5</td>
 * <td>com.ibm.ejs.ns.jndi.CNInitialContextFactory</td>
 * <td>iiop://localhost:2809</td>
 * <td>&nbsp;</td>
 * <td>java:comp/env</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>WEBSPHERE 6</td>
 * <td>com.ibm.websphere.naming.WsnInitialContextFactory</td>
 * <td>iiop://localhost:900</td>
 * <td>com.ibm.ws.naming</td>
 * <td>java:comp/env</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>WEBLOGIC 8 / 9 / 10</td>
 * <td>weblogic.jndi.WLInitialContextFactory</td>
 * <td>t3://localhost:7001</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>CORBA SUN</td>
 * <td>com.sun.jndi.cosnaming.CNCtxFactory</td>
 * <td>iiop://localhost:2001</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>DNS SUN</td>
 * <td>com.sun.jndi.dns.DnsContextFactory</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>LDAP SUN</td>
 * <td>com.sun.jndi.ldap.LdapCtxFactory</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>RMI SUN</td>
 * <td>com.sun.jndi.rmi.registry.RegistryContextFactory</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class NamingContextBuilder extends AbstractRuntimeContextBuilder<NamingService> {

   // standard initial context properties **************************************************************************************************

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

   // kaleido custom properties ************************************************************************************************************

   /** jndi optional prefix name for resource name (no need with jee6) */
   public static String EnvPrefixName = "java.naming.env.prefix";

   /**
    * <p>
    * This feature allow you to cache your resource lookup (like initial context or ejb home). Be careful, in a clustered environment, your
    * resource can be bound to a specific server. Failover policies might be implemented in the EJB homes... So it depends on the vendor's
    * implementation. However, if your application server and your environment allows it, you will have great benefit to cache it. For
    * example, for weblogic, weblogic-ejb-jar.xml descriptor allow you to specify home-is-clusterable for your ejb home, and it is activated
    * by default. If caching is enabled, when a lookup failed on a cached resource, throwing {@link NamingException},
    * {@link CommunicationException}... Resource will be remove from cache, and a new one will be lookup.
    * </p>
    * <ul>
    * <li><code>none</code> -> no caching resource (default usage)</li>
    * <li><code>context</code> -> caching {@link InitialContext}</li>
    * <li><code>home</code> -> caching {@link EJBHome}</li>
    * <li><code>all</code> -> caching all</li>
    * </ul>
    * Values : <code>none |  all | context | home</code>
    * 
    * @see #CachingStrategy
    */
   public static String Caching = "caching";

   /**
    * if {@link #Caching} property is enabled, you can define your resource cache lookup policy :
    * <ul>
    * <li><code>thread-local</code> -> resource cache in thread local</li>
    * <li><code>global</code> -> resource cache static way in a Concurrent HashMap</li>
    * </ul>
    * Values : <code>threadlocal | global</code>
    * 
    * @see #Caching
    */
   public static String CachingStrategy = "caching.strategy";

   /**
    * Use it if you want to enable <b>failover</b> for resource context creation or resource lookup.<br/>
    * <br/>
    * The following exceptions will be handled :
    * <table border="1">
    * <tr>
    * <td><b>naming type</b></td>
    * <td><b>context</b></td>
    * <td><b>handle exceptions</b></td>
    * </tr>
    * <tr>
    * <td>jndi</td>
    * <td>initial context creation</td>
    * <td>javax.naming.NamingException<br/>
    * javax.naming.CommunicationException</td>
    * </tr>
    * <tr>
    * <td>jndi</td>
    * <td>lookup and remote call</td>
    * <td>java.rmi.RemoteException<br/>
    * <- java.rmi.ConnectException<br/>
    * <- java.rmi.NoSuchObjectException<br/>
    * <- java.rmi.AccessException<br/>
    * <- java.rmi.ConnectIOException<br/>
    * <- java.rmi.UnknownHostException<br/>
    * <- java.rmi.UnmarshalException</td>
    * </tr>
    * </table>
    * <br/>
    * <b>Values :</b> <code>true|false</code>
    */
   public static String FailoverEnabled = "failover.enabled";

   /**
    * if failover is enabled ({@link #FailoverEnabled}), it defines the
    * time in <b>millisecond</b> to sleep after the last fail. It could be set to 0 in order to not sleep
    */
   public static String FailoverWaitBeforeRetry = "failover.wait";

   /**
    * if failover is enabled ({@link #FailoverEnabled}), it defines the
    * maximum number of time to attempt a retry
    */
   public static String FailoverMaxRetry = "failover.maxretry";

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
    * @see #EnvPrefixName
    */
   public NamingContextBuilder withEnvprefixname(final String envprefixname) {
	getContextParameters().put(EnvPrefixName, envprefixname);
	return this;
   }

   /**
    * @param providerurl the provider url to set
    * @see #ProviderUrl
    */
   public NamingContextBuilder withProviderUrl(final String providerurl) {
	getContextParameters().put(ProviderUrl, providerurl);
	return this;
   }

   /**
    * @param initialcontextfactory the initial context factory to set
    * @see #InitialContextFactory
    */
   public NamingContextBuilder withInitialContextFactory(final String initialcontextfactory) {
	getContextParameters().put(InitialContextFactory, initialcontextfactory);
	return this;
   }

   /**
    * @param urlpkgprefixes the urlpkgprefixes to set
    * @see #UrlPkgPrefixes
    */
   public NamingContextBuilder withUrlpkgPrefixes(final String urlpkgprefixes) {
	getContextParameters().put(UrlPkgPrefixes, urlpkgprefixes);
	return this;
   }

   /**
    * @param dnsurl the dnsurl to set
    * @see #DnsUrl
    */
   public NamingContextBuilder withDnsUrl(final String dnsurl) {
	getContextParameters().put(DnsUrl, dnsurl);
	return this;
   }

   /**
    * @param authoritative the authoritative to set
    * @see #Authoritative
    */
   public NamingContextBuilder withAuthoritative(final String authoritative) {
	getContextParameters().put(Authoritative, authoritative);
	return this;
   }

   /**
    * @param batchsize the batchsize to set
    * @see #BatchSize
    */
   public NamingContextBuilder withBatchSize(final String batchsize) {
	getContextParameters().put(BatchSize, batchsize);
	return this;
   }

   /**
    * @param language the language to set
    * @see #Language
    */
   public NamingContextBuilder withLanguage(final String language) {
	getContextParameters().put(Language, language);
	return this;
   }

   /**
    * @param referral the referral to set
    * @see #Referral
    */
   public NamingContextBuilder withReferral(final String referral) {
	getContextParameters().put(Referral, referral);
	return this;
   }

   /**
    * @param securityauthentication the security authentication to set
    * @see #SecurityAuthentication
    */
   public NamingContextBuilder withSecurityAuthentication(final String securityauthentication) {
	getContextParameters().put(SecurityAuthentication, securityauthentication);
	return this;
   }

   /**
    * @param securitycredentials the security credentials to set
    * @see #SecurityCredentials
    */
   public NamingContextBuilder withSecurityCredentials(final String securitycredentials) {
	getContextParameters().put(SecurityCredentials, securitycredentials);
	return this;
   }

   /**
    * @param securityprincipal the security principal to set
    * @see #SecurityPrincipal
    */
   public NamingContextBuilder withSecurityPrincipal(final String securityprincipal) {
	getContextParameters().put(SecurityPrincipal, securityprincipal);
	return this;
   }

   /**
    * @param securityprotocol the security protocol to set
    * @see #SecurityProtocol
    */
   public NamingContextBuilder withSecurityProtocol(final String securityprotocol) {
	getContextParameters().put(SecurityProtocol, securityprotocol);
	return this;
   }

   /**
    * @param stateFactories the state factories to set
    * @see #StateFactories
    */
   public NamingContextBuilder withStateFactories(final String stateFactories) {
	getContextParameters().put(StateFactories, stateFactories);
	return this;
   }

   /**
    * @param corbaORBInitialHost
    * @see #CorbaORBInitialHost
    */
   public NamingContextBuilder withCorbaORBInitialHost(final String corbaORBInitialHost) {
	getContextParameters().put(CorbaORBInitialHost, corbaORBInitialHost);
	return this;
   }

   /**
    * @param corbaORBInitialPort
    * @see #CorbaORBInitialPort
    */
   public NamingContextBuilder withCorbaORBInitialPort(final String corbaORBInitialPort) {
	getContextParameters().put(CorbaORBInitialPort, corbaORBInitialPort);
	return this;
   }

   /**
    * @param failoverEnabled
    * @see #FailoverEnabled
    */
   public NamingContextBuilder withFailoverEnabled(final String failoverEnabled) {
	getContextParameters().put(FailoverEnabled, failoverEnabled);
	return this;
   }

   /**
    * @param failoverMaxRetry
    * @see #FailoverMaxRetry
    */
   public NamingContextBuilder withFailoverMaxRetry(final String failoverMaxRetry) {
	getContextParameters().put(FailoverMaxRetry, failoverMaxRetry);
	return this;
   }

   /**
    * @param failoverWaitBeforeRetry
    * @see #FailoverWaitBeforeRetry
    */
   public NamingContextBuilder withFailoverWaitBeforeRetry(final String failoverWaitBeforeRetry) {
	getContextParameters().put(FailoverWaitBeforeRetry, failoverWaitBeforeRetry);
	return this;
   }

   /**
    * @param caching
    * @see #Caching
    */
   public NamingContextBuilder withCaching(final String caching) {
	getContextParameters().put(Caching, caching);
	return this;
   }

   /**
    * @param cachingStrategy
    * @see #CachingStrategy
    */
   public NamingContextBuilder withCachingStrategy(final String cachingStrategy) {
	getContextParameters().put(CachingStrategy, cachingStrategy);
	return this;
   }
}
