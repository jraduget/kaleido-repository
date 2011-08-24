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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.NamingMessageBundle;
import static org.kaleidofoundry.core.naming.NamingConstants.JndiNamingPluginName;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.EnvPrefixName;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.FailoverEnabled;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.FailoverMaxRetry;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.FailoverWaitBeforeRetry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JNDI naming service implementation
 * 
 * @author Jerome RADUGET
 */
@Declare(value = JndiNamingPluginName, description = "jndi naming service implementation")
public class JndiNamingService implements NamingService {

   protected static final Logger logger = LoggerFactory.getLogger(JndiNamingService.class);

   private final RuntimeContext<NamingService> context;

   /**
    * @param context
    * @throws NamingServiceException
    */
   public JndiNamingService(final RuntimeContext<NamingService> context) throws NamingServiceException {
	this.context = context;
   }

   /**
    * don't use it,
    * this constructor is only needed and used by some IOC framework like spring.
    */
   JndiNamingService() {
	this.context = null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingService#lookup(java.lang.String, java.lang.Class)
    */
   @Override
   public <R> R locate(@NotNull final String resourceName, @NotNull final Class<R> ressourceClass) throws NamingServiceException {

	final String jndiEnvNamePrefix = context.getString(EnvPrefixName); // optional java comp env prefix
	final StringBuilder fullResourceName = new StringBuilder(); // full resource name

	// we prefix resource name if needed
	if (!StringHelper.isEmpty(jndiEnvNamePrefix) && !resourceName.startsWith(jndiEnvNamePrefix)) {
	   fullResourceName.append(jndiEnvNamePrefix);
	   if (!jndiEnvNamePrefix.endsWith("/")) {
		fullResourceName.append("/");
	   }
	}
	fullResourceName.append(resourceName);

	// lookup & check cast
	Context intialContext = null;
	final Object resource;

	try {
	   // feed init context properties (can be empty)
	   intialContext = new InitialContext(context.toProperties());
	   for (final String property : context.keySet()) {
		intialContext.addToEnvironment(property, context.getProperty(property));
	   }

	   // lookup the resource
	   if (isFailoverEnabled()) {
		resource = lookupWithFailover(intialContext, fullResourceName.toString());
	   } else {
		resource = intialContext.lookup(fullResourceName.toString());
	   }

	   // cast check of the result object (remote or local)
	   try {
		return ressourceClass.cast(PortableRemoteObject.narrow(resource, ressourceClass));
	   } catch (final ClassCastException cce) {
		throw new NamingServiceException("naming.jndi.error.initialContext.lookup.classcast", fullResourceName.toString(), ressourceClass.getName(),
			resource.getClass().getName());
	   }
	} catch (final NamingException nae) {
	   throw handleNamingException(nae, fullResourceName.toString());
	} finally {
	   // free initial context
	   try {
		if (intialContext != null) {
		   intialContext.close();
		}
	   } catch (final NamingException ne) {
		throw new NamingServiceException("naming.jndi.error.initialContext.close", ne, fullResourceName.toString(), ressourceClass.getName());
	   }
	}
   }

   /**
    * @param ne
    * @param resourceName
    * @return i18n exception conversion from NamingException parameter
    */
   protected NamingServiceException handleNamingException(final NamingException ne, final String resourceName) {

	// direct not found resource
	if (ne instanceof NameNotFoundException) { return new NamingServiceNotFoundException(resourceName, (NameNotFoundException) ne, context); }

	// cause is not found resource
	if (ne instanceof NamingException && ne.getCause() != null && ne.getCause() instanceof NameNotFoundException) { return new NamingServiceNotFoundException(
		resourceName, (NameNotFoundException) ne.getCause(), context); }

	// otherwise ...
	return new NamingServiceException("naming.jndi.error.initialContext.lookup", ne, resourceName, ne.getMessage(), context.toString());
   }

   /**
    * @param <R>
    * @param context
    * @return context instance build using runtime context properties
    * @throws NamingException
    */
   protected static <R> Context createInitialContext(final RuntimeContext<NamingService> context) throws NamingException {
	final Context initialContext = new InitialContext();
	for (final String propertyName : context.keySet()) {
	   final String propertyValue = context.getString(propertyName);
	   if (propertyValue != null) {
		initialContext.addToEnvironment(propertyName, propertyValue);
	   }
	}
	return initialContext;
   }

   /**
    * @return current instance context
    */
   RuntimeContext<NamingService> getContext() {
	return context;
   }

   /**
    * @return does failover is enabled
    * @see NamingContextBuilder#FailoverEnabled
    */
   protected boolean isFailoverEnabled() {
	return context.getBoolean(FailoverEnabled, false);
   }

   /**
    * @return max attempt after failure
    * @see NamingContextBuilder#FailoverMaxRetry
    */
   protected int getFailoverMaxRetry() {
	final Integer maxRetry = context.getInteger(FailoverMaxRetry);
	if (maxRetry != null) {
	   return maxRetry.intValue();
	} else {
	   return -1;
	}
   }

   /**
    * @return time to sleep after failure
    * @see NamingContextBuilder#FailoverWaitBeforeRetry
    */
   protected int getFailoverWaitBeforeRetry() {
	final Integer sleepOnFailure = context.getInteger(FailoverWaitBeforeRetry);
	if (sleepOnFailure != null) {
	   return sleepOnFailure.intValue();
	} else {
	   return 0;
	}
   }

   /**
    * jndi lookup using if configure a failover processing
    * 
    * @param intialContext
    * @param resourceName
    * @return the lookup resource
    * @throws NamingException
    */
   protected Object lookupWithFailover(final Context intialContext, final String resourceName) throws NamingException {

	int retryCount = 0;
	int maxRetryCount = 1;
	NamingException lastError = null;

	while (retryCount < maxRetryCount) {
	   try {
		return intialContext.lookup(resourceName);
	   } catch (final NamingException nae) {
		lastError = nae;
		maxRetryCount = getFailoverMaxRetry();
		// no fail-over, we throw the exception
		if (maxRetryCount <= 0) {
		   throw nae;
		}
		// wait for the configuring delay (in milliseconds)
		else {
		   retryCount++;
		   final int sleepTime = getFailoverWaitBeforeRetry();
		   if (retryCount < maxRetryCount) {
			logger.warn(NamingMessageBundle.getMessage("naming.failover.retry.get.info", resourceName, sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   logger.error(NamingMessageBundle.getMessage("naming.failover.retry.error", sleepTime), nae);
			   throw nae;
			}
		   } else {
			logger.error(NamingMessageBundle.getMessage("naming.failover.retry.get.info", resourceName, sleepTime, retryCount, maxRetryCount), nae);
		   }
		}
	   }
	}

	if (lastError != null) {
	   throw lastError;
	} else {
	   throw new IllegalStateException();
	}

   }

}
