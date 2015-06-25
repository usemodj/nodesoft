'use strict';

angular.module('nodesoftApp')
    .controller('PostController', function ($scope, Post, User, Forum, Topic, PostSearch, ParseLinks) {
        $scope.posts = [];
        $scope.users = User.query();
        $scope.forums = Forum.query();
        $scope.topics = Topic.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Post.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.posts = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Post.get({id: id}, function(result) {
                $scope.post = result;
                $('#savePostModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.post.id != null) {
                Post.update($scope.post,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Post.save($scope.post,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Post.get({id: id}, function(result) {
                $scope.post = result;
                $('#deletePostConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Post.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deletePostConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            PostSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.posts = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#savePostModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.post = {name: null, content: null, root: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
