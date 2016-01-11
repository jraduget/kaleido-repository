/*  
 * Copyright 2008-2016 the original author or authors 
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
 * @see NamingServiceJndiSample04
 * @author jraduget
 */
public class RemoteJndiIntegrationTest04 extends RemoteJndiIntegrationTest {

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.RemoteJunitLauncher#getNamingServiceJndiSample()
    */
   @Override
   public NamingServiceJndiSample getNamingServiceJndiSample() {
	return new NamingServiceJndiSample04();
   }

   @Test
   public void checkContext() {
	JndiNamingService ns = (JndiNamingService) getNamingServiceJndiSample().getNamingService();
	assertNotNull(ns);
	assertNotNull(ns.getContext());
	assertEquals("myManualNamingService", ns.getContext().getName());
	assertEquals("com.sun.enterprise.naming.SerialInitContextFactory", ns.getContext().getProperty(InitialContextFactory));
	assertEquals("true", ns.getContext().getProperty(FailoverEnabled));
	assertEquals("all", ns.getContext().getProperty(Caching));
   }
}
