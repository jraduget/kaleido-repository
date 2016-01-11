/*
 *  Copyright 2008-2016 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolver;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.FinalizationException;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;
import com.google.appengine.api.files.LockException;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalFileServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * https://developers.google.com/appengine/docs/java/tools/localunittesting
 * https://developers.google.com/appengine/docs/java/tools/localunittesting/javadoc/com/google/appengine/tools/development/testing/
 * LocalServiceTestConfig
 * 
 * @author jraduget
 */
public class GaeFileStoreTest extends AbstractFileStoreTest {

   protected static LocalServiceTestHelper helper;
   protected static FileService fileService;
   // protected static BlobstoreService blobstoreService;

   protected static MimeTypeResolver mimeTypeResolver;

   @BeforeClass
   public static void init() throws IOException {
	helper = new LocalServiceTestHelper(new LocalFileServiceTestConfig(), new LocalBlobstoreServiceTestConfig());
	// helper.setEnforceApiDeadlines(false);
	// helper.setEnvIsLoggedIn(true);
	// helper.setEnvIsAdmin(true);
	// helper.setSimulateProdLatencies(false);
	helper.setUp();

	fileService = FileServiceFactory.getFileService();
	// blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	mimeTypeResolver = MimeTypeResolverFactory.getService();
   }

   @AfterClass
   public static void tearDown() {
	helper.tearDown();
   }

   @Before
   public void setUp() throws FileNotFoundException, FinalizationException, LockException, IOException, InterruptedException {

	final String baseUri = "gs:/kaleido/store/test";
	final String bucketName = baseUri.replace("gs:/", "");
	final RuntimeContext<FileStore> context = new FileStoreContextBuilder("gaeFileStore").withBaseUri(baseUri).build();
	fileStore = new GaeFileStore(context);

	// get
	existingResources.put("gs:/kaleido/store/test/path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);
	existingResources.put("path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);
	existingResources.put("/path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);
	existingResources.put("path/foo2.dat", "123456789azertyuiopqsdfghjklmwxcvbn123456789azertyu" +
			"iopqsdfghjklmwxcvbn123456789azertyuiopqsdfghjklmwxcvbn123456789azertyuiopqsdfghj" +
			"klmwxcvbn123456789azertyuiopqsdfghjklmwxcvbn123456789azertyuiopqsdfghjklmwxcvbn1234" +
			"56789azertyuiopqsdfghjklmwxcvbn123456789azertyuiopqsdfghjklmwxcvbn123456" +
			"789azertyuiopqsdfghjklmwxcvbn");

	// not exists
	nonExistingResources.add("path/notexist.txt");
	nonExistingResources.add("gs:/kaleido/store/test/path/notexist.txt");

	// store
	existingResourcesForStore.put("newpath/newfoo.txt", "line1\nline2\nline3");
	
	// remove
	existingResourcesForRemove.put("path/foo.txt", DEFAULT_RESOURCE_MOCK_TEST);

	for (Entry<String, String> entryToTest : existingResources.entrySet()) {
	   // fileStore.store(entryToTest.getKey(), entryToTest.getValue());

	   String mimeType = mimeTypeResolver.getMimeType(FileHelper.getFileNameExtension(entryToTest.getKey()));

	   // write datas used by the test
	   GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder().setBucket(bucketName).setKey(entryToTest.getKey());
	   if (mimeType != null) {
		optionsBuilder.setMimeType(mimeType);
	   }
	   optionsBuilder.setContentEncoding("UTF-8");
	   optionsBuilder.setAcl("public-read");
	   AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());

	   FileWriteChannel writeChannel = fileService.openWriteChannel(writableFile, true);
	   if (mimeTypeResolver.isText(mimeType)) {
		PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF-8"));
		out.write(entryToTest.getValue());
		out.close();		
	   } else {
		InputStream in = new ByteArrayInputStream(entryToTest.getValue().getBytes());
		final byte[] buff = new byte[64];
		int lengthToWrite = in.read(buff);
		while (lengthToWrite != -1) {
		   writeChannel.write(ByteBuffer.wrap(buff, 0, lengthToWrite));
		   lengthToWrite = in.read(buff);
		}
	   }
	   writeChannel.closeFinally();
	}
   }

   @Override
   @Test
   public void loadNotFound() throws ResourceException {
	// Override the default due to a gae local testing defect :
	// http://code.google.com/p/googleappengine/issues/detail?id=8308
   }

   @Override
   @Test
   public void notExists() throws ResourceException {
	// Override the default due to a gae local testing defect :
	// http://code.google.com/p/googleappengine/issues/detail?id=8308
   }

   @Override
   @Test
   public void store() throws ResourceException {
	// Override the default due to a gae local testing defect :
	// http://code.google.com/p/googleappengine/issues/detail?id=8308
   }

   @Override
   @Test
   public void move() throws ResourceException {
	// Override the default due to a gae local testing defect :
	// http://code.google.com/p/googleappengine/issues/detail?id=8308
   }
   
   @Override
   @Test
   public void remove() throws ResourceException {
	// Override the default due to a gae local testing defect :
	// http://code.google.com/p/googleappengine/issues/detail?id=8308
   }
}
