'use strict';

angular.module('nodesoftApp')
    .controller('TaxonomyController', function ($scope, $filter, Taxonomy, TaxonomySearch, ParseLinks) {
        $scope.taxonomys = [];
        $scope.page = 1;
        var beforeSort = '';
        var sorted = false;

        $scope.loadAll = function() {
            Taxonomy.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.taxonomys = result;
                $scope.taxonomys = $filter('orderBy')($scope.taxonomys, 'position', false);
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

        $scope.confirmDelete = function (taxonomy) {
            Taxonomy.delete({id: taxonomy.id},
                function () {
                    $scope.loadAll();
                    //$scope.taxonomys.splice($scope.taxonomys.indexOf(taxonomy), 1);
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
        
        $scope.sortableOptions = {
                change: function(e, ui) {
                    var entry = $scope.taxonomys.map(function(item){
                        return item.id;
                    }).join(',');
                    beforeSort = entry;
                },
                // called after a node is dropped
                stop: function(e, ui) {
                    var entry = $scope.taxonomys.map(function(item){
                        return item.id;
                    }).join(',');
                    sorted = entry != beforeSort;
                    // IF sorted == true, updatePosition()
                    if(sorted){
                        $scope.updatePosition(entry);
                    }
                }
            };

            $scope.updatePosition = function(entry){
                Taxonomy.updatePosition({entry: entry}, function(result){
                	$scope.loadAll();
                });
            };
    });
    
