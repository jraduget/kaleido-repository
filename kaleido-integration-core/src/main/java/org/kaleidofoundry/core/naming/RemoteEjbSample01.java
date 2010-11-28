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
 * -Dkaleido.configurations=classpath:/naming/myRemoteContext.properties
 * </pre>
 * 
 * Resource file : "classpath:/naming/myRemoteContext.properties" contains :
 * 
 * <pre>
 * # naming service remote context (glassfish v3 server)
 * naming.jndi.myNamingCtx.java.naming.factory.initial=com.sun.enterprise.naming.SerialInitContextFactory
 * naming.jndi.myNamingCtx.java.naming.factory.url.pkgs=com.sun.enterprise.naming
 * naming.jndi.myNamingCtx.java.naming.factory.state=com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl
 * naming.jndi.myNamingCtx.org.omg.CORBA.ORBInitialHost=127.0.0.1
 * naming.jndi.myNamingCtx.org.omg.CORBA.ORBInitialPort=3700
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class RemoteEjbSample01 {

   @Context("myNamingCtx")
   private NamingService namingService;

   /**
    * Stdout for echo("hello world")":
    * 
    * <pre>
    * hello world
    * </pre>
    * 
    * @param message
    * @return input message
    * @throws NamingServiceException
    */
   public String echo(final String message) {

	// get ejb client service (remote)
	MyRemoteBean myRemoteBean = namingService.locate("java:global/kaleido-integration-ear/kaleido-integration-ejb/MyBean", MyRemoteBean.class);

	// call it
	return myRemoteBean.echo(message);
   }

}
