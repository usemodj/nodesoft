'use strict';

angular.module('nodesoftApp')
    .controller('TaxonController', function ($scope, Taxon, Taxonomy, TaxonSearch, ParseLinks) {
        $scope.taxons = [];
        $scope.taxonomys = Taxonomy.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Taxon.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.taxons = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Taxon.get({id: id}, function(result) {
                $scope.taxon = result;
                $('#saveTaxonModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.taxon.id != null) {
                Taxon.update($scope.taxon,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Taxon.save($scope.taxon,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            Taxon.get({id: id}, function(result) {
                $scope.taxon = result;
                $('#deleteTaxonConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Taxon.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTaxonConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            TaxonSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.taxons = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTaxonModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.taxon = {name: null, position: null, permalink: null, lft: null, rgt: null, description: null, metaTitle: null, metaDescription: null, metaKeywords: null, depth: null, iconFileName: null, iconContentType: null, iconFileSize: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
