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
package org.kaleidofoundry.core.config;

import java.util.LinkedHashSet;

/**
 * An abstract and default configuration changes listener
 * 
 * @author jraduget
 */
public abstract class ConfigurationAdapter implements ConfigurationListener {

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationListener#propertyUpdate(org.kaleidofoundry.core.config.ConfigurationChangeEvent)
    */
   @Override
   public void propertyUpdate(final ConfigurationChangeEvent evt) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationListener#propertyCreate(org.kaleidofoundry.core.config.ConfigurationChangeEvent)
    */
   @Override
   public void propertyCreate(final ConfigurationChangeEvent evt) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationListener#propertyRemove(org.kaleidofoundry.core.config.ConfigurationChangeEvent)
    */
   @Override
   public void propertyRemove(final ConfigurationChangeEvent evt) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationListener#configurationUnload(org.kaleidofoundry.core.config.Configuration)
    */
   @Override
   public void configurationUnload(final Configuration source) {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationListener#propertiesChanges(java.util.LinkedHashSet)
    */
   @Override
   public void propertiesChanges(final LinkedHashSet<ConfigurationChangeEvent> events) {
   }

}
