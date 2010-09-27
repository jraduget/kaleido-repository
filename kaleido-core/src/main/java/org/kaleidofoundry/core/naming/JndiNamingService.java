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

import static org.kaleidofoundry.core.naming.NamingContextBuilder.EnvPrefixName;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * @author Jerome RADUGET
 */
public class JndiNamingService<R> implements NamingService<R> {

   private final RuntimeContext<NamingService<R>> context;
   private final Context intialContext;

   public JndiNamingService(final RuntimeContext<NamingService<R>> context) throws NamingServiceException {
	this.context = context;
	try {
	   this.intialContext = new InitialContext();
	} catch (NamingException ne) {
	   throw handleNamingException(ne, null);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingService#lookup(java.lang.String, java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public R lookup(final String resourceName, final Class<R> ressourceClass) throws NamingServiceException {

	String jndiEnvNamePrefix = context.getProperty(EnvPrefixName); // optional java comp env prefix
	StringBuilder fullResourceName = new StringBuilder(); // full resource name

	// we prefix resource name if needed
	if (!StringHelper.isEmpty(jndiEnvNamePrefix) && !resourceName.startsWith(jndiEnvNamePrefix)) {
	   fullResourceName.append(jndiEnvNamePrefix);
	   if (!jndiEnvNamePrefix.endsWith("/")) {
		fullResourceName.append("/");
	   }
	}
	fullResourceName.append(resourceName);

	// lookup the resource
	Object resource;
	try {
	   resource = intialContext.lookup(fullResourceName.toString());
	} catch (final NamingException nae) {
	   throw handleNamingException(nae, fullResourceName.toString());
	}

	// cast check
	try {
	   return (R) PortableRemoteObject.narrow(resource, ressourceClass);
	} catch (final ClassCastException cce) {
	   throw new NamingServiceException("naming.jndi.error.initialContext.lookup.classcast", fullResourceName.toString(), ressourceClass.getName(), resource
		   .getClass().getName());
	}

   }

   /**
    * @param ne
    * @param resourceName
    * @return i18n exception conversion from NamingException parameter
    */
   @NotYetImplemented
   protected NamingServiceException handleNamingException(final NamingException ne, final String resourceName) {

	if (ne instanceof NameNotFoundException) { return new NamingNotFoundException(resourceName, context); }

	if (ne instanceof NamingException) { return new NamingServiceException("naming.jndi.error.initialContext.lookup", ne, resourceName, ne.getMessage(),
		ne.getMessage()); }

	return null;
   }

   /**
    * @param <R>
    * @param context
    * @return context instance build using runtime context properties
    * @throws NamingException
    */
   protected static <R> Context createInitialContext(final RuntimeContext<NamingService<R>> context) throws NamingException {
	Context initialContext = new InitialContext();
	for (String propertyName : context.keySet()) {
	   String propertyValue = context.getProperty(propertyName);
	   if (propertyValue != null) {
		initialContext.addToEnvironment(propertyName, propertyValue);
	   }
	}
	return initialContext;
   }
}
