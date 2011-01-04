/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    * @see org.kaleidofoundry.core.i18n.I18nRuntimeException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.CORE.getResourceName();
   }

}
