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
package org.kaleidofoundry.core.i18n;

import java.util.ResourceBundle;

/**
 * @author Jerome RADUGET
 * @see ResourceBundle.Control
 */
public enum MessageBundleControlFormat {

   /** Standard JDK Properties file loader */
   STANDARD_PROPERTIES("properties"),
   /** Properties xml file loader control */
   XML_PROPERTIES("properties.xml"),
   /** Properties jpa database control -> properties store exploded in table line */
   JPA_ENTITY_PROPERTIES("entity.jpa"),
   /** Properties jpa database control -> properties store in a clob */
   // JPA_CLOB_PROPERTIES("clob.jpa")
   ;

   private String extention;

   MessageBundleControlFormat(final String extention) {
	this.extention = extention;
   }

   /**
    * @return resource properties extensions
    */
   public String getExtention() {
	return extention;
   }

}
