'use strict';

/**
 * @ngdoc directive
 * @name staticApp.directive:main
 * @description
 * # main
 */
angular.module('staticApp')
  .directive('main', function () {
    return {
      templateUrl: 'views/main.html',
      restrict: 'E',
      controller: 'MainCtrl'
    };
  });
