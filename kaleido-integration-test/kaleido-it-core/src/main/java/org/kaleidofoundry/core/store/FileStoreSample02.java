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
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.FileStoreContextBuilder.ConnectTimeout;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.ReadTimeout;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Readonly;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple file store usage</h3> Inject {@link FileStore} context and instance using {@link Context} annotation with parameters which
 * overrides configuration file
 * </p>
 * 
 * @see FileStoreSample01
 * @author jraduget
 */
public class FileStoreSample02 {

   // @Parameter overrides myStore configuration settings
   @Context(value = "myStore", parameters = { @Parameter(name = Readonly, value = "true"), @Parameter(name = ConnectTimeout, value = "0"),
	   @Parameter(name = ReadTimeout, value = "0") })
   protected FileStore myStore;

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
    * @return the content of the file "http://localhost:8380/kaleido-it/store/foo.txt"
    * @throws ResourceException
    */
   public String echo() throws ResourceException {
	String storeRelativePath = "store/foo.txt";
	String text = myStore.get(storeRelativePath).getText("UTF8");
	System.out.printf("file content [%s] :\n%s", storeRelativePath, text);
	return text;
   }

}
