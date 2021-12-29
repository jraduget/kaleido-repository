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
package org.kaleidofoundry.core.i18n;

import static org.kaleidofoundry.core.env.model.EnvironmentConstants.KALEIDO_PERSISTENT_UNIT_NAME;
import static org.kaleidofoundry.core.i18n.model.I18nMessageConstants.Query_MessagesByLocale.Name;
import static org.kaleidofoundry.core.i18n.model.I18nMessageConstants.Query_MessagesByLocale.Parameter_Locale;
import static org.kaleidofoundry.core.i18n.model.I18nMessageConstants.Query_MessagesByLocale.Parameter_ResourceName;

import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kaleidofoundry.core.i18n.model.I18nMessage;
import org.kaleidofoundry.core.i18n.model.I18nMessageLanguage;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Task;

/**
 * Service which handle {@link I18nMessage} persistent model
 * 
 * @author jraduget
 */
@Stateless(mappedName = "ejb/i18n/manager")
@Path("/i18n/")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class I18nMessageController {

   @PersistenceContext(unitName = KALEIDO_PERSISTENT_UNIT_NAME)
   EntityManager em;

   /**
    * @param resourceName
    * @param locale
    * @return messages which have the language of the given locale ( {@link Locale#getISO3Country()} )
    */
   @Path("/i18n/{resourceName}/{locale}")
   @SuppressWarnings("unchecked")
   @Task(comment = "use JPA Criteria API 2.0 if possible : Class.forName(\"javax.persistence.criteria.QueryBuilder\"); if not found use jpql ?")
   public List<I18nMessageLanguage> findMessagesByLocale(@NotNull @PathParam("resourceName") final String resourceName,
	   @NotNull @PathParam("locale") final Locale locale) {

	// JPA 2.0 generic named query
	// TypedQuery<I18nMessageLanguage> query = getEntityManager().createNamedQuery(Name,
	// I18nMessageLanguage.class);
	// query.setParameter(Parameter_ResourceName, resourceName);
	// query.setParameter(Parameter_Locale, locale.toString());

	// JPA 1.x for jee5 compatibility
	Query query = em.createNamedQuery(Name);

	// Query query = getEntityManager().createNativeQuery(Name, I18nMessageLanguage.class);
	query.setParameter(Parameter_ResourceName, resourceName);
	query.setParameter(Parameter_Locale, locale.toString());

	List<I18nMessageLanguage> result = query.getResultList();

	return result;
   }
}
