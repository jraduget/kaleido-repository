package org.kaleidofoundry.core.context;

import java.text.ParseException;

import org.junit.Test;

public interface MyServiceTest {

   @Test
   public abstract void runtimeContextInjectionTest();

   @Test
   public abstract void configurationInjectionTest() throws ParseException;

   @Test
   public abstract void cacheManagerInjectionTest();

   @Test
   public abstract void cacheInjectionTest();

   @Test
   public abstract void i18nMessagesInjectionTest();

   @Test
   public abstract void namingServiceInjectionTest();

}