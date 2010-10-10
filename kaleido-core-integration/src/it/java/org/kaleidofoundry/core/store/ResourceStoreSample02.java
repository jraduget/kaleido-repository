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
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple resource store usage</h3>
 * Inject {@link ResourceStore} context and instance using {@link Context} annotation with parameters which overrides configuration file
 * </p>
 * 
 * @see ResourceStoreSample01
 * 
 * @author Jerome RADUGET
 */
public class ResourceStoreSample02 {

   // @Parameter overrides myHttp configuration setting
   @Context(value = "myHttp",
	   parameters = {
	   @Parameter(name = ResourceContextBuilder.readonly, value = "true", type = Boolean.class),
	   @Parameter(name = ResourceContextBuilder.connectTimeout, value = "0", type = Integer.class),
	   @Parameter(name = ResourceContextBuilder.readTimeout, value = "0", type = Integer.class)
   }
   )
   protected ResourceStore resourceStore;

   /**
    * method example that use the injected store
    * @return the content of the resource "http://localhost/foo.txt"
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
