/*
 *  Copyright 2008-2010 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.naming;

import static org.kaleidofoundry.core.naming.NamingConstants.NamingPluginName;

import org.kaleidofoundry.core.plugin.Declare;

/**
 * NamingService is a service locator interface<br/>
 * 
 * @author Jerome RADUGET
 */
@Declare(value = NamingPluginName, description = "naming service plugin interface")
public interface NamingService {

   /**
    * @param <R>
    * @param resourceName
    * @param ressourceClass
    * @return resourceName name of the resource like jndiName
    * @throws NamingServiceException
    */
   <R> R locate(final String resourceName, final Class<R> ressourceClass) throws NamingServiceException;

}
