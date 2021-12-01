/*
 *  Copyright 2008-2021 the original author or authors.
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

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
 * <td>Constant that holds the name of the environment property for specifying the initial context factory to use. The value of the property
 * should be the fully qualified class name of the factory class that will create an initial context. This property may be specified in the
 * environment parameter passed to the initial context constructor, an applet parameter, a system property, or an application resource file.
 * If it is not specified in any of these sources, NoInitialContextException is thrown when an initial context is required to complete an
 * operation. The value of this constant is "java.naming.factory.initial". <br/>
 * see {@link Context#INITIAL_CONTEXT_FACTORY}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.factory.state</td>
 * <td>Constant that holds the name of the environment property for specifying the list of state factories to use. The value of the property
 * should be a colon-separated list of the fully qualified class names of state factory classes that will be used to get an object's state
 * given the object itself. This property may be specified in the environment, an applet parameter, a system property, or one or more
 * resource files. The value of this constant is "java.naming.factory.state". <br/>
 * see {@link Context#STATE_FACTORIES}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.factory.url.pkgs</td>
 * <td>Constant that holds the name of the environment property for specifying the list of package prefixes to use when loading in URL
 * context factories. The value of the property should be a colon-separated list of package prefixes for the class name of the factory class
 * that will create a URL context factory. This property may be specified in the environment, an applet parameter, a system property, or one
 * or more resource files. The prefix com.sun.jndi.url is always appended to the possibly empty list of package prefixes. The value of this
 * constant is "java.naming.factory.url.pkgs". <br/>
 * see {@link Context#URL_PKG_PREFIXES}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.provider.url</td>
 * <td>Constant that holds the name of the environment property for specifying configuration information for the service provider to use.
 * The value of the property should contain a URL string (e.g. "ldap://somehost:389"). This property may be specified in the environment, an
 * applet parameter, a system property, or a resource file. If it is not specified in any of these sources, the default configuration is
 * determined by the service provider. The value of this constant is "java.naming.provider.url". <br/>
 * see {@link Context#PROVIDER_URL}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.dns.url</td>
 * <td>Constant that holds the name of the environment property for specifying the DNS host and domain names to use for the JNDI URL context
 * (for example, "dns://somehost/wiz.com"). This property may be specified in the environment, an applet parameter, a system property, or a
 * resource file. If it is not specified in any of these sources and the program attempts to use a JNDI URL containing a DNS name, a
 * ConfigurationException will be thrown. The value of this constant is "java.naming.dns.url". <br/>
 * see {@link Context#DNS_URL}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.authoritative</td>
 * <td>Constant that holds the name of the environment property for specifying the authoritativeness of the service requested. If the value
 * of the property is the string "true", it means that the access is to the most authoritative source (i.e. bypass any cache or replicas).
 * If the value is anything else, the source need not be (but may be) authoritative. If unspecified, the value defaults to "false". The
 * value of this constant is "java.naming.authoritative". <br/>
 * see {@link Context#AUTHORITATIVE}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.authentication</td>
 * <td>Constant that holds the name of the environment property for specifying the security level to use. Its value is one of the following
 * strings: "none", "simple", "strong". If this property is unspecified, the behaviour is determined by the service provider. The value of
 * this constant is "java.naming.security.authentication". <br/>
 * see {@link Context#SECURITY_AUTHENTICATION}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.principal</td>
 * <td>Constant that holds the name of the environment property for specifying the identity of the principal for authenticating the caller
 * to the service. The format of the principal depends on the authentication scheme. If this property is unspecified, the behaviour is
 * determined by the service provider. The value of this constant is "java.naming.security.principal". <br/>
 * see {@link Context#SECURITY_PRINCIPAL}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.credentials</td>
 * <td>Constant that holds the name of the environment property for specifying the credentials of the principal for authenticating the
 * caller to the service. The value of the property depends on the authentication scheme. For example, it could be a hashed password,
 * clear-text password, key, certificate, and so on. If this property is unspecified, the behaviour is determined by the service provider.
 * The value of this constant is "java.naming.security.credentials". <br/>
 * see {@link Context#SECURITY_CREDENTIALS}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.language</td>
 * <td>Constant that holds the name of the environment property for specifying the preferred language to use with the service. The value of
 * the property is a colon-separated list of language tags as defined in RFC 1766. If this property is unspecified, the language preference
 * is determined by the service provider. The value of this constant is "java.naming.language".<br/>
 * see {@link Context#LANGUAGE}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.batchsize</td>
 * <td>Constant that holds the name of the environment property for specifying the batch size to use when returning data via the service's
 * protocol. This is a hint to the provider to return the results of operations in batches of the specified size, so the provider can
 * optimize its performance and usage of resources. The value of the property is the string representation of an integer. If unspecified,
 * the batch size is determined by the service provider. The value of this constant is "java.naming.batchsize".<br/>
 * see {@link Context#BATCHSIZE}</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>java.naming.security.protocol</td>
 * <td>Constant that holds the name of the environment property for specifying the security protocol to use. Its value is a string
 * determined by the service provider (e.g. "ssl"). If this property is unspecified, the behaviour is determined by the service provider.
 * The value of this constant is "java.naming.security.protocol".<br/>
 * see {@link Context#SECURITY_PROTOCOL}</td>
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
 * This feature enable caching of the sought resource. Be careful, in a clustered environment, your resource can be bound to a specific
 * server. For example, failover policies might be implemented in the EJB homes... It depends on the vendor's implementation. However, if
 * your application server and your environment allows it, you will have great benefit to cache it. For example, for weblogic,
 * weblogic-ejb-jar.xml descriptor allow you to specify home-is-clusterable for your ejb home, and it is activated by default. If caching is
 * enabled, when a lookup failed on a cached resource, throwing {@link NamingException}, {@link CommunicationException}... the resource will
 * be remove from cache, and a new one will be lookup.
 * </p>
 * <ul>
 * <li><code>none</code> -> no caching resource (default usage)</li>
 * <li><code>context</code> -> caching {@link InitialContext}</li>
 * <li><code>home</code> -> caching {@link EJBHome}</li>
 * <li><code>all</code> -> caching both</li>
 * </ul>
 * Values : <code>none |  all | context | home</code></td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>caching.strategy</td>
 * <td>If {@link #Caching} property is enabled, you can define your caching policy :<br/>
 * <ul>
 * <li><code>thread-local</code> -> resource is cached in thread local</li>
 * <li><code>global</code> -> resource is cached static way in a Concurrent HashMap</li>
 * </ul>
 * Values : <code>threadlocal | global</code></td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>failover.enabled</td>
 * <td>Use it if you want <b>to enable failover</b> for resource context creation or resource lookup.<br/>
 * <br/>
 * The following exceptions will be handled by the failover processing : <br/>
 * <br/>
 * <b>for initial context creation :</b>
 * <ul>
 * <li>javax.naming.NamingException</li>
 * <li>javax.naming.CommunicationException</li>
 * </ul>
 * <b>for lookup process :</b>
 * <ul>
 * <li>java.rmi.RemoteException</li>
 * <li><- java.rmi.ConnectException</li>
 * <li><- java.rmi.NoSuchObjectException</li>
 * <li><- java.rmi.AccessException</li>
 * <li><- java.rmi.ConnectIOException</li>
 * <li><- java.rmi.UnknownHostException</li>
 * <li><- java.rmi.UnmarshalException</li>
 * </ul>
 * <br/>
 * <b>Values :</b> <code>true|false</code></td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>failover.wait</td>
 * <td>If <b>failover is enabled</b> ({@link #FailoverEnabled}), this property defines the time in <b>millisecond</b> to sleep after the
 * last fail. It could be set to 0 in order to retry processing without sleeping</td>
 * </tr>
 * <tr>
 * <td>jndi</td>
 * <td>failover.maxretry</td>
 * <td>If <b>failover is enabled</b> ({@link #FailoverEnabled}), this property defines the maximum number of time to attempt a retry after a
 * fail</td>
 * </tr>
 * </table>
 * <br/>
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
 * @author jraduget
 */
public class NamingContextBuilder extends AbstractRuntimeContextBuilder<NamingService> {

   // standard initial context properties **************************************************************************************************

   /**
    * @see Context#PROVIDER_URL
    */
   public static final String ProviderUrl = "providerUrl";
   /**
    * @see Context#INITIAL_CONTEXT_FACTORY
    */
   public static final String InitialContextFactory = "factoryInitialClass";
   /**
    * @see Context#URL_PKG_PREFIXES
    */
   public static final String UrlPkgPrefixes = "factoryUrlPkgs";
   /**
    * @see Context#DNS_URL
    */
   public static final String DnsUrl = "dnsUrl";
   /**
    * @see Context#AUTHORITATIVE
    */
   public static final String Authoritative = "authoritative";
   /**
    * @see Context#BATCHSIZE
    */
   public static final String BatchSize = "batchsize";
   /**
    * @see Context#LANGUAGE
    */
   public static final String Language = "language";
   /**
    * @see Context#REFERRAL
    */
   public static final String Referral = "referral";
   /**
    * @see Context#SECURITY_AUTHENTICATION
    */
   public static final String SecurityAuthentication = "securityAuthentication";
   /**
    * @see Context#SECURITY_PRINCIPAL
    */
   public static final String SecurityPrincipal = "securityPrincipal";
   /**
    * @see Context#SECURITY_CREDENTIALS
    */
   public static final String SecurityCredentials = "securityCredentials";
   /**
    * @see Context#SECURITY_PROTOCOL
    */
   public static final String SecurityProtocol = "securityProtocol";
   /**
    * @see Context#STATE_FACTORIES
    */
   public static final String StateFactories = "factoryState";
   /**
    * org.omg.CORBA.ORBInitialHost
    */
   public static final String CorbaORBInitialHost = "orbInitialHost";
   /**
    * org.omg.CORBA.ORBInitialPort
    */
   public static final String CorbaORBInitialPort = "orbInitialPort";

   /** Mapping between context properties and java naming properties */
   static final ConcurrentMap<String, String> CONTEXT_PARAMETER_MAPPER = new ConcurrentHashMap<String, String>();

   static {
	CONTEXT_PARAMETER_MAPPER.put(ProviderUrl, Context.PROVIDER_URL);
	CONTEXT_PARAMETER_MAPPER.put(InitialContextFactory, Context.INITIAL_CONTEXT_FACTORY);
	CONTEXT_PARAMETER_MAPPER.put(UrlPkgPrefixes, Context.URL_PKG_PREFIXES);
	CONTEXT_PARAMETER_MAPPER.put(DnsUrl, Context.DNS_URL);
	CONTEXT_PARAMETER_MAPPER.put(Authoritative, Context.AUTHORITATIVE);
	CONTEXT_PARAMETER_MAPPER.put(BatchSize, Context.BATCHSIZE);
	CONTEXT_PARAMETER_MAPPER.put(Language, Context.LANGUAGE);
	CONTEXT_PARAMETER_MAPPER.put(Referral, Context.REFERRAL);
	CONTEXT_PARAMETER_MAPPER.put(SecurityAuthentication, Context.SECURITY_AUTHENTICATION);
	CONTEXT_PARAMETER_MAPPER.put(SecurityPrincipal, Context.SECURITY_PRINCIPAL);
	CONTEXT_PARAMETER_MAPPER.put(SecurityCredentials, Context.SECURITY_CREDENTIALS);
	CONTEXT_PARAMETER_MAPPER.put(SecurityProtocol, Context.SECURITY_PROTOCOL);
	CONTEXT_PARAMETER_MAPPER.put(StateFactories, Context.STATE_FACTORIES);
	CONTEXT_PARAMETER_MAPPER.put(CorbaORBInitialHost, "org.omg.CORBA.ORBInitialHost");
	CONTEXT_PARAMETER_MAPPER.put(CorbaORBInitialPort, "org.omg.CORBA.ORBInitialPort");

   }

   // kaleido custom properties ************************************************************************************************************

   /**
    * see {@link NamingContextBuilder}
    */
   public static final String EnvPrefixName = "envPrefix";

   /**
    * see {@link NamingContextBuilder}
    * 
    * @see #CachingStrategy
    */
   public static final String Caching = "caching";

   /**
    * see {@link NamingContextBuilder}
    * 
    * @see #Caching
    */
   public static final String CachingStrategy = "caching.strategy";

   /**
    * see {@link NamingContextBuilder}
    */
   public static final String FailoverEnabled = "failover.enabled";

   /**
    * see {@link NamingContextBuilder}
    */
   public static final String FailoverWaitBeforeRetry = "failover.wait";

   /**
    * see {@link NamingContextBuilder}
    */
   public static final String FailoverMaxRetry = "failover.maxretry";

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
    * @param staticParameters
    */
   public NamingContextBuilder(final Class<NamingService> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    */
   public NamingContextBuilder(final Class<NamingService> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public NamingContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(staticParameters, configurations);
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
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public NamingContextBuilder(final String name, final Class<NamingService> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
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
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public NamingContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
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
