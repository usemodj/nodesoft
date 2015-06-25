'use strict';

angular.module('nodesoftApp')
    .controller('TaxonController', function ($scope, $stateParams, Taxon, Taxonomy, TaxonSearch, ParseLinks) {
        $scope.taxons = [];
        $scope.page = 1;
        
        $scope.taxonomy = {};
        $scope.load = function (id) {
            Taxonomy.get({id: id}, function(result) {
              $scope.taxonomy = result;
              //if($scope.taxonomy.taxons) $scope.taxonomy.nodes = makeTree({q: $scope.taxonomy.taxons});
              if($scope.taxonomy.taxons) $scope.taxonomy.nodes = [$scope.taxonomy.taxons[0]];
              console.log(result)
            });
        };
        $scope.load($stateParams.id);

        $scope.newSubItem = function(scope) {
            var nodeData = scope.$modelValue;
            //console.log(scope);
            nodeData.children.push({
                id: nodeData.id * 10 + nodeData.children.length,
                name: nodeData.name + '.' + (nodeData.children.length + 1),
                children: []
            });

        };

        $scope.editItem = function(scope) {
            //console.log(scope);
            scope.editing = true;
        };

        $scope.cancelEditingItem = function(scope) {
            scope.editing = false;
        };

        $scope.saveItem = function(scope) {
            scope.editing = false;
        };

        var getRootNodesScope = function() {
            return angular.element(document.getElementById("tree-root")).scope();
        };

        $scope.collapseAll = function() {
            var scope = getRootNodesScope();
            scope.collapseAll();
        };

        $scope.expandAll = function() {
            var scope = getRootNodesScope();
            scope.expandAll();
        };

        $scope.showUpdate = function (id) {
            Taxon.get({id: id}, function(result) {
                $scope.taxon = result;
                $('#saveTaxonModal').modal('show');
            });
        };

        $scope.save = function () {
        	$scope.message = '';
            $scope.taxonomy.taxons = visitor($scope.taxonomy.nodes);
            console.log($scope.taxonomy);
            
            Taxonomy.updateTaxons($scope.taxonomy, function(result){
            	$scope.message = 'Taxons saved!';
            });
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
