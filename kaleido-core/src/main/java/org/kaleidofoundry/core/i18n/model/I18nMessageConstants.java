/*  
 * Copyright 2008-2014 the original author or authors 
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
package org.kaleidofoundry.core.i18n.model;

/**
 * Constants for {@link I18nMessage}
 * 
 * @author jraduget
 */
public interface I18nMessageConstants {

   /** {@link I18nMessage} entity table name */
   String Table_I18nMessage = "I18N_ENTRY";
   /** {@link I18nMessageLanguage} entity table name */
   String Table_I18nMessageLanguage = "I18N_LANGUAGE";
   /** {@link I18nMessageGroup} entity table name */
   String Table_I18nMessageGroupe = "I18N_GROUP";

   /**
    * Query static final informations
    */
   public static interface Query_MessagesByLocale {
	String Name = "i18n.findMessagesByLanguageIso";
	String Parameter_ResourceName = "resourceName";
	String Parameter_Locale = "localeId";
	String Jql = "select ml from I18nMessageLanguage ml, I18nMessage m where ml.message.id=m.id and m.group.code = :" + Parameter_ResourceName
		+ " and ml.localeId = :" + Parameter_Locale;
   }

   /** Default message group */
   I18nMessageGroup DefaultMessageGroup = new I18nMessageGroup("DEFAULT");

}
