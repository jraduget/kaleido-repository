/*
 *  Copyright 2008-2011 the original author or authors.
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
package org.kaleidofoundry.core.io;

import static org.kaleidofoundry.core.io.ConsoleManagerBean.BEGINLINE_ARGS;
import static org.kaleidofoundry.core.io.ConsoleManagerBean.MAXLINE_COUNT_ARGS;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.util.URLEncoderHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * @author Jerome RADUGET
 */
public class ConsoleManagerRestTest extends Assert {

   private static final String RESOURCE_TO_TEST = "classpath:/io/java_install.txt";

   private Client client;

   @Before
   public void setup() {
	// client configuration
	ClientConfig config = new DefaultClientConfig();
	client = Client.create(config);
	getBaseResource().path("register").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).get(String.class);
   }

   @Test
   public void head() {

	try {
	   getBaseResource().path("head").queryParam("resource", URLEncoderHelper.encode("file:/tmp/none")).accept(MediaType.TEXT_PLAIN).get(String.class);
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	}

	String result;
	StringBuilder bufferResult;

	result = getBaseResource().path("head").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(MAXLINE_COUNT_ARGS, "1")
		.accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 001
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = getBaseResource().path("head").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(MAXLINE_COUNT_ARGS, "2")
		.accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 001
	bufferResult.append("  extracting: bin/awt.dll").append("\n"); // 002
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);
   }

   @Test
   public void tail() {

	try {
	   getBaseResource().path("tail").queryParam("resource", URLEncoderHelper.encode("file:/tmp/none")).accept(MediaType.TEXT_PLAIN).get(String.class);
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	}

	String result;
	StringBuilder bufferResult;

	result = getBaseResource().path("tail").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(MAXLINE_COUNT_ARGS, "1")
		.accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("A total of 3294 files (of which 3267 are classes) were written to output.").append("\n"); // 551
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = getBaseResource().path("tail").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(MAXLINE_COUNT_ARGS, "3")
		.accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("A total of 2579113 bytes were read in 0 segment(s).").append("\n"); // 549
	bufferResult.append("A total of 7571805 file content bytes were written.").append("\n"); // 550
	bufferResult.append("A total of 3294 files (of which 3267 are classes) were written to output.").append("\n"); // 551
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);
   }

   @Test
   public void extract() {

	try {
	   getBaseResource().path("extract").queryParam("resource", URLEncoderHelper.encode("file:/tmp/none")).accept(MediaType.TEXT_PLAIN).get(String.class);
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	}

	String result;
	StringBuilder bufferResult;

	result = getBaseResource().path("extract").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(BEGINLINE_ARGS, "1")
		.queryParam(MAXLINE_COUNT_ARGS, "1").accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 491
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = getBaseResource().path("extract").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(BEGINLINE_ARGS, "1")
		.queryParam(MAXLINE_COUNT_ARGS, "2").accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 491
	bufferResult.append("  extracting: bin/awt.dll").append("\n");
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = getBaseResource().path("extract").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(BEGINLINE_ARGS, "491")
		.queryParam(MAXLINE_COUNT_ARGS, "4").accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: lib/zi/Pacific/Fakaofo").append("\n"); // 491
	bufferResult.append("  extracting: lib/zi/Pacific/Fiji").append("\n");
	bufferResult.append("  extracting: lib/zi/Pacific/Funafuti").append("\n");
	bufferResult.append("  extracting: lib/zi/Pacific/Galapagos").append("\n");
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = getBaseResource().path("extract").queryParam("resource", URLEncoderHelper.encode(RESOURCE_TO_TEST)).queryParam(BEGINLINE_ARGS, "549")
		.queryParam(MAXLINE_COUNT_ARGS, "1").accept(MediaType.TEXT_PLAIN).get(String.class);
	bufferResult = new StringBuilder();
	bufferResult.append("A total of 3294 files (of which 3267 are classes) were written to output.").append("\n"); // 549
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);
   }

   private URI getBaseURI() {
	return UriBuilder.fromUri("http://localhost:8380/kaleido-it").build();
   }

   private WebResource getBaseResource() {
	WebResource resource = client.resource(getBaseURI());
	return resource.path("rest").path("consoles");
   }
}
