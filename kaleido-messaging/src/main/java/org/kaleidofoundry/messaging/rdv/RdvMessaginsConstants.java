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
package org.kaleidofoundry.messaging.rdv;

/**
 * Constants use by RDV transport / listener
 * 
 * @author Jerome RADUGET
 */
public interface RdvMessaginsConstants {

   /** RDV transport service parameter */
   String RDV_SERVICE_PARAMETER = "tibco.rdv.service";
   /** Network transport parameter */
   String RDV_NETWORK_PARAMETER = "tibco.rdv.network";
   /** Daemon transport parameter */
   String RDV_DAEMON_PARAMETER = "tibco.rdv.daemon";
   /** RDV transport type @see {@link RdvTransportTypeEnum} */
   String RDV_TRANSPORT_TYPE = "tibco.rdv.type";
   /** Cmname for RDV certified transport */
   String RDV_CERTIFIED_CMNAME = "tibco.rdv.cmname";
   /** Timeout for RDV certified transport */
   String RDV_CERTIFIED_TIMEOUT = "tibco.rdv.timeout";

   /** RDV listener subject */
   String RDV_SUBJECTS = "tibco.rdv.sujects";

}
