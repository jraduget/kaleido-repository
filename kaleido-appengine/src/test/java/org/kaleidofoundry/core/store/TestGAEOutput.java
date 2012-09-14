package org.kaleidofoundry.core.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalFileServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class TestGAEOutput {

   protected static LocalServiceTestHelper helper;
   protected static FileService fileService;

   @BeforeClass
   public static void init() throws IOException {
	helper = new LocalServiceTestHelper(new LocalFileServiceTestConfig(), new LocalBlobstoreServiceTestConfig());
	
	helper.setUp();

	fileService = FileServiceFactory.getFileService();

	GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder().setBucket("mybucket").setKey("myfile").setMimeType("text/html");//.setAcl("public_read");
	// .addUserMetadata("myfield1", "my field value");
	AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());
	// Open a channel to write to it
	boolean lock = false;
	FileWriteChannel writeChannel = fileService.openWriteChannel(writableFile, lock);
	
	writeChannel.write(ByteBuffer.wrap("The woods are lovely dark and deep.".getBytes()));
	
	
	String path = writableFile.getFullPath();

	// Write more to the file in a separate request:
	writableFile = new AppEngineFile(path);

	// This time lock because we intend to finalize
	lock = true;
	writeChannel = fileService.openWriteChannel(writableFile, lock);

	// This time we write to the channel directly.
	writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep.".getBytes()));

	// Now finalize
	writeChannel.closeFinally();
	// At this point the file is visible in App Engine as:
	// "/gs/mybucket/myfile"
	// and to anybody on the Internet through Google Storage as:
	// (http://commondatastorage.googleapis.com/mybucket/myfile)
	// So reading it through Files API:
	String filename = "/gs/mybucket/myfile";
	AppEngineFile readableFile = new AppEngineFile(filename);
	FileReadChannel readChannel = fileService.openReadChannel(readableFile, false);

	System.out.println(new BufferedReader(new InputStreamReader(Channels.newInputStream(readChannel))).readLine());
	// line = "The woods are lovely dark and deep."
	readChannel.close();

   }

   @Test
   public void test() {

   }

}
