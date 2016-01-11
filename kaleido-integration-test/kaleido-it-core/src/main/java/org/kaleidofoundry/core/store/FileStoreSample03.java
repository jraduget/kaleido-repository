/*
 * Copyright 2008-2016 the original author or authors
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
 * <h3>Simple file store usage</h3> Build {@link FileStore} context and instance manually by coding, using context builder
 * </p>
 * 
 * @see FileStoreSample01
 * @author jraduget
 */
public class FileStoreSample03 {

   // no automatic context injection
   private final FileStore myStore;

   public FileStoreSample03() throws ResourceException {

	RuntimeContext<FileStore> context = new FileStoreContextBuilder("myManualStore", FileStore.class).withBaseUri("http://localhost:8380/kaleido-it/")
		.withReadonly(true).withProxySet(false)
		// configure connect and read timeout for the java net layer
		.withConnectTimeout(0).withReadTimeout(0)
		// 5 attempts, with a wait of 2 seconds between each attempt
		.withMaxRetryOnFailure(5).withSleepTimeBeforeRetryOnFailure(2000).build();

	myStore = FileStoreFactory.provides(context);

   }

   /**
    * <b>Example method using injected file store</b><br/>
    * <br/>
    * 1. it connect to the given file resource with the injected context (proxy, credentials, ...)<br/>
    * 2. it get the resource file content (text here), using the right charset ("UTF8" is the default is not specified) <br/>
    * <br/>
    * <b>Path are relative from the file store root uri, like :</b>
    * <ul>
    * <li>http://localhost:8380/kaleido-it/</li>
    * <li>ftp://localhost/kaleido-it/</li>
    * <li>classpath:/org/kaleidofoundry/core/</li>
    * </ul>
    * 
    * @return the content of the resource "http://localhost:8380/kaleido-it/store/foo.txt"
    * @throws ResourceException
    */
   public String echo() throws ResourceException {
	String resourceRelativePath = "store/foo.txt";
	String text = myStore.get(resourceRelativePath).getText("UTF8");
	System.out.printf("resource content [%s] :\n%s", resourceRelativePath, text);
	return text;
   }

}
