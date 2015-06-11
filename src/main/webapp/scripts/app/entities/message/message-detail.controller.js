'use strict';

angular.module('nodesoftApp')
    .controller('MessageDetailController', function ($scope, $stateParams, Message, User, Ticket) {
        $scope.message = {};
        $scope.load = function (id) {
            Message.get({id: id}, function(result) {
              $scope.message = result;
            });
        };
        $scope.load($stateParams.id);
    });
