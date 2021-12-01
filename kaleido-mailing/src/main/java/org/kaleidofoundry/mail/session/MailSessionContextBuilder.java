package org.kaleidofoundry.mail.session;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

import javax.mail.Session;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;

public class MailSessionContextBuilder extends AbstractRuntimeContextBuilder<MailSessionService> {

   /** see {@link MailSessionProviderEnum} */
   public static final String PROVIDER = "provider";

   /** Smtp host */
   public static final String LOCAL_SMTP_HOST = "smtp.host";
   /** Smtp port */
   public static final String LOCAL_SMTP_PORT = "smtp.port";
   /** Smtp debug mod */
   public static final String LOCAL_SMTP_DEBUG = "smtp.debug";
   
   /** Smtp authentication <code>true|false</code> */
   public static final String LOCAL_SMTP_AUTH = "smtp.auth.enable";
   /** Smtp authentication user */
   public static final String LOCAL_SMTP_USER = "smtp.auth.user";
   /** Smtp authentication password */
   public static final String LOCAL_SMTP_PASSWORD = "smtp.auth.password";
   
   /** Smtp TLS is enable */
   public static final String LOCAL_SMTP_TLS = "smtp.tls";

   /** Smtp SSL is enabled */
   public static final String LOCAL_SMTP_SSL = "smtp.ssl";
   /** Default security provider when SSL is used to send mail */
   public final static String LOCAL_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
   /** Socket factory class to use when SSL is enabled */
   public static final String LOCAL_SMTP_SSL_SOCKETFACTORY = "smtp.socketFactory.class";
   /** Socket factory fallback to use when SSL is enabled */
   public static final String LOCAL_SMTP_SSL_SOCKETFACTORY_FALLBACK = "smtp.socketFactory.fallback";
   /** Socket factory port to use when SSL is enabled */
   public static final String LOCAL_SMTP_SSL_SOCKETFACTORY_PORT = "smtp.socketFactory.port";

   /** Jndi name to lookup {@link Session} */
   public static final String JNDI_NAMING_SERVICE_NAME = "namingService.name";
   /** Jndi naming service name to lookup the {@link Session} */
   public static final String JNDI_NAMING_SERVICE_REF = "namingService.service-ref";

   /**
    * @param provider
    * @return current builder instance
    */
   public MailSessionContextBuilder withProvider(final String provider) {
	getContextParameters().put(PROVIDER, provider);
	return this;
   }

   /**
    * @param host
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpHost(final String host) {
	getContextParameters().put(LOCAL_SMTP_HOST, host);
	return this;
   }

   /**
    * @param port
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpPort(final String port) {
	getContextParameters().put(LOCAL_SMTP_PORT, port);
	return this;
   }

   /**
    * @param debug
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpDebug(final boolean debug) {
	getContextParameters().put(LOCAL_SMTP_DEBUG, debug);
	return this;
   }

   /**
    * @param authen
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpAuthentication(final String authen) {
	getContextParameters().put(LOCAL_SMTP_AUTH, authen);
	return this;
   }

   /**
    * @param user
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpUser(final String user) {
	getContextParameters().put(LOCAL_SMTP_USER, user);
	return this;
   }

   /**
    * @param password
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpPassword(final String password) {
	getContextParameters().put(LOCAL_SMTP_PASSWORD, password);
	return this;
   }

   /**
    * @param tls
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpTLS(final boolean tls) {
	getContextParameters().put(LOCAL_SMTP_TLS, tls);
	return this;
   }

   /**
    * @param ssl
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpSSL(final boolean ssl) {
	getContextParameters().put(LOCAL_SMTP_SSL, ssl);
	return this;
   }

   /**
    * @param socketFactoryClass
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpSocketFactoryClass(final String socketFactoryClass) {
	getContextParameters().put(LOCAL_SMTP_SSL_SOCKETFACTORY_FALLBACK, socketFactoryClass);
	return this;
   }

   /**
    * @param socketFactoryFallback
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpSocketFactoryFallback(final String socketFactoryFallback) {
	getContextParameters().put(LOCAL_SMTP_SSL_SOCKETFACTORY_FALLBACK, socketFactoryFallback);
	return this;
   }

   /**
    * @param socketFactoryPort
    * @return current builder instance
    */
   public MailSessionContextBuilder withSmtpSocketFactoryPort(final String socketFactoryPort) {
	getContextParameters().put(LOCAL_SMTP_SSL_SOCKETFACTORY_PORT, socketFactoryPort);
	return this;
   }

   /**
    * @param namingServiceName
    * @return current builder instance
    */
   public MailSessionContextBuilder withNamingServiceName(final String namingServiceName) {
	getContextParameters().put(JNDI_NAMING_SERVICE_NAME, namingServiceName);
	return this;
   }

   /**
    * @param namingServiceRef
    * @return current builder instance
    */
   public MailSessionContextBuilder withNamingServiceRef(final String namingServiceRef) {
	getContextParameters().put(JNDI_NAMING_SERVICE_REF, namingServiceRef);
	return this;
   }

   /**
    * 
    */
   public MailSessionContextBuilder() {
	super();
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public MailSessionContextBuilder(final Class<MailSessionService> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    * @param staticParameters
    */
   public MailSessionContextBuilder(final Class<MailSessionService> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    */
   public MailSessionContextBuilder(final Class<MailSessionService> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public MailSessionContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(staticParameters, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public MailSessionContextBuilder(final String name, final Class<MailSessionService> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public MailSessionContextBuilder(final String name, final Class<MailSessionService> pluginInterface,
	   final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public MailSessionContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public MailSessionContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public MailSessionContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public MailSessionContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public MailSessionContextBuilder(final String name) {
	super(name);
   }

   /**
    * @param parameter parameter name
    * @param value parameter value
    * @return current builder instance
    */
   public MailSessionContextBuilder withParameter(final String parameter, final Serializable value) {
	getContextParameters().put(parameter, value);
	return this;
   }

}
