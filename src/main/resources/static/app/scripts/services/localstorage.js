'use strict';

/**
 * @ngdoc service
 * @name staticApp.localStorage
 * @description
 * # localStorage
 * Factory in the staticApp.
 */
angular.module('staticApp')
  .factory('localStorage', function () {
    // Service logic
    // ...

    var service = this;

    service.setItem = function (key, val) {
      localStorage.setItem(key, val);
    };

    service.getItem = function (key) {
      localStorage.getItem(key);
    };

    return service;
  });
