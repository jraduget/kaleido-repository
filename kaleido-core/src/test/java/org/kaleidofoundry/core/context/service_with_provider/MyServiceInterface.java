/*  
 * Copyright 2008-2016 the original author or authors 
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
package org.kaleidofoundry.core.context.service_with_provider;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.context.ProviderService;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * sample plugin service with an instance provider {@link ProviderService}
 * 
 * @author jraduget
 */
@Declare(value = "myProvidedService")
@Provider(value = MyServiceProvider.class)
public interface MyServiceInterface {

   RuntimeContext<MyServiceInterface> getContext();

}
