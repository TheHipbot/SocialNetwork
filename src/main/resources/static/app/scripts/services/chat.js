'use strict';

/**
 * @ngdoc service
 * @name staticApp.chat
 * @description
 * # chat
 * Factory in the staticApp.
 */
angular.module('staticApp')
  .factory('chat', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

    var service = this,
        ws = new WebSocket('ws://localhost:8080/chat/'),
        handlers = {};

    service.connect = function (id, name) {
      id = id || '';
      ws.send('{"event": "connect","id":"' + id + '","name":"' + name + '"}');
    };

    service.getAllUsers = function (id) {
      id = id || '';
      ws.send('{"event": "getConnectedUsers","id":"' + id + '"}');
    };

    service.getConnectedFollowers = function (id) {
      id = id || '';
      ws.send('{"event": "getConnectedFollowers","id":"' + id + '"}');
    };

    service.getAllFollowers = function (id) {
      id = id || '';
      ws.send('{"event": "getFollowers","id":"' + id + '"}');
    };

    service.registerUser = function (name) {
      ws.send('{"event": "register","name":"' + name + '"}');
    };

    service.followUser = function (id, follow) {
      ws.send('{"event": "followUser","id":"' + id + '","follow":"' + follow + '"}');
    };

    service.createTweet = function (id, name, hash, msg) {
      ws.send('{"event": "createTweet","id":"' + id + '","name":"' + name + '", "hash":"' + hash + '", "msg":"' + msg + '"}');
    };

    service.getTweets = function (ids) {
      var obj = {
        event: "getTweets",
        ids: ids,
        hash: ""
      };

      ws.send(JSON.stringify(obj));
    };

    service.getTweetsByHash = function (hash) {
      ws.send('{"event": "getTweets","ids":"", "hash":"' + hash + '"}');
    };

    function eventHandler(event) {
      var data = JSON.parse(event.data),
          eventType,
          callback;

      eventType = data.event;

      callback = handlers[eventType];

      if (angular.isFunction(callback)) {
        callback(data);
      }
      eventBroadcaster(event);
    }

    function eventBroadcaster(event) {
      var data = JSON.parse(event.data);

      console.log('Received event: ' + data.event);
      console.log('Data: ' + event.data);

      $rootScope.$broadcast('chat:' + data.event, data);
    }

    function socketOpenHandler() {
      //$rootScope.$broadcast('chat:connected', 'connected');
      var id = localStorage.getItem('id');
      service.connect(localStorage.getItem('id'), localStorage.getItem('name'));
      service.getAllFollowers(id);
    }

    function resetStorage () {
      localStorage.setItem('id', '');
      localStorage.setItem('name', '');
      localStorage.setItem('following', '');
    }

    handlers.createdUser = function (data) {
      localStorage.setItem('id', data.id);
      localStorage.setItem('name', data.name);
      localStorage.setItem('following', JSON.stringify(data.following));
    };

    handlers.invalid = function (data) {
      resetStorage();
      $rootScope.$broadcast('chat:register', 'invalid');
    };

    handlers.userList = function (data) {
      var curr,
          update;
      if (data.userType === 'followers') {
        curr = JSON.parse(localStorage.getItem('followers'));
        update = angular.extend({}, curr, data.users);
        localStorage.setItem('followers', JSON.stringify(update));
      }
    };

    ws.onmessage = eventHandler;

    ws.onopen = function () {
      $timeout(socketOpenHandler, 1000);
    };

    ws.onclose = function () {
      $rootScope.$broadcast('chat:disconnected', 'disconnected');
    };

    return service;
  }]);
