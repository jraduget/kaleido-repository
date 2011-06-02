/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.kaleidofoundry.core.io.Tail.TailLine;

/**
 * Helper methods for {@link Tail} class
 * 
 * @author Jerome RADUGET
 */
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
   public static List<TailLine> tail(final String resourcePath, final long beginLine, final long lastLine) throws IOException, URISyntaxException {
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
   public static List<TailLine> tail(final ClassLoader loader, final String classPathResource, final long beginLine, final long lastLine) throws IOException {
	return new Tail(loader, classPathResource).tail(beginLine, lastLine);
   }

}
