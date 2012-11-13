KaleidoFoundry project
=============

[KaleidoFoundry][KaleidoFoundry] is a java technical foundation, **productive, plugeable, scalable and extensible**. It provides modules for configuration, caching, i18n, messaging... Start simple, and when you will need a more complex architecture like a clustered environment, a robust caching provider solution, a messaging system...**kaleido will fit** without changing your java code. The main modules are:

 * a `configuration` service management, dynamic and centralized (across cluster or local), manageable using REST API,
 * a `file storage` abstraction (`file` / `classpath` / `http` / `ftp` / `webapp` / `clob` / `ssh`),
 * a `caching` integration and abstraction (`ehcache`, `infinispan`, `coherence`, `gae`, `local map`),
 * an `i18n` service management for labels & exception messages (`resource bundle API extension`),
 * a `naming service` for local or remote jndi access (`datasource` / `jms queue` / `ejb`),
 * an asynchronous `messaging` client (`jms`, `amq`, `mq`, `tibco rdv` / `ems`),
 * and a `mail` sender service (`async` or not, `local` or `centralized`).
 
Kaleido's components are based on a runtime **`@Context` injection mecanism**. You benefit a **dynamic and centralized** configuration management in your modules (across clusters, modules...). It fits with your favorite IOC like Java `CDI`, `Spring` framework, `Guice`, or can be used standalone with aspectJ...


Requirements
------------
 * Java >= 1.6 
 * Java EE 5 & 6  (if you need an application server)

Modules
------------
 * kaleido-core	: the core modules
   - File storage API abstraction for (`http, ftp, classpath, webapp, jpa clob, ...`)
   - Caching API abstraction for (`local map, ehcache, jboss cache, jboss infinispan, oracle coherence`, ...)
   - Configuration API abstraction for `properties, xml, yaml, database...` (based on cache and resource store)
   - I18n java API extension, for label & exceptions (resource bundle extention, based on cache and resource store)
   - Naming service API `jndi lookup ejb, jms queue / topic, datasource, ...`
   - Context injection API `@Context` enable runtime configuration injection
   - Other thinks like `@NotNull, @NotYetImplemented, @PersistenceContext` in unmanaged env...
 * kaleido-jee6	: Jave EE6 integration module (for context injection, jpa 2 ...)
 * kaleido-spring : 	Spring integration module (for context injection)
 * kaleido-guice :	Guice integration module (for context injection)
 * kaleido-messaging  :  Asynchronous messaging module : `jms, amq, websphere mq, tibco rdv...` 

Integration test modules 
------------
 * kaleido-it-parent
 * kaleido-it-core
 * kaleido-it-jee5
 * kaleido-it-jee6
 * kaleido-it-war
 * kaleido-it-ear

Upcoming 
------------
 * kaleido-mail        Mailling module
 * kaleido-webadmin	Web admin (i18n, configuration, log, cache informations, messages...)



[KaleidoFoundry]: http://http://www.kaleidofoundry.org