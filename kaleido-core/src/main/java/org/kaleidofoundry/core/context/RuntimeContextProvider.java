package org.kaleidofoundry.core.context;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.RuntimeContextMessageBundle;

import java.lang.reflect.Field;

import javax.security.auth.login.Configuration;

import org.kaleidofoundry.core.context.annotation.Context;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Provide an independent {@link RuntimeContext} builder<br/>
 * <p>
 * A field / argument ... can be annotated by {@link Context} annotation<br/>
 * This annotation provides some information, like runtime context name, and optionally a configuration id<br/>
 * The methods of this provider will bound the given {@link Context} annotated field to the currents registered {@link Configuration},
 * by building and returning the {@link RuntimeContext} that will match
 * </p>
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@Immutable
public class RuntimeContextProvider<T> {

   /**
    * @param contextualAnnotation
    * @return runtime context instance which map with the context annotation argument
    */
   public RuntimeContext<T> provides(final @NotNull Context contextualAnnotation) {

	if (StringHelper.isEmpty(contextualAnnotation.name())) { throw new IllegalArgumentException(RuntimeContextMessageBundle
		.getMessage("context.annotation.name.empty")); }

	RuntimeContext<T> runtimeContext = new RuntimeContext<T>(contextualAnnotation.name());
	feedRuntimeContext(runtimeContext);
	return runtimeContext;
   }

   /**
    * @param annotatedField
    * @return new runtime context instance which map with the context annotation of the field argument
    */
   public RuntimeContext<T> provides(@NotNull final Field annotatedField) {

	Context contextAnnot = annotatedField.getAnnotation(Context.class);

	if (contextAnnot == null) { throw new IllegalArgumentException(RuntimeContextMessageBundle.getMessage("context.annotation.field.illegal",
		Context.class.getName())); }

	return provides(annotatedField.getAnnotation(Context.class));
   }

   /**
    * @param <R>
    * @param contextAnnot
    * @param runtimeContextArg
    * @return new instance of a RuntimeContext<R>
    */
   public <R> RuntimeContext<R> provides(final @NotNull Context contextAnnot, final @NotNull RuntimeContext<R> runtimeContextArg) {

	if (StringHelper.isEmpty(contextAnnot.name())) { throw new IllegalArgumentException(RuntimeContextMessageBundle
		.getMessage("context.annotation.name.empty")); }

	RuntimeContext<R> newRuntimeContext = new RuntimeContext<R>(contextAnnot.name());
	feedRuntimeContext(newRuntimeContext);
	return newRuntimeContext;
   }

   /**
    * feed {@link RuntimeContext} properties content, using current configuration
    * 
    * @param <R>
    * @param runtimeContext
    */
   protected <R> void feedRuntimeContext(final RuntimeContext<R> runtimeContext) {
	// TODO - bind it to implementation
	// runtimeContext.setProperty(property, value);
   }

}
