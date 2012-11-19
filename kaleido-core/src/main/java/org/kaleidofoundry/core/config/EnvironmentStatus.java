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
package org.kaleidofoundry.core.config;

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
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Jerome RADUGET
 */
@XmlType
@XmlRootElement(name = "status")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentStatus implements Serializable {

   @XmlType
   static enum Status {
	/** Not yet started, only init phase have been done */
	INIT,
	/** Configurations are loaded */
	STARTED,
	/** Stopped */
	STOPPED,
	/** In error */
	ERROR;
   }

   private static final long serialVersionUID = 1L;

   public EnvironmentStatus() {
   }

   public EnvironmentStatus(Status status, String error) {
	super();
	this.status = status;
	this.error = error;
   }

   private Status status;
   private String error;

   public Status getStatus() {
	return status;
   }

   public void setStatus(Status status) {
	this.status = status;
   }

   public String getError() {
	return error;
   }

   public void setError(String error) {
	this.error = error;
   }

}
