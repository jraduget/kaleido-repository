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

import java.io.ByteArrayInputStream;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author jraduget
 */
public class HttpFileStoreTest extends AbstractFileStoreTest {

   private static Server server;

   @BeforeClass
   public static void setupStatic() throws Exception {
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
	// jetty http server stop
	server.stop();
	server.destroy();
   }

   @Before
   @Override
   public void setup() throws Throwable {
	super.setup();

	// be-careful, there is no-proxy settings for this test ;-)
	final RuntimeContext<FileStore> context = new FileStoreContextBuilder("httpStore").withBaseUri("http://localhost:9090/test/").build();
	fileStore = new HttpFileStore(context);

	existingResources.put("kaleidofoundry/it/store/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);
	nonExistingResources.add("kaleidofoundry/it/store/foo");
   }

   @Test
   @Override
   public void store() throws ResourceException {
	try {
	   fileStore.store("kaleidofoundry/it/store/toStore.txt", new ByteArrayInputStream("foo".getBytes()));
	   fail();
	} catch (final ResourceException rse) {
	   assertEquals("store.readonly.illegal", rse.getCode());
	}
   }

   @Test
   @Override
   public void move() throws ResourceException {
	try {
	   fileStore.move("kaleidofoundry/it/store/foo.txt", "kaleidofoundry/it/store/foo.old");
	   fail();
	} catch (final ResourceException rse) {
	   assertEquals("store.readonly.illegal", rse.getCode());
	}
   }

   @Test
   @Override
   public void remove() throws ResourceException {
	try {
	   fileStore.remove("kaleidofoundry/it/store/toRemove.txt");
	   fail();
	} catch (final ResourceException rse) {
	   assertEquals("store.readonly.illegal", rse.getCode());
	}
   }
}
