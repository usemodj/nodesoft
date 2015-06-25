'use strict';

angular.module('nodesoftApp')
    .controller('PostDetailController', function ($scope, $stateParams, Post, User, Forum, Topic) {
        $scope.post = {};
        $scope.load = function (id) {
            Post.get({id: id}, function(result) {
              $scope.post = result;
            });
        };
        $scope.load($stateParams.id);
    });
