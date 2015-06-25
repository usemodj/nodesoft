'use strict';

angular.module('nodesoftApp')
    .controller('TopicController', function ($scope, Topic, Forum, User, Post, TopicSearch, ParseLinks) {
        $scope.topics = [];
        $scope.forums = Forum.query();
        $scope.users = User.query();
        $scope.posts = Post.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Topic.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.topics = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Topic.get({id: id}, function(result) {
                $scope.topic = result;
                $('#saveTopicModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.topic.id != null) {
                Topic.update($scope.topic,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Topic.save($scope.topic,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Topic.get({id: id}, function(result) {
                $scope.topic = result;
                $('#deleteTopicConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Topic.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTopicConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            TopicSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.topics = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTopicModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.topic = {name: null, views: null, replies: null, locked: null, sticky: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
