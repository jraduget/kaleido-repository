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
package org.kaleidofoundry.core.config;

import java.net.URISyntaxException;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class XmlPropertiesConfigurationTest extends AbstractConfigurationTest {

   public XmlPropertiesConfigurationTest() throws ResourceException, URISyntaxException {
	super();
   }

   @Override
   protected Configuration newInstance() throws ResourceException, URISyntaxException {
	return new XmlPropertiesConfiguration("propXmlCpConfig", "classpath:/org/kaleidofoundry/core/config/test.properties.xml",
		new RuntimeContext<org.kaleidofoundry.core.config.Configuration>(Configuration.class));
   }

}
