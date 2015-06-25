'use strict';

angular.module('nodesoftApp')
    .controller('TaxonomyController', function ($scope, Taxonomy, TaxonomySearch, ParseLinks) {
        $scope.taxonomys = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Taxonomy.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.taxonomys = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Taxonomy.get({id: id}, function(result) {
                $scope.taxonomy = result;
                $('#saveTaxonomyModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.taxonomy.id != null) {
                Taxonomy.update($scope.taxonomy,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Taxonomy.save($scope.taxonomy,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Taxonomy.get({id: id}, function(result) {
                $scope.taxonomy = result;
                $('#deleteTaxonomyConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Taxonomy.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTaxonomyConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            TaxonomySearch.query({query: $scope.searchQuery}, function(result) {
                $scope.taxonomys = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTaxonomyModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.taxonomy = {name: null, position: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
