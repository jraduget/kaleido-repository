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
package org.kaleidofoundry.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Proxy java testing<br/>
 * <ul>
 * <li>api : http://java.sun.com/javase/6/docs/api/index.html?java/net/Proxy.html
 * <li>guide : http://java.sun.com/developer/technicalArticles/JavaLP/Interposing/
 * <li>event listener : http://java.sun.com/products/jfc/tsc/articles/generic-listener2/index.html
 * </ul>
 * 
 * @author jraduget
 */
public class ProxySample  {

   static interface Service {
	void methodReturningVoid();

	String methodReturningString(final String foo);
   }

   static class ServiceImpl implements Service {

	public ServiceImpl() {
	   System.out.println("ServiceImpl constructor call " + toString());
	}

	public void methodReturningVoid() {
	   System.out.println("ServiceImpl methodReturningVoid call " + toString());
	}

	public String methodReturningString(final String foo) {
	   System.out.println("ServiceImpl methodReturningString call " + toString());
	   return foo;
	}
   }

   static class DynamicProxy<T> implements InvocationHandler {

	private final T targetInstance;

	public DynamicProxy(final T pTargetInstance) {
	   this.targetInstance = pTargetInstance;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

	   StringBuilder str = new StringBuilder();

	   str.append("inkoked class: ").append(targetInstance.getClass().getName());
	   str.append(" method: ").append(method.getName());

	   if (args != null && args.length >= 0) {
		str.append(" with args: (");
		for (Object arg : args) {
		   str.append(arg != null ? String.valueOf(arg) : "null");
		   str.append(" , ");
		}
		str.append(")");
	   }

	   System.out.println(str.toString());
	   return method.invoke(targetInstance, args);

	}

	@SuppressWarnings("unchecked")
	public static <R> R create(final R pTargetInstance) {
	   return (R) java.lang.reflect.Proxy.newProxyInstance(pTargetInstance.getClass().getClassLoader(), pTargetInstance.getClass().getInterfaces(),
		   new DynamicProxy<R>(pTargetInstance));
	}

   }

   private Service proxyService;

   @Before
   public void setup() {
	proxyService = DynamicProxy.create(new ServiceImpl());
   }

   @After
   public void cleanup() {
	System.out.println();
   }

   @Test
   public void createProxyService() {
	assertNotNull(proxyService);
   }

   @Test
   public void createAndInvokedProxyService() {
	assertNotNull(proxyService);
	proxyService.methodReturningVoid();
	System.out.println(proxyService.methodReturningString("foo"));
   }
}
