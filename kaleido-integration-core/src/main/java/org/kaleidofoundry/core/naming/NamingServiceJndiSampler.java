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
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.ThrowableHelper;

/**
 * @author Jerome RADUGET
 */
public class NamingServiceJndiSampler extends AbstractJavaSamplerClient {

   private static final String UserMessage = "message";

   static {
	// load configuration & context
	try {
	   ConfigurationFactory.provides("myConfig", "classpath:/naming/myContext.properties");
	} catch (ResourceException rse) {
	   rse.printStackTrace();
	   throw new IllegalStateException(rse);
	} catch (RuntimeException rte) {
	   rte.printStackTrace();
	   throw rte;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.apache.jmeter.protocol.java.sampler.JavaSamplerClient#runTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
    */
   @Override
   public SampleResult runTest(JavaSamplerContext context) {

	// sampler result
	SampleResult results = new SampleResult();
	// global sampler status
	boolean mainSampleStatusOK = true;
	// user message to process (for echo command)
	String userMessage = context.getParameter(UserMessage);

	// start sample
	results.sampleStart();

	// ejb call
	try {

	   // context and instance injection of the naming service
	   NamingServiceJndiSample01 sample = new NamingServiceJndiSample01();

	   results.setThreadName("naming-service-sampler-echoFromEJB");
	   results.setResponseMessage(sample.echoFromEJB(userMessage));
	   results.setResponseCodeOK();
	} catch (Throwable th) {
	   results.setResponseMessage(ThrowableHelper.getStackTrace(th));
	   mainSampleStatusOK = false;
	}

	results.setSuccessful(mainSampleStatusOK);
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
