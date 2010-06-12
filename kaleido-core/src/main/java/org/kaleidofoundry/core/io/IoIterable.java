package org.kaleidofoundry.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Io {@link Iterable} introduce a close method, in order to use it on {@link InputStream}, {@link Reader}, ...
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
public interface IoIterable<T> extends Iterable<T> {

   /**
    * close it when you have finish to use your {@link Iterable}
    * 
    * @throws IOException
    */
   public void close() throws IOException;

}
