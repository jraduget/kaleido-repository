/*
 *  Copyright 2008-2016 the original author or authors.
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
package org.kaleidofoundry.messaging.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.ContextHelper;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.Consumer;
import org.kaleidofoundry.messaging.ConsumerProvider;

/**
 * {@link Consumer} producer is used to extend {@link Inject} in order to use {@link Context} annotation
 * 
 * @author jraduget
 */
public class ConsumerProducer extends ConsumerProvider {

   public ConsumerProducer() {
	super(Consumer.class);
   }

   @Produces
   public Consumer getConsumer(final InjectionPoint injectionPoint) {

	Context context;
	String defaultName;

	// try CDI bean name
	defaultName = injectionPoint.getBean() != null ? injectionPoint.getBean().getName() : null;

	// try field / method name
	if (StringHelper.isEmpty(defaultName)) {
	   defaultName = injectionPoint.getMember().getName();
	}

	// context needed annotation
	context = injectionPoint.getAnnotated().getAnnotation(Context.class);

	// if no @Context annotation is present, create a default one with no name
	if (context == null) {
	   context = ContextHelper.createContext(defaultName);
	}

	return provides(context, defaultName, Consumer.class);

   }

}
