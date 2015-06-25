'use strict';

angular.module('nodesoftApp')
    .controller('OptionValueController', function ($scope, OptionValue, OptionType, OptionValueSearch, ParseLinks) {
        $scope.optionValues = [];
        $scope.optiontypes = OptionType.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            OptionValue.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.optionValues = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            OptionValue.get({id: id}, function(result) {
                $scope.optionValue = result;
                $('#saveOptionValueModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.optionValue.id != null) {
                OptionValue.update($scope.optionValue,
                    function () {
                        $scope.refresh();
                    });
            } else {
                OptionValue.save($scope.optionValue,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            OptionValue.get({id: id}, function(result) {
                $scope.optionValue = result;
                $('#deleteOptionValueConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            OptionValue.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOptionValueConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            OptionValueSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.optionValues = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveOptionValueModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.optionValue = {name: null, presentation: null, position: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
