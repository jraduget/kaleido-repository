package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.GuiceResourceStoreConstants.HttpStorePluginName;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

import com.google.inject.Inject;

/**
 * @author Jérôme RADUGET
 */
@DeclarePlugin(HttpStorePluginName)
public class GuiceHttpResourceStore extends HttpResourceStore {

   @Inject
   public GuiceHttpResourceStore(final RuntimeContext<ResourceStore> context) {
	super(context);
   }

}
