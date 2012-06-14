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
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;

/**
 * @author Jerome RADUGET
 */
public class FileSystemStoreTest extends AbstractFileStoreTest {

   private final List<File> filesToDelete = new ArrayList<File>();

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Before
   @Override
   public void setup() throws Throwable {
	super.setup();

	// create a temporary file, in order to get the temp directory
	final File tmpFile = folder.newFile("kaleido-store-get.test");
	final String tempPath = tmpFile.getCanonicalPath().substring(0, tmpFile.getCanonicalPath().lastIndexOf(File.separator));
	filesToDelete.add(tmpFile);

	final RuntimeContext<FileStore> context = new FileStoreContextBuilder().withBaseUri("file:/" + FileHelper.buildUnixAppPath(tempPath)).build();
	fileStore = new FileSystemStore(context);

	// 1. existing resources (to get) - create temp file as mock
	final File tmpFileToGet = folder.newFile("kaleido-resource-get.test");
	FileWriter fout = new FileWriter(tmpFileToGet);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();
	filesToDelete.add(tmpFileToGet);
	fout.close();
	existingResources.put(FileHelper.getFileName(tmpFileToGet.getCanonicalPath()), DEFAULT_RESOURCE_MOCK_TEST);

	// 2. resources to get (but which not exists)
	nonExistingResources.add("foo");

	// 3. resources to store
	final File tmpFileToStore = folder.newFile("kaleido-resource-store.test");
	final String filenameToStore = FileHelper.getFileName(tmpFileToStore.getCanonicalPath());
	filesToDelete.add(tmpFileToStore);
	tmpFileToStore.delete();
	existingResourcesForStore.put(filenameToStore, DEFAULT_RESOURCE_MOCK_TEST);

	// 4. resources to remove
	final File tmpFileToRemmove = folder.newFile("kaleido-resource-remove.test");
	fout = new FileWriter(tmpFileToRemmove);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();
	filesToDelete.add(tmpFileToRemmove);
	fout.close();
	existingResources.put(FileHelper.getFileName(tmpFileToRemmove.getCanonicalPath()), DEFAULT_RESOURCE_MOCK_TEST);

	// 5. resources to move
	final File tmpFileToMove = folder.newFile("kaleido-resource-move.test");
	fout = new FileWriter(tmpFileToMove);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();
	filesToDelete.add(tmpFileToMove);
	fout.close();

	final String fileToMovePath = FileHelper.getFileName(tmpFileToMove.getCanonicalPath());
	existingResourcesForMove.put(fileToMovePath, fileToMovePath + ".move");
	// TODO move with a new directory that not exists

   }

   @After
   @Override
   public void cleanup() throws Throwable {
	super.cleanup();

	// for (final File file : filesToDelete) {
	// if (file.exists()) {
	// System.out.printf("cleanup resources - deleted \"%s\" is %s", file.getCanonicalFile(), file.delete() ? "OK\n" : "KO\n");
	// }
	// }
   }

   @Test
   public void baseDirTest() throws ResourceException {
	FileStore fileStore = FileStoreFactory.provides("file:/${basedir}");
	ResourceHandler resourceHandler = fileStore.get("src/test/resources/store/foo.txt");
	assertNotNull(resourceHandler);
	assertNotNull(resourceHandler.getResourceUri());
	assertEquals("line1\nline2", resourceHandler.getText());
   }

   @Test
   public void currentDirTest() throws ResourceException {
	FileStore fileStore = FileStoreFactory.provides("file:/.");
	ResourceHandler resourceHandler = fileStore.get("src/test/resources/store/foo.txt");
	assertNotNull(resourceHandler);
	assertNotNull(resourceHandler.getResourceUri());
	assertEquals("line1\nline2", resourceHandler.getText());
   }

   @Test
   public void parentDirTest() throws ResourceException {
	FileStore fileStore = FileStoreFactory.provides("file:/..");
	ResourceHandler resourceHandler = fileStore.get("kaleido-core/src/test/resources/store/foo.txt");
	assertNotNull(resourceHandler);
	assertNotNull(resourceHandler.getResourceUri());
	assertEquals("line1\nline2", resourceHandler.getText());
   }

}
