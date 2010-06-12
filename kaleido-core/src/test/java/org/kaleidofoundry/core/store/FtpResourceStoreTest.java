package org.kaleidofoundry.core.store;

import java.net.URI;

import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author Jerome RADUGET
 */
public class FtpResourceStoreTest extends AbstractResourceStoreTest {

   @Before
   @Override
   public void setup() throws Throwable {
	super.setup();
	resourceStore = new FtpResourceStore(new RuntimeContext<ResourceStore>());

	// anonymous account : ftp://hostname/resourcepath
	// account : ftp://username:password@hostname/resourcepath
	existingResources.put(new URI("ftp://localhost/foo.txt"), DEFAULT_RESOURCE_MOCK_TEST);

	nonExistingResources.add(new URI("ftp://localhost/foo"));
   }

}
