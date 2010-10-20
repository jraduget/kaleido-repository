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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.glassfish.api.embedded.LifecycleException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;

/**
 * @author Jerome RADUGET
 */
public class EjbNamingServiceTest extends Assert {

   public EJBContainer ec;
   private Context context;

   private static final String MyEjbJndiName = "java:global/myBean";

   @Before
   public void setup() throws LifecycleException {

	// disable i18n message bundle control to speed up test (no need of a local derby instance startup)
	I18nMessagesFactory.disableJpaControl();

	// ejb deployment
	// File fileToDeploye = new File("target/kaleido-core-1.0.0-SNAPSHOT.jar");
	// deployer = server.getDeployer();
	// deployer.deploy(fileToDeploye, new DeployCommandParameters());

	// other way for ejb :
	// http://old.nabble.com/Embedded-GlassFish---No-EJBContainer-available-with-multiple-JUnit-tests-td29816681.html
	Map<String, Object> properties = new HashMap<String, Object>();
	properties.put(EJBContainer.MODULES, new File("target/classes"));
	// properties.put(EJBContainer.MODULES, new File("target/test-classes"));
	ec = EJBContainer.createEJBContainer(properties);
	context = ec.getContext();
   }

   @After
   public void cleanup() throws NamingException {
	if (context != null) {
	   context.close();
	}
	if (ec != null) {
	   ec.close();
	}
	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void localEjbTest() {
	fail("not implemented");
   }

   @Test
   public void remoteEjbTest() {
	fail("not implemented");
   }

}
