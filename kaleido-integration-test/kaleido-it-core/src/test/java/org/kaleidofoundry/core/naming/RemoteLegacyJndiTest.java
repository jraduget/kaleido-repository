/*
 *  Copyright 2008-2016 the original author or authors.
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

import org.junit.Assert;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Legacy jndi look up and new jndi naming service lookup
 * 
 * @author jraduget
 */
public class RemoteLegacyJndiTest  {

   private static final String datasourceJndiName = "jdbc/kaleido";

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
	   ic.addToEnvironment("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
	   ic.addToEnvironment("org.omg.CORBA.ORBInitialPort", "3700");
	   // ic.addToEnvironment(Context.SECURITY_PRINCIPAL, "admin");
	   // ic.addToEnvironment(Context.SECURITY_CREDENTIALS, "admin");

	   // timeout test settings ko
	   // ic.addToEnvironment("com.sun.corba.ee.transport.ORBTCPConnectTimeouts" , "100:50000:20");
	   // ic.addToEnvironment("com.sun.corba.ee.transport.ORBTCPTimeouts" , "100:50000:20");
	   // ic.addToEnvironment("com.sun.CORBA.transport.ORBTCPReadTimeouts" , "100:50000:20");
	   // ic.addToEnvironment("com.sun.corba.ee.transport.ORBWaitForResponseTimeout" , "1");

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
	RuntimeContext<NamingService> context = new NamingContextBuilder().withInitialContextFactory("com.sun.enterprise.naming.SerialInitContextFactory")
		.withUrlpkgPrefixes("com.sun.enterprise.naming").withStateFactories("com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl")
		.withCorbaORBInitialHost("127.0.0.1").withCorbaORBInitialPort("3700").build();

	NamingService namingService = new JndiNamingService(context);
	DataSource datasource = namingService.locate(datasourceJndiName, DataSource.class);
	assertNotNull(datasource);
	assertNotNull(datasource.getConnection());
   }

}
