'use strict';

angular.module('nodesoftApp')
    .controller('AssetController', function ($scope, $stateParams, Asset, AssetSearch, ParseLinks, Product, Variant, Upload) {
        $scope.assets = [];
        $scope.page = 1;
        $scope.files = [];
        
        Product.get({id: $stateParams.productId}, function(result){
        	$scope.product = result;
        
        });
        Variant.query({productId: $stateParams.productId, deleted: false}, function(result) {
            $scope.variants = result;
        });
        
        //listen for the file selected event
        $scope.$on("fileSelected", function (event, args) {
          $scope.$apply(function () {
            //add the file object to the scope's files collectionU
            $scope.files.push(args.file);
          });
        });
        
        var beforeSort = '';
        var sorted = false;
        $scope.sortableOptions = {
            change: function(e, ui) {
                var entry = $scope.assets.map(function(item){
                    return item.id;
                }).join(',');
                beforeSort = entry;
            },
            // called after a node is dropped
            stop: function(e, ui) {
                var entry = $scope.assets.map(function(item){
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
            Asset.updatePosition({entry: entry}, function(result){

            });
        };

        $scope.loadAll = function() {
            Asset.query({productId: $stateParams.productId, page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.assets = result;
                //console.log(result);
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Asset.get({id: id}, function(result) {
                $scope.asset = result;
                $('#saveAssetModal').modal('show');
            });
        };

        $scope.save = function () {
        	//$scope.asset.viewableId = $scope.product.variant.id;
        	$scope.asset.viewableType = 'Variant';
            $scope.progress = 0;
            $scope.error = null;
            
            //console.log($scope.asset);
            $scope.upload = Upload.upload({
              url: 'api/assets/upload',
              method: 'POST',
              fields : {
            	  asset: $scope.asset
              },
              sendFieldsAs: 'json',
              file: ($scope.files != null)? $scope.files: null,
              fileFormDataName: 'file'
            }).progress(function (evt) {
              // Math.min is to fix IE which reports 200% sometimes
              var uploading =  parseInt(100.0 * evt.loaded / evt.total);
              $scope.progress = Math.min(100, uploading);
            }).success(function (data, status, headers, config) {
              //console.log(config);
              //console.log('>>success data')
              //console.log(data);
              $scope.refresh();
              //return $state.go('ticketView',{id: data.id}, {reload: true});
            }).error(function (data, status, headers, config) {
              //console.log(config);
              //console.log('>>success data')
              console.log(data);
              $scope.error = data;
            });
        };

        $scope.delete = function (id) {
            Asset.get({id: id}, function(result) {
                $scope.asset = result;
                $('#deleteAssetConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Asset.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAssetConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            AssetSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.assets = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveAssetModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.asset = {viewableId: null, viewableType: null, contentType: null, fileSize: null, fileName: null, filePath: null, alt: null, position: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
