'use strict';

angular.module('nodesoftApp')
    .controller('ArticleDetailController', function ($scope, $stateParams, Article, User) {
        $scope.article = {};
        $scope.load = function (id) {
            Article.get({id: id}, function(result) {
              $scope.article = result;
            });
        };
        $scope.load($stateParams.id);
    });
