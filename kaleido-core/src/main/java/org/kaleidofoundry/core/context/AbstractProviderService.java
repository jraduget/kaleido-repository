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
package org.kaleidofoundry.core.context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.lang.annotation.Reviews;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Base implementation for {@link ProviderService} <br/>
 * Dynamics context are registered here in order to trigger configuration changes
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@ThreadSafe
@Reviews(reviews = {
	@Review(category = ReviewCategoryEnum.Refactor, comment = "use singletons annotation information and move default registry instance here ?"),
	@Review(category = ReviewCategoryEnum.Refactor, comment = "create default method introspection which provide instance") })
public abstract class AbstractProviderService<T> implements ProviderService<T> {

   protected final Class<T> genericClassInterface;

   protected final Set<RuntimeContext<T>> dynamicsRegisterContext;

   /**
    * @param genericClassInterface
    */
   public AbstractProviderService(final Class<T> genericClassInterface) {
	this.genericClassInterface = genericClassInterface;
	this.dynamicsRegisterContext = Collections.synchronizedSet(new HashSet<RuntimeContext<T>>());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#provides(org.kaleidofoundry.core.context.Context, java.lang.Class)
    */
   @Override
   public final T provides(final Context context, final Class<T> genericClassInterface) throws ProviderException {
	return provides(RuntimeContext.createFrom(context, genericClassInterface));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.ProviderService#provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public final T provides(final RuntimeContext<T> context) throws ProviderException {
	if (context.isDynamics()) {
	   dynamicsRegisterContext.add(context);
	}
	return _provides(context);
   }

   /**
    * @param context
    * @return new T instance, build from context informations
    * @throws ProviderException
    */
   protected abstract T _provides(RuntimeContext<T> context) throws ProviderException;

}
