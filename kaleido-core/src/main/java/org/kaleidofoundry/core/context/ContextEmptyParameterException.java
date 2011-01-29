/*
 *  Copyright 2008-2010 the original author or authors.
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
package org.kaleidofoundry.core.context;

import java.util.Locale;

/**
 * {@link ContextException} used when an context parameter is not set
 * 
 * @author Jerome RADUGET
 */
public class ContextEmptyParameterException extends ContextIllegalParameterException {

   private static final long serialVersionUID = 4225599225267075709L;

   private final String parameter;

   /**
    * @param parameter name of the {@link RuntimeContext} parameter
    * @param context
    * @param locale
    */
   public ContextEmptyParameterException(final String parameter, final RuntimeContext<?> context, final Locale locale) {
	super("context.parameter.empty", locale, parameter, context.getName());
	this.parameter = parameter;
   }

   /**
    * @param parameter
    * @param context
    */
   public ContextEmptyParameterException(final String parameter, final RuntimeContext<?> context) {
	super("context.parameter.empty", parameter, context.getName());
	this.parameter = parameter;
   }

   /**
    * @return name of the parameter which cause the problem
    */
   public String getParameter() {
	return parameter;
   }

}
