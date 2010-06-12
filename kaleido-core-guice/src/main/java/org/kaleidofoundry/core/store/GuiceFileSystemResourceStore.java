/*
 * $License$
 */
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.GuiceResourceStoreConstants.FileSystemStorePluginName;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

import com.google.inject.Inject;

/**
 * @author Jérôme RADUGET
 */
@DeclarePlugin(FileSystemStorePluginName)
public class GuiceFileSystemResourceStore extends FileSystemResourceStore {

   /**
    * @param context
    */
   @Inject
   public GuiceFileSystemResourceStore(final RuntimeContext<ResourceStore> context) {
	super(context);
   }

}
