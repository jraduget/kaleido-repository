package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Semantic for stateless service
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface Stateless {
}
