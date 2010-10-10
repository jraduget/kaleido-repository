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

import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.lang.annotation.Reviews;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Base implementation for {@link ProviderService}
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

   /**
    * @param genericClassInterface
    */
   public AbstractProviderService(final Class<T> genericClassInterface) {
	this.genericClassInterface = genericClassInterface;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#provides(org.kaleidofoundry.core.context.Context, java.lang.Class)
    */
   @Override
   public T provides(final Context context, final Class<T> genericClassInterface) throws ProviderException, Throwable {
	return provides(RuntimeContext.createFrom(context, genericClassInterface));
   }

}
