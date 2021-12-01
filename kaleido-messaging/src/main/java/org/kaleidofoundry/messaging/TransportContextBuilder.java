/*  
 * Copyright 2008-2021 the original author or authors 
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
package org.kaleidofoundry.messaging;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.messaging.rdv.RdvTransportTypeEnum;

/**
 * @author jraduget
 */
public class TransportContextBuilder extends AbstractRuntimeContextBuilder<Transport> {

   /** Transport type : see {@link TransportProviderEnum} */
   public static final String TRANSPORT_PROVIDER = "provider";

   /** ConnectionFactory url (for non jndi access) */
   public static final String JMS_CONNECTION_FACTORY_URL = "connectionFactory.url";
   /** ConnectionFactory user */
   public static final String JMS_CONNECTION_FACTORY_USER = "connectionFactory.user";
   /** ConnectionFactory password */
   public static final String JMS_CONNECTION_FACTORY_PASSWORD = "connectionFactory.password";
   /**
    * Trust All Packages parameter to allow the serialization of more classes by allowing any object to be serialized and deserialized, this
    * is not as secure as leaving it disabled in most cases
    */
   public static final String JMS_CONNECTION_FACTORY_TRUSTALLPACKAGES = "connectionFactory.trustAllPackages";
   /** Session transacted property */
   public static final String JMS_SESSION_TRANSACTED = "session.transacted";
   /** Destination type : queue|topic (queue is the default) */
   public static final String JMS_DESTINATION_TYPE = "destination.type";

   /** ConnectionFactory naming service name */
   public static final String JMS_CONNECTION_FACTORY_NAME = "namingService.name";
   /** ConnectionFactory naming service reference */
   public static final String JMS_CONNECTION_FACTORY_NAMING_SERVICE_REF = "namingService.service-ref";

   /**
    * Session acknowledgeMode property
    * 
    * @see javax.jms.Session.AUTO_ACKNOWLEDGE
    * @see javax.jms.Session.CLIENT_ACKNOWLEDGE
    * @see javax.jms.Session.DUPS_OK_ACKNOWLEDGE
    */
   public static final String JMS_SESSION_ACKNOLEDGE_MODE = "session.acknowledgeMode";

   /** ConnectionFactory host (for provider like Websphere MQ) */
   public static final String MQ_CONNECTION_FACTORY_HOST = "connectionFactory.host";
   /** ConnectionFactory port (for provider like Websphere MQ) */
   public static final String MQ_CONNECTION_FACTORY_PORT = "connectionFactory.port";
   /** ConnectionFactory channel (for provider like Websphere MQ) */
   public static final String MQ_CONNECTION_FACTORY_CHANNEL = "connectionFactory.channel";
   /** ConnectionFactory transport type (for provider like Websphere MQ) */
   public static final String MQ_CONNECTION_FACTORY_TRANSPORT_TYPE = "connectionFactory.transportType";
   /** Destination target client (for provider like Websphere MQ) : 1 (non jms) is the default */
   public static final String MQ_DESTINATION_TARGET_CLIENT = "destination.targetClient";
   /** Destination persistence type (for provider like Websphere MQ) : 1 (persistent) is the default */
   public static final String MQ_DESTINATION_PERSISTENT_TYPE = "destination.persistentType";

   /** RDV transport service parameter */
   public static String RDV_SERVICE_PARAMETER = "tibco.rdv.service";
   /** Network transport parameter */
   public static String RDV_NETWORK_PARAMETER = "tibco.rdv.network";
   /** Daemon transport parameter */
   public static String RDV_DAEMON_PARAMETER = "tibco.rdv.daemon";
   /** RDV transport type @see {@link RdvTransportTypeEnum} */
   public static String RDV_TRANSPORT_TYPE = "tibco.rdv.type";
   /** Cmname for RDV certified transport */
   public static String RDV_CERTIFIED_CMNAME = "tibco.rdv.cmname";
   /** Timeout for RDV certified transport */
   public static String RDV_CERTIFIED_TIMEOUT = "tibco.rdv.timeout";

   public TransportContextBuilder() {
	super();
   }

   public TransportContextBuilder(Class<Transport> pluginInterface, ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   public TransportContextBuilder(Class<Transport> pluginInterface, Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   public TransportContextBuilder(Class<Transport> pluginInterface) {
	super(pluginInterface);
   }

   public TransportContextBuilder(ConcurrentMap<String, Serializable> staticParameters, Configuration... configurations) {
	super(staticParameters, configurations);
   }

   public TransportContextBuilder(String name, Class<Transport> pluginInterface, ConcurrentMap<String, Serializable> staticParameters,
	   Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
   }

   public TransportContextBuilder(String name, Class<Transport> pluginInterface, Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   public TransportContextBuilder(String name, Configuration... configurations) {
	super(name, configurations);
   }

   public TransportContextBuilder(String name, String prefixProperty, ConcurrentMap<String, Serializable> staticParameters, Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
   }

   public TransportContextBuilder(String name, String prefixProperty, Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   public TransportContextBuilder(String name, String prefix) {
	super(name, prefix);
   }

   public TransportContextBuilder(String name) {
	super(name);
   }

   public TransportContextBuilder withProvider(final String provider) {
	getContextParameters().put(TRANSPORT_PROVIDER, provider);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryUrl(final String connectionFactoryUrl) {
	getContextParameters().put(JMS_CONNECTION_FACTORY_URL, connectionFactoryUrl);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryUser(final String connectionFactoryUser) {
	getContextParameters().put(JMS_CONNECTION_FACTORY_USER, connectionFactoryUser);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryPassword(final String connectionFactoryPassword) {
	getContextParameters().put(JMS_CONNECTION_FACTORY_PASSWORD, connectionFactoryPassword);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryTrustAllPackages(final Boolean trustAllPackages) {
	getContextParameters().put(JMS_CONNECTION_FACTORY_TRUSTALLPACKAGES, trustAllPackages);
	return this;
   }

   public TransportContextBuilder withSessionTransacted(final String sessionTransacted) {
	getContextParameters().put(JMS_SESSION_TRANSACTED, sessionTransacted);
	return this;
   }

   public TransportContextBuilder withDestinationType(final String destinationType) {
	getContextParameters().put(JMS_DESTINATION_TYPE, destinationType);
	return this;
   }

   public TransportContextBuilder withNamingServiceName(final String namingServiceName) {
	getContextParameters().put(JMS_CONNECTION_FACTORY_NAME, namingServiceName);
	return this;
   }

   public TransportContextBuilder withNamingServiceServiceRef(final String namingServiceServiceRef) {
	getContextParameters().put(JMS_CONNECTION_FACTORY_NAMING_SERVICE_REF, namingServiceServiceRef);
	return this;
   }

   public TransportContextBuilder withSessionAcknowledgeMode(final String sessionAcknowledgeMode) {
	getContextParameters().put(JMS_SESSION_ACKNOLEDGE_MODE, sessionAcknowledgeMode);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryHost(final String connectionFactoryHost) {
	getContextParameters().put(MQ_CONNECTION_FACTORY_HOST, connectionFactoryHost);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryPort(final String connectionFactoryPort) {
	getContextParameters().put(MQ_CONNECTION_FACTORY_PORT, connectionFactoryPort);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryChannel(final String connectionFactoryChannel) {
	getContextParameters().put(MQ_CONNECTION_FACTORY_CHANNEL, connectionFactoryChannel);
	return this;
   }

   public TransportContextBuilder withConnectionFactoryTransportType(final String connectionFactoryTransportType) {
	getContextParameters().put(MQ_CONNECTION_FACTORY_TRANSPORT_TYPE, connectionFactoryTransportType);
	return this;
   }

   public TransportContextBuilder withDestinationTargetClient(final String destinationTargetClient) {
	getContextParameters().put(MQ_DESTINATION_TARGET_CLIENT, destinationTargetClient);
	return this;
   }

   public TransportContextBuilder withDestinationPersistentType(final String destinationPersistentType) {
	getContextParameters().put(MQ_DESTINATION_PERSISTENT_TYPE, destinationPersistentType);
	return this;
   }

   public TransportContextBuilder withTibcoRdvService(final String tibcoRdvService) {
	getContextParameters().put(RDV_SERVICE_PARAMETER, tibcoRdvService);
	return this;
   }

   public TransportContextBuilder withTibcoRdvNetwork(final String tibcoRdvNetwork) {
	getContextParameters().put(RDV_NETWORK_PARAMETER, tibcoRdvNetwork);
	return this;
   }

   public TransportContextBuilder withTibcoRdvDaemon(final String tibcoRdvDaemon) {
	getContextParameters().put(RDV_DAEMON_PARAMETER, tibcoRdvDaemon);
	return this;
   }

   public TransportContextBuilder withTibcoRdvType(final String tibcoRdvType) {
	getContextParameters().put(RDV_TRANSPORT_TYPE, tibcoRdvType);
	return this;
   }

   public TransportContextBuilder withTibcoRdvCmname(final String tibcoRdvCmname) {
	getContextParameters().put(RDV_CERTIFIED_CMNAME, tibcoRdvCmname);
	return this;
   }

   public TransportContextBuilder withTibcoRdvTimeout(final String tibcoRdvTimeout) {
	getContextParameters().put(RDV_CERTIFIED_TIMEOUT, tibcoRdvTimeout);
	return this;
   }
}
