'use strict';

/**
 * @ngdoc directive
 * @name staticApp.directive:messageList
 * @description
 * # messageList
 */
angular.module('staticApp')
  .directive('messageList', function () {
    return {
      templateUrl: 'views/messagelist.html',
      restrict: 'E',
      controller: 'MessagelistCtrl'
    };
  });
