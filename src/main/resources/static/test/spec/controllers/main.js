'use strict';

describe('Controller: MainCtrl', function () {

  // load the controller's module
  beforeEach(module('staticApp'));

  var MainCtrl,
      scope,
      chat;

  //localStorage = jasmine.createSpyObj('ls', ['getItem', 'setItem']);

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope, _chat_) {
    scope = $rootScope.$new();
    MainCtrl = $controller('MainCtrl', {
      $scope: scope
    });
    chat = _chat_;
  }));

  describe('#updateConnection()', function () {

    beforeEach(function () {
      spyOn(localStorage, 'getItem').and.returnValue('jeremy');
    });

    it('should update name from local storage', function () {
      MainCtrl.updateConnection(true);
      expect(localStorage.getItem).toHaveBeenCalledWith('name');
      expect(scope.name).toBe('jeremy');
    });

    it('should update connected with call parameter', function () {
      MainCtrl.updateConnection(true);
      expect(scope.connected).toBe(true);
      MainCtrl.updateConnection(false);
      expect(scope.connected).toBe(false);
    });

    it('should update connectedText with connection status', function () {
      MainCtrl.updateConnection(true);
      expect(scope.connectedText).toBe('jeremy Connected');
      MainCtrl.updateConnection(false);
      expect(scope.connectedText).toBe('Not Connected');
    });
  });

  describe('#submitRegistration()', function () {

    beforeEach(function () {
      spyOn(chat, 'registerUser');
      spyOn(localStorage, 'getItem').and.returnValue('jeremy');
    });

    it('should call chat.registerUser if name is set', function () {
      MainCtrl.updateConnection(true);
      scope.submitRegistration();
      expect(chat.registerUser).toHaveBeenCalledWith('jeremy');
    });

    it('should not call chat.registerUser if name is not set', function () {
      scope.name = '';
      scope.submitRegistration();
      expect(chat.registerUser).not.toHaveBeenCalled();

      scope.name = null;
      scope.submitRegistration();
      expect(chat.registerUser).not.toHaveBeenCalled();
    });
  });
});
