'use strict';

describe('Directive: messageList', function () {

  // load the directive's module
  beforeEach(module('staticApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<message-list></message-list>');
    element = $compile(element)(scope);
  }));
});
