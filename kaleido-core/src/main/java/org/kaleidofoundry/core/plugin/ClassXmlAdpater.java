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
package org.kaleidofoundry.core.plugin;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Jerome RADUGET
 */
public class ClassXmlAdpater extends XmlAdapter<String, Class<?>> {

   /*
    * (non-Javadoc)
    * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
    */
   @Override
   public Class<?> unmarshal(String v) throws Exception {
	return Class.forName(v);
   }

   /*
    * (non-Javadoc)
    * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
    */
   @Override
   public String marshal(Class<?> v) throws Exception {
	return v != null ? v.toString() : "";
   }

}
