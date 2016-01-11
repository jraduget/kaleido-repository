/*  
 * Copyright 2008-2016 the original author or authors 
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

import java.io.Serializable;
import java.util.Map;

/**
 * JavaBean Message
 * 
 * @author jraduget
 */
public class JavaBeanMessage extends AbstractMessage implements Message {

   private final Serializable javaBean;

   public JavaBeanMessage(final Serializable javaBean) {
	super(null);
	this.javaBean = javaBean;
   }

   public JavaBeanMessage(final Serializable javaBean, final Map<String, Object> parameters) {
	super(parameters);
	this.javaBean = javaBean;
   }

   public Serializable getJavaBean() {
	return javaBean;
   }

   public MessageTypeEnum getType() {
	return MessageTypeEnum.JavaBean;
   }

   @Override
   public String toString() {
	if (javaBean != null)
	   return javaBean.toString();
	else
	   return null;
   }
}
