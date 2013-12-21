/*  
 * Copyright 2008-2014 the original author or authors 
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

/**
 * @author jraduget
 */
public class ClientContextBuilder extends AbstractRuntimeContextBuilder<Client> {

   /**
    * 0 (lower) - 9 (highest)
    * 0-4 as gradations of normal priority
    * 5-9 gradations of expedited priority.
    * 
    * @see javax.jms.Message.getJMSPriority()
    */
   public static final String MESSAGE_PRIORITY = "message.priority";
   /**
    * 0 : no message expiration
    * 
    * @see javax.jms.Message.getJMSExpiration()
    */
   public static final String MESSAGE_EXPIRATION = "message.expiration";

   /** async , sync */
   public static final String MESSAGE_CONSUMPTION = "message.consumption";

   /** Full date format for date handling in message */
   public static final String MESSAGE_FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

   /** RDV listener subject */
   public static final String RDV_SUBJECTS = "tibco.rdv.sujects";

   /** Consumer / Producer thread pool count */
   public static final String THREAD_POOL_COUNT_PROPERTY = "threadCount";

   /** Consumer thread prefix */
   public static final String CONSUMER_THREAD_PREFIX_PROPERTY = "threadPrefix";

   /** Consumer executor service waiting time on shutdown */
   public static final String CONSUMER_THREAD_POOL_WAIT_ON_SHUTDOWN = "waitOnShutdown";

   /** Consumer receive timeout */
   public static final String CONSUMER_RECEIVE_TIMEOUT_PROPERTY = "timeout";

   /** Print handled messages by the consumer each */
   public static final String CONSUMER_PRINT_PROCESSED_MESSAGES_MODULO = "printProcessedMsgModulo";

   /** Enable / disable consumer debugging information */
   public static final String DEBUG_PROPERTY = "debug";

   /** Use to fix a minimum response time for message consuming */
   public static final String CONSUMER_RESPONSE_DURATION = "responseDuration";

   /** Consumer transport reference */
   public static final String TRANSPORT_REF = "transport-ref";

   /** Buffer size when consumer read a raw bytes or stream message */
   public static final String CONSUMER_READ_BUFFER_SIZE = "readBufferSier";

   /** Consumer thread pool count */
   public static final String CONSUMER_MESSAGE_SELECTOR_PROPERTY = "message.selector";

   /** <code>true|false</code> If true, inhibit the delivery of messages published by its own connection. Default value is false */
   public static final String CONSUMER_NOLOCAL_PROPERTY = "message.noLocal";

   /** public static final String Destination name of the consumer (queue or topic) - it can be the jndi or phycical queue / topic name */
   public static final String CONSUMER_DESTINATION = "destination.name";

   /** Destinations names separated by {@link #PRODUCER_DESTINATIONS_SEPARATOR} - it can be the jndi or phycical queue / topic name */
   public static final String PRODUCER_DESTINATIONS = "destination.names";

   /** Destinations names separator */
   public static final String PRODUCER_DESTINATIONS_SEPARATOR = ";";

   /**
    * 
    */
   public ClientContextBuilder() {
   }

   /**
    * @param name
    */
   public ClientContextBuilder(String name) {
	super(name);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public ClientContextBuilder(ConcurrentMap<String, Serializable> staticParameters, Configuration... configurations) {
	super(staticParameters, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public ClientContextBuilder(String name, String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    * @param configurations
    */
   public ClientContextBuilder(String name, Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public ClientContextBuilder(String name, String prefixProperty, Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public ClientContextBuilder(String name, String prefixProperty, ConcurrentMap<String, Serializable> staticParameters, Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
   }

   /**
    * @param pluginInterface
    */
   public ClientContextBuilder(Class<Client> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param pluginInterface
    * @param staticParameters
    */
   public ClientContextBuilder(Class<Client> pluginInterface, ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public ClientContextBuilder(Class<Client> pluginInterface, Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public ClientContextBuilder(String name, Class<Client> pluginInterface, Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public ClientContextBuilder(String name, Class<Client> pluginInterface, ConcurrentMap<String, Serializable> staticParameters,
	   Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
   }

   public ClientContextBuilder withTibcoRdvSujects(final String tibcoRdvSujects) {
	getContextParameters().put(RDV_SUBJECTS, tibcoRdvSujects);
	return this;
   }

   public ClientContextBuilder withThreadCount(final String threadCount) {
	getContextParameters().put(THREAD_POOL_COUNT_PROPERTY, threadCount);
	return this;
   }

   public ClientContextBuilder withThreadPrefix(final String threadPrefix) {
	getContextParameters().put(CONSUMER_THREAD_PREFIX_PROPERTY, threadPrefix);
	return this;
   }

   public ClientContextBuilder withWaitOnShutdown(final String waitOnShutdown) {
	getContextParameters().put(CONSUMER_THREAD_POOL_WAIT_ON_SHUTDOWN, waitOnShutdown);
	return this;
   }

   public ClientContextBuilder withTimeout(final String timeout) {
	getContextParameters().put(CONSUMER_RECEIVE_TIMEOUT_PROPERTY, timeout);
	return this;
   }

   public ClientContextBuilder withPrintProcessedMsgModulo(final String printProcessedMsgModulo) {
	getContextParameters().put(CONSUMER_PRINT_PROCESSED_MESSAGES_MODULO, printProcessedMsgModulo);
	return this;
   }

   public ClientContextBuilder withDebug(final String debug) {
	getContextParameters().put(DEBUG_PROPERTY, debug);
	return this;
   }

   public ClientContextBuilder withResponseDuration(final String responseDuration) {
	getContextParameters().put(CONSUMER_RESPONSE_DURATION, responseDuration);
	return this;
   }

   public ClientContextBuilder withTransportRef(final String transportRef) {
	getContextParameters().put(TRANSPORT_REF, transportRef);
	return this;
   }

   public ClientContextBuilder withMessageSelector(final String messageSelector) {
	getContextParameters().put(CONSUMER_MESSAGE_SELECTOR_PROPERTY, messageSelector);
	return this;
   }

   public ClientContextBuilder withMessageNoLocal(final String messageNoLocal) {
	getContextParameters().put(CONSUMER_NOLOCAL_PROPERTY, messageNoLocal);
	return this;
   }

   public ClientContextBuilder withDestinationName(final String destinationName) {
	getContextParameters().put(CONSUMER_DESTINATION, destinationName);
	return this;
   }

   public ClientContextBuilder withDestinationNames(final String destinationNames) {
	getContextParameters().put(PRODUCER_DESTINATIONS, destinationNames);
	return this;
   }

}
