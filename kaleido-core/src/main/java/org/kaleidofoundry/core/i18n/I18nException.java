/*
 *  Copyright 2008-2011 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.i18n;

import java.io.Serializable;
import java.util.Locale;

import org.kaleidofoundry.core.lang.ErrorCode;

/**
 * <b>I18n exception</b><br/>
 * <p>
 * An i18n exception is used to identified precisely an exception by a code and a i18n message.<br/>
 * <p>
 * 
 * @see I18nMessagesFactory
 * @author Jerome RADUGET
 */
public interface I18nException extends ErrorCode, Serializable {

   /**
    * @return Token parameters passed for the template message
    */
   String[] getParameters();

   /**
    * @return specified locale to use to get i18n exception message
    */
   Locale getLocale();

   /**
    * @return i18n message
    */
   String getMessage();
}
