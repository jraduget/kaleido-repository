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

import java.util.Map;

import org.kaleidofoundry.core.lang.annotation.Task;

/**
 * A text message
 * 
 * @author jraduget
 */
@Task(comment = "handle charset accessor")
public class TextMessage extends AbstractMessage {

   private final String text;

   public TextMessage(final String text) {
	this(null, text, null);
   }

   public TextMessage(final String text, final Map<String, Object> parameters) {
	super(parameters);
	this.text = text;
   }

   public TextMessage(final String correlationId, final String text) {
	this(correlationId, text, null);
   }

   public TextMessage(final String correlationId, final String text, final Map<String, Object> parameters) {
	super(correlationId, parameters);
	this.text = text;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Message#getType()
    */
   @Override
   public MessageTypeEnum getType() {
	return MessageTypeEnum.Text;
   }

   /**
    * @return the text
    */
   public String getText() {
	return text;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return text;
   }

}
