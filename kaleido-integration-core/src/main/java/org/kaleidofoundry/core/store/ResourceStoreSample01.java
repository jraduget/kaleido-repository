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

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>First simple resource store usage</h3>
 * This example will inject  {@link ResourceStore} context and instance using {@link Context} annotation without parameters
 * </p>
 * The following java env. variable have been set :
 * 
 * <pre>
 * -Dkaleido.configurations=myContext=classpath:/store/myContext.properties
 * </pre>
 * 
 * Resource file, "classpath:/store/myContext.properties" contains :
 * 
 * <pre>
 * # configuration http context properties
 * resourceStore.myResourceCtx.uriScheme=http
 * resourceStore.myResourceCtx.readonly=false
 * resourceStore.myResourceCtx.connectTimeout=1500
 * resourceStore.myResourceCtx.readTimeout=10000
 * 
 * # if you need proxy settings, uncomment and configure followings :
 * #resourceStore.myResourceCtx.proxySet=true
 * #resourceStore.myResourceCtx.proxyHost=yourProxyHost
 * #resourceStore.myResourceCtx.proxyUser=yourProxyUser
 * #resourceStore.myResourceCtx.proxyPassword=proxyUserPassword
 * 
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class ResourceStoreSample01 {

   @Context("myResourceCtx")
   protected ResourceStore resourceStore;

   /**
    * method example that use the injected store
    * 
    * @return the content of the resource "http://localhost/kaleidofoundry/it/store/foo.txt"
    * @throws ResourceException
    * @throws IOException
    */
   public String echo() throws ResourceException, IOException {

	ResourceHandler rh = null;
	BufferedReader reader = null;
	String inputLine;

	try {
	   StringBuilder stb = new StringBuilder();

	   // connect to the resource with the injected context (proxy, credentials, ...)
	   rh = resourceStore.get(URI.create("http://localhost/kaleidofoundry/it/store/foo.txt"));

	   // handle the input stream resource as usual
	   reader = new BufferedReader(new InputStreamReader(rh.getInputStream(), "UTF8"));
	   while ((inputLine = reader.readLine()) != null) {
		stb.append(inputLine).append("\n");
	   }
	   return stb.toString();

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
