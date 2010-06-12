/*
 * $License$
 */
package org.kaleidofoundry.core.naming;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * JndiResourceLocator
 * !! Peux �tre �tendu � vos besoins (Si vous exposer des service via jndi) !!
 * Classe centralisant les acc�s Jndi pour un Context (InitialContext)particulier
 * Vous pouvez �tendre cette classe, et en faire un singleton par "instance d'InitialContext"...
 * Vous pouvez activez ou non, un cache de ressource, en activant le boolean cacheEnabled � true
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@Review(comment = "Cache lookup resources (not needed from J2EE1.3 ? done server side ?")
// TODO cache activeable ou non � partir du context jndi true / false :)
public class JndiResourceLocator<T> {

   private final JndiContext<T> jndiContext;
   private transient final Context initialContext;

   protected boolean cacheEnabled = false;
   private final Map<String, T> cache;

   /**
    * Instanciation d'un client jndi locale
    * 
    * @throws JndiResourceException Si impossible d'instanci� l'InitialContext
    */
   public JndiResourceLocator() throws JndiResourceException {
	this(null);
   }

   /**
    * Instanciation d'un client jndi avec contexte de connection
    * 
    * @param jndiContext
    * @throws JndiResourceException Si impossible d'instanci� l'InitialContext
    */
   public JndiResourceLocator(final JndiContext<T> jndiContext) throws JndiResourceException {
	this.jndiContext = jndiContext;
	initialContext = JndiHelper.createInitialContext(this.jndiContext);
	cache = Collections.synchronizedMap(new HashMap<String, T>());
   }

   /**
    * @param jndiName Nom jndi de l'objet distant
    * @param jndiRessourceClass Class de l'objet jndi acc�d�
    * @return Ressource jndi "cast�e"
    * @throws JndiResourceException
    */
   public T lookup(final String jndiName, final Class<T> jndiRessourceClass) throws JndiResourceException {
	// In cache ?
	T object = getCacheRessource(jndiName);
	// Nom complet de la ressource
	String jndiFullRessName = jndiName;

	// On prefix si besoin le nom de la ressource
	if (!StringHelper.isEmpty(jndiContext.getJavaEnvNamePrefix()) && !jndiName.startsWith(jndiContext.getJavaEnvNamePrefix())) {
	   jndiFullRessName = jndiContext.getJavaEnvNamePrefix() + jndiName;
	}

	// Not in cache
	if (object == null) {
	   // Lookup
	   object = JndiHelper.lookup(initialContext, jndiFullRessName, jndiRessourceClass);
	   // Cache It
	   cacheRessource(jndiName, object);
	}

	// Return
	return object;
   }

   /**
    * Cache la ressource jndi (si cache activ�)
    * 
    * @param jndiName
    * @param reference
    */
   protected void cacheRessource(final String jndiName, final T reference) {
	if (cacheEnabled) {
	   cache.put(jndiName, reference);
	}
   }

   /**
    * @param jndiName
    * @return Ressource Jndi r�cup�rer du cache
    */
   protected T getCacheRessource(final String jndiName) {
	if (cacheEnabled) {
	   return cache.get(jndiName);
	} else {
	   return null;
	}
   }

}
