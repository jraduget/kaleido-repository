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
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.glassfish.api.ActionReport;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.api.embedded.ContainerBuilder;
import org.glassfish.api.embedded.EmbeddedDeployer;
import org.glassfish.api.embedded.EmbeddedFileSystem;
import org.glassfish.api.embedded.LifecycleException;
import org.glassfish.api.embedded.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;

/**
 * @author Jerome RADUGET
 */
public class JndiNamingServiceTest extends Assert {

   public static Server server;
   private static EmbeddedDeployer deployer;

   private static final String DataSourceJndiName = "jdbc/myDatasource";

   @BeforeClass
   public static void setupStatic() throws LifecycleException {

	// disable i18n message bundle control to speed up test (no need of a local derby instance startup)
	I18nMessagesFactory.disableJpaControl();

	// server builder
	String serverInstance = "jndiNamingServerTest";
	Server.Builder builder = new Server.Builder(serverInstance);
	EmbeddedFileSystem.Builder efsb = new EmbeddedFileSystem.Builder().installRoot(new File("target/glassfish/" + serverInstance + "/installroot"))
		.instanceRoot(new File("target/glassfish/" + serverInstance + "/instanceroot")).autoDelete(true);
	EmbeddedFileSystem efs = efsb.build();
	builder.embeddedFileSystem(efs);

	// server instance
	server = builder.build();
	server.addContainer(server.createConfig(ContainerBuilder.Type.ejb));
	server.start();

	// datasource deployment
	String databaseInstance = "myDatabaseToTest";
	String command = "create-jdbc-connection-pool";
	ParameterMap props = new ParameterMap();
	props.add("datasourceclassname", "org.apache.derby.jdbc.EmbeddedDataSource");
	props.add("restype", "javax.sql.DataSource");
	props.add("ping", "true");
	props.add("property", "ConnectionAttributes=create\\=true:DatabaseName=target/derby/" + databaseInstance);
	props.add("DEFAULT", databaseInstance);

	CommandRunner run = server.getHabitat().getComponent(CommandRunner.class);
	ActionReport rep = server.getHabitat().getComponent(ActionReport.class);
	run.getCommandInvocation(command, rep).parameters(props).execute();

	command = "create-jdbc-resource";
	props = new ParameterMap();
	props.add("connectionpoolid", databaseInstance);
	props.add("jndi_name", DataSourceJndiName);
	CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
	ActionReport report = server.getHabitat().getComponent(ActionReport.class);
	runner.getCommandInvocation(command, report).parameters(props).execute();

	// ejb deployment
	// File fileToDeploye = new File("target/kaleido-core-1.0.0-SNAPSHOT.jar");
	// deployer = server.getDeployer();
	// deployer.deploy(fileToDeploye, new DeployCommandParameters());

   }

   @AfterClass
   public static void cleanupStatic() throws LifecycleException {

	// re-enable i18n jpa message bundle control
	I18nMessagesFactory.enableJpaControl();

	if (deployer != null) {
	   deployer.undeployAll();
	}
	if (server != null) {
	   server.stop();
	}
   }

   @Test(expected = NamingServiceException.class)
   public void namingErrorTest() {
	// test naming service connection
	RuntimeContext<NamingService> context = new NamingContextBuilder().withInitialContextFactory("com.sun.enterprise.naming.SerialInitContextFactory")
		.withUrlpkgPrefixes("com.sun.enterprise.naming").withStateFactories("com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl")
		.withCorbaORBInitialHost("127.0.0.1").withCorbaORBInitialPort("9999").build();
	NamingService namingService = new JndiNamingService(context);
	namingService.locate(DataSourceJndiName, DataSource.class);
	fail("expected NamingServiceException");
   }

   @Test(expected = NamingServiceNotFoundException.class)
   public void noResourceFoundTest() {
	RuntimeContext<NamingService> context = new NamingContextBuilder().build();
	NamingService namingService = new JndiNamingService(context);
	namingService.locate("jdbc/noresource", DataSource.class);
	fail("expected NamingNotFoundException");
   }

   @Test
   public void localResourceTest() throws NamingException, SQLException {
	// test standard jndi lookup
	InitialContext ic = null;
	DataSource datasource;
	try {
	   ic = new InitialContext();
	   datasource = (DataSource) ic.lookup(DataSourceJndiName);
	   assertNotNull(datasource);
	   assertNotNull(datasource.getConnection());
	} finally {
	   if (ic != null) {
		ic.close();
	   }
	}
	// test naming service connection
	RuntimeContext<NamingService> context = new NamingContextBuilder().build();
	NamingService namingService = new JndiNamingService(context);
	DataSource datasource2 = namingService.locate(DataSourceJndiName, DataSource.class);
	assertNotNull(datasource2);
	assertNotNull(datasource2.getConnection());
   }

   @Test
   public void remoteResourceTest() throws NamingException, SQLException {
	// test standard jndi lookup
	InitialContext ic = null;
	DataSource datasource;
	try {
	   ic = new InitialContext();
	   ic.addToEnvironment("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
	   ic.addToEnvironment("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
	   ic.addToEnvironment("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
	   ic.addToEnvironment("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
	   ic.addToEnvironment("org.omg.CORBA.ORBInitialPort", "3700");
	   datasource = (DataSource) ic.lookup(DataSourceJndiName);
	   assertNotNull(datasource);
	   assertNotNull(datasource.getConnection());
	} finally {
	   if (ic != null) {
		ic.close();
	   }
	}
	// test naming service connection
	RuntimeContext<NamingService> context = new NamingContextBuilder().withInitialContextFactory("com.sun.enterprise.naming.SerialInitContextFactory")
		.withUrlpkgPrefixes("com.sun.enterprise.naming").withStateFactories("com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl")
		.withCorbaORBInitialHost("127.0.0.1").withCorbaORBInitialPort("3700").build();
	NamingService namingService = new JndiNamingService(context);
	DataSource datasource2 = namingService.locate(DataSourceJndiName, DataSource.class);
	assertNotNull(datasource2);
	assertNotNull(datasource2.getConnection());
   }

}
