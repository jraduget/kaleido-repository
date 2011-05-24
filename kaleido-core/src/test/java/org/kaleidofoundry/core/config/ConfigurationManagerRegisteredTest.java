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
package org.kaleidofoundry.core.config;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class ConfigurationManagerRegisteredTest extends ConfigurationManagerPersistentTest {

   @Override
   @Before
   public void setup() {
	//
	super.setup();
	// register configuration
	ConfigurationFactory.provides(MyConfigurationName, MyConfigurationUri);
   }

   @Override
   @After
   public void cleanup() {
	super.cleanup();
	try {
	   ConfigurationFactory.destroy(MyConfigurationName);
	} catch (StoreException ste) {

	}
   }
}
