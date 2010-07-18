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
package org.kaleidofoundry.mail.session;

import java.util.ArrayList;
import java.util.List;


/**
 * Enumération des Implementation de MailSession Disponible
 * 
 * @author Jerome RADUGET
 */
public class MailSessionEnum {

   private static List<MailSessionEnum> dsEnum = new ArrayList<MailSessionEnum>();

   /** Implementation Envoi Via accès local au service mail */
   public static MailSessionEnum LOCALE = new MailSessionEnum(MailSessionConstants.LocalMailSessionType, LocalMailSessionService.class);

   /** Implementation Envoi Via accès Jndi au service mail */
   public static MailSessionEnum JNDI = new MailSessionEnum(MailSessionConstants.JndiMailSessionType, JndiMailSessionService.class);

   MailSessionEnum(final String key, final Class<? extends MailSessionService> impl) {
	this.key = key;
	this.implementationClass = impl;
	dsEnum.add(this);
   }

   private final String key;
   private final Class<? extends MailSessionService> implementationClass;

   public String getKey() {
	return key;
   }

   public Class<? extends MailSessionService> getImplementationClass() {
	return implementationClass;
   }

   @Override
   public String toString() {
	return key;
   }

   public static List<MailSessionEnum> getEnums() {
	return dsEnum;
   }
}