package org.kaleidofoundry.core.store;

import java.io.IOException;
import java.io.InputStream;

import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * Default {@link ResourceHandler} implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
public class ResourceHandlerBean implements ResourceHandler {

   private final InputStream input;

   /**
    * @param input
    */
   public ResourceHandlerBean(@NotNull final InputStream input) {
	this.input = input;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStream()
    */
   @Override
   @NotNull
   public InputStream getInputStream() {
	return input;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#release()
    */
   @Override
   public void release() {
	if (input != null) {
	   try {
		input.close();
	   } catch (IOException ioe) {
		throw new IllegalStateException("can't close inputstream: " + ioe.getMessage(), ioe);
	   }
	}
   }

}
