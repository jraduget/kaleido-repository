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
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreRef;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.StorageAllowed;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple configuration usage</h3> Inject {@link Configuration} context and instance using {@link Context} annotation with parameters
 * which overrides configuration file
 * </p>
 * 
 * @see ConfigurationSample01
 * @author Jerome RADUGET
 */
public class ConfigurationSample02 {

   @Context(value = "myConfig", parameters = { @Parameter(name = StorageAllowed, value = "false"),
	   @Parameter(name = FileStoreUri, value = "http://localhost:8380/kaleido-it/config/myHttpConfig.properties"),
	   @Parameter(name = CacheManagerRef, value = "myCacheManager"), @Parameter(name = FileStoreRef, value = "myConfigStore") })
   private Configuration myConfig;

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
