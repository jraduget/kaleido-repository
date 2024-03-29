/*  
 * Copyright 2008-2021 the original author or authors 
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
import java.io.InputStream;
import java.io.Reader;

/**
 * Io {@link Iterable} introduce a close method, in order to use it on {@link InputStream}, {@link Reader}, ...
 * 
 * @author jraduget
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
