'use strict';

angular.module('kaleidoAdminApp')
  .controller('NavbarCtrl', function ($scope, $location) {
    $scope.menu = [{
      'title': 'Settings',
      'link': '/settings'
    } ,
    {
      'title': 'Profile',
      'link': '/profile'
    },
    {
      'title': 'Help',
      'link': '/help'
    }  
    ];
    
    $scope.isActive = function(route) {
      return route === $location.path();
    };


  });
