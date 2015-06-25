'use strict';

angular.module('nodesoftApp')
    .controller('ForumDetailController', function ($scope, $stateParams, Forum, Topic, Post) {
        $scope.forum = {};
        $scope.load = function (id) {
            Forum.get({id: id}, function(result) {
              $scope.forum = result;
            });
        };
        $scope.load($stateParams.id);
    });
