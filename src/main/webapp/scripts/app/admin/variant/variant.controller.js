'use strict';

angular.module('nodesoftApp')
    .controller('VariantController', function ($scope, $stateParams, Variant, Product, OptionValue, VariantSearch, ParseLinks) {
        $scope.variants = [];
        $scope.page = 1;
        $scope.deleted = false;
        var beforeSort = '';
        var sorted = false;

        Product.get({id: $stateParams.productId}, function(result){
        	$scope.product = result;
        	console.log(result)
        });
        
        $scope.loadAll = function() {
            Variant.query({productId: $stateParams.productId, deleted: $scope.deleted}, function(result, headers) {
                //$scope.links = ParseLinks.parse(headers('link'));
                $scope.variants = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Variant.get({id: id}, function(result) {
                $scope.variant = result;
                $('#saveVariantModal').modal('show');
            });
        };

        $scope.save = function () {
        	$scope.variant.productId = $stateParams.productId;
        	console.log($scope.variant);
        	
            if ($scope.variant.id != null) {
                Variant.update($scope.variant,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Variant.save($scope.variant,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.active = function(id){
        	Variant.setActive({id: id}, function(result){
        		$scope.deleted = false;
        		$scope.loadAll();
        	});
        },
        
        $scope.delete = function (id) {
            Variant.get({id: id}, function(result) {
                $scope.variant = result;
                $('#deleteVariantConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Variant.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteVariantConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            VariantSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.variants = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveVariantModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.variant = {sku: null, isMaster: null, deletedDate: null, price: null, costPrice: null, costCurrency: null, position: null, weight: null, height: null, width: null, depth: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
        
        $scope.sortableOptions = {
            change: function(e, ui) {
                var entry = $scope.variants.map(function(item){
                    return item.id;
                }).join(',');
                beforeSort = entry;
            },
            // called after a node is dropped
            stop: function(e, ui) {
                var entry = $scope.variants.map(function(item){
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
            Variant.updatePosition({entry: entry}, function(result){
            	$scope.loadAll();
            });
        };
    });
