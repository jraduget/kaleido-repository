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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;

/**
 * @author Jerome RADUGET
 */
public class FileSystemResourceStoreTest extends AbstractResourceStoreTest {

   private final List<File> filesToDelete = new ArrayList<File>();

   @Before
   @Override
   public void setup() throws Throwable {
	super.setup();

	// create a temporary file, in order to get the temp directory
	final File tmpFile = File.createTempFile("kaleido-store-get-", ".test");
	final String tempPath = tmpFile.getCanonicalPath().substring(0, tmpFile.getCanonicalPath().lastIndexOf(File.separator));
	filesToDelete.add(tmpFile);

	final RuntimeContext<ResourceStore> context = new ResourceContextBuilder().withUriRootPath("file:/" + FileHelper.buildUnixAppPath(tempPath)).build();
	resourceStore = new FileSystemResourceStore(context);

	// 1. resources to test - create temp file
	final File tmpFileToGet = File.createTempFile("kaleido-resource-get-", ".test");
	final FileWriter fout = new FileWriter(tmpFileToGet);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();
	filesToDelete.add(tmpFileToGet);
	fout.close();

	existingResources.put(FileHelper.getFileName(tmpFileToGet.getCanonicalPath()), DEFAULT_RESOURCE_MOCK_TEST);

	// 2. resources that does not exist
	nonExistingResources.add("foo");

	// 3. resources to store
	final File tmpFileToStore = File.createTempFile("kaleido-resource-store-", ".test");
	final String filenameToStore = FileHelper.getFileName(tmpFileToStore.getCanonicalPath());
	filesToDelete.add(tmpFileToStore);
	tmpFileToStore.delete();

	final InputStream inStore = new ByteArrayInputStream(DEFAULT_RESOURCE_MOCK_TEST.getBytes("UTF-8"));
	final ResourceHandler resource = new ResourceHandlerBean(filenameToStore, inStore);
	existingResourcesForStore.put(filenameToStore, resource);

   }

   @After
   @Override
   public void cleanup() throws Throwable {
	super.cleanup();

	for (final File file : filesToDelete) {
	   if (file.exists()) {
		System.out.printf("cleanup resources - deleted \"%s\" is %s", file.getCanonicalFile(), file.delete() ? "OK\n" : "KO\n");
	   }
	}
   }

}
