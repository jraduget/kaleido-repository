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
package org.kaleidofoundry.core.persistence;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceProperty;
import javax.persistence.PersistenceUnit;

import org.kaleidofoundry.core.util.StringHelper;

/**
 * {@link EntityManagerFactory} and {@link EntityManager} field / parameter injection
 * 
 * <pre>
 * // EntityManagerFactory field injection
 * public class MyClass01 { 
 * 
 * 	// EntityManagerFactory field injection
 * 	private &#064;Inject &#064;PersistenceUnit(unitName="...") EntityManagerFactory emf;
 * 
 *   	// EntityManager field injection
 * 	private &#064;Inject &#064;PersistenceContext(unitName="...") EntityManager em;
 * 
 *    ... 
 * }
 * </pre>
 * 
 * <pre>
 * // constructor injection
 * public class MyClass02 { 
 * 
 * 	private final EntityManagerFactory emf;
 * 
 * 	public MyClass02(&#064;Inject &#064;PersistenceUnit(unitName="...") EntityManagerFactory emf) {
 * 		this.emf=emf;
 * 	 }
 * 	
 * ... 
 * }
 * </pre>
 * 
 * @author jraduget
 */
public class EntityManagerFactoryProducer {

   @Produces
   public EntityManagerFactory getEntityManagerFactory(final InjectionPoint injectionPoint) {

	String defaultPersistenceUnit;
	PersistenceUnit persistenceUnit;

	// try CDI bean name	
	defaultPersistenceUnit = injectionPoint.getBean() != null ? injectionPoint.getBean().getName() : null;	

	// try field / method name
	if (StringHelper.isEmpty(defaultPersistenceUnit)) {
	   defaultPersistenceUnit = injectionPoint.getMember().getName();
	}

	persistenceUnit = injectionPoint.getAnnotated().getAnnotation(PersistenceUnit.class);

	// if no @PersistenceUnit annotation is present, create a default one with field / argument name
	if (persistenceUnit == null) {
	   final String unitNameToUse = defaultPersistenceUnit;
	   persistenceUnit = new PersistenceUnit() {

		@Override
		public Class<? extends Annotation> annotationType() {
		   return PersistenceUnit.class;
		}

		@Override
		public String unitName() {
		   return unitNameToUse;
		}

		@Override
		public String name() {
		   return unitNameToUse;
		}
	   };
	} else {
	   defaultPersistenceUnit = persistenceUnit.unitName();
	}

	return UnmanagedEntityManagerFactory.getEntityManagerFactory(defaultPersistenceUnit);

   }

   @Produces
   public EntityManager getEntityManager(final InjectionPoint injectionPoint) {

	String defaultPersistenceUnit;
	PersistenceContext persistenceContext;

	// try CDI bean name
	defaultPersistenceUnit = injectionPoint.getBean().getName();

	// try field / method name
	if (StringHelper.isEmpty(defaultPersistenceUnit)) {
	   defaultPersistenceUnit = injectionPoint.getMember().getName();
	}

	persistenceContext = injectionPoint.getAnnotated().getAnnotation(PersistenceContext.class);

	// if no @PersistenceUnit annotation is present, create a default one with field / argument name
	if (persistenceContext == null) {
	   final String unitNameToUse = defaultPersistenceUnit;
	   persistenceContext = new PersistenceContext() {

		/*
		 * (non-Javadoc)
		 * @see javax.persistence.PersistenceContext#properties()
		 */
		@Override
		public PersistenceProperty[] properties() {
		   return null;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.persistence.PersistenceContext#type()
		 */
		@Override
		public PersistenceContextType type() {
		   return null;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
		   return PersistenceContext.class;
		}

		@Override
		public String unitName() {
		   return unitNameToUse;
		}

		@Override
		public String name() {
		   return unitNameToUse;
		}
	   };

	} else {
	   defaultPersistenceUnit = persistenceContext.unitName();
	}

	return UnmanagedEntityManagerFactory.currentEntityManager(defaultPersistenceUnit);

   }
}
