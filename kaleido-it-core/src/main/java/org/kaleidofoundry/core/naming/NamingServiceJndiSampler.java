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
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.ThrowableHelper;

/**
 * @author Jerome RADUGET
 */
public class NamingServiceJndiSampler extends AbstractJavaSamplerClient {

   private static final String ConfigurationUri = "configurationUri";
   private static final String EchoMessage = "echoMessage";

   @Override
   public void setupTest(final JavaSamplerContext context) {
	// load configuration & context
	try {
	   ConfigurationFactory.provides("myConfig", context.getParameter(ConfigurationUri));
	} catch (ProviderException rte) {
	   throw rte;
	}
   }

   @Override
   public void teardownTest(final JavaSamplerContext context) {
	try {
	   ConfigurationFactory.unregister("myConfig");
	} catch (StoreException rse) {
	   throw new IllegalStateException(rse);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.apache.jmeter.protocol.java.sampler.JavaSamplerClient#runTest(org.apache.jmeter.protocol.java.sampler.JavaSamplerContext)
    */
   @Override
   public SampleResult runTest(final JavaSamplerContext context) {

	// parent sampler result
	SampleResult parentResults = new SampleResult();
	// childs sampler result
	SampleResult childResults;
	// global sampler status
	boolean mainSampleStatusOK = true;
	// user message to process (for echo command)
	String userMessage = context.getParameter(EchoMessage);
	// naming service sample
	NamingServiceJndiSample01 sample;

	// start sample
	parentResults.sampleStart();
	parentResults.setThreadName("naming-service");
	parentResults.setSampleLabel("naming-service-sampler");

	// 1. context injection cost
	childResults = new SampleResult();
	childResults.sampleStart();
	childResults.setSampleLabel("context injection");
	sample = new NamingServiceJndiSample01();
	childResults.sampleEnd();
	childResults.setResponseCodeOK();
	childResults.setSuccessful(true);
	parentResults.addSubResult(childResults);

	// 2. ejb call
	childResults = new SampleResult();
	try {
	   // context and instance injection of the naming service
	   childResults.sampleStart();
	   childResults.setSampleLabel("echoFromEJB");
	   childResults.setResponseMessage(sample.echoFromEJB(userMessage));
	   childResults.sampleEnd();
	   childResults.setResponseCodeOK();
	   childResults.setSuccessful(true);
	} catch (Throwable th) {
	   childResults.setResponseMessage(ThrowableHelper.getStackTrace(th));
	   childResults.setSuccessful(false);
	   mainSampleStatusOK = false;
	}
	parentResults.addSubResult(childResults);

	// 3. jdbc datasource call
	childResults = new SampleResult();
	try {
	   // context and instance injection of the naming service
	   childResults.sampleStart();
	   childResults.setSampleLabel("echoFromDatabase");
	   childResults.setResponseMessage(sample.echoFromDatabase(userMessage));
	   childResults.sampleEnd();
	   childResults.setResponseCodeOK();
	   childResults.setSuccessful(true);
	} catch (Throwable th) {
	   childResults.setResponseMessage(ThrowableHelper.getStackTrace(th));
	   childResults.setSuccessful(false);
	   mainSampleStatusOK = false;
	}
	parentResults.addSubResult(childResults);

	// 4. jms call
	childResults = new SampleResult();
	try {
	   // context and instance injection of the naming service
	   childResults.sampleStart();
	   childResults.setSampleLabel("echoFromJMS");
	   childResults.setResponseMessage(sample.echoFromJMS(userMessage).getJMSCorrelationID());
	   childResults.sampleEnd();
	   childResults.setResponseCodeOK();
	   childResults.setSuccessful(true);
	} catch (Throwable th) {
	   childResults.setResponseMessage(ThrowableHelper.getStackTrace(th));
	   childResults.setSuccessful(false);
	   mainSampleStatusOK = false;
	}
	parentResults.addSubResult(childResults);

	// global parent sampler status
	parentResults.setSuccessful(mainSampleStatusOK);
	if (mainSampleStatusOK) {
	   parentResults.setResponseCodeOK();
	}
	return parentResults;
   }

   /*
    * (non-Javadoc)
    * @see org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient#getDefaultParameters()
    */
   @Override
   public Arguments getDefaultParameters() {
	Arguments args = new Arguments();
	args.addArgument(ConfigurationUri, "classpath:/naming/myContext.properties");
	args.addArgument(EchoMessage, "Hello world!");
	return args;
   }
}
