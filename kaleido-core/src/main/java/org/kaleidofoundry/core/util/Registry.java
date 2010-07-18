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
package org.kaleidofoundry.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Registry pattern, implement with a {@link ConcurrentHashMap} <br/>
 * Contains list of couple <name, class instance>
 * 
 * @author Jerome RADUGET
 * @param <K>
 * @param <T>
 */
@ThreadSafe
public class Registry<K, T> extends ConcurrentHashMap<K, T> implements ConcurrentMap<K, T> {

   private static final long serialVersionUID = -2537287170029457353L;

   /**
    * default constructor
    */
   public Registry() {
   }

   /**
    * Set return ware not modifiable
    * 
    * @see java.util.concurrent.ConcurrentHashMap#entrySet()
    */
   @Override
   public Set<Entry<K, T>> entrySet() {
	return Collections.unmodifiableSet(super.entrySet());
   }

   /**
    * Set return ware not modifiable
    * 
    * @see java.util.HashMap#keySet()
    */
   @Override
   public Set<K> keySet() {
	return Collections.unmodifiableSet(super.keySet());
   }

   /**
    * Collection return ware not modifiable
    * 
    * @see java.util.HashMap#values()
    */
   @Override
   public Collection<T> values() {
	return Collections.unmodifiableCollection(super.values());
   }

}
