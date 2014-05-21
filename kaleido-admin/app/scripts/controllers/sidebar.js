'use strict';

angular.module('kaleidoAdminApp')
  .controller('SidebarCtrl', function ($scope, $location) {

    // menu items
    $scope.menu = [{
    	'title': 'Environments',
    	'link' : 'environments'
    }
    , {
    	'title': 'Configurations',
    	'link' : 'configurations'
    }
    , {
    	'title': 'Stores',
    	'link' : 'fs'
    }
    , {
    	'title': 'Caching',
    	'link' : 'caching'
    }
	, {
    	'title': 'I18n',
    	'link' : 'i18n'
    } 
    , {
    	'title': 'Messaging',
    	'link' : 'messaging'
    } 
    , {
    	'title': 'Mailing',
    	'link' : 'mailling'
    }           
    ];
    
    $scope.isActive = function(route) {
      return route === $location.path();
    };
    

    // menu action selection
    $scope.select = function(link) {
        //alert(link);
    }
  });
