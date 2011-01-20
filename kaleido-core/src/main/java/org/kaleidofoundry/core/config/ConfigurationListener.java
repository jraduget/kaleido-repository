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
package org.kaleidofoundry.core.config;

import java.util.EventListener;
import java.util.LinkedHashSet;

/**
 * Configuration changes listener
 * 
 * @author Jerome RADUGET
 */
public interface ConfigurationListener extends EventListener {

   /**
    * @param evt
    * @see Configuration#setProperty(String, java.io.Serializable)
    */
   void propertyUpdate(ConfigurationChangeEvent evt);

   /**
    * @param evt
    * @see Configuration#setProperty(String, java.io.Serializable)
    */
   void propertyCreate(ConfigurationChangeEvent evt);

   /**
    * @param evt
    * @see Configuration#removeProperty(String)
    */
   void propertyRemove(ConfigurationChangeEvent evt);

   /**
    * @param events
    * @see Configuration#fireConfigurationChangesEvents()
    */
   void propertiesChanges(LinkedHashSet<ConfigurationChangeEvent> events);

   /**
    * @param source
    * @see Configuration#unload()
    */
   void configurationUnload(Configuration source);

}
