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
package org.kaleidofoundry.core.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Person test service
 * 
 * @author Jerome RADUGET
 */
public class PersonService {

   @PersistenceContext(unitName = "kaleido-core-custom")
   EntityManager entityManager;

   /**
    * @param id
    * @return entity which maps to the given id
    */
   public PersonEntity findById(final Integer id) {
	return entityManager.find(PersonEntity.class, id);
   }

   /**
    * create new person entity
    * 
    * @param entity
    */
   public void create(final PersonEntity entity) {
	entityManager.persist(entity);
	entityManager.flush();
   }

   /**
    * @param entity
    */
   public void remove(final PersonEntity entity) {
	entityManager.remove(entityManager.merge(entity));
   }

   /**
    * @param entity
    */
   public void update(final PersonEntity entity) {
	entityManager.merge(entity);
   }
}
