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
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple configuration usage</h3>
 * Inject {@link Configuration} context and instance using {@link InjectContext} annotation with parameters which overrides configuration file
 * </p>
 * 
 * @see ConfigurationSample01
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationSample02 {

   @InjectContext(value="mySimpleConfig",
	   parameters={
	   @Parameter(name=ConfigurationContextBuilder.Readonly, value="false"),
	   @Parameter(name=ConfigurationContextBuilder.ResourceUri, value="http://localhost/kaleidofoundry/it/config/myHttpConfig.properties"),
	   @Parameter(name=ConfigurationContextBuilder.CacheManagerRef, value="myCacheManager"),
	   @Parameter(name=ConfigurationContextBuilder.ResourceStoreRef, value="myConfigStore")
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
    * @return
    */
   Configuration getConfiguration() {
	return configuration;
   }
}
