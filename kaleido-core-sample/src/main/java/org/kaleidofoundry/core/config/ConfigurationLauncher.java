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
package org.kaleidofoundry.core.config;

import java.io.IOException;

import org.kaleidofoundry.core.store.ResourceException;

/**
 * Configuration launcher test
 * 
 * @author Jerome RADUGET
 * @see ConfigurationConstants#JavaEnvProperties
 */
public class ConfigurationLauncher {

   public static void main(final String[] args) throws ResourceException, IOException {

	try {
	   // -Dkaleido.configurations=mySimpleConfig=classpath:/config/mySimpleConfig.properties
	   System.getProperties().put(ConfigurationConstants.JavaEnvProperties, "mySimpleConfig=classpath:/config/mySimpleConfig.properties");
	   // load given configurations
	   ConfigurationFactory.init();


	   try {
		System.out.println("Testing Configuration context & instance injection, using annotation :");
		new ConfigurationSample01().echo();
	   } catch (Throwable th) {
		th.printStackTrace();
	   }

	   try {
		System.out.println("Testing Configuration context & instance injection, using annotation with parameters :");
		new ConfigurationSample02().echo();
	   } catch (Throwable th) {
		th.printStackTrace();
	   }

	   try {
		System.out.println("Testing Configuration context & instance injection, using annotation with parameters :");
		new ConfigurationSample03().echo();
	   } catch (Throwable th) {
		th.printStackTrace();
	   }

	} finally {
	   // free configuration resources
	   ConfigurationFactory.destroyAll();
	}
   }

}
