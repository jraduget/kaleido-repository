/*  
 * Copyright 2008-2010 the original author or authors 
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
 * Constantes pour Transport et Listener RDV
 * 
 * @author Jerome RADUGET
 */
public interface RdvTransportConstants {

   /** Separateur entre les sujets écoutés */
   public static final String Subjects_Separator = ";";

   /** Chemin propriété pour avoir le service RDV */
   public static final String PATH_KEY_Service = "tibco.rdv.service";
   /** Chemin propriété pour avoir le network RDV */
   public static final String PATH_KEY_Network = "tibco.rdv.network";
   /** Chemin propriété pour avoir le daemon RDV */
   public static final String PATH_KEY_Daemon = "tibco.rdv.daemon";
   /** Chemin propriété pour avoir le type de transport RDV @see {@link RdvTransportTypeEnum} */
   public static final String PATH_KEY_Type = "tibco.rdv.type";
   /** Chemin propriété pour avoir le cmname du transport RDV certified */
   public static final String PATH_KEY_CMname = "tibco.rdv.cmname";
   /** Chemin propriété pour avoir le timeout du transport RDV certified */
   public static final String PATH_KEY_Timeout = "tibco.rdv.timeout";

   /** Chemin propriété pour avoir le sujet du listener RDV */
   public static final String PATH_KEY_Subject = "tibco.rdv.sujects";

}
