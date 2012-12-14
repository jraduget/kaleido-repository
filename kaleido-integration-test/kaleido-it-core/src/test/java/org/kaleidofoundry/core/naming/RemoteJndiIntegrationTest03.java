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

import static org.kaleidofoundry.core.naming.NamingContextBuilder.Caching;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.FailoverEnabled;
import static org.kaleidofoundry.core.naming.NamingContextBuilder.InitialContextFactory;

import org.junit.Test;

/**
 * @see NamingServiceJndiSample03
 * @author Jerome RADUGET
 */
public class RemoteJndiIntegrationTest03 extends RemoteJndiIntegrationTest {

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.RemoteJunitLauncher#getNamingServiceJndiSample()
    */
   @Override
   public NamingServiceJndiSample getNamingServiceJndiSample() {
	return new NamingServiceJndiSample03();
   }

   @Test
   public void checkContext() {
	JndiNamingService ns = (JndiNamingService) getNamingServiceJndiSample().getNamingService();
	assertNotNull(ns);
	assertNotNull(ns.getContext());
	assertEquals("myNamingService", ns.getContext().getName());
	assertEquals("com.sun.enterprise.naming.SerialInitContextFactory", ns.getContext().getProperty(InitialContextFactory));
	assertEquals("false", ns.getContext().getProperty(FailoverEnabled));
	assertEquals("none", ns.getContext().getProperty(Caching));
   }

}
