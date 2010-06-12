/*
 * $License$
 */
package org.kaleidofoundry.core.store;

import java.util.Locale;

/**
 * @author Jerome RADUGET
 */
public class ResourceNotFoundException extends StoreException {

   private static final long serialVersionUID = -3825066742298741103L;

   /**
    * @param resourceId
    * @param locale
    */
   public ResourceNotFoundException(final String resourceId, final Locale locale) {
	super("store.resource.notfound", locale, resourceId);
   }

   /**
    * @param resourceId
    */
   public ResourceNotFoundException(final String resourceId) {
	super("store.resource.notfound", resourceId);
   }

   /**
    * @param resourceId
    * @param cause
    * @param locale
    */
   public ResourceNotFoundException(final String resourceId, final Throwable cause, final Locale locale) {
	super("store.resource.notfound", cause, locale, resourceId);
   }

   /**
    * @param resourceId
    * @param cause
    */
   public ResourceNotFoundException(final String resourceId, final Throwable cause) {
	super("store.resource.notfound", cause, resourceId);
   }

}
