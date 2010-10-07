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
package org.kaleidofoundry.core.naming;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;

import junit.framework.Assert;

/**
 * 
 * @author Jerome RADUGET
 *
 */
public class JndiNamingServiceTest extends Assert {

   private static Server server;
   
   @BeforeClass
   public static void setupStatic() throws Exception {
	// disable i18n jpa for test performance
	I18nMessagesFactory.disableJpaControl();

	// jetty http server start
	final SocketConnector connector = new SocketConnector();
	connector.setPort(9090);
	server = new Server();
	server.setConnectors(new Connector[] { connector });
	final WebAppContext context = new WebAppContext();
	context.setServer(server);
	context.setContextPath("/test");
	context.setWar("src/test/webapp");
	server.setHandler(context);
	server.start();
   }

   @AfterClass
   public static void cleanupStatic() throws Exception {
	// re-enable i18n jpa
	I18nMessagesFactory.enableJpaControl();
	// jetty http server stop
	server.stop();
	server.destroy();
   }

}
