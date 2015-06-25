'use strict';

angular.module('nodesoftApp')
    .controller('ProductController', function ($scope, Product, OptionType, Taxon, ProductSearch, ParseLinks) {
        $scope.products = [];
        $scope.optiontypes = OptionType.query();
        $scope.taxons = Taxon.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Product.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.products = result;
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
            if ($scope.product.id != null) {
                Product.update($scope.product,
                    function () {
                        $scope.refresh();
                    });
            } else {
                Product.save($scope.product,
                    function () {
                        $scope.refresh();
                    });
            }
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
    });
