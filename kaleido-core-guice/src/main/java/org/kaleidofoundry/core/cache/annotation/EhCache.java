package org.kaleidofoundry.core.cache.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.cache.EhCache1xImpl;

import com.google.inject.BindingAnnotation;

/**
 * Guice binding annotation for binding a {@link org.kaleidofoundry.core.cache.Cache} to {@link EhCache1xImpl}
 * 
 * @author Jerome RADUGET
 */
@Target( { FIELD, PARAMETER })
@Retention(RUNTIME)
@BindingAnnotation
public @interface EhCache {

   /** @return cache name */
   String name();
}
