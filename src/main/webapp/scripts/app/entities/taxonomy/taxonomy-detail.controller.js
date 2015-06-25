'use strict';

angular.module('nodesoftApp')
    .controller('TaxonomyDetailController', function ($scope, $stateParams, Taxonomy) {
        $scope.taxonomy = {};
        $scope.load = function (id) {
            Taxonomy.get({id: id}, function(result) {
              $scope.taxonomy = result;
            });
        };
        $scope.load($stateParams.id);
    });
