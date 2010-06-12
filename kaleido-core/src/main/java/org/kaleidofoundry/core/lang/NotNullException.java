package org.kaleidofoundry.core.lang;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nRuntimeException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;
import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * <ul>
 * <li>You can annotated method parameter with {@link NotNull}, so if annotated parameter is null when method is call, a
 * {@link NotNullException} will be thrown (use aspect)
 * <li>You can annotated method with {@link NotNull}, so if method return null, a {@link NotNullException} will be thrown (use aspect)
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public class NotNullException extends I18nRuntimeException {

   private static final long serialVersionUID = -3933830352293530392L;

   public final static String ERROR_NotNullArgument = "exception.notnull.argument";
   public final static String ERROR_NotNullReturn = "exception.notnull.return";

   /**
    * @param methodSignature
    * @param parameterName
    * @param callerLocation
    * @param callerInfo
    */
   public NotNullException(final String methodSignature, final String parameterName, final String callerInfo, final String callerLocation) {
	super(ERROR_NotNullArgument, methodSignature, parameterName, callerInfo, callerLocation);
   }

   /**
    * @param methodSignature
    * @param parameterName
    * @param callerLocation
    * @param callerInfo
    * @param locale
    */
   public NotNullException(final String methodSignature, final String parameterName, final String callerInfo, final String callerLocation, final Locale locale) {
	super(ERROR_NotNullArgument, locale, methodSignature, parameterName, callerInfo, callerLocation);
   }

   /**
    * @param methodSignature
    * @param callerInfo
    * @param callerLocation
    */
   public NotNullException(final String methodSignature, final String callerInfo, final String callerLocation) {
	super(ERROR_NotNullReturn, methodSignature, callerInfo, callerLocation);
   }

   /**
    * @param methodSignature
    * @param callerInfo
    * @param callerLocation
    * @param locale
    */
   public NotNullException(final String methodSignature, final String callerInfo, final String callerLocation, final Locale locale) {
	super(ERROR_NotNullReturn, locale, methodSignature, callerInfo, callerLocation);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.exception.I18nCodedRuntimeException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.Core.getResourceName();
   }

}
