'use strict';

angular.module('nodesoftApp')
    .controller('ForumController', function ($scope, Forum, Topic, Post, ForumSearch, ParseLinks) {
        $scope.forums = [];
        $scope.forum = {};
        $scope.page = 1;
        $scope.loadAll = function() {
            Forum.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.forums = result;
                console.log(result);
                
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Forum.get({id: id}, function(result) {
                $scope.forum = result;
                $('#saveForumModal').modal('show');
            });
        };

        $scope.save = function () {
        	
            if ($scope.forum.id != null) {
                Forum.update($scope.forum,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Forum.save($scope.forum,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Forum.get({id: id}, function(result) {
                $scope.forum = result;
                $('#deleteForumConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Forum.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteForumConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            ForumSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.forums = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveForumModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.forum = {name: null, description: null, display: null, lft: null, rgt: null, postCount: null, topicCount: null, locked: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
