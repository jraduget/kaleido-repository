package org.kaleidofoundry.core.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * A to string instance serializer interface
 * 
 * @author Jerome RADUGET
 */
public interface ToStringSerializer {

   /**
    * {@link Serializable} to String serialization
    * 
    * @param value instance to serialize
    * @param type interface type of the instance to serialize (if no interface, put the type of value argument)
    * @return String conversion of the requested object
    */
   <T extends Serializable> String serialize(final T value, @NotNull final Class<T> type);


   /**
    * String to {@link Serializable} instance deserialization
    * 
    * @param <T>
    * @param value string representation of value to deserialize
    * @param type interface of the instance to serialize (if no interface, put the type of value argument)
    * @return deserialization of the string argument
    * @throws IllegalStateException for date or number parse error
    */
   <T extends Serializable> T deserialize(final String value, @NotNull final Class<T> type) throws IllegalStateException;

   /**
    * List to String serialization
    * 
    * @param <T>
    * @param value collection instances to serialize
    * @param type interface of the instance to serialize (if no interface, put the type of value argument)
    * @return String conversion of the requested object list
    */
   <T extends Serializable> String serialize(final Collection<T> values, @NotNull final Class<T> type);

   /**
    * String to List<{@link Serializable}> instance deserialization
    * 
    * @param <T>
    * @param values string representation of values to deserialize
    * @param type interface type of the instance to serialize (if no interface, put the type of value argument)
    * @return deserialization list of the string argument
    */
   <T extends Serializable> List<T> deserializeToList(final String values, @NotNull final Class<T> type);

}