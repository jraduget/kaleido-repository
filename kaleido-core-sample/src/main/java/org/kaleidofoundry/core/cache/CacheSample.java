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
package org.kaleidofoundry.core.cache;

import java.util.GregorianCalendar;

import org.kaleidofoundry.core.context.InjectContext;

/**
 * Simple resource store usage<br/>
 * The following java env. variable have been set :
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/store/myCacheConfig.properties
 * </pre>
 * 
 * Resource file : "classpath:/cache/myCacheConfig.properties" contains :
 * 
 * <pre>
 * # cache context properties
 * cache.myCache.name=
 * cache.myCache.resourceUri=foo
 * #cache.myCache.resourceStore=...
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class CacheSample {

   @InjectContext("myCache")
   protected Cache<String, YourBean> myCache;

   public CacheSample() {
	// feed cache with somes bean entries
	myCache.put("bean1", new YourBean("name1", GregorianCalendar.getInstance(), true, 2));
	myCache.put("bean2", new YourBean("name2", GregorianCalendar.getInstance(), false, 15));
   }

   /**
    * method example that use the injected cache
    */
   public void echo() {
	System.out.printf("cache name: %s", myCache.getName());
	System.out.printf("cache size: %s", myCache.size());
	System.out.printf("cache entry[%s]: %s", "bean1", myCache.get("bean1").toString());
	System.out.printf("cache entry[%s]: %s", "bean2", myCache.get("bean2").toString());
   }

}
