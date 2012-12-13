package org.kaleidofoundry.sample.filestore;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.launcher.junit.KaleidoCdiJunit4ClassRunner;

@RunWith(KaleidoCdiJunit4ClassRunner.class)
@NamedConfiguration(name = "filestore", uri = "classpath:/configurations/filestore.yaml")
public class FileStoreSample {

   @Inject
   @Context
   protected FileStore yourStore;

   @Test
   public void get() throws IOException {

	// resource path are locally to the store base uri
	ResourceHandler resource = yourStore.get("path/foo.txt");

	// get its informations
	System.out.printf("path: %s\n", resource.getResourceUri());
	System.out.printf("mimeType: %s\n", resource.getMimeType());
	System.out.printf("charset: %s\n", resource.getCharset());
	System.out.printf("lastModified: %tc\n", new Date(resource.getLastModified()));
	// get its contents
	System.out.printf("content (text): %s\n", resource.getText());
	System.out.printf("content (bytes): %s\n", String.valueOf(resource.getBytes()));
   }

}
