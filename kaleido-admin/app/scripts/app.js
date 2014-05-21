'use strict';

angular.module('kaleidoAdminApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ui.bootstrap'
])
  .config(function ($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {
        redirectTo: '/configurations'
      })
      .when('/configurations', {
        templateUrl: 'partials/configuration-list.html',
        controller: 'ConfigurationListCtrl'
      })
      .when('/configurations/:configName', {
        templateUrl: 'partials/configuration-detail.html',
        controller: 'ConfigurationDetailCtrl'
      })      
      .otherwise({
        redirectTo: '/'
      });
      
    $locationProvider.html5Mode(true);
  });