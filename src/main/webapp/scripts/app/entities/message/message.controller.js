'use strict';

angular.module('nodesoftApp')
    .controller('MessageController', function ($scope, Message, User, Ticket, MessageSearch, ParseLinks, Auth) {
        $scope.messages = [];
        $scope.users = User.query();
        $scope.tickets = Ticket.query();
        $scope.page = 1;
        console.log('>> auth.identity:');
        Auth.identity(false, function(account){
        	console.log(account);
        });
        var user = Auth.identity2();
        console.log(user);
        
        $scope.loadAll = function() {
            Message.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.messages = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Message.get({id: id}, function(result) {
                $scope.message = result;
                $('#saveMessageModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.message.id != null) {
                Message.update($scope.message,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Message.save($scope.message,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Message.get({id: id}, function(result) {
                $scope.message = result;
                $('#deleteMessageConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Message.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteMessageConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            MessageSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.messages = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveMessageModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.message = {content: null, root: null, ipaddress: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
