'use strict';

angular.module('nodesoftApp')
    .controller('AssetDetailController', function ($scope, $stateParams, Asset) {
        $scope.asset = {};
        $scope.load = function (id) {
            Asset.get({id: id}, function(result) {
              $scope.asset = result;
            });
        };
        $scope.load($stateParams.id);
    });
