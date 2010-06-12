package org.kaleidofoundry.core.ioc;

import java.lang.reflect.Field;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.context.RuntimeContextProvider;
import org.kaleidofoundry.core.context.annotation.Context;
import org.kaleidofoundry.core.lang.annotation.Review;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Guice {@link TypeListener} used for listening and injecting {@link RuntimeContext} to the annotated {@link Context} property / method /
 * ...
 * 
 * @author Jerome RADUGET
 * @see <a
 *      href="http://code.google.com/p/google-guice/wiki/CustomInjections">http://code.google.com/p/google-guice/wiki/CustomInjections</a>
 */
public class ContextTypeListener implements TypeListener {

   @Override
   public <T> void hear(final TypeLiteral<T> typeLiteral, final TypeEncounter<T> typeEncounter) {

	RuntimeContextProvider<T> runtimeContextProvider = new RuntimeContextProvider<T>();

	for (Field field : typeLiteral.getRawType().getDeclaredFields()) {

	   // scan RuntimeContext typed fields, annotated with @Context
	   if (field.getType() == RuntimeContext.class && field.isAnnotationPresent(Context.class)) {
		typeEncounter.register(new ContextMembersInjector<T>(field, runtimeContextProvider));
	   }
	}
   }

}

/**
 * Guice MembersInjector used by {@link TypeListener} for injecting {@link RuntimeContext}<br/>
 * 
 * @author Jerome RADUGET
 * @param <T>
 * @see <a
 *      href="http://code.google.com/p/google-guice/wiki/CustomInjections">http://code.google.com/p/google-guice/wiki/CustomInjections</a>
 */
class ContextMembersInjector<T> implements MembersInjector<T> {

   private final Field field;
   private final RuntimeContextProvider<T> runtimeContextProvider;

   ContextMembersInjector(final Field field, final RuntimeContextProvider<T> runtimeContextProvider) {
	this.field = field;
	this.field.setAccessible(true);
	this.runtimeContextProvider = runtimeContextProvider;
   }

   @Override
   @Review(comment = "error management with i18n error ?")
   public void injectMembers(final T t) {
	try {
	   field.set(t, runtimeContextProvider.provides(field));
	} catch (IllegalAccessException e) {
	   throw new RuntimeException(e);
	}
   }
}