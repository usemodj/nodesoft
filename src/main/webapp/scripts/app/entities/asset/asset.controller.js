'use strict';

angular.module('nodesoftApp')
    .controller('AssetController', function ($scope, Asset, AssetSearch, ParseLinks) {
        $scope.assets = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Asset.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.assets = result;
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
            if ($scope.asset.id != null) {
                Asset.update($scope.asset,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Asset.save($scope.asset,
                    function () {
                        $scope.refresh();
                    });
            }
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
