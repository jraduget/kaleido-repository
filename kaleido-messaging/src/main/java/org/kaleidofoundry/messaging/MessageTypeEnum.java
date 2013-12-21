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
package org.kaleidofoundry.messaging;

/**
 * Type of a message, with the class implementation mapping
 * 
 * @author jraduget
 */
public enum MessageTypeEnum {

   /** Xml Message */
   Xml("xml", XmlMessage.class),
   /** TextMessage */
   Text("txt", TextMessage.class),
   /** Binary Message */
   Bytes("bytes", BytesMessage.class),
   /** JavaBean Message */
   JavaBean("bean", JavaBeanMessage.class),
   /** Map parameters message */
   Default("default", BaseMessage.class);

   MessageTypeEnum(final String code, final Class<? extends Message> implementation) {
	this.code = code;
	this.implementation = implementation;
   }

   private final String code;
   private final Class<? extends Message> implementation;

   public Class<? extends Message> getImplementation() {
	return implementation;
   }

   public String getCode() {
	return code;
   }

}
