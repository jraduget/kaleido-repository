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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.kaleidofoundry.core.context.InjectContext;

/**
 * <p>
 * <h3>Simple resource store usage</h3>
 * Inject {@link ResourceStore} context and instance using {@link InjectContext} annotation without parameters
 * </p>
 * The following java env. variable have been set :
 * 
 * <pre>
 * -Dkaleido.configurations=myHttpConfig=classpath:/store/myHttpConfig.properties
 * </pre>
 * 
 * Resource file, "classpath:/store/myHttpConfig.properties" contains :
 * 
 * <pre>
 * # configuration http context properties
 * resourceStore.myHttp.readonly=false
 * resourceStore.myHttp.proxySet=false
 * resourceStore.myHttp.connectTimeout=1500
 * resourceStore.myHttp.readTimeout=10000
 * # if you need proxy settings, uncomment and configure followings :
 * #resourceStore.myHttp.proxySet=true
 * #resourceStore.myHttp.proxyHost=yourProxyHost
 * #resourceStore.myHttp.proxyUser=yourProxyUser
 * #resourceStore.myHttp.proxyPassword=proxyUserPassword
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class ResourceStoreSample01 {

   @InjectContext("myHttp")
   protected ResourceStore resourceStore;

   /**
    * method example that use the injected store
    * 
    * @throws ResourceException
    * @throws IOException
    */
   public void echo() throws ResourceException, IOException {

	ResourceHandler rh = null;
	BufferedReader reader = null;
	String inputLine;

	try {
	   // connect to the resource with the injected context (proxy, credentials, ...)
	   rh = resourceStore.get(URI.create("http://localhost/foo.txt"));

	   // handle the input stream resource as usual
	   reader = new BufferedReader(new InputStreamReader(rh.getInputStream(), "UTF8"));
	   while ((inputLine = reader.readLine()) != null) {
		System.out.println(inputLine);
	   }

	} finally {
	   // free buffered reader
	   if (reader != null) {
		reader.close();
	   }
	   // free the resource
	   if (rh != null) {
		rh.release();
	   }
	}
   }
}
