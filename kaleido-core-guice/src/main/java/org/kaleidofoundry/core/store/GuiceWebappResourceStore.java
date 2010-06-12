/*
 * $License$
 */
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.GuiceResourceStoreConstants.WebappStorePluginName;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

import com.google.inject.Inject;

/**
 * @author Jérôme RADUGET
 */
@DeclarePlugin(WebappStorePluginName)
public class GuiceWebappResourceStore extends WebappResourceStore {

   /**
    * @param context
    */
   @Inject
   public GuiceWebappResourceStore(final RuntimeContext<ResourceStore> context) {
	super(context);
   }

}
