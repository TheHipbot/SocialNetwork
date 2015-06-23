'use strict';

/**
 * @ngdoc function
 * @name staticApp.controller:UserlistCtrl
 * @description
 * # UserlistCtrl
 * Controller of the staticApp
 */
angular.module('staticApp')
  .controller('UserlistCtrl', ['$scope', '$rootScope', 'chat', function ($scope, $rootScope, chat) {

    var id;

    $scope.allConnectedUsers = {};
    $scope.connectedFollowers = {};
    $scope.allFollowers = {};

    $scope.followUser = function (follow) {
      chat.followUser(id, follow);
    };

    $scope.$on('chat:connected', function () {
      id = localStorage.getItem('id');
      chat.getAllUsers(id);
    });

    $scope.$on('chat:userUpdate', function (event, data) {
      if (data.connected) {
        if (data.follower) {
          $scope.$apply(function () {
            delete $scope.allConnectedUsers[data.id];
            $scope.connectedFollowers[data.id] = data.name;
          });
        } else {
          $scope.$apply(function () {
            $scope.allConnectedUsers[data.id] = data.name;
          });
        }
      } else {
        $scope.$apply(function () {
          delete $scope.allConnectedUsers[data.id];
          delete $scope.connectedFollowers[data.id];
        });
      }
    });

    $scope.$on('chat:userList', function (event, data) {
      if (data.userType === 'all' && data.updateType === 'connected') {
        console.log('Received userList allConnected');
        console.log(data.users);
        $scope.allConnectedUsers = data.users;
      } else if (data.userType === 'followers' && data.updateType === 'connected') {
        console.log('Received userList connectedFollowers');
        console.log(data.users);
        $scope.connectedFollowers = data.users;
      }
      $scope.$apply(function () {
        $scope.connectedUsers = data.users;
      });
    });
  }]);
