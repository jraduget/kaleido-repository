package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.GuiceResourceStoreConstants.ClasspathStorePluginName;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

import com.google.inject.Inject;

/**
 * @author Jérôme RADUGET
 */
@DeclarePlugin(ClasspathStorePluginName)
public class GuiceClasspathResourceStore extends ClasspathResourceStore {

   @Inject
   public GuiceClasspathResourceStore(final RuntimeContext<ResourceStore> context) {
	super(context);
   }

}
