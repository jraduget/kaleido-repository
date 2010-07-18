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
 * Simple resource store usage<br/>
 * The following java env. variable have been set :
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/store/myHttpConfig.properties
 * </pre>
 * 
 * Resource file : "classpath:/store/myHttpConfig.properties" contains :
 * 
 * <pre>
 * # configuration http context properties
 * resourceStore.myHttp.user=foo
 * resourceStore.myHttp.password=foopwd
 * resourceStore.myHttp.method=GET
 * resourceStore.myHttp.proxyType=DIRECT
 * resourceStore.myHttp.contentType=text/xml
 * resourceStore.myHttp.connectTimeout=150
 * resourceStore.myHttp.readTimeout=150
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class ResourceStoreSample {

   @InjectContext("myHttp")
   protected ResourceStore resourceStore;

   /**
    * method example that use the injected store
    * 
    * @throws StoreException
    * @throws IOException
    */
   public void echo() throws StoreException, IOException {

	ResourceHandler rh = null;
	BufferedReader reader = null;
	String inputLine;

	try {
	   // connect to the resource with the injected context (proxy, credentials, ...)
	   rh = resourceStore.get(URI.create("http://yourhost/yourResource"));

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
