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

import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.ResourceStoreRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.ResourceUri;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.StorageAllowed;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple configuration usage</h3>
 * Inject {@link Configuration} context and instance using {@link Context} annotation with parameters which overrides configuration file
 * </p>
 * 
 * @see ConfigurationSample01
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationSample02 {

   @Context(value="mySimpleConfig",
	   parameters={
	   @Parameter(name=StorageAllowed, value="false"),
	   @Parameter(name=ResourceUri, value="http://localhost/kaleidofoundry/it/config/myHttpConfig.properties"),
	   @Parameter(name=CacheManagerRef, value="myCacheManager"),
	   @Parameter(name=ResourceStoreRef, value="myConfigStore")
   }
   )
   private Configuration configuration;

   /**
    * a sample method, using injected configuration
    */
   public void echo() {
	System.out.printf("application name: %s\n", configuration.getString("myapp.name"));
	System.out.printf("application admin mail : %s\n", configuration.getString("myapp.admin.email"));
	System.out.printf("date sample: %s\n", configuration.getString("myapp.sample.date"));
	System.out.printf("date typed sample: %s\n", configuration.getDate("myapp.sample.date"));
	System.out.printf("float sample : %s\n", configuration.getString("myapp.sample.float"));
	System.out.printf("float typed sample : %s\n", configuration.getFloat("myapp.sample.float"));
	System.out.printf("boolean sample : %s\n", configuration.getString("myapp.sample.boolean"));
	System.out.printf("boolean typed sample : %s\n", configuration.getBoolean("myapp.sample.boolean"));

	System.out.println("keys:");
	for (final String key : configuration.keySet()) {
	   System.out.printf("\tkey=%s\n", key);
	}
   }


   /**
    * used only for junit assertions
    * 
    * @return current configuration instance
    */
   Configuration getConfiguration() {
	return configuration;
   }
}
