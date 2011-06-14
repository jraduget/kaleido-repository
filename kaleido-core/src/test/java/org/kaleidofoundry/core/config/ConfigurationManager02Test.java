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
import org.junit.Test;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Test {@link ConfigurationManagerBean} with a configuration that have been registered and whose meta model have been persisted
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationManager02Test extends ConfigurationManager01Test {

   @Override
   @Before
   public void setup() {
	// call meta model creation in ancestor
	super.setup();
	// register configuration
	ConfigurationFactory.provides(MyConfigurationName, MyConfigurationUri);
   }

   @Override
   @After
   public void cleanup() {
	super.cleanup();
	try {
	   ConfigurationFactory.unregister(MyConfigurationName);
	} catch (StoreException ste) {
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.AbstractConfigurationManagerTest#store()
    */
   @Override
   @Test
   public void store() {
	super.store();
	try {
	   configurationManager.store(MyConfigurationName);
	   fail();
	} catch (StoreException se) {
	   assertEquals("store.readonly.illegal", se.getCode());
	}
   }
}
