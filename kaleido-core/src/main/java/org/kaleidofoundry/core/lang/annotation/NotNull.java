package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.lang.NotNullException;

/**
 * Semantic "is not null" for a method parameter or method result<br/>
 * <ul>
 * <li>You can annotated method parameter with {@link NotNull}, so if annotated parameter is null when method is call, a
 * {@link NotNullException} will be thrown (use aspect)
 * <li>You can annotated method with {@link NotNull}, so if method return null, a {@link NotNullException} will be thrown (use aspect)
 * </ul>
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
}
