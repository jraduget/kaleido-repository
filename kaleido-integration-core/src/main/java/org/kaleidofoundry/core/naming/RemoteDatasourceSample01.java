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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>Simple naming service usage</h3> Inject {@link NamingService} context and instance using {@link Context} annotation without
 * parameters, but using external configuration
 * </p>
 * <br/>
 * <b>Precondition :</b> The following java env. variable have been set
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/naming/myContext.properties
 * </pre>
 * 
 * Resource file : "classpath:/naming/myContext.properties" contains :
 * 
 * <pre>
 * # naming service for glassfish v3 server
 * naming.myRemoteCtx.java.naming.factory.initial=com.sun.enterprise.naming.SerialInitContextFactory
 * naming.myRemoteCtx.java.naming.factory.url.pkgs=com.sun.enterprise.naming
 * naming.myRemoteCtx.java.naming.factory.state=com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl
 * naming.myRemoteCtx.org.omg.CORBA.ORBInitialHost=127.0.0.1
 * naming.myRemoteCtx.org.omg.CORBA.ORBInitialPort=3700
 * # caching and failover policies in a remote context
 * naming.myRemoteCtx.caching=all
 * naming.myRemoteCtx.caching.strategy=global
 * naming.myRemoteCtx.failover.enabled=true
 * naming.myRemoteCtx.failover.wait=2000
 * naming.myRemoteCtx.failover.maxretry=5
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class RemoteDatasourceSample01 {

   @Context("myRemoteCtx")
   private NamingService namingService;

   /**
    * @return
    * @throws SQLException
    * @throws NamingServiceException
    */
   public Connection getConnection() throws SQLException {

	// get remote datasource
	DataSource myDatasource = namingService.locate("jdbc/kaleido", DataSource.class);

	// return a connection from the pool
	return myDatasource.getConnection();
   }

}
