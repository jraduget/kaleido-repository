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
package org.kaleidofoundry.messaging;

import static org.kaleidofoundry.messaging.ClientContextBuilder.THREAD_POOL_COUNT_PROPERTY;
import static org.kaleidofoundry.messaging.MessagingConstants.I18N_RESOURCE;
import static org.kaleidofoundry.messaging.ClientContextBuilder.DEBUG_PROPERTY;
import static org.kaleidofoundry.messaging.ClientContextBuilder.TRANSPORT_REF;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractProducer implements Producer {

   /** Default Logger */
   protected static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

   // message counters
   protected final AtomicInteger ProcessedMessagesOK = new AtomicInteger(0);
   protected final AtomicInteger ProcessedMessagesKO = new AtomicInteger(0);
   protected final AtomicInteger ProcessedMessagesSKIPPED = new AtomicInteger(0);

   /** I18n messaging bundle */
   protected final I18nMessages MESSAGING_BUNDLE = I18nMessagesFactory.provides(I18N_RESOURCE, InternalBundleHelper.CoreMessageBundle);
   protected final RuntimeContext<Producer> context;
   protected final Transport transport;

   /** Executor service used to implements timeout feature when sending message */
   @Task(comment="Lazy creation of the executor service for google application engine, use com.google.appengine.api.taskqueue.Queue ?")
   protected final ExecutorService pool;

   public AbstractProducer(RuntimeContext<Producer> context) {
	this.context = context;

	// transport
	String transportRef = this.context.getString(TRANSPORT_REF);
	if (!StringHelper.isEmpty(transportRef)) {
	   this.transport = TransportFactory.provides(transportRef, context);
	   this.transport.getProducers().put(getName(), this);
	} else {
	   throw new EmptyContextParameterException(TRANSPORT_REF, context);
	}

	// thread executor service
	int threadCount = this.context.getInteger(THREAD_POOL_COUNT_PROPERTY, 1);

	LOGGER.info("Creating producer [{}] with a thread pool size of {}", context.getName(), threadCount);
	this.pool = Executors.newFixedThreadPool(threadCount);

   }

   @Override
   public void send(final Message message, final long timeout) throws MessagingException {

	if (timeout <= 0) {
	   send(message);
	}
	
	FutureTask<Throwable> future = new FutureTask<Throwable>(new Callable<Throwable>() {
	   public Throwable call() {
		try {
		   send(message);
		   return null;
		} catch (Throwable th) {
		   return th;
		}
	   }
	});

	pool.execute(future);

	try {
	   Throwable error = future.get(timeout, TimeUnit.MILLISECONDS);

	   if (error != null) {
		if (error instanceof MessagingException) {
		   throw (MessagingException) error;
		} else {
		   throw new IllegalStateException("Internal producer error", error);
		}
	   }

	} catch (TimeoutException ex) {
	   throw MessageTimeoutException.buildProducerTimeoutException(getName());
	} catch (ExecutionException eex) {
	   throw new IllegalStateException("Executor service error", eex);
	} catch (InterruptedException iex) {
	   throw new IllegalStateException("Thread have been interrupted", iex);
	}

   }

   @Override
   public void send(final Collection<Message> messages, final  long timeout) throws MessagingException {

	if (timeout <= 0) {
	   send(messages);
	}

	FutureTask<Throwable> future = new FutureTask<Throwable>(new Callable<Throwable>() {
	   public Throwable call() {
		try {
		   send(messages);
		   return null;
		} catch (Throwable th) {
		   return th;
		}
	   }
	});

	pool.execute(future);

	try {
	   Throwable error = future.get(timeout, TimeUnit.MILLISECONDS);

	   if (error != null) {
		if (error instanceof MessagingException) {
		   throw (MessagingException) error;
		} else {
		   throw new IllegalStateException("Internal producer error", error);
		}
	   }

	} catch (TimeoutException ex) {
	   throw MessageTimeoutException.buildProducerTimeoutException(getName());
	} catch (ExecutionException eex) {
	   throw new IllegalStateException("Executor service error", eex);
	} catch (InterruptedException iex) {
	   throw new IllegalStateException("Thread have been interrupted", iex);
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Client#getTransport()
    */
   @Override
   public Transport getTransport() throws TransportException {
	return transport;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Producer#getName()
    */
   @Override
   public String getName() {
	return context.getName();
   }

   @Override
   public void stop() throws TransportException {
	pool.shutdown();	
	this.transport.getProducers().remove(getName());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Client#getStatistics()
    */
   @Override
   public UsageStatistics getStatistics() {
	return new UsageStatistics(ProcessedMessagesOK.get(), ProcessedMessagesKO.get(), 0);
   }

   protected boolean isDebug() {
	return context.getBoolean(DEBUG_PROPERTY, false) || LOGGER.isDebugEnabled();
   }

   protected void debugMessage(Message message) {
	if (isDebug()) {
	   LOGGER.info(">>> sending message with providerId={} , correlationId={} , parameters={}",
		   new String[] { message.getProviderId(), message.getCorrelationId(), String.valueOf(message.getParameters()) });
	   LOGGER.info("{}", message.toString());
	}
   }

   
}
