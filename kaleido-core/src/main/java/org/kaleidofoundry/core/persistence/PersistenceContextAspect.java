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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect use to inject {@link EntityManager} an {@link EntityManagerFactory} in an unmanaged environment :
 * <ul>
 * <li>lazy initialize when EntityManager field type is "get" (not via getter outside from constructor), and if field have to be yet set
 * <li>directly, after constructor call, once all internal initialization are done
 * <li>
 * </ul>
 * Chosen solution : 1. lazy initialize when field is get, outside of the constructor
 * 
 * @author Jerome RADUGET
 */
@Aspect
public class PersistenceContextAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceContextAspect.class);

   private List<String> initializedPU =  Collections.synchronizedList(new ArrayList<String>());
   
   public PersistenceContextAspect() {
	LOGGER.debug("@Aspect(PersistenceContextAspect) new instance");
   }

   // no need to filter on field modifier here, otherwise you can use private || !public at first get argument
   @Pointcut("get(@javax.persistence.PersistenceUnit javax.persistence.EntityManagerFactory *) && if()")
   public static boolean trackEntityManagerFactoryField(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut(PersistenceContextAspect) - trackEntityManagerFactoryField match");
	return true;
   }

   // no need to filter on field modifier here, otherwise you can use private || !public at first get argument
   @Pointcut("get(@javax.persistence.PersistenceContext javax.persistence.EntityManager *) && if()")
   public static boolean trackEntityManagerField(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut(PersistenceContextAspect) - trackEntityManagerField match");
	return true;
   }

   // track field with ProceedingJoinPoint and annotation information with @annotation(annotation)
   @Around("trackEntityManagerFactoryField(jp, esjp) && @annotation(annotation)")
   public Object trackEntityManagerFactoryToInject(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final ProceedingJoinPoint thisJoinPoint,
	   final PersistenceUnit annotation) throws Throwable {

	if (thisJoinPoint.getSignature() instanceof FieldSignature) {
	   FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
	   Object target = thisJoinPoint.getTarget();
	   Field field = fs.getField();
	   field.setAccessible(true);
	   Object currentValue = field.get(target);

	   String persistenceUnit = annotation.unitName();
	   if (currentValue == null && !initializedPU.contains(persistenceUnit)) {
		initializedPU.add(persistenceUnit);
		EntityManagerFactory entityManagerFactory = UnmanagedEntityManagerFactory.getEntityManagerFactory(persistenceUnit);
		field.set(target, entityManagerFactory);
		return entityManagerFactory;
	   } else {
		return thisJoinPoint.proceed();
	   }
	} else {
	   throw new IllegalStateException("aspect advise handle only field, please check your pointcut");
	}
   }

   // track field with ProceedingJoinPoint and annotation information with @annotation(annotation)
   @Around("trackEntityManagerField(jp, esjp) && @annotation(annotation)")
   public Object trackEntityManagerToInject(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final ProceedingJoinPoint thisJoinPoint,
	   final PersistenceContext annotation) throws Throwable {

	if (thisJoinPoint.getSignature() instanceof FieldSignature) {
	   FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
	   Object target = thisJoinPoint.getTarget();
	   Field field = fs.getField();
	   field.setAccessible(true);
	   Object currentValue = field.get(target);

	   if (currentValue == null) {
		String persistenceUnit = annotation.unitName();
		EntityManager entityManager = UnmanagedEntityManagerFactory.currentEntityManager(persistenceUnit);
		field.set(target, entityManager);
		return entityManager;
	   } else {
		return thisJoinPoint.proceed();
	   }
	} else {
	   throw new IllegalStateException("aspect advise handle only field, please check your pointcut");
	}
   }

}
