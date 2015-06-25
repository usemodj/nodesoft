'use strict';

angular.module('nodesoftApp')
    .controller('AdminProductController', function ($scope, $state, Product, OptionType, Taxon, ProductSearch, ParseLinks) {
        $scope.products = [];
        $scope.page = 1;
        $scope.now = new Date();
        
        $scope.loadAll = function() {
            Product.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.products = result;
                console.log(result);
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Product.get({id: id}, function(result) {
                $scope.product = result;
                $('#saveProductModal').modal('show');
            });
        };

        $scope.save = function () {
        	$scope.product.variant.isMaster = true;
            Product.save($scope.product,
                function (result) {
            		console.log(result);
	                $('#saveProductModal').on('hidden.bs.modal', function(){
	                	$state.go("admin.productEdit", {id: result.id}, {reload: true});
	                });

                	$('#saveProductModal').modal('hide');

                });
        };

        $scope.delete = function (id) {
            Product.get({id: id}, function(result) {
                $scope.product = result;
                $('#deleteProductConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Product.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteProductConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            ProductSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.products = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveProductModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.product = {name: null, properties: null, description: null, availableDate: null, deletedDate: null, slug: null, metaDescription: null, metaKeywords: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    })
    .controller('EditProductController', function ($scope, $stateParams, Product, OptionType, Taxon) {
        $scope.product = {};
        OptionType.query(function(result){
        	$scope.optionTypes = result;
        });
        Taxon.query(function(result){
        	$scope.taxons = result;
        });

        $scope.load = function (id) {
            Product.get({id: id}, function(result) {
              $scope.product = result;
              if($scope.product.optionTypes){
            	  $scope.product.optionTypeIds = $scope.product.optionTypes.map(function(item){
            		  return item.id;
            	  });
              }
              if($scope.product.taxons){
            	  $scope.product.taxonIds = $scope.product.taxons.map(function(item){
            		  return item.id;
            	  });
              }
            });
        };
        $scope.load($stateParams.id);
        
        $scope.save = function(){
        	$scope.message="";
        	$scope.error="";
        	console.log($scope.product);
            Product.update($scope.product,
                    function (result) {
            			//console.log(result);
                        $scope.message = "Saved";
                    }, function(response) {
                    	//console.log(response)
                        if(response.status !== 200) {
                            $scope.error = response.data || response.statusText || "Fail Saving";
                        }
                    });
        }

    })
    .controller('CloneProductController', function ($scope, $state, $stateParams, Product, OptionType, Taxon) {
        $scope.product = {};
        OptionType.query(function(result){
        	$scope.optionTypes = result;
        });
        Taxon.query(function(result){
        	$scope.taxons = result;
        });
        
        $scope.load = function (id) {
            Product.get({id: id}, function(result) {
              $scope.product = result;
              console.log(result);
              
              if($scope.product.optionTypes){
            	  $scope.product.optionTypeIds = $scope.product.optionTypes.map(function(item){
            		  return item.id;
            	  });
              }
              if($scope.product.taxons){
            	  $scope.product.taxonIds = $scope.product.taxons.map(function(item){
            		  return item.id;
            	  });
              }
            });
        };
        $scope.load($stateParams.id);
        
        $scope.save = function(){
        	$scope.message="";
        	$scope.error="";
        	//console.log($scope.product);
            Product.clone($scope.product,
                    function (result) {
            			//console.log(result);
                        $state.go('admin.productEdit',{id: result.id});
                    }, function(response) {
                    	console.log(response)
                        if(response.status !== 200) {
                            $scope.error = response.data || response.error || "Fail Saving";
                        }
                    });
        }

    });

