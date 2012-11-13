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
public class FileStoreMainLauncher {

   public static void main(final String[] args) throws ResourceException, IOException {

	try {
	   // load given configurations
	   ConfigurationFactory.init("myContext=classpath:/store/myContext.properties");

	   // launch first test
	   try {
		System.out.printf("Test with simple context injection:\n%s", new FileStoreSample01().echo());
	   } catch (Throwable th) {
		th.printStackTrace();
	   }
	   // launch second test
	   try {
		System.out.printf("\nTest with advanced context injection:\n%s", new FileStoreSample02().echo());
	   } catch (Throwable th) {
		th.printStackTrace();
	   }
	   // launch third test
	   try {
		System.out.printf("\nTest with manually context build:\n%s", new FileStoreSample03().echo());
	   } catch (Throwable th) {
		th.printStackTrace();
	   }
	} finally {
	   // free configuration resources
	   ConfigurationFactory.unregisterAll();
	}
   }
}
