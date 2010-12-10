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

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * @author Jerome RADUGET
 */
public class NamingServiceJndiSampler extends AbstractJavaSamplerClient {

   private static final String UserMessage = "message";
   
   /*
    * (non-Javadoc)
    * @see org.apache.jmeter.protocol.java.sampler.JavaSamplerClient#runTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
    */
   @Override
   public SampleResult runTest(JavaSamplerContext context) {

	SampleResult results = new SampleResult();
	results.sampleStart();

	try {
	   // context and instance injection of the naming service
	   NamingServiceJndiSample01 sample = new NamingServiceJndiSample01();

	   // ejb call
	   String userMessage = context.getParameter(UserMessage);
	   String result = sample.echoFromEJB(userMessage);
	   
	   results.setSuccessful(true);
	   results.setResponseCodeOK();
	   results.setResponseMessage(result);		

	} catch (Throwable th) {
	   results.setSuccessful(false);
	}
	
	results.sampleEnd();
	return results;
   }

   /*
    * (non-Javadoc)
    * @see org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#getDefaultParameters()
    */
   @Override
   public Arguments getDefaultParameters() {
	Arguments args = new Arguments();
	args.addArgument(UserMessage, "Hello world!");
	return args;
   }
}
