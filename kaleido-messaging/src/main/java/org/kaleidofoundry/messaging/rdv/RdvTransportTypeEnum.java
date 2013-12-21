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
package org.kaleidofoundry.messaging.rdv;

/**
 * RDV transport enumeration
 * <ul>
 * <li>RELIABLE</li>
 * <li>CERTIFIED</li>
 * <li>DQUEUE</li>
 * </ul>
 * 
 * @author jraduget
 */
public enum RdvTransportTypeEnum {

   /** Basic Transport */
   RELIABLE("reliable"),
   /** Transport with message confirmation */
   CERTIFIED("certified"),
   /** Transport with distributed Queue */
   DQUEUE("dqueue");

   RdvTransportTypeEnum(final String code) {
	this.code = code;
   }

   private final String code;

   public String getCode() {
	return code;
   }

   public boolean equals(final String code) {
	return this.getCode().equalsIgnoreCase(code);
   }
}