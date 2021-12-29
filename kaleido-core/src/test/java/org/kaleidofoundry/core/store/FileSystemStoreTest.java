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
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.env.model.EnvironmentConstants.DEFAULT_BASE_DIR_PROPERTY;

import java.io.File;
import java.io.FileWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;

import static org.junit.Assert.*;

/**
 * @author jraduget
 */
public class FileSystemStoreTest extends AbstractFileStoreTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Before
   @Override
   public void setup() throws Throwable {
	super.setup();

	// create a temporary file, in order to get the temp directory
	final File tmpFile = folder.newFile("kaleido-store-get.test");
	final String tempPath = tmpFile.getCanonicalPath().substring(0, tmpFile.getCanonicalPath().lastIndexOf(File.separator));

	String tempPathUri = FileHelper.buildUnixAppPath(tempPath);
	tempPathUri = tempPath.startsWith("/") ? "file:" + tempPathUri : "file:/" + tempPathUri;
	final RuntimeContext<FileStore> context = new FileStoreContextBuilder("fsStore").withBaseUri(tempPathUri).build();
	fileStore = new FileSystemStore(context);

	// 1. existing resources (to get) - create temp file as mock
	final File tmpFileToGet = folder.newFile("kaleido-resource-get.test");
	FileWriter fout = new FileWriter(tmpFileToGet);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();
	fout.close();
	existingResources.put(FileHelper.getFileName(tmpFileToGet.getCanonicalPath()), DEFAULT_RESOURCE_MOCK_TEST);

	// 2. resources to get (but which not exists)
	nonExistingResources.add("foo");

	// 3. resources to store
	final File tmpFileToStore = folder.newFile("kaleido-resource-store.test");
	final String filenameToStore = FileHelper.getFileName(tmpFileToStore.getCanonicalPath());
	tmpFileToStore.delete();
	existingResourcesForStore.put(filenameToStore, DEFAULT_RESOURCE_MOCK_TEST);

	// 4. resources to remove
	final File tmpFileToRemmove = folder.newFile("kaleido-resource-remove.test");
	fout = new FileWriter(tmpFileToRemmove);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();
	fout.close();
	existingResources.put(FileHelper.getFileName(tmpFileToRemmove.getCanonicalPath()), DEFAULT_RESOURCE_MOCK_TEST);
	existingResourcesForRemove.put(FileHelper.getFileName(tmpFileToRemmove.getCanonicalPath()), DEFAULT_RESOURCE_MOCK_TEST);

	// 5. resources to move
	final File tmpFileToMove = folder.newFile("kaleido-resource-move.test");
	fout = new FileWriter(tmpFileToMove);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();
	fout.close();

	final String fileToMovePath = FileHelper.getFileName(tmpFileToMove.getCanonicalPath());
	existingResourcesForMove.put(fileToMovePath, fileToMovePath + ".move");
	// TODO move with a new directory that not exists

   }

   @After
   @Override
   public void cleanup() throws Throwable {
	folder.delete();
   }

   @Test
   public void baseDirTest() throws ResourceException {
	FileStoreProvider.init(null);
	FileStore fileStore = FileStoreFactory.provides("file:/${" + DEFAULT_BASE_DIR_PROPERTY + "}");
	ResourceHandler resourceHandler = fileStore.get("src/test/resources/store/foo.txt");
	assertNotNull(resourceHandler);
	assertNotNull(resourceHandler.getUri());
	assertEquals("line1\nline2", resourceHandler.getText());
   }

   @Test
   public void currentDirTest() throws ResourceException {
	FileStore fileStore = FileStoreFactory.provides("file:/.");
	ResourceHandler resourceHandler = fileStore.get("src/test/resources/store/foo.txt");
	assertNotNull(resourceHandler);
	assertNotNull(resourceHandler.getUri());
	assertEquals("line1\nline2", resourceHandler.getText());
   }

   @Test
   public void parentDirTest() throws ResourceException {
	FileStore fileStore = FileStoreFactory.provides("file:/..");
	ResourceHandler resourceHandler = fileStore.get("kaleido-core/src/test/resources/store/foo.txt");
	assertNotNull(resourceHandler);
	assertNotNull(resourceHandler.getUri());
	assertEquals("line1\nline2", resourceHandler.getText());
   }

}
