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
package org.kaleidofoundry.core.context;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * {@link Context} creator helper
 * 
 * @author jraduget
 */
public abstract class ContextHelper {

   /**
    * @param name
    * @return new Context instance
    */
   public static Context createContext(final String name) {
	return createContext(name, new Parameter[] {});
   }

   /**
    * @param name
    * @param parameters
    * @return new Context instance
    */
   public static Context createContext(final String name, final Parameter[] parameters) {
	return createContext(name, true, parameters);
   }

   /**
    * @param name
    * @param dynamics
    * @param parameters
    * @return new Context instance
    */
   public static Context createContext(final String name, final boolean dynamics, final Parameter[] parameters) {
	return createContext(name, dynamics, parameters, new String[] {});
   }

   /**
    * @param name
    * @param dynamics
    * @param parameters
    * @param configurations
    * @return new Context instance
    */
   public static Context createContext(final String name, final boolean dynamics, final Parameter[] parameters, final String[] configurations) {
	return new Context() {

	   @Override
	   public Class<? extends Annotation> annotationType() {
		return Context.class;
	   }

	   @Override
	   public String value() {
		return name;
	   }

	   @Override
	   public Parameter[] parameters() {
		return parameters;
	   }

	   @Override
	   public boolean dynamics() {
		return dynamics;
	   }

	   @Override
	   public String[] configurations() {
		return configurations;
	   }

	};
   }

   /**
    * @param name
    * @param value
    * @param type
    * @return new Parameter instance
    */
   public static Parameter createParameter(final String name, final String value, final Class<? extends Serializable> type) {
	return new Parameter() {

	   @Override
	   public Class<? extends Annotation> annotationType() {
		return Parameter.class;
	   }

	   @Override
	   public String name() {
		return name;
	   }

	   @Override
	   public String value() {
		return value;
	   }

	   @Override
	   public Class<? extends Serializable> type() {
		return type;
	   }

	};
   }
}
