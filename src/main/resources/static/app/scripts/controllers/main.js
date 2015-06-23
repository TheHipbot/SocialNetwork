'use strict';

/**
 * @ngdoc function
 * @name staticApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the staticApp
 */
angular.module('staticApp')
  .controller('MainCtrl', ['$scope', 'chat', function ($scope, chat) {
    var ctrl = this;

    $scope.connected = false;
    $scope.register = false;
    $scope.connectedText = 'Not Connected';
    $scope.name = '';

    $scope.$on('chat:connected', function (event, data) {
      $scope.$apply(function() {
        ctrl.updateConnection(true);
        console.log('connected');
      });
    });

    $scope.$on('chat:disconnected', function (event, data) {
      ctrl.updateConnection(false);
    });

    $scope.$on('chat:register', function (event, data) {
      console.log('Register event received: ' + data);
      $scope.$apply(function () {
        $scope.register = true;
      });
    });

    $scope.$on('chat:createdUser', function (event, data) {
      $scope.$apply(function () {
        $scope.register = false;
      });
    });

    $scope.submitRegistration = function () {
      console.log('Registering ' + $scope.name);
      if ($scope.name) {
        chat.registerUser($scope.name);
      }
    };

    ctrl.updateConnection = function(isConnected) {
      $scope.name = localStorage.getItem('name');
      $scope.connected = isConnected;
      $scope.connectedText = isConnected? $scope.name + ' Connected' : 'Not Connected';
    };

  }]);
