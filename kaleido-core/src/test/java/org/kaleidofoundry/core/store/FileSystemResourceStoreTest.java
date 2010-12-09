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
package org.kaleidofoundry.core.store;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author Jerome RADUGET
 */
public class FileSystemResourceStoreTest extends AbstractResourceStoreTest {

   private URI tmpFileName;

   @Before
   @Override
   public void setup() throws Throwable {
	super.setup();

	final RuntimeContext<ResourceStore> context = new ResourceContextBuilder().withUriRootPath("file:/").build();
	resourceStore = new FileSystemResourceStore(context);

	// create temp file to test
	final File tmpFile = File.createTempFile("kaleidofoundry-", ".test");
	final FileWriter fout = new FileWriter(tmpFile);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();

	tmpFileName = tmpFile.toURI();

	existingResources.put(tmpFileName.getPath(), DEFAULT_RESOURCE_MOCK_TEST);

	nonExistingResources.add("foo");
   }

   @After
   @Override
   public void cleanup() throws Throwable {
	super.cleanup();
	if (tmpFileName != null) {
	   new File(tmpFileName).delete();
	}
   }
}
