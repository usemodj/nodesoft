'use strict';

angular.module('nodesoftApp')
    .controller('ArticleController', function ($scope, Article, User, ArticleSearch, ParseLinks) {
        $scope.articles = [];
        $scope.users = User.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Article.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.articles.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.articles = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Article.get({id: id}, function(result) {
                $scope.article = result;
                $('#saveArticleModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.article.id != null) {
                Article.update($scope.article,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Article.save($scope.article,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Article.get({id: id}, function(result) {
                $scope.article = result;
                $('#deleteArticleConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Article.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteArticleConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            ArticleSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.articles = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $('#saveArticleModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.article = {subject: null, summary: null, content: null, imgUrl: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
