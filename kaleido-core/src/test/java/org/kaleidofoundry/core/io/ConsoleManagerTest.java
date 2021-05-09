/*
 * Copyright 2008-2021 the original author or authors
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
package org.kaleidofoundry.core.io;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.store.FileStoreConsoleController;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * {@link FileStoreConsoleController} Tests
 * 
 * @author jraduget
 */
public class ConsoleManagerTest  {

   static final Logger LOGGER = LoggerFactory.getLogger(FileStoreConsoleController.class);

   static final String ClassPathResource = "classpath:/io/java_install.txt";

   private FileStoreConsoleController console;

   @Before
   public void setUp() throws ResourceNotFoundException {
	console = new FileStoreConsoleController();
	console.register("classpath:/io/java_install.txt");
   }

   /**
    * @throws IOException
    * @throws ResourceException
    */
   @Test
   public void head() throws IOException, ResourceException {
	String result;
	StringBuilder bufferResult;

	result = console.head(ClassPathResource, 1);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 001
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = console.head(ClassPathResource, 2);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 001
	bufferResult.append("  extracting: bin/awt.dll").append("\n"); // 002
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);
   }

   /**
    * @throws IOException
    * @throws ResourceException
    */
   @Test
   public void tail() throws IOException, ResourceException {
	String result;
	StringBuilder bufferResult;

	result = console.tail(ClassPathResource, 1);
	bufferResult = new StringBuilder();
	bufferResult.append("A total of 3294 files (of which 3267 are classes) were written to output.").append("\n"); // 551
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = console.tail(ClassPathResource, 3);
	bufferResult = new StringBuilder();
	bufferResult.append("A total of 2579113 bytes were read in 0 segment(s).").append("\n"); // 549
	bufferResult.append("A total of 7571805 file content bytes were written.").append("\n"); // 550
	bufferResult.append("A total of 3294 files (of which 3267 are classes) were written to output.").append("\n"); // 551
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);
   }

   /**
    * @throws IOException
    * @throws ResourceException
    */
   @Test
   public void extract() throws IOException, ResourceException {

	String result;
	StringBuilder bufferResult;

	result = console.extract(ClassPathResource, 1, 1);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 491
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = console.extract(ClassPathResource, 1, 2);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: bin/").append("\n"); // 491
	bufferResult.append("  extracting: bin/awt.dll").append("\n");
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = console.extract(ClassPathResource, 491, 4);
	bufferResult = new StringBuilder();
	bufferResult.append("  extracting: lib/zi/Pacific/Fakaofo").append("\n"); // 491
	bufferResult.append("  extracting: lib/zi/Pacific/Fiji").append("\n");
	bufferResult.append("  extracting: lib/zi/Pacific/Funafuti").append("\n");
	bufferResult.append("  extracting: lib/zi/Pacific/Galapagos").append("\n");
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);

	result = console.extract(ClassPathResource, 549, 1);
	bufferResult = new StringBuilder();
	bufferResult.append("A total of 3294 files (of which 3267 are classes) were written to output.").append("\n"); // 549
	assertNotNull(bufferResult);
	assertEquals(bufferResult.toString(), result);
   }
}
