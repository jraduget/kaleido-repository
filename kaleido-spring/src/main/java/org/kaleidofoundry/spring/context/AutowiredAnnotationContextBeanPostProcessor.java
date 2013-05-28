package org.kaleidofoundry.spring.context;

import java.beans.PropertyDescriptor;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;

//@Component
public class AutowiredAnnotationContextBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {
   
   public AutowiredAnnotationContextBeanPostProcessor() {
   }

   @Override
   protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
	// TODO Auto-generated method stub
	return super.findAutowireCandidates(type);
   }

   @Override
   public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
	// TODO Auto-generated method stub
	return super.postProcessBeforeInstantiation(beanClass, beanName);
   }

   @Override
   public void processInjection(Object bean) throws BeansException {
	// TODO Auto-generated method stub
	super.processInjection(bean);
   }

   @Override
   public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

	try {
	   return super.postProcessPropertyValues(pvs, pds, bean, beanName);
	} catch (BeanCreationException bce) {
	   if (bce.getRootCause() instanceof NoSuchBeanDefinitionException) {
		return pvs;
	   } else {
		throw bce;
	   }
	}
   }

}
