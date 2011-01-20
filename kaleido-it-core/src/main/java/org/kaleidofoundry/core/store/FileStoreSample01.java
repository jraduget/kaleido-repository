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

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>First simple file store usage</h3> This example will inject {@link FileStore} context and instance using {@link Context}
 * annotation without parameters
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
 * fileStore.myStoreCtx.uriScheme=http
 * fileStore.myStoreCtx.readonly=false
 * fileStore.myStoreCtx.connectTimeout=1500
 * fileStore.myStoreCtx.readTimeout=10000
 * 
 * # if you need proxy settings, uncomment and configure followings :
 * #fileStore.myStoreCtx.proxySet=true
 * #fileStore.myStoreCtx.proxyHost=yourProxyHost
 * #fileStore.myStoreCtx.proxyUser=yourProxyUser
 * #fileStore.myStoreCtx.proxyPassword=proxyUserPassword
 * 
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class FileStoreSample01 {

   @Context("myStoreCtx")
   protected FileStore fileStore;

   /**
    * <b>Example method using injected file store</b><br/>
    * <br/>
    * 1. it connect to the given file resource with the injected context (proxy, credentials, ...)<br/>
    * 2. it get the resource file content (text here), using the right charset ("UTF8" is the default is not specified) <br/>
    * <br/>
    * <b>Path are relative from the file store root uri, like :</b>
    * <ul>
    * <li>http://localhost:8080/kaleido-integration/</li>
    * <li>ftp://localhost/kaleido-integration/</li>
    * <li>classpath:/org/kaleidofoundry/core/</li>
    * </ul>
    * 
    * @return the content of the resource "http://localhost:8080/kaleido-integration/store/foo.txt"
    * @throws StoreException
    */
   public String echo() throws StoreException {
	String storeRelativePath = "store/foo.txt";
	String text = fileStore.get(storeRelativePath).getText("UTF8");
	System.out.printf("resource content [%s] :\n%s", storeRelativePath, text);
	return text;
   }
}
