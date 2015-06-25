'use strict';

angular.module('nodesoftApp')
    .controller('VariantDetailController', function ($scope, $stateParams, Variant, Product, OptionValue) {
        $scope.variant = {};
        $scope.load = function (id) {
            Variant.get({id: id}, function(result) {
              $scope.variant = result;
            });
        };
        $scope.load($stateParams.id);
    });
