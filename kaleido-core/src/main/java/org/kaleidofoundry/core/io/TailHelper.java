/*
 * $License$
 */
package org.kaleidofoundry.core.io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.kaleidofoundry.core.io.Tail.TailLine;
import org.kaleidofoundry.core.lang.annotation.Tested;

/**
 * Helper methods for {@link Tail} class
 * 
 * @author Jerome RADUGET
 */
@Tested
public abstract class TailHelper {

   /**
    * @param resourcePath
    * @param lastLine
    * @return last n line of the buffer
    * @throws IOException
    * @throws URISyntaxException
    * @see Tail
    */
   public static List<TailLine> tail(final String resourcePath, final long lastLine) throws IOException, URISyntaxException {
	return new Tail(resourcePath).tail(lastLine);
   }

   /**
    * @param resourcePath
    * @param beginLine
    * @param lastLine
    * @return line of the buffer comprised between beginLine and lastLine
    * @throws IOException
    * @throws URISyntaxException
    * @see Tail
    */
   public static List<TailLine> tail(final String resourcePath, final long beginLine, final long lastLine) throws IOException,
	   URISyntaxException {
	return new Tail(resourcePath).tail(beginLine, lastLine);
   }

   /**
    * @param loader class loader used to get the resource from the classpath
    * @param classPathResource
    * @param lastLine
    * @return last n line of the buffer
    * @throws IOException
    * @see Tail
    */
   public static List<TailLine> tail(final ClassLoader loader, final String classPathResource, final long lastLine) throws IOException {
	return new Tail(loader, classPathResource).tail(lastLine);
   }

   /**
    * @param loader class loader used to get the resource from the classpath
    * @param classPathResource
    * @param beginLine
    * @param lastLine
    * @return line of the buffer comprised between beginLine and lastLine
    * @throws IOException
    * @see Tail
    */
   public static List<TailLine> tail(final ClassLoader loader, final String classPathResource, final long beginLine, final long lastLine)
	   throws IOException {
	return new Tail(loader, classPathResource).tail(beginLine, lastLine);
   }

}
