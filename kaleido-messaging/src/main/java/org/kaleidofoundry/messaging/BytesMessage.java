/*  
 * Copyright 2008-2012 the original author or authors 
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

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

import org.kaleidofoundry.core.lang.Charsets;

/**
 * A binary message
 * 
 * @author Jerome RADUGET
 */
public class BytesMessage extends AbstractMessage implements Message {

   private final byte[] content;

   public BytesMessage(final byte[] content) {
	this(content, null);
   }

   public BytesMessage(final byte[] content, final Map<String, Object> parameters) {
	super(parameters);
	this.content = content;
   }

   public BytesMessage(String correlationId, final byte[] content, Map<String, Object> parameters) {
	super(correlationId, parameters);
	this.content = content;
   }

   public byte[] getBytes() {
	return content;
   }

   public ByteArrayInputStream getByteArrayInputStream() {
	return new ByteArrayInputStream(content);
   }

   public MessageTypeEnum getType() {
	return MessageTypeEnum.Bytes;
   }

   @Override
   public String toString() {
	return toString(Charsets.UTF_8.getCharset());
   }

   public String toString(Charset charset) {
	if (content != null) {
	   return charset.decode(ByteBuffer.wrap(content)).toString();
	} else {
	   return null;
	}
   }
}
