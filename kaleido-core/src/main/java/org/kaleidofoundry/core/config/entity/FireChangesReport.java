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
package org.kaleidofoundry.core.config.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jerome RADUGET
 */
@XmlRootElement(name = "changes")
@XmlAccessorType(XmlAccessType.FIELD)
public class FireChangesReport {

   private final String configId;
   private final String configUri;
   private final Integer created;
   private final Integer updated;
   private final Integer removed;
   private final Integer listernerCount;

   public FireChangesReport() {
	this.configId = null;
	this.configUri = null;
	this.created = 0;
	this.updated = 0;
	this.removed = 0;
	this.listernerCount = 0;
   }

   /**
    * @param configId
    * @param created
    * @param updated
    * @param removed
    */
   public FireChangesReport(final String configId, final String configUri, final Integer created, final Integer updated, final Integer removed,
	   final Integer listernerCount) {
	super();
	this.configId = configId;
	this.configUri = configUri;
	this.created = created;
	this.updated = updated;
	this.removed = removed;
	this.listernerCount = listernerCount;
   }

   /**
    * @return the configId
    */
   public String getConfigId() {
	return configId;
   }

   /**
    * @return the configUri
    */
   public String getConfigUri() {
	return configUri;
   }

   /**
    * @return the created
    */
   public Integer getCreated() {
	return created;
   }

   /**
    * @return the updated
    */
   public Integer getUpdated() {
	return updated;
   }

   /**
    * @return the removed
    */
   public Integer getRemoved() {
	return removed;
   }

   /**
    * @return the listernerCount
    */
   public Integer getListernerCount() {
	return listernerCount;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return "FireChangesReport [configId=" + configId + ", configUri=" + configUri + ", created=" + created + ", updated=" + updated + ", removed=" + removed
		+ ", listernerCount=" + listernerCount + "]";
   }

}
