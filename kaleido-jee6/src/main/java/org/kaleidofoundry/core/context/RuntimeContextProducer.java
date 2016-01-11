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
package org.kaleidofoundry.core.context;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * {@link RuntimeContext} field / parameter injection
 * 
 * <pre>
 * // field injection
 * public class MyClass01 { 
 * 
 * 	// RuntimeContext field injection, using &#064;Context meta informations
 * 	private &#064;Inject &#064;Context(...) RuntimeContext&lt;?&gt; myContext;
 * 
 * ... 
 * }
 * </pre>
 * 
 * <pre>
 * // constructor injection
 * public class MyClass02 { 
 * 
 * 	private final RuntimeContext&lt;?&gt; myContext;
 * 
 * 	public MyClass02(&#064;Inject &#064;Context(...) RuntimeContext&lt;?&gt; myContext) {
 * 		this.myContext=myContext;
 * 	 }
 * 	
 * ... 
 * }
 * </pre>
 * 
 * @author jraduget
 */
public class RuntimeContextProducer {

   @Produces
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public RuntimeContext getRuntimeContext(final InjectionPoint injectionPoint) {

	Context context;
	String defaultName;
	final Class runtimeContextType = injectionPoint.getBean().getBeanClass();

	// try CDI bean name
	defaultName = injectionPoint.getBean() != null ? injectionPoint.getBean().getName() : null;

	// try field / method name
	if (StringHelper.isEmpty(defaultName)) {
	   defaultName = injectionPoint.getMember().getName();
	}

	// context needed annotation
	context = injectionPoint.getAnnotated().getAnnotation(Context.class);

	// if no @Context annotation is present, create a default one
	if (context == null) {
	   context = ContextHelper.createContext(defaultName);
	}

	if (context != null) {
	   return RuntimeContext.createFrom(context, defaultName, runtimeContextType);
	} else {
	   return new RuntimeContext(defaultName, runtimeContextType, new Configuration[] {});
	}
   }
}
