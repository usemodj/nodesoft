'use strict';

angular.module('nodesoftApp')
    .controller('TicketController', function ($scope, Ticket, User, Message, TicketSearch, ParseLinks) {
        $scope.tickets = [];
        $scope.users = User.query();
        $scope.messages = Message.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Ticket.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.tickets = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Ticket.get({id: id}, function(result) {
                $scope.ticket = result;
                $('#saveTicketModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.ticket.id != null) {
                Ticket.update($scope.ticket,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Ticket.save($scope.ticket,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Ticket.get({id: id}, function(result) {
                $scope.ticket = result;
                $('#deleteTicketConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Ticket.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTicketConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            TicketSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.tickets = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTicketModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ticket = {subject: null, status: null, views: null, replies: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
