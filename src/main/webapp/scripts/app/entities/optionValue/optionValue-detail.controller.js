'use strict';

angular.module('nodesoftApp')
    .controller('OptionValueDetailController', function ($scope, $stateParams, OptionValue, OptionType) {
        $scope.optionValue = {};
        $scope.load = function (id) {
            OptionValue.get({id: id}, function(result) {
              $scope.optionValue = result;
            });
        };
        $scope.load($stateParams.id);
    });
