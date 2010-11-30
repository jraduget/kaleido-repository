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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * 
 * @author Jerome RADUGET
 *
 */
public class RemoteEjbJunitLauncher extends Assert {

   @Before
   public void setup() throws ResourceException {
	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myConfig=classpath:/naming/myRemoteContext.properties
	ConfigurationFactory.provides("myConfig", "classpath:/naming/myRemoteContext.properties");
   }

   @After
   public void cleanupClass() throws ResourceException {
	I18nMessagesFactory.clearCache();
	ConfigurationFactory.destroy("myConfig");
   }

   @Test
   public void testRemoteEjb01() {
	RemoteEjbSample01 remoteEjbSample = new RemoteEjbSample01();	
	assertEquals("hello world", remoteEjbSample.echo("hello world"));
	assertEquals("hello world2", remoteEjbSample.echo("hello world2"));
	assertEquals("hello world3", remoteEjbSample.echo("hello world3"));	
	assertFalse("foo?".equals(remoteEjbSample.echo("foo")));
   }

}
