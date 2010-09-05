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
 * <p>
 * <h3>Simple configuration usage</h3> Inject {@link Configuration} context and instance using {@link InjectContext} annotation without
 * parameters
 * </p>
 * <p>
 * The following java env. variable have been set :
 * 
 * <pre>
 * -Dkaleido.configurations=mySimpleConfig=classpath:/config/myContext.properties
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
 *</pre>
 * 
 * Context resource file : "classpath:/config/myContext.properties" contains :
 * 
 * <pre>
 * 
 * # simple configuration context properties
 * configuration.mySimpleConfig.readonly=true
 * configuration.mySimpleConfig.resourceUri=classpath:/config/myConfig.properties
 * 
 * # simple configuration context (optional) - used for cache manager policies
 * #configuration.mySimpleConfig.cacheManagerRef=myCacheManager
 * cacheManager.myCacheManager.providerCode=ehCache1x
 * cacheManager.myCacheManager.resourceUri=classpath:/config/ehcache.xml
 * 
 * 
 * 
 * 
 * 
 * # http configuration context properties
 * configuration.myHttpConfig.readonly=true
 * configuration.myHttpConfig.resourceUri=http://localhost/kaleidofoundry/it/config/myHttpConfig.properties
 * 
 * # http configuration context (optional) - used for resource store (proxy or other parameters...)
 * #configuration.myHttpConfig.resourceStoreRef=myConfigStore
 * resourceStore.myConfigStore.readonly=false
 * resourceStore.myConfigStore.connectTimeout=1500
 * resourceStore.myConfigStore.readTimeout=10000
 * resourceStore.myConfigStore.proxySet=false
 * #resourceStore.myConfigStore.proxyHost=yourProxyHost
 * #resourceStore.myConfigStore.proxyUser=yourProxyUser
 * #resourceStore.myConfigStore.proxyPassword=proxyUserPassword
 * 
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author Jerome RADUGET
 * @see ConfigurationContextBuilder
 */
public class ConfigurationSample01 {

   @InjectContext("mySimpleConfig")
   private Configuration configuration;

   /**
    * a sample method, using injected configuration
    */
   public void echo() {

	// you can use several configuration key syntax like : { "myapp.name" , "//myapp/name" , "myapp/name" }
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
    * @return
    */
   Configuration getConfiguration() {
	return configuration;
   }
}
