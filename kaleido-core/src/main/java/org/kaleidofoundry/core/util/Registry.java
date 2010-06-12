/*
 * $License$
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
 * Contains liste of couple <name, class instance>
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@ThreadSafe
public class Registry<T> extends ConcurrentHashMap<String, T> implements ConcurrentMap<String, T> {

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
   public Set<Entry<String, T>> entrySet() {
	return Collections.unmodifiableSet(super.entrySet());
   }

   /**
    * Set return ware not modifiable
    * 
    * @see java.util.HashMap#keySet()
    */
   @Override
   public Set<String> keySet() {
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
