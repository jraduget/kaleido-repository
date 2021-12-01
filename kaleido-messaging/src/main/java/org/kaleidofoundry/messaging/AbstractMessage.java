/*  
 * Copyright 2008-2021 the original author or authors 
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Message
 * 
 * @author jraduget
 */
public abstract class AbstractMessage implements Message {

   private String providerId;
   private String correlationId;
   private final Map<String, Object> parameters;

   public AbstractMessage(final Map<String, Object> parameters) {
	this.parameters = new HashMap<String, Object>();
	if (parameters != null) {
	   this.parameters.putAll(parameters);
	}
   }

   public AbstractMessage(String correlationId, Map<String, Object> parameters) {
	this(parameters);
	this.correlationId = correlationId;
   }

   public String getProviderId() {
	return providerId;
   }

   public void setProviderId(String id) {
	this.providerId = id;
   }

   public String getCorrelationId() {
	return correlationId;
   }

   public void setCorrelationId(String correlationId) {
	this.correlationId = correlationId;
   }

   public Map<String, Object> getParameters() {
	return Collections.unmodifiableMap(parameters);
   }

   @Override
   public String toString() {
	return toString(null);
   }

   /**
    * @param exeptions parameters to exclude from output
    * @return
    */
   public String toString(final List<String> exeptions) {

	final StringBuilder str = new StringBuilder();

	if (parameters != null) {
	   str.append("{");
	   for (final String param : parameters.keySet()) {
		if (exeptions == null || !exeptions.contains(param)) {
		   str.append(param).append("=").append(parameters.get(param)).append(" ; ");
		}
	   }
	   str.append("}");
	}

	return str.toString();
   }

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
	return result;
   }

   @Override
   public boolean equals(final Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	final AbstractMessage other = (AbstractMessage) obj;
	if (parameters == null) {
	   if (other.parameters != null) return false;
	} else if (!parameters.equals(other.parameters)) return false;
	return true;
   }

}
