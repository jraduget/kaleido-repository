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
package org.kaleidofoundry.core.cache;

import java.io.IOException;

import org.kaleidofoundry.core.config.ConfigurationConstants;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * Configuration launcher test
 * 
 * @author jraduget
 * @see ConfigurationConstants#JavaEnvProperties
 */
public class CacheManagerMainLauncher {

   public static void main(final String[] args) throws ResourceException, IOException {

	try {
	   // load and register given configuration
	   // another way to to this, set following java env variable : -Dkaleido.configurations=myConfig=classpath:/cache/myContext.properties
	   ConfigurationFactory.provides("myConfig", "classpath:/cache/myContext.properties");

	   try {
		System.out.println("\nTesting CacheManager context & instance injection, using annotation with external configuration :");
		new CacheManagerSample01().echo();
	   } catch (Throwable th) {
		th.printStackTrace();
	   }

	   try {
		System.out.println("Testing CacheManager context & instance injection, using annotation without external configuration :");
		new CacheManagerSample02().echo();
	   } catch (Throwable th) {
		th.printStackTrace();
	   }

	   try {
		System.out.println("\nTesting CacheManager context & instance injection, using annotation, mixing the use of parameters & external configuration :");
		new CacheManagerSample03().echo();
	   } catch (Throwable th) {
		th.printStackTrace();
	   }

	   try {
		System.out.println("\nTesting CacheManager context & instance creation, manually by coding :");
		new CacheManagerSample04().echo();
	   } catch (Throwable th) {
		th.printStackTrace();
	   }

	} finally {
	   // free configuration resources
	   ConfigurationFactory.unregister("myConfig");
	}
   }
}
