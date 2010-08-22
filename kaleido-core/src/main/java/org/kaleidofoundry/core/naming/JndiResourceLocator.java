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
package org.kaleidofoundry.core.naming;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.lang.annotation.Reviews;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * JndiResourceLocator
 * !! Peux être étendu à vos besoins (Si vous exposer des service via jndi) !!
 * Classe centralisant les accès Jndi pour un Context (InitialContext)particulier
 * Vous pouvez étendre cette classe, et en faire un singleton par "instance d'InitialContext"...
 * Vous pouvez activez ou non, un cache de ressource, en activant le boolean cacheEnabled à true
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@Reviews(reviews = { @Review(comment = "Cache lookup resources (not needed from J2EE1.3 ? done client side ?", category = ReviewCategoryEnum.Improvement),
	@Review(comment = "cache activeable ou non ˆ partir du context jndi true / false :) ?", category = ReviewCategoryEnum.Improvement) })
public class JndiResourceLocator<T> {

   private final JndiContext<T> jndiContext;
   private transient final Context initialContext;

   protected boolean cacheEnabled = false;
   private final Map<String, T> cache;

   /**
    * Instanciation d'un client jndi locale
    * 
    * @throws JndiResourceException Si impossible d'instancié l'InitialContext
    */
   public JndiResourceLocator() throws JndiResourceException {
	this(null);
   }

   /**
    * Instanciation d'un client jndi avec contexte de connection
    * 
    * @param jndiContext
    * @throws JndiResourceException Si impossible d'instancié l'InitialContext
    */
   public JndiResourceLocator(final JndiContext<T> jndiContext) throws JndiResourceException {
	this.jndiContext = jndiContext;
	initialContext = JndiHelper.createInitialContext(this.jndiContext);
	cache = Collections.synchronizedMap(new HashMap<String, T>());
   }

   /**
    * @param jndiName Nom jndi de l'objet distant
    * @param jndiRessourceClass Class de l'objet jndi accédé
    * @return Ressource jndi "castée"
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
    * Cache la ressource jndi (si cache activé)
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
    * @return Ressource Jndi récupérer du cache
    */
   protected T getCacheRessource(final String jndiName) {
	if (cacheEnabled) {
	   return cache.get(jndiName);
	} else {
	   return null;
	}
   }

}
