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
package org.kaleidofoundry.core.context.jee6;

import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.cache.CacheContextBuilder.CacheName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ProviderCode;
import static org.kaleidofoundry.core.config.ConfigurationContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;

import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.CacheProvidersEnum;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.MyServiceAssertions;
import org.kaleidofoundry.core.context.Parameter;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
@Stateless(mappedName = "ejb/MyServiceBean6")
public class MyServiceBean implements MyServiceRemoteBean {

   private final static Logger LOGGER = LoggerFactory.getLogger(MyServiceBean.class);

   public MyServiceBean() {
	// the fields injection order is not guaranteed with CDI... something myCustomCacheManager is processed after myCustomCache
	CacheManagerFactory.provides(CacheProvidersEnum.infinispan4x.name(), new RuntimeContext<CacheManager>("myCustomCacheManager"));
   }

   @Inject
   @PersistenceContext(unitName = "kaleido")
   private EntityManager entityManager;

   @Inject
   @PersistenceUnit(unitName = "kaleido")
   private EntityManagerFactory entityManagerFactory;

   @Inject
   @Context
   private RuntimeContext<?> myContext;

   @Inject
   @Context("namedCtx")
   private RuntimeContext<?> myNamedContext;

   @Inject
   @Context(parameters = { @Parameter(name = FileStoreUri, value = "classpath:/config/myConfig.properties") })
   private Configuration myConfig;

   @Inject
   @Context
   private CacheManager myDefaultCacheManager;

   @Inject
   @Context(parameters = { @Parameter(name = ProviderCode, value = "infinispan4x") })
   private CacheManager myCustomCacheManager;

   @Inject
   @Context
   private Cache<Integer, String> myDefaultCache;

   @Inject
   @Context(parameters = { @Parameter(name = CacheName, value = "myNamedCache"), @Parameter(name = CacheManagerRef, value = "myCustomCacheManager") })
   private Cache<Integer, String> myCustomCache;

   @Inject
   @Context
   private I18nMessages myDefaultMessages;

   @Inject
   @Context(parameters = { @Parameter(name = BaseName, value = "i18n/messages") })
   private I18nMessages myBaseMessages;

   @Inject
   @Context
   private NamingService myNamingService;

   @PostConstruct
   public void postConstruct() {
	LOGGER.info("@PostConstruct " + toString());
   }

   @PreDestroy
   public void preDestroy() {
	LOGGER.info("@PreDestroy " + toString());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#runtimeContextInjectionAssertions()
    */
   @Override
   public void runtimeContextInjectionAssertions() {
	MyServiceAssertions.runtimeContextInjectionAssertions(myContext, myNamedContext);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#configurationInjectionAssertions()
    */
   @Override
   public void configurationInjectionAssertions() throws ParseException {
	MyServiceAssertions.configurationInjectionAssertions(myConfig);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#cacheManagerInjectionAssertions()
    */
   @Override
   public void cacheManagerInjectionAssertions() {
	MyServiceAssertions.cacheManagerInjectionAssertions(myDefaultCacheManager, myCustomCacheManager);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#cacheInjectionAssertions()
    */
   @Override
   public void cacheInjectionAssertions() {
	MyServiceAssertions.cacheInjectionAssertions(myDefaultCache, myCustomCache);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#i18nMessagesInjectionAssertions()
    */
   @Override
   public void i18nMessagesInjectionAssertions() {
	MyServiceAssertions.i18nMessagesInjectionAssertions(myDefaultMessages, myBaseMessages);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#namingServiceInjectionAssertions()
    */
   @Override
   public void namingServiceInjectionAssertions() {
	MyServiceAssertions.namingServiceInjectionAssertions(myNamingService);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#entityManagerFactoryInjectionAssertions()
    */
   @Override
   public void entityManagerFactoryInjectionAssertions() {
	MyServiceAssertions.entityManagerFactoryInjectionAssertions(entityManagerFactory);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.jee6.MyServiceLocalBean#entityManagerInjectionAssertions()
    */
   @Override
   public void entityManagerInjectionAssertions() {
	MyServiceAssertions.entityManagerInjectionAssertions(entityManager);
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	StringBuilder str = new StringBuilder();
	str.append("\n\t").append(super.toString());
	str.append("\n\tmyNamingService=").append(myNamingService != null ? myNamingService.toString() : "null");
	str.append("\n\tmyDefaultMessages=").append(myDefaultMessages != null ? myDefaultMessages.toString() : "null");
	str.append("\n\tmyBaseMessages=").append(myBaseMessages != null ? myBaseMessages.toString() : "null");
	str.append("\n\tmyDefaultCache=").append(myDefaultCache != null ? myDefaultCache.toString() : "null");
	str.append("\n\tmyCustomCache=").append(myCustomCache != null ? myCustomCache.toString() : "null");
	str.append("\n\tmyDefaultCacheManager=").append(myDefaultCacheManager != null ? myDefaultCacheManager.toString() : "null");
	str.append("\n\tmyCustomCacheManager=").append(myCustomCacheManager != null ? myCustomCacheManager.toString() : "null");
	str.append("\n\tmyConfig=").append(myConfig != null ? myConfig.toString() : "null");
	str.append("\n\tmyContext=").append(myContext != null ? myContext.toString() : "null");
	str.append("\n\tmyNamedContext=").append(myNamedContext != null ? myNamedContext.toString() : "null");
	str.append("\n\tentityManagerFactory=").append(entityManagerFactory != null ? entityManager.toString() : "null");
	str.append("\n\tentityManager=").append(entityManager != null ? entityManager.toString() : "null");
	return str.toString();
   }

}
