package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.GuiceResourceStoreConstants.FtpStorePluginName;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

import com.google.inject.Inject;

/**
 * @author Jérôme RADUGET
 */
@DeclarePlugin(FtpStorePluginName)
public class GuiceFtpResourceStore extends FtpResourceStore {

   @Inject
   public GuiceFtpResourceStore(final RuntimeContext<ResourceStore> context) {
	super(context);
   }
}
