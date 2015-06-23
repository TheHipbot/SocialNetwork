'use strict';

/**
 * @ngdoc function
 * @name staticApp.controller:MessagelistCtrl
 * @description
 * # MessagelistCtrl
 * Controller of the staticApp
 */
angular.module('staticApp')
  .controller('MessagelistCtrl', ['$scope', 'chat', function ($scope, chat) {
    var id,
        name,
        ctrl;

    ctrl = this;

    $scope.tweets = [];
    $scope.hashTweets = [];

    $scope.searchHash = '';

    $scope.form = {
      hash: '',
      msg: ''
    };

    $scope.$on('chat:connected', function () {
      var followers = JSON.parse(localStorage.getItem('followers')),
          ids;

      ids = [];

      angular.forEach(followers, function (name, i) {
        ids.push(i);
      });

      id = localStorage.getItem('id');
      name = localStorage.getItem('name');

      chat.getTweets(ids);
    });

    $scope.$on('chat:tweet', function (event, data) {
      $scope.tweets.push({
        name: data.name,
        hash: data.hash,
        msg: data.msg
      });
      $scope.$apply();
      console.log($scope.tweets);
    });

    $scope.$on('chat:tweetsById', function (event, data) {
      $scope.tweets = data.tweets;
      $scope.$apply();
      console.log($scope.tweets);
    });

    $scope.$on('chat:tweetsByHash', function (event, data) {
      $scope.hashTweets = data.tweets;
      $scope.$apply();
    });

    $scope.createTweet = function () {
      chat.createTweet(id, name, $scope.form.hash, $scope.form.msg);
      $scope.form.hash = '';
      $scope.form.msg = '';
    };

    $scope.searchByHash = function () {
      chat.getTweetsByHash($scope.searchHash);
      $scope.searchHash = '';
    };

    function connectedHandler () {
      var followers = JSON.parse(localStorage.getItem('followers')),
        ids;

      ids = [];

      angular.forEach(followers, function (name, i) {
        ids.push(i);
      });

      id = localStorage.getItem('id');
      name = localStorage.getItem('name');

      chat.getTweets(ids);
    }
  }]);
