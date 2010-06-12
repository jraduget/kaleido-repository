package org.kaleidofoundry.core.store;

import java.io.InputStream;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * Represents a client resource binding<br/>
 * Once {@link ResourceStore} client have get its resource, he have to free it, by calling {@link #release()}<br/>
 * You'd better use {@link ResourceHandler} locally or with {@link ThreadLocal}, because instance will not be thread
 * safe (it handles an {@link InputStream})<br/>
 * 
 * @author Jerome RADUGET
 */
@NotThreadSafe
public interface ResourceHandler {

   /**
    * @return input stream of the resource
    */
   @NotNull
   InputStream getInputStream();

   /**
    * release resource and the eventual connections
    */
   void release();
}
