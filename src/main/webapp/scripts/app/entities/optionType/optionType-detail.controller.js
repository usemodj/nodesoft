'use strict';

angular.module('nodesoftApp')
    .controller('OptionTypeDetailController', function ($scope, $stateParams, OptionType) {
        $scope.optionType = {};
        $scope.load = function (id) {
            OptionType.get({id: id}, function(result) {
              $scope.optionType = result;
            });
        };
        $scope.load($stateParams.id);
    });
