package org.kaleidofoundry.core.i18n.entity;

import static org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory.KaleidoPersistentContextUnitName;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import mazz.i18n.annotation.I18NMessage;

import org.kaleidofoundry.core.i18n.entity.I18nMessageConstants.Query_MessagesByLocale;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;

/**
 * Service which handle {@link I18NMessage} persistent model
 * 
 * @author Jerome RADUGET
 */
public class I18nMessageService {

   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   protected EntityManager em;

   /**
    * @return current entity manager (handle managed one or not)
    */
   @NotNull
   protected final EntityManager getEntityManager() {
	// unmanaged environment
	if (em == null) {
	   return UnmanagedEntityManagerFactory.currentEntityManager(KaleidoPersistentContextUnitName);
	}
	// managed environment
	else {
	   return em;
	}
   }

   /**
    * @param resourceName
    * @param locale
    * @return messages which have the language of the given locale ( {@link Locale#getISO3Country()} )
    */
   // TODO use JPA Criteria API 2.0 if possible : Class.forName("javax.persistence.criteria.QueryBuilder"); if not found use jpql ?
   public List<I18nMessageLanguage> findMessagesByLocale(@NotNull final String resourceName, @NotNull final Locale locale) {

	TypedQuery<I18nMessageLanguage> query = getEntityManager().createNamedQuery(Query_MessagesByLocale.Name, I18nMessageLanguage.class);
	query.setParameter(Query_MessagesByLocale.Parameter_ResourceName, resourceName);
	query.setParameter(Query_MessagesByLocale.Parameter_Locale, locale.toString());

	List<I18nMessageLanguage> result = query.getResultList();

	return result;
   }
}
