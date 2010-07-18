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

import org.kaleidofoundry.core.context.InjectContext;

/**
 * Simple configuration usage<br/>
 * The following java env. variable have been set :
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/config/mySimpleConfig.properties
 * </pre>
 * 
 * Resource file : "classpath:/config/mySimpleConfig.properties" contains :
 * 
 * <pre>
 * # configuration context properties
 * configuration.mySimpleConfig.cache=kaleido-local
 * configuration.mySimpleConfig.readonly=true;
 * configuration.mySimpleConfig.resourceUri=classpath:/config/mySimpleConfig.properties
 * #configuration.mySimpleConfig.resourceStore=
 * 
 * # application properties
 * myapp.name=my new application
 * myapp.admin.email=myadmin@mysociete.com
 * myapp.sample.date=2010-12-01T02:45:30
 * myapp.sample.float=123.45
 * myapp.sample.boolean=false
 * 
 * </pre>
 * 
 * @author Jerome RADUGET
 * @see ConfigurationConstants#JavaEnvProperties
 */
public class ConfigurationSample {

   @InjectContext("mySimpleConfig")
   private Configuration configuration;

   public void echo() {
	System.out.printf("application name: %s", configuration.getString("myapp.name"));
	System.out.printf("application admin mail : %s", configuration.getString("myapp.admin.email"));
	System.out.printf("date sample: %s", configuration.getString("myapp.sample.date"));
	System.out.printf("float sample : %s", configuration.getString("myapp.sample.float"));
	System.out.printf("boolean sample : %s", configuration.getString("myapp.sample.boolean"));

	System.out.println("keys:");
	for (final String key : configuration.keySet()) {
	   System.out.printf("\tkey=%s", key);
	}
   }

}
