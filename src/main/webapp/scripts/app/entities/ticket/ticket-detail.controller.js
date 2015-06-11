'use strict';

angular.module('nodesoftApp')
    .controller('TicketDetailController', function ($scope, $stateParams, Ticket, User, Message) {
        $scope.ticket = {};
        $scope.load = function (id) {
            Ticket.get({id: id}, function(result) {
              $scope.ticket = result;
            });
        };
        $scope.load($stateParams.id);
    });
