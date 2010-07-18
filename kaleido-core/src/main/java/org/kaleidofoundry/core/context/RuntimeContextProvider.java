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
package org.kaleidofoundry.core.context;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.RuntimeContextMessageBundle;

import java.lang.reflect.Field;

import javax.security.auth.login.Configuration;

import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Provide an independent {@link RuntimeContext} builder<br/>
 * <p>
 * A field / argument ... can be annotated by {@link InjectContext} annotation<br/>
 * This annotation provides some information, like runtime context name, and optionally a configuration id<br/>
 * The methods of this provider will bound the given {@link InjectContext} annotated field to the currents registered {@link Configuration}, by
 * building and returning the {@link RuntimeContext} that will match
 * </p>
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@Immutable
public class RuntimeContextProvider<T> {

   /**
    * @param contextualAnnotation
    * @return runtime context instance which map with the context annotation argument
    */
   public RuntimeContext<T> provides(final @NotNull InjectContext contextualAnnotation) {

	if (StringHelper.isEmpty(contextualAnnotation.value())) { throw new IllegalArgumentException(RuntimeContextMessageBundle
		.getMessage("context.annotation.name.empty")); }

	final RuntimeContext<T> runtimeContext = new RuntimeContext<T>(contextualAnnotation.value());
	feedRuntimeContext(runtimeContext);
	return runtimeContext;
   }

   /**
    * @param annotatedField
    * @return new runtime context instance which map with the context annotation of the field argument
    */
   public RuntimeContext<T> provides(@NotNull final Field annotatedField) {

	final InjectContext contextAnnot = annotatedField.getAnnotation(InjectContext.class);

	if (contextAnnot == null) { throw new IllegalArgumentException(RuntimeContextMessageBundle.getMessage("context.annotation.field.illegal", InjectContext.class
		.getName())); }

	return provides(annotatedField.getAnnotation(InjectContext.class));
   }

   /**
    * @param <R>
    * @param contextAnnot
    * @param runtimeContextArg
    * @return new instance of a RuntimeContext<R>
    */
   public <R> RuntimeContext<R> provides(final @NotNull InjectContext contextAnnot, final @NotNull RuntimeContext<R> runtimeContextArg) {

	if (StringHelper.isEmpty(contextAnnot.value())) { throw new IllegalArgumentException(RuntimeContextMessageBundle
		.getMessage("context.annotation.name.empty")); }

	final RuntimeContext<R> newRuntimeContext = new RuntimeContext<R>(contextAnnot.value());
	feedRuntimeContext(newRuntimeContext);
	return newRuntimeContext;
   }

   /**
    * feed {@link RuntimeContext} properties content, using current configuration
    * 
    * @param <R>
    * @param runtimeContext
    */
   protected <R> void feedRuntimeContext(final RuntimeContext<R> runtimeContext) {
	// TODO - bind it to implementation
	// runtimeContext.setProperty(property, value);
   }

}
