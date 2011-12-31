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
package org.kaleidofoundry.core.lang.label;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.CollectionsHelper;

/**
 * Common labels class, used to categorize some persistent entities with multiple text labels <br/>
 * 
 * @author Jerome RADUGET
 */
@Embeddable
// @Access(AccessType.PROPERTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class Labels implements Serializable, Iterable<String> {

   private static final long serialVersionUID = 1390615860432459965L;

   public Labels() {
	itemSet = new TreeSet<String>();
   }

   public Labels(final String... labels) {
	this();
	itemSet.addAll(Arrays.asList(labels));
   }

   @Column(name = "LABELS")
   private String items;

   @Transient
   @XmlTransient
   private LabelCategory category;

   @Transient
   @XmlTransient
   final Set<String> itemSet;

   /**
    * add new label to the list
    * 
    * @param label
    * @return current instance
    */
   public Labels add(@NotNull final String label) {
	itemSet.add(label);
	update();
	return this;
   }

   /**
    * add new label to the list
    * 
    * @param label
    * @return current instance
    */
   public Labels remove(@NotNull final String label) {
	itemSet.remove(label);
	update();
	return this;
   }

   /**
    * @return label iterator
    */
   public Iterator<String> iterator() {
	return itemSet.iterator();

   }

   /**
    * @return the category
    */
   public LabelCategory getCategory() {
	return category;
   }

   /**
    * @param category the category to set
    */
   public void setCategory(final LabelCategory category) {
	this.category = category;
	LabelManager.putLabels(this);
   }

   /**
    * does text argument is contains in one of the labels
    * 
    * @param text
    * @return <code>true|false</code>
    */
   public boolean contains(final String text) {
	return items != null && items.contains(text);
   }

   private void update() {
	items = CollectionsHelper.collectionToString(itemSet, "|");
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((category == null) ? 0 : category.hashCode());
	result = prime * result + ((itemSet == null) ? 0 : itemSet.hashCode());
	result = prime * result + ((items == null) ? 0 : items.hashCode());
	return result;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof Labels)) { return false; }
	Labels other = (Labels) obj;
	if (category != other.category) { return false; }
	if (itemSet == null) {
	   if (other.itemSet != null) { return false; }
	} else if (!itemSet.equals(other.itemSet)) { return false; }
	if (items == null) {
	   if (other.items != null) { return false; }
	} else if (!items.equals(other.items)) { return false; }
	return true;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return itemSet.toString();
   }

}
