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

import static java.lang.System.out;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * <p>
 * <h3>Simple configuration usage</h3>
 * Build {@link Configuration} context and instance manually by coding, using context builder
 * </p>
 * 
 * @see ConfigurationSample01
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationSample03 {

   // no automatic context injection here
   private final Configuration myConfig;

   public ConfigurationSample03() throws ResourceException {

	RuntimeContext<Configuration> context = new ConfigurationContextBuilder("myManualCtx", Configuration.class)
	.withName("myConfig")
	.withStorageAllowed(false)
	.withFileStoreUri("http://localhost:8080/kaleido-it/config/myHttpConfig.properties")
	.withCacheManagerRef("myCacheManager")
	.withFileStoreRef("myConfigStore")
	.build();

	myConfig = ConfigurationFactory.provides(context);
   }

   /**
    * a sample method, using injected configuration
    */
   public void echo() {
	out.printf("application name: %s\n", myConfig.getString("myapp.name"));
	out.printf("application admin mail : %s\n", myConfig.getString("myapp.admin.email"));
	out.printf("date sample: %s\n", myConfig.getString("myapp.sample.date"));
	out.printf("date typed sample: %s\n", myConfig.getDate("myapp.sample.date"));
	out.printf("float sample : %s\n", myConfig.getString("myapp.sample.float"));
	out.printf("float typed sample : %s\n", myConfig.getFloat("myapp.sample.float"));
	out.printf("boolean sample : %s\n", myConfig.getString("myapp.sample.boolean"));
	out.printf("boolean typed sample : %s\n", myConfig.getBoolean("myapp.sample.boolean"));

	out.println("keys:");
	for (final String key : myConfig.keySet()) {
	   out.printf("\tkey=%s\n", key);
	}
   }


   /**
    * used only for junit assertions
    * 
    * @return current configuration instance
    */
   Configuration getConfiguration() {
	return myConfig;
   }
}
