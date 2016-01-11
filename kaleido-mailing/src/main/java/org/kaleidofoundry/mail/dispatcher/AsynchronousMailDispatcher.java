/*  
 * Copyright 2008-2016 the original author or authors 
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
package org.kaleidofoundry.mail.dispatcher;

import static org.kaleidofoundry.mail.MailConstants.AsynchronousMailDispatcherPluginName;
import static org.kaleidofoundry.mail.dispatcher.MailDispatcherContextBuilder.THREAD_COUNT;
import static org.kaleidofoundry.mail.dispatcher.MailDispatcherContextBuilder.TIMEOUT;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.mail.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
@Declare(AsynchronousMailDispatcherPluginName)
public class AsynchronousMailDispatcher extends SynchronousMailDispatcher {

   private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousMailDispatcher.class);

   private final ExecutorService executorService;

   public AsynchronousMailDispatcher(final RuntimeContext<MailDispatcher> context) {
	super(context);
	final int threadCount = context.getInteger(THREAD_COUNT, 10);
	LOGGER.info("Starting mail dispatcher service {} with a thread pool size of {}", context.getName(), threadCount);
	executorService = Executors.newFixedThreadPool(threadCount);
   }

   @Override
   public void send(final MailMessage... messages) throws MailDispatcherException {
	this.send(null, messages);
   }

   @Override
   public void send(final MailMessageErrorHandler handler, final MailMessage... messages) {

	final Integer timeout = context.getInteger(TIMEOUT);
	Future<Void> sendFuture = executorService.submit(new Callable<Void>() {
	   public Void call() {
		AsynchronousMailDispatcher.super.send(handler, messages);
		return null;
	   }
	});

	try {
	   if (timeout != null) {
		sendFuture.get(timeout, TimeUnit.MILLISECONDS);
	   } else {
		sendFuture.get();
	   }
	} catch (InterruptedException e) {
	   LOGGER.error("Error while sending mail messages", e);
	} catch (ExecutionException e) {
	   //e.getCause()
	   //handler.
	   LOGGER.error("Error while sending mail messages", e);
	} catch (TimeoutException e) {
	   LOGGER.error("Error while sending mail messages in the configured time %d ms", timeout, e);	   
	}

   }

   public void close() {
	LOGGER.info("Closing mail dispatcher service {}...", context.getName());

	// Disable new tasks from being submitted
	executorService.shutdown();
	try {
	   // Wait a while for existing tasks to terminate
	   if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
		executorService.shutdownNow(); // Cancel currently executing tasks
		// Wait a while for tasks to respond to being cancelled
		if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) LOGGER.error("Mail dispatcher service pool did not terminate");
	   }
	} catch (InterruptedException ie) {
	   // (Re-)Cancel if current thread also interrupted
	   executorService.shutdownNow();
	   // Preserve interrupt status
	   Thread.currentThread().interrupt();
	} finally {
	   LOGGER.info("Mail dispatcher service {} closed", context.getName());
	}
   }
}
