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
package org.kaleidofoundry.core.spring.xml;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationException;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.StoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Jerome RADUGET
 */
@Service
public class MySpringService {

   @Autowired
   @Qualifier("myStore")
   private FileStore store;

   @Autowired
   @Qualifier("myConfig")
   private Configuration configuration;

   /**
    * @param resource resource name (relative path)
    * @throws StoreException
    */
   public String getStoreResource(final String resource) throws StoreException {
	return store.get(resource).getText();
   }

   /**
    * @param property
    * @return the given configuration property
    * @throws ConfigurationException
    */
   public String getConfigurationProperty(final String property) throws ConfigurationException {
	return configuration.getString(property);
   }
}
