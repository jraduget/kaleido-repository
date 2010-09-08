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
package org.kaleidofoundry.core.i18n.entity;

import static org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory.KaleidoPersistentContextUnitName;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mazz.i18n.annotation.I18NMessage;

import org.kaleidofoundry.core.i18n.entity.I18nMessageConstants.Query_MessagesByLocale;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;

/**
 * Service which handle {@link I18NMessage} persistent model
 * 
 * @author Jerome RADUGET
 */
public class I18nMessageService {

   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   private EntityManager em;

   /**
    * @return current entity manager (handle managed one or not). Can't be null.
    */
   @NotNull
   protected final EntityManager getEntityManager() {
	/*
	 * done via aop :) cf. PersistenceContextAspect
	 * // unmanaged environment
	 * if (em == null) {
	 * return UnmanagedEntityManagerFactory.currentEntityManager(KaleidoPersistentContextUnitName);
	 * }
	 * // managed environment
	 * else {
	 * return em;
	 * }
	 */
	return em;
   }

   /**
    * @param resourceName
    * @param locale
    * @return messages which have the language of the given locale ( {@link Locale#getISO3Country()} )
    */
   @SuppressWarnings("unchecked")
   @Review(comment = "use JPA Criteria API 2.0 if possible : Class.forName(\"javax.persistence.criteria.QueryBuilder\"); if not found use jpql ?")
   public List<I18nMessageLanguage> findMessagesByLocale(@NotNull final String resourceName, @NotNull final Locale locale) {

	// JPA 2.0 generic named query
	// TypedQuery<I18nMessageLanguage> query = getEntityManager().createNamedQuery(Query_MessagesByLocale.Name,
	// I18nMessageLanguage.class);
	// query.setParameter(Query_MessagesByLocale.Parameter_ResourceName, resourceName);
	// query.setParameter(Query_MessagesByLocale.Parameter_Locale, locale.toString());

	// JPA 1.x for jee5 compatibility
	Query query = getEntityManager().createNamedQuery(Query_MessagesByLocale.Name);

	// Query query = getEntityManager().createNativeQuery(Query_MessagesByLocale.Name, I18nMessageLanguage.class);
	query.setParameter(Query_MessagesByLocale.Parameter_ResourceName, resourceName);
	query.setParameter(Query_MessagesByLocale.Parameter_Locale, locale.toString());

	List<I18nMessageLanguage> result = query.getResultList();

	return result;
   }
}
