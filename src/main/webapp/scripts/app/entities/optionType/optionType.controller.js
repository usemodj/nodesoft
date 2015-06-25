'use strict';

angular.module('nodesoftApp')
    .controller('OptionTypeController', function ($scope, OptionType, OptionTypeSearch, ParseLinks) {
        $scope.optionTypes = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            OptionType.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.optionTypes = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            OptionType.get({id: id}, function(result) {
                $scope.optionType = result;
                $('#saveOptionTypeModal').modal('show');
            });
        };

        $scope.save = function () {
            if ($scope.optionType.id != null) {
                OptionType.update($scope.optionType,
                    function () {
                        $scope.refresh();
                    });
            } else {
                OptionType.save($scope.optionType,
                    function () {
                        $scope.refresh();
                    });
            }
        };

        $scope.delete = function (id) {
            OptionType.get({id: id}, function(result) {
                $scope.optionType = result;
                $('#deleteOptionTypeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            OptionType.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOptionTypeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            OptionTypeSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.optionTypes = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveOptionTypeModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.optionType = {name: null, presentation: null, position: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
