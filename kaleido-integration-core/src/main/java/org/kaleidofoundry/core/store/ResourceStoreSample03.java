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

import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * <p>
 * <h3>Simple resource store usage</h3> Build {@link ResourceStore} context and instance manually by coding, using context builder
 * </p>
 * 
 * @see ResourceStoreSample01
 * @author Jerome RADUGET
 */
public class ResourceStoreSample03 {

   // no automatic context injection
   private final ResourceStore resourceStore;

   public ResourceStoreSample03() throws ResourceException {

	RuntimeContext<ResourceStore> context =
	   new ResourceContextBuilder("myManualResourceCtx", ResourceStore.class)
	.withUriRootPath("http://localhost:8080/kaleido-integration/")
	.withReadonly(true)
	.withConnectTimeout(0)
	.withReadTimeout(0)
	.withProxySet(false)
	.build();

	resourceStore = ResourceStoreFactory.provides(context);

   }

   /**
    * <b>Example method using injected Resource Store</b><br/>
    * <br/>
    * 1. it connect to the given resource with the injected context (proxy, credentials, ...)<br/>
    * 2. it get the resource content (text here), using the right charset ("UTF8" is the default is not specified) <br/>
    * <br/>
    * <b>Path are relative from the resource store root uri, like :</b>
    * <ul>
    * <li>http://localhost:8080/kaleido-integration/</li>
    * <li>ftp://localhost/kaleido-integration/</li>
    * <li>classpath:/org/kaleidofoundry/core/</li>
    * </ul>
    * 
    * @return the content of the resource "http://localhost:8080/kaleido-integration/store/foo.txt"
    * @throws ResourceException
    */
   public String echo() throws ResourceException {
	String resourceRelativePath = "store/foo.txt";
	String text = resourceStore.get(resourceRelativePath).getText("UTF8");
	System.out.printf("resource content [%s] :\n%s", resourceRelativePath, text);
	return text;
   }

}
