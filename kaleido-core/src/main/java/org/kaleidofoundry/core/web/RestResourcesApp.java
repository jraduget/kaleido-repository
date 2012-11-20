/*
 *  Copyright 2008-2011 the original author or authors.
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
package org.kaleidofoundry.core.web;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.kaleidofoundry.core.config.ConfigurationManagerBean;
import org.kaleidofoundry.core.env.EnvironmentController;
import org.kaleidofoundry.core.io.ConsoleManagerBean;
import org.kaleidofoundry.core.store.FileStoreController;

/**
 * Rest application servlet used to exposed REST resources
 * 
 * @author Jerome RADUGET
 */
public class RestResourcesApp extends Application {

   @Override
   public Set<Class<?>> getClasses() {
	Set<Class<?>> s = new HashSet<Class<?>>();
	// exception handler
	s.add(RestConfigurationNotFoundMapper.class);
	s.add(RestResourceNotFoundMapper.class);
	s.add(RestPropertyNotFoundMapper.class);
	// manager module
	s.add(EnvironmentController.class);
	s.add(ConsoleManagerBean.class);
	s.add(ConfigurationManagerBean.class);
	s.add(FileStoreController.class);
	
	return s;
   }

}
