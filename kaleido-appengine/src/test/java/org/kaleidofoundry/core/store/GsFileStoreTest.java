/*
 *  Copyright 2008-2012 the original author or authors.
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
package org.kaleidofoundry.core.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.kaleidofoundry.core.context.RuntimeContext;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FinalizationException;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;
import com.google.appengine.api.files.LockException;
import com.google.appengine.api.files.RecordWriteChannel;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalFileServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * https://developers.google.com/appengine/docs/java/tools/localunittesting
 * 
 * @author Jerome RADUGET
 */
@Ignore
public class GsFileStoreTest extends AbstractFileStoreTest {

   protected static LocalServiceTestHelper helper;
   protected static FileService fileService;
   protected static BlobstoreService blobstoreService;

   @BeforeClass
   public static void init() throws IOException {
	helper = new LocalServiceTestHelper(new LocalFileServiceTestConfig(), new LocalBlobstoreServiceTestConfig());
	helper.setUp();

	fileService = FileServiceFactory.getFileService();
	blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
   }

   @AfterClass
   public static void tearDown() {
	helper.tearDown();
   }

   @Before
   public void setUp() throws FileNotFoundException, FinalizationException, LockException, IOException {

	final String bucketName = "gs:/kaleido/store/test";
	final RuntimeContext<FileStore> context = new FileStoreContextBuilder().withBaseUri(bucketName).build();
	fileStore = new GSFileStore(context);

	existingResources.put("path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);
	// existingResources.put("/path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);

	nonExistingResources.add("gs:/path/foo");
	nonExistingResources.add("path/foo");

	existingResourcesForStore.put("newpath/newfoo.txt", "line1\nline2\nline3");
	existingResourcesForRemove.put("path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);

	for (Entry<String, String> entryToTest : existingResources.entrySet()) {
	   // fileStore.store(entryToTest.getKey(), entryToTest.getValue());

	   // write datas used by the test	   
	   GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder().setBucket(bucketName).setKey(entryToTest.getKey());
	   optionsBuilder.setMimeType("text/plain");
	   optionsBuilder.setContentEncoding("UTF8");
	   final AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());
	   RecordWriteChannel writeChannel = fileService.openRecordWriteChannel(writableFile, true);
	   PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
	   out.write(entryToTest.getValue());
	   out.close();
	   writeChannel.closeFinally();
	}
   }

}
