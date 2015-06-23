'use strict';

describe('Service: chat', function () {

  // load the service's module
  beforeEach(module('staticApp'));

  // instantiate service
  var chat,
      mockSock,
      wsMock = jasmine.createSpyObj('wsMock', ['onmessage', 'send']);

  mockSock = function () {
    return wsMock;
  };

  beforeEach(inject(function (_chat_) {
    chat = _chat_;
  }));

  it('should do something', function () {
    expect(!!chat).toBe(true);
  });

  describe('getAllUsers()', function () {

    xit('should retrieve a list of all users', function () {
      chat.getAllUsers();
      expect(wsMock.send).toHaveBeenCalled();
    });
  });

});
