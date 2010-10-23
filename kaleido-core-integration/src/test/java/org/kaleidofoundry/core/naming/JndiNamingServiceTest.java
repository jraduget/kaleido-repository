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

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;

/**
 * @author Jerome RADUGET
 */
public class JndiNamingServiceTest extends Assert {

   private static final String datasourceJndiName = "jdbc/sonar";

   @Before
   public void setup() {
	// disable i18n message bundle control to speed up test (no need of a local derby instance startup)
	I18nMessagesFactory.disableJpaControl();
   }

   @After
   public void cleanup() {
	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();
   }

   @Test
   public void remoteLegacyDatasourceTest() throws NamingException, SQLException {
	// test standard jndi lookup
	InitialContext ic = null;
	DataSource datasource;
	try {
	   ic = new InitialContext();
	   ic.addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
	   ic.addToEnvironment(Context.URL_PKG_PREFIXES, "com.sun.enterprise.naming");
	   ic.addToEnvironment(Context.STATE_FACTORIES, "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
//	   ic.addToEnvironment("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
//	   ic.addToEnvironment("org.omg.CORBA.ORBInitialPort", "3700");
	   ic.addToEnvironment(Context.SECURITY_PRINCIPAL, "admin");
	   ic.addToEnvironment(Context.SECURITY_CREDENTIALS, "kaleidoadmin");
	   Object remoteObj = ic.lookup(datasourceJndiName);
	   datasource = DataSource.class.cast(PortableRemoteObject.narrow(remoteObj, DataSource.class));
	   assertNotNull(datasource);
	   assertNotNull(datasource.getConnection());
	} catch (Throwable th) {
	   th.printStackTrace();
	   fail(th.getClass().getName() + " : " + th.getMessage());
	} finally {
	   if (ic != null) {
		ic.close();
	   }
	}
   }

   @Test
   public void remoteDatasourceTest() throws NamingException, SQLException {
	// test naming service connection
	RuntimeContext<NamingService> context = new NamingContextBuilder()
		.withInitialContextFactory("com.sun.enterprise.naming.SerialInitContextFactory")
		.withUrlpkgPrefixes("com.sun.enterprise.naming")
		.withStateFactories("com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl")
		.withCorbaORBInitialHost("127.0.0.1")
		.withCorbaORBInitialPort("3700")
		.withSecurityPrincipal("admin")
		.withSecurityCredentials("kaleidoadmin")
		.build();
	NamingService namingService = new JndiNamingService(context);
	DataSource datasource = namingService.locate(datasourceJndiName, DataSource.class);
	assertNotNull(datasource);
	assertNotNull(datasource.getConnection());
   }

}
