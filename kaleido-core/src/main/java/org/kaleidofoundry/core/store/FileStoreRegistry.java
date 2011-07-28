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
package org.kaleidofoundry.core.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.Registry;

/**
 * {@link FileStore} registry
 * 
 * @author Jerome RADUGET
 */
public class FileStoreRegistry extends Registry<String, List<FileStore>> {

   private static final long serialVersionUID = -7666992951667284669L;

   /**
    * @param baseUri
    * @param fileStore
    * @return list of stores, register with the root path URI
    */
   public synchronized List<FileStore> put(final @NotNull String baseUri, final @NotNull FileStore fileStore) {
	List<FileStore> currents = get(baseUri);

	if (currents == null) {
	   currents = Collections.synchronizedList(new ArrayList<FileStore>());
	   put(baseUri, currents);
	} else {
	   currents = get(baseUri);
	}
	currents.add(fileStore);

	return currents;
   }

}
