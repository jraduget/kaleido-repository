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

import static org.kaleidofoundry.core.naming.NamingConstants.JndiNamingPluginName;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.EnvPrefixName;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.validation.constraints.NotNull;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.lang.annotation.Reviews;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Jndi naming service implementation
 * 
 * @author Jerome RADUGET
 */
@Declare(value = JndiNamingPluginName, description = "jndi naming service plugin implementation")
@Reviews(reviews = { @Review(category = ReviewCategoryEnum.Improvement, comment = "fail over on client side with context property : failover.enabled ; failover.waiting ; failover.maxretry ; home cache ") })
public class JndiNamingService implements NamingService {

   private final RuntimeContext<NamingService> context;

   /**
    * @param context
    * @throws NamingServiceException
    */
   public JndiNamingService(final RuntimeContext<NamingService> context) throws NamingServiceException {
	this.context = context;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingService#lookup(java.lang.String, java.lang.Class)
    */
   @Override
   public <R> R locate(@NotNull final String resourceName, @NotNull final Class<R> ressourceClass) throws NamingServiceException {

	final String jndiEnvNamePrefix = context.getProperty(EnvPrefixName); // optional java comp env prefix
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
	   for (String property : context.keySet()) {
		intialContext.addToEnvironment(property, context.getProperty(property));
	   }

	   // lookup the resource
	   resource = intialContext.lookup(fullResourceName.toString());

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
	if (ne instanceof NameNotFoundException) { return new NamingServiceNotFoundException(resourceName, context); }

	// cause is not found resource
	if (ne instanceof NamingException && ne.getCause() != null && ne.getCause() instanceof NameNotFoundException) { return new NamingServiceNotFoundException(
		resourceName, context); }

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
	   final String propertyValue = context.getProperty(propertyName);
	   if (propertyValue != null) {
		initialContext.addToEnvironment(propertyName, propertyValue);
	   }
	}
	return initialContext;
   }
}
