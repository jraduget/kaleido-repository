/*
 *  Copyright 2008-2021 the original author or authors.
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
package org.kaleidofoundry.core.config;

import java.util.Locale;

/**
 * @author jraduget
 */
public class ConfigurationNotFoundException extends ConfigurationException {

   private static final long serialVersionUID = -545934800514981835L;

   /**
    * @param configurationName
    */
   public ConfigurationNotFoundException(final String configurationName) {
	this(null, configurationName);
   }

   /**
    * @param locale
    * @param configurationName
    */
   public ConfigurationNotFoundException(final Locale locale, final String configurationName) {
	super("config.notfound.error", locale, configurationName);
   }

}
