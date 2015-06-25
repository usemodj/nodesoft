'use strict';

angular.module('nodesoftApp')
    .controller('VariantController', function ($scope, Variant, Product, OptionValue, VariantSearch, ParseLinks) {
        $scope.variants = [];
        $scope.products = Product.query();
        $scope.optionvalues = OptionValue.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Variant.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
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
    });
