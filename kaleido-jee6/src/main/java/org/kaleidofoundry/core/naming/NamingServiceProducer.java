/*
 *  Copyright 2008-2011 the original author or authors.
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

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.ContextHelper;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * {@link NamingService} producer is used to extend {@link Inject} in order to use {@link Context} annotation
 * 
 * @author Jerome RADUGET
 */
public class NamingServiceProducer extends NamingServiceProvider {

   /**
    */
   public NamingServiceProducer() {
	super(NamingService.class);
   }

   @Produces
   public NamingService getNamingService(final InjectionPoint injectionPoint) {

	Context context;
	String defaultName;

	// try CDI bean name
	defaultName = injectionPoint.getBean().getName();

	// try field / method name
	if (StringHelper.isEmpty(defaultName)) {
	   defaultName = injectionPoint.getMember().getName();
	}

	// context needed annotation
	context = injectionPoint.getAnnotated().getAnnotation(Context.class);

	// if no @Context annotation is present, create a default one with no name
	if (context == null) {
	   context = ContextHelper.createContext("");
	}

	return provides(context, defaultName, NamingService.class);

   }

}
