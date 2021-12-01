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
package org.kaleidofoundry.core.store;

/**
 * This class is used to register an uri schemes to a kaleido {@link FileStore} implementation
 * 
 * @author jraduget
 */
public class FileStoreTypeBean implements FileStoreType {

   private final String name;

   /**
    * @param name
    */
   public FileStoreTypeBean(final String name) {
	this.name = name;
   }

   /**
    * @return scheme
    */
   public String name() {
	return name;
   }

   @Override
   public boolean isCustom() {
	return true;
   }

}
