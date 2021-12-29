/*
 *  Copyright 2008-2021 the original author or authors.
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
package org.kaleidofoundry.core.lang.label;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.lang.label.model.LabelCategory;
import org.kaleidofoundry.core.lang.label.model.Labels;

/**
 * Label manager is used to categorize some labels items, like configuration properties, i18n messages... <br/>
 * 
 * @author jraduget
 */
public class LabelManager {

   private static final Map<LabelCategory, TreeSet<String>> labelsByCategory = new ConcurrentHashMap<LabelCategory, TreeSet<String>>();

   /**
    * register new labels item
    * 
    * @param labels
    */
   public static void putLabels(final Labels labels) {
	if (labels.getCategory() != null) {
	   if (labelsByCategory.get(labels.getCategory()) == null) {
		labelsByCategory.put(labels.getCategory(), new TreeSet<String>());
	   }
	   Iterator<String> labelIt = labels.iterator();
	   while (labelIt.hasNext()) {
		labelsByCategory.get(labels.getCategory()).add(labelIt.next());
	   }
	}
   }

   /**
    * @return current registered labels
    */
   public static Map<LabelCategory, TreeSet<String>> getLabelsByCategory() {
	return labelsByCategory;
   }
}
