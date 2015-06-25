'use strict';

angular.module('nodesoftApp')
    .controller('TopicDetailController', function ($scope, $stateParams, Topic, Forum, User, Post) {
        $scope.topic = {};
        $scope.load = function (id) {
            Topic.get({id: id}, function(result) {
              $scope.topic = result;
            });
        };
        $scope.load($stateParams.id);
    });
