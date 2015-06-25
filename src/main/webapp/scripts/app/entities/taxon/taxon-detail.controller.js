'use strict';

angular.module('nodesoftApp')
    .controller('TaxonDetailController', function ($scope, $stateParams, Taxon, Taxonomy) {
        $scope.taxon = {};
        $scope.load = function (id) {
            Taxon.get({id: id}, function(result) {
              $scope.taxon = result;
            });
        };
        $scope.load($stateParams.id);
    });
