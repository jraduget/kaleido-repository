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
package org.kaleidofoundry.core.cache;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.kaleidofoundry.core.inject.UnmanagedCdiInjector;

/**
 * Weld CDI junit runner
 * 
 * @author Jerome RADUGET
 */
public class WeldCdiJunit4Runner extends BlockJUnit4ClassRunner {

   public WeldCdiJunit4Runner(final Class<?> klass) throws InitializationError {
	super(klass);
   }

   /*
    * (non-Javadoc)
    * @see org.junit.runners.ParentRunner#run(org.junit.runner.notification.RunNotifier)
    */
   @Override
   public void run(final RunNotifier notifier) {
	UnmanagedCdiInjector.init();
	super.run(notifier);
	UnmanagedCdiInjector.shutdown();
   }

   /*
    * (non-Javadoc)
    * @see org.junit.runners.BlockJUnit4ClassRunner#createTest()
    */
   @Override
   protected Object createTest() throws Exception {
	// create the unit test instance with weld instead of the default one
	return UnmanagedCdiInjector.getContainer().instance().select(getTestClass().getJavaClass()).get();
   }

}
