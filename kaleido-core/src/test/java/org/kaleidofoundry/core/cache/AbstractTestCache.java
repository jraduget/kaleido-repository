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
package org.kaleidofoundry.core.cache;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.lang.NotNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Cache Factory
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractTestCache extends Assert {

   protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestCache.class);

   /** legal cache to use, must be initialize by the concrete class test */
   protected Cache<Integer, Person> cache;

   /**
    * disable i18n message bundle control to speed up test (no need of a local derby instance startup)
    */
   @BeforeClass
   public static void setupClass() {
	I18nMessagesFactory.disableJpaControl();
   }

   /**
    * re-enable i18n message bundle control
    */
   @AfterClass
   public static void cleanupClass() {
	I18nMessagesFactory.enableJpaControl();
   }

   // testing part *****************************************************************************************************

   /**
    * test good cache instantiation
    */
   @Test
   public void checkCache() {
	assertNotNull(cache);
   }

   /**
    * getting null key not allowed
    */
   @Test(expected = NotNullException.class)
   public void illegalGet() {
	assertNotNull(cache);
	cache.get(null);
   }

   /**
    * putting null key not allowed
    */
   @Test(expected = NotNullException.class)
   public void illegalPut01() {
	assertNotNull(cache);
	cache.put(null, Person.newMockInstance());
   }

   /**
    * putting null value not allowed
    */
   @Test(expected = NotNullException.class)
   public void illegalPut02() {
	assertNotNull(cache);
	cache.put(1, null);
   }

   /**
    * removing null key not allowed
    */
   @Test(expected = NotNullException.class)
   public void illegalRemove() {
	assertNotNull(cache);
	cache.remove(null);
   }

   /**
    * test get and size features
    */
   @Test
   public void get() {
	assertNotNull(cache);
	assertEquals(Person.class.getName(), cache.getName());
	assertEquals(0, cache.size());
	assertNull(cache.get(-1));
   }

   /**
    * test put and size features
    */
   @Test
   public void put() {

	Person mockPerson1 = Person.newMockInstance();
	final Person mockPersonToCompare1 = mockPerson1.clone();

	final Person mockPerson2 = Person.newMockInstance();
	final Person mockPersonToCompare2;

	mockPerson2.setFirstName(mockPerson2.getFirstName() + "-changed");
	mockPerson2.setLastName(mockPerson2.getLastName() + "-changed");
	mockPerson2.setBirthdate(new Date());

	mockPersonToCompare2 = mockPerson2.clone();

	// 0. not same references
	assertNotSame(mockPerson1, mockPerson2);
	assertNotSame(mockPerson1, mockPersonToCompare1);
	assertNotSame(mockPerson2, mockPersonToCompare2);

	// 1. try first put in cache
	cache.put(mockPerson1.getId(), mockPerson1);
	assertNotNull(mockPerson1.getId());
	assertTrue(cache.keys().contains(mockPerson1.getId()));
	// assert that first entry put have same reference when get it
	assertSame(mockPerson1, cache.get(mockPerson1.getId()));
	// assert that first entry put have right properties
	mockPerson1 = cache.get(mockPerson1.getId());
	assertNotNull(mockPerson1);
	assertNotNull(mockPerson1.getId());
	assertEquals(mockPersonToCompare1.getId(), mockPerson1.getId());
	assertEquals(mockPersonToCompare1.getFirstName(), mockPerson1.getFirstName());
	assertEquals(mockPersonToCompare1.getLastName(), mockPerson1.getLastName());
	assertEquals(mockPersonToCompare1.getBirthdate(), mockPerson1.getBirthdate());

	// 2. try second put in cache, replacing old instance
	cache.put(mockPerson2.getId(), mockPerson2);
	assertNotNull(mockPerson1.getId());
	assertTrue(cache.keys().contains(mockPerson1.getId()));
	// assert that second entry put have right replace old instance
	mockPerson1 = cache.get(mockPerson2.getId());
	assertNotNull(mockPerson2);
	assertNotNull(mockPerson2.getId());
	assertEquals(mockPersonToCompare2.getId(), mockPerson2.getId());
	assertEquals(mockPersonToCompare2.getFirstName(), mockPerson2.getFirstName());
	assertEquals(mockPersonToCompare2.getLastName(), mockPerson2.getLastName());
	assertEquals(mockPersonToCompare2.getBirthdate(), mockPerson2.getBirthdate());
   }

   /**
    * test remove and size features
    */
   @Test
   public void remove() {

	final int maxSize = 50;

	assertNotNull(cache);
	assertEquals(Person.class.getName(), cache.getName());
	assertEquals(0, cache.size());

	final Collection<Person> personnes = new ArrayList<Person>();

	for (int id = 1; id < maxSize; id++) {
	   final Person p = Person.newMockInstance();
	   p.setId(id);
	   personnes.add(p);

	   cache.put(p.getId(), p);
	   assertEquals(id, cache.size());
	}

	assertEquals(maxSize - 1, cache.size());

	cache.remove(1);
	assertEquals(maxSize - 2, cache.size());

	cache.removeAll();
	assertEquals(0, cache.size());
   }

   /**
    * TODO for queryable cache
    * 
    * @throws ParseException
    */
   @Test
   @Ignore
   public void filterCacheByName() throws ParseException {

	int idPerson = 1;
	Adress mainAdress;
	Person person;
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

	mainAdress = new Adress("2 RUE SAINT MARTIN", "ETAGE 3", "75008", "PARIS");
	person = new Person(idPerson++, "Dupond", "Paul", df.parse("1958/06/28"), mainAdress).addAdress(mainAdress).addAdress(
		new Adress("22 RUE LA CANEBIERE", null, "13001", "MARSEILLE"));
	cache.put(person.getId(), person);

	mainAdress = new Adress("10 RUE DE RIVOLI", "ETAGE 3", "13006", "MARSEILLE");
	person = new Person(idPerson++, "DUPONT", "Jacques", df.parse("1978/10/28"), mainAdress).addAdress(mainAdress).addAdress(
		new Adress("ALLEE de RIVOLI", null, "33150", "CENON")).addAdress(new Adress("56 RUE LA CANEBIERE", null, "13001", "MARSEILLE"));

	cache.put(person.getId(), person);

	person = new Person(idPerson++, "X", "Pierre", df.parse("1980/01/05"), null);
	cache.put(person.getId(), person);

	person = new Person(idPerson++, "Y", "Paul", df.parse("1960/05/09"), null);
	cache.put(person.getId(), person);

	assertEquals(4, cache.size());

	/*
	 * if (cache instanceof Coherence35xCacheImpl) {
	 * NamedCache namedCache = ((Coherence35xCacheImpl)cache).getNamedCache();
	 * com.tangosol.util.Filter filter;
	 * // simple filter on person lastName
	 * filter = new EqualsFilter("getLastName", "DUPONT");
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(1, namedCache.entrySet(filter).size());
	 * // simple filter on person lastName length
	 * filter = new GreaterEqualsFilter(new ChainedExtractor("getLastName.trim.length"), 5);
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(2, namedCache.entrySet(filter).size());
	 * // simple filter on person lastName
	 * filter = new LikeFilter("getLastName", "DUPON%", true);
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(2, namedCache.entrySet(filter).size());
	 * // simple filter on person city adress
	 * filter = new EqualsFilter("getMainAdress.getCity", "MARSEILLE");
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(1, namedCache.entrySet(filter).size());
	 * // filter on person mainAdress
	 * filter = new AndFilter(new EqualsFilter("getMainAdress.getCity", "MARSEILLE"), new IsNotNullFilter("getLastName"));
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(1, namedCache.entrySet(filter).size());
	 * // filter on person adresses
	 * filter = new ContainsFilter("getAdresses", new Adress("10 RUE DE RIVOLI", "ETAGE 3", "13006", "MARSEILLE"));
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(1, namedCache.entrySet(filter).size());
	 * // filter on person adresses count (not work for Collections.unmodifiableList(list))
	 * filter = new GreaterEqualsFilter(new ChainedExtractor("getAdresses.size"), 2);
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(2, namedCache.entrySet(filter).size());
	 * // filter on person adresses
	 * filter = new ContainsFilter(new ListElementAttributeExtractor(new ReflectionExtractor("getAdresses"),new
	 * ReflectionExtractor("getCity")) , "MARSEILLE");
	 * assertNotNull(namedCache.entrySet(filter));
	 * assertEquals(2, namedCache.entrySet(filter).size());
	 * }
	 * if (cache instanceof Infinispan4xCacheImpl) {
	 * org.infinispan.Cache infiniCache = ((Infinispan4xCacheImpl)cache).getInfinispanCache();
	 * // The QueryHelper must be instantiated before putting objects into the cache.
	 * //QueryHelper qh = new QueryHelper(this.cache, new Properties(), myClassToQuery.class);
	 * // TODO
	 * }
	 */
   }

}
