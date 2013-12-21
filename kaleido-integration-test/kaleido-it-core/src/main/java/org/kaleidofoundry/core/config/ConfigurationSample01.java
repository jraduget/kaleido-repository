/*
 * Copyright 2008-2014 the original author or authors
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

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>Simple configuration usage</h3> Inject {@link Configuration} context and instance using {@link Context} annotation without parameters
 * </p>
 * <p>
 * The following java env. variable have been set :
 * 
 * <pre>
 * -Dkaleido.configurations=myConfig=classpath:/config/myContext.properties
 * </pre>
 * 
 * Configuration resource file : "classpath:/config/myConfig.properties" contains :
 * 
 * <pre>
 * 
 * # your application properties
 * myapp.name=my new application
 * myapp.admin.email=myadmin@mysociete.com
 * myapp.sample.date=2010-12-01T02:45:30
 * myapp.sample.float=123.45
 * myapp.sample.boolean=false
 * </pre>
 * 
 * Context resource file : "classpath:/config/myContext.properties" contains :
 * 
 * <pre>
 * 
 * # simple configuration context properties
 * configurations.myConfig.readonly=true
 * configurations.myConfig.fileStoreUri=classpath:/config/myConfig.properties
 * 
 * # simple configuration context (optional) - used for cache manager policies
 * #configurations.myConfig.cacheManagerRef=myCacheManager
 * cacheManagers.myCacheManager.providerCode=ehCache
 * cacheManagers.myCacheManager.fileStoreUri=classpath:/config/ehcache.xml
 * 
 * 
 * 
 * 
 * 
 * # http configuration context properties
 * configurations.myHttpConfig.readonly=true
 * configurations.myHttpConfig.fileStoreUri=http://localhost:8380/kaleido-it/config/myHttpConfig.properties
 * 
 * # http configuration context (optional) - used for file store (proxy or other parameters...)
 * #configurations.myHttpConfig.fileStoreRef=myConfigStore
 * fileStores.myConfigStore.readonly=false
 * fileStores.myConfigStore.connectTimeout=1500
 * fileStores.myConfigStore.readTimeout=10000
 * fileStores.myConfigStore.proxySet=false
 * #fileStores.myConfigStore.proxyHost=yourProxyHost
 * #fileStores.myConfigStore.proxyUser=yourProxyUser
 * #fileStores.myConfigStore.proxyPassword=proxyUserPassword
 * 
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author jraduget
 * @see ConfigurationContextBuilder
 */
public class ConfigurationSample01 {

   @Context
   private Configuration myConfig;

   /**
    * a sample method, using injected configuration
    */
   public void echo() {

	// you can use several configuration key syntax like : { "myapp.name" , "//myapp/name" , "myapp/name" }
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
