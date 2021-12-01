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

/**
 * @author jraduget
 */
public class UsageStatistics {

   private long messageOkCount;
   private long messageKoCount;
   private long messageSkippedCount;

   public UsageStatistics() {
   }

   public UsageStatistics(long messageOkCount, long messageKoCount, long messageSkippedCount) {
	super();
	this.messageOkCount = messageOkCount;
	this.messageKoCount = messageKoCount;
	this.messageSkippedCount = messageSkippedCount;
   }

   /**
    * @return the messageOkCount
    */
   public long getMessageOkCount() {
	return messageOkCount;
   }

   /**
    * @param messageOkCount the messageOkCount to set
    */
   public void setMessageOkCount(long messageOkCount) {
	this.messageOkCount = messageOkCount;
   }

   /**
    * @return the messageKoCount
    */
   public long getMessageKoCount() {
	return messageKoCount;
   }

   /**
    * @param messageKoCount the messageKoCount to set
    */
   public void setMessageKoCount(long messageKoCount) {
	this.messageKoCount = messageKoCount;
   }

   /**
    * @return the messageSkippedCount
    */
   public long getMessageSkippedCount() {
	return messageSkippedCount;
   }

   /**
    * @param messageSkippedCount the messageSkippedCount to set
    */
   public void setMessageSkippedCount(long messageSkippedCount) {
	this.messageSkippedCount = messageSkippedCount;
   }

}
