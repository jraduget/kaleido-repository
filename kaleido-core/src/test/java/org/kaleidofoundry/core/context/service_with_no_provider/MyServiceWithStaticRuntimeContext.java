/*  
 * Copyright 2008-2021 the original author or authors 
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
package org.kaleidofoundry.core.context.service_with_no_provider;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * @author jraduget
 */
@Declare(value = "myService.with.static.context")
public class MyServiceWithStaticRuntimeContext implements MyServiceInterface {

   private static RuntimeContext<MyServiceInterface> context;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.test.MyServiceInterface#getContext()
    */
   public RuntimeContext<MyServiceInterface> getContext() {
	return context;
   }

}
