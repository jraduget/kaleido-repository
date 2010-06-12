/*
 * $License$
 */
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.GuiceResourceStoreConstants.ClobJpaStorePluginName;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

import com.google.inject.Inject;

/**
 * @author Jérôme RADUGET
 */
@DeclarePlugin(ClobJpaStorePluginName)
public class GuiceJpaResourceStore extends JpaResourceStore {

   /**
    * @param context
    */
   @Inject
   public GuiceJpaResourceStore(final RuntimeContext<ResourceStore> context) {
	super(context);
   }

}
