'use strict';

/**
 * @ngdoc directive
 * @name staticApp.directive:userList
 * @description
 * # userList
 */
angular.module('staticApp')
  .directive('userList', function () {
    return {
      templateUrl: 'views/userlist.html',
      restrict: 'E',
      controller: 'UserlistCtrl'
    };
  });
