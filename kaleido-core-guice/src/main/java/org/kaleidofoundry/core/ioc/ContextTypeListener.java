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
package org.kaleidofoundry.core.ioc;

import java.lang.reflect.Field;

import org.kaleidofoundry.core.context.InjectContext;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.context.RuntimeContextProvider;
import org.kaleidofoundry.core.lang.annotation.Review;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Guice {@link TypeListener} used for listening and injecting {@link RuntimeContext} to the annotated {@link InjectContext} property / method /
 * ...
 * 
 * @author Jerome RADUGET
 * @see <a
 *      href="http://code.google.com/p/google-guice/wiki/CustomInjections">http://code.google.com/p/google-guice/wiki/CustomInjections</a>
 */
public class ContextTypeListener implements TypeListener {

   @Override
   public <T> void hear(final TypeLiteral<T> typeLiteral, final TypeEncounter<T> typeEncounter) {

	RuntimeContextProvider<T> runtimeContextProvider = new RuntimeContextProvider<T>();

	for (Field field : typeLiteral.getRawType().getDeclaredFields()) {

	   // scan RuntimeContext typed fields, annotated with @Context
	   if (field.getType() == RuntimeContext.class && field.isAnnotationPresent(InjectContext.class)) {
		typeEncounter.register(new ContextMembersInjector<T>(field, runtimeContextProvider));
	   }
	}
   }

}

/**
 * Guice MembersInjector used by {@link TypeListener} for injecting {@link RuntimeContext}<br/>
 * 
 * @author Jerome RADUGET
 * @param <T>
 * @see <a
 *      href="http://code.google.com/p/google-guice/wiki/CustomInjections">http://code.google.com/p/google-guice/wiki/CustomInjections</a>
 */
class ContextMembersInjector<T> implements MembersInjector<T> {

   private final Field field;
   private final RuntimeContextProvider<T> runtimeContextProvider;

   ContextMembersInjector(final Field field, final RuntimeContextProvider<T> runtimeContextProvider) {
	this.field = field;
	this.field.setAccessible(true);
	this.runtimeContextProvider = runtimeContextProvider;
   }

   @Override
   @Review(comment = "error management with i18n error ?")
   public void injectMembers(final T t) {
	try {
	   field.set(t, runtimeContextProvider.provides(field));
	} catch (IllegalAccessException e) {
	   throw new RuntimeException(e);
	}
   }
}