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

import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * <p>
 * <h3>Simple naming service usage</h3> Inject {@link NamingService} context and instance manually by coding, using context builder
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class RemoteEjbSample02 {

   private final NamingService namingService;

   public RemoteEjbSample02() {

	RuntimeContext<NamingService> context = new NamingContextBuilder("myManualNamingCtx", NamingService.class)
	.withInitialContextFactory("com.sun.enterprise.naming.SerialInitContextFactory")
	.withUrlpkgPrefixes("com.sun.enterprise.naming")
	.withStateFactories("com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl")
	.withCorbaORBInitialHost("127.0.0.1")
	.withCorbaORBInitialPort("3700")
	.build();

	namingService = NamingServiceFactory.provides(context);
   }

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
	MyRemoteBean myRemoteBean = namingService.locate("ejb/myBean", MyRemoteBean.class);

	// call it
	return myRemoteBean.echo(message);
   }

}
