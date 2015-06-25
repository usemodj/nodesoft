'use strict';

angular.module('nodesoftApp')
    .controller('ProductDetailController', function ($scope, $stateParams, Product, OptionType, Taxon) {
        $scope.product = {};
        $scope.load = function (id) {
            Product.get({id: id}, function(result) {
              $scope.product = result;
            });
        };
        $scope.load($stateParams.id);
    });
