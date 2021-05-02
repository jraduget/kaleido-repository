/*
 *  Copyright 2008-2021 the original author or authors.
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
package org.kaleidofoundry.core.store.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jraduget
 */
@XmlRootElement(name = "store")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileStoreEntry implements Serializable {

   private static final long serialVersionUID = 1L;

   private String name;
   private String baseUri;

   public FileStoreEntry() {
   }

   public FileStoreEntry(String name, String baseUri) {
	super();
	this.name = name;
	this.baseUri = baseUri;
   }

   public String getName() {
	return name;
   }

   public void setName(String name) {
	this.name = name;
   }

   public String getBaseUri() {
	return baseUri;
   }

   public void setBaseUri(String baseUri) {
	this.baseUri = baseUri;
   }
}
