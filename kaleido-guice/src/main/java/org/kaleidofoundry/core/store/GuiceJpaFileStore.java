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
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.GuiceFileStoreConstants.ClobJpaStorePluginName;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;

import com.google.inject.Inject;

/**
 * @author jraduget
 */
@Declare(ClobJpaStorePluginName)
public class GuiceJpaFileStore extends JpaFileStore {

   /**
    * @param context
    */
   @Inject
   public GuiceJpaFileStore(final RuntimeContext<FileStore> context) {
	super(context);
   }

}
