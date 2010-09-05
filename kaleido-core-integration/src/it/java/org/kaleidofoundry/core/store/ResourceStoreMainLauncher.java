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
package org.kaleidofoundry.core.store;

import java.io.IOException;

import org.kaleidofoundry.core.config.ConfigurationConstants;
import org.kaleidofoundry.core.config.ConfigurationFactory;

/**
 * Configuration launcher test
 * 
 * @author Jerome RADUGET
 * @see ConfigurationConstants#JavaEnvProperties
 */
public class ResourceStoreMainLauncher {

   public static void main(final String[] args) throws ResourceException, IOException {

	try {
	   // -Dkaleido.configurations=myContext=classpath:/store/myContext.properties
	   System.getProperties().put(ConfigurationConstants.JavaEnvProperties, "myContext=classpath:/store/myContext.properties");
	   // load given configurations
	   ConfigurationFactory.init();

	   // launch first test
	   try {
		System.out.printf("Test with simple context injection:\n%s", new ResourceStoreSample01().echo());
	   } catch (Throwable th) {
		th.printStackTrace();
	   }
	   // launch second test
	   try {
		System.out.printf("\nTest with advanced context injection:\n%s", new ResourceStoreSample02().echo());
	   } catch (Throwable th) {
		th.printStackTrace();
	   }
	   // launch third test
	   try {
		System.out.printf("\nTest with manually context build:\n%s", new ResourceStoreSample03().echo());
	   } catch (Throwable th) {
		th.printStackTrace();
	   }
	} finally {
	   // free configuration resources
	   ConfigurationFactory.destroyAll();
	}
   }
}
