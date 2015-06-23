'use strict';

describe('Controller: UserlistCtrl', function () {

  // load the controller's module
  beforeEach(module('staticApp'));

  var UserlistCtrl,
      scope,
      chat;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope, _chat_) {
    chat = _chat_;
    scope = $rootScope.$new();
    UserlistCtrl = $controller('UserlistCtrl', {
      $scope: scope
    });
  }));

  describe('#getAllUsers()', function () {
    it('should send getAllUsers message via chat service', function () {
      spyOn(chat, 'getAllUsers');
      scope.getAllUsers();
      expect(chat.getAllUsers).toHaveBeenCalled();
    });
  });

});
