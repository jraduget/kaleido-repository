package org.kaleidofoundry.core.store.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.store.JpaResourceStore;
import org.kaleidofoundry.core.store.ResourceStore;

import com.google.inject.BindingAnnotation;

/**
 * Guice binding annotation for binding a {@link ResourceStore} to {@link JpaResourceStore}
 * 
 * @author Jerome RADUGET
 */
@Target( { FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
@BindingAnnotation
public @interface Jpa {

}
