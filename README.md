KaleidoFoundry project [![Build Status](https://buildhive.cloudbees.com/job/kaleidofoundry/job/kaleido-repository/badge/icon)](https://buildhive.cloudbees.com/job/kaleidofoundry/job/kaleido-repository/)
=============

[KaleidoFoundry][KaleidoFoundry] is a java technical foundation, **productive, plugeable, scalable and extensible**. It provides modules for configuration, caching, i18n, messaging... Start simple, and when you will need a more complex architecture like a clustered environment, a robust caching provider solution, a messaging system... **kaleido will fit** without changing your java code. The main features are:

 * `Configuration` management REST API, dynamic and centralized (across cluster or local), manageable using REST API,
 * `File storage` abstraction API (`file` / `classpath` / `http` / `ftp` / `webapp` / `clob` / 'gae' / `ssh`),
 * `Caching` integration and abstraction API (`ehcache`, `infinispan`, `coherence`, `gae`, `local map`),
 * `I18n` service management for labels & exception messages (`resource bundle API extension`),
 * `Naming service` for local or remote jndi access (`datasource` / `jms queue` / `ejb`),
 * `Messaging` (asynchronous) client (`jms`, `amq`, `mq`, `tibco rdv` / `ems`),
 * `Mail` sender service (`async` or not, `local` or `centralized`),
 * `Configuration` abstraction API (`properties`, `xml`, `yaml`, `database`, ...).

Kaleido's components are based on a runtime `@Context` **injection mecanism**. You benefit a **dynamic and centralized** configuration management in your modules (across clusters nodes...). It fits with your favorite IOC like Java `CDI`, `Spring` framework, `Guice`, or can be used standalone with aspectJ...


Requirements
------------
 * Java >= 1.6 
 * Java EE 5 & 6  (if you need an application server)


[KaleidoFoundry]: http://http://www.kaleidofoundry.org