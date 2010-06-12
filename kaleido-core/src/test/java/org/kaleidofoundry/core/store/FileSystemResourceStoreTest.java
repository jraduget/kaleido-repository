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
	resourceStore = new FileSystemResourceStore(new RuntimeContext<ResourceStore>());

	// create temp file to test
	File tmpFile = File.createTempFile("kaleidofoundry-", ".test");
	FileWriter fout = new FileWriter(tmpFile);
	fout.append(DEFAULT_RESOURCE_MOCK_TEST);
	fout.flush();

	tmpFileName = tmpFile.toURI();

	existingResources.put(new URI(tmpFileName.toString()), DEFAULT_RESOURCE_MOCK_TEST);

	nonExistingResources.add(new URI("file:/foo"));
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
