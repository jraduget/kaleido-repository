'use strict';

angular.module('kaleidoAdminApp')
    .service('ConfigurationService', function ($http) {

    /**
     * @class ConfiguraitonService
     */
    return {
        update: function (input) {
            var output;
            // do something with the data
            return output;
        },

        fetchAll: function (scope) {
            /*
            $http.get('http://localhost:8080/kaleido-it-war/rest/configurations', {headers: {'Access-Control-Allow-Origin': 'true'}})
                .success(function(data, status, headers, config) {
                    alert(data);
                    scope.configurations = data.configuration;
                })
                .error(function(data, status, headers, config) {
                    // TODO notification widget
                    alert(data);
                });
            */
            scope.configurations = [{"uri":"classpath:/config/myConfig.properties","name":"myConfig","properties":{"property":[{"name":"//myapp/admin/email","value":"myadmin@mysociete.com","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/sample/boolean","value":"false","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/sample/float","value":"123.45","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/name","value":"my new application","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/sample/date","value":"2010-12-01T02:45:30","type":"java.lang.String","description":"<no persistent property description>"}]},"loaded":"true","storable":"true","updateable":"true"},{"uri":"classpath:/cache/myContext.properties","name":"myCacheConfig","properties":{"property":[{"name":"//cacheManagers/myCacheManager/fileStoreUri","value":"classpath:/cache/ehcache.xml","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//cacheManagers/myCacheManager/providerCode","value":"ehCache","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//caches/myCache/cacheName","value":"CacheSample01","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//caches/myCache/cacheManagerRef","value":"myCacheManager","type":"java.lang.String","description":"<no persistent property description>"}]},"loaded":"true","storable":"true","updateable":"true"},{"uri":"classpath:/store/myContext.properties","name":"myStore","properties":{"property":[{"name":"//fileStores/myStore/connectTimeout","value":"1500","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//fileStores/myStore/readTimeout","value":"10000","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//fileStores/myStore/readonly","value":"false","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//fileStores/myStore/baseUri","value":"http://localhost:8380/kaleido-it/","type":"java.lang.String","description":"<no persistent property description>"}]},"loaded":"true","storable":"true","updateable":"true"},{"uri":"classpath:/i18n/myContext.properties","name":"myI18nConfig","properties":{"property":[{"name":"//i18ns/myBundle/locale/lang","value":"en","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//i18ns/myBundle/locale/country","value":"GB","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//i18ns/myBundle/baseName","value":"i18n/messages","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//cacheManagers/myCacheManager/providerCode","value":"ehCache","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//cacheManagers/myCacheManager/fileStoreUri","value":"classpath:/i18n/ehcache.xml","type":"java.lang.String","description":"<no persistent property description>"}]},"loaded":"true","storable":"true","updateable":"true"}];

            var cpt=0;
            scope.configurations.forEach(function(entry) {
                entry.order=cpt++;
            });
        },

        fetchByName: function (configName, scope) {
            scope.config = {"uri":"classpath:/config/myConfig.properties","name":"myConfig","properties":{"property":[{"name":"//myapp/admin/email","value":"myadmin@mysociete.com","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/sample/boolean","value":"false","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/sample/float","value":"123.45","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/name","value":"my new application","type":"java.lang.String","description":"<no persistent property description>"},{"name":"//myapp/sample/date","value":"2010-12-01T02:45:30","type":"java.lang.String","description":"<no persistent property description>"}]},"loaded":"true","storable":"true","updateable":"true"};
        }
    };
});