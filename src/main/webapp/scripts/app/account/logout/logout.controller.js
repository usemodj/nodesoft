'use strict';

angular.module('nodesoftApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
