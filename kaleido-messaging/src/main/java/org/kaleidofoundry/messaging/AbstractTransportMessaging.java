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
package org.kaleidofoundry.messaging;

/**
 * Abstract TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractTransportMessaging implements TransportMessaging {

   private String code;
   private String version;
   private TransportMessagingContext context;

   /**
    * @param context
    * @throws TransportMessagingException
    */
   public AbstractTransportMessaging(final TransportMessagingContext context) throws TransportMessagingException {
	if (context == null) throw new IllegalArgumentException();

	this.context = context;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#getCode()
    */
   public String getCode() {
	return code;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#getVersion()
    */
   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#getVersion()
    */
   public String getVersion() {
	return version;
   }

   public void setCode(final String code) {
	this.code = code;
   }

   public void setVersion(final String version) {
	this.version = version;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#getContext()
    */
   public TransportMessagingContext getContext() {
	return context;
   }

   public void setContext(final TransportMessagingContext context) {
	this.context = context;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#isBinaryMessage()
    */
   public boolean isBinaryMessage() {
	return getContext().isBinaryMessage();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#isXmlMessage()
    */
   public boolean isXmlMessage() {
	return getContext().isXmlMessage();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#isJavaBeanMessage()
    */
   public boolean isJavaBeanMessage() {
	return getContext().isJavaBeanMessage();
   }
}
