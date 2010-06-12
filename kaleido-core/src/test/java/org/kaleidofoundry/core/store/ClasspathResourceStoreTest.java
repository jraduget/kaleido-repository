package org.kaleidofoundry.core.store;

import java.net.URI;

import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author Jerome RADUGET
 */
public class ClasspathResourceStoreTest extends AbstractResourceStoreTest {

   @Before
   @Override
   public void setup() throws Throwable {

	super.setup();

	resourceStore = new ClasspathResourceStore(new RuntimeContext<ResourceStore>());

	existingResources.put(new URI("classpath:/org/kaleidofoundry/core/store/foo.txt"), DEFAULT_RESOURCE_MOCK_TEST);
	existingResources.put(new URI("classpath://org/kaleidofoundry/core/store/foo.txt"), DEFAULT_RESOURCE_MOCK_TEST);

	nonExistingResources.add(new URI("classpath:/org/kaleidofoundry/core/store/foo"));
   }

}
