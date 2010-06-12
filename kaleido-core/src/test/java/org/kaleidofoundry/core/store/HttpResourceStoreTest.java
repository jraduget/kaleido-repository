package org.kaleidofoundry.core.store;

import java.net.URI;

import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author Jerome RADUGET
 */
public class HttpResourceStoreTest extends AbstractResourceStoreTest {

   @Before
   @Override
   public void setup() throws Throwable {
	super.setup();
	resourceStore = new HttpResourceStore(new RuntimeContext<ResourceStore>());

	existingResources.put(new URI("http://localhost/foo.txt"), DEFAULT_RESOURCE_MOCK_TEST);
	nonExistingResources.add(new URI("http://localhost/foo"));
   }

}
