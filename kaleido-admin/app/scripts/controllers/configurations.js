'use strict';

angular.module('kaleidoAdminApp')
  .controller('ConfigurationListCtrl', function ($scope, ConfigurationService) {

    ConfigurationService.fetchAll($scope);

  })
  .controller('ConfigurationDetailCtrl', function ($scope, $routeParams, ConfigurationService) {

    ConfigurationService.fetchByName($routeParams.configName, $scope);

  })  
  ;
