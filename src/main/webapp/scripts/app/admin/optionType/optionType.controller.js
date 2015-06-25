'use strict';

angular.module('nodesoftApp')
    .controller('OptionTypeController', function ($scope, $filter, OptionType, OptionTypeSearch, ParseLinks) {
        $scope.optionTypes = [];
        $scope.page = 1;
        var beforeSort = '';
        var sorted = false;

        $scope.loadAll = function() {
            OptionType.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.optionTypes = result;
                $scope.optionTypes = $filter('orderBy')($scope.optionTypes, 'position', false);
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

        $scope.sortableOptions = {
                change: function(e, ui) {
                    var entry = $scope.optionTypes.map(function(item){
                        return item.id;
                    }).join(',');
                    beforeSort = entry;
                },
                // called after a node is dropped
                stop: function(e, ui) {
                    var entry = $scope.optionTypes.map(function(item){
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
                OptionType.updatePosition({entry: entry}, function(result){
                	$scope.loadAll();
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
    })
    .controller('EditOptionTypeController', function ($scope, $filter, $stateParams, OptionType, OptionValue) {
        $scope.optionType = {};
        var beforeSort = '';
        var sorted = false;

        $scope.load = function (id) {
            OptionType.get({id: id}, function(result) {
              $scope.optionType = result;
              if($scope.optionType.optionValues){
                  $scope.optionType.optionValues = $filter('orderBy')($scope.optionType.optionValues, 'position');
              }
            });
        };
        $scope.load($stateParams.id);

        $scope.sortableOptions = {
            change: function(e, ui) {
                var entry = $scope.optionType.optionValues.map(function(item){
                    return item.id;
                }).join(',');
                beforeSort = entry;
            },
            // called after a node is dropped
            stop: function(e, ui) {
                var entry = $scope.optionType.optionValues.map(function(item){
                    return item.id;
                }).join(',');
                sorted = entry != beforeSort;
                //IF sorted == true, updatePosition()
                if(sorted){
                    //$scope.updatePosition(entry);
                }
            }
        };
        
        $scope.addOptionValue = function(){
            $scope.optionType.optionValues.unshift({name:'',presentation:'', position:0});
        };

        $scope.deleteOptionValue = function( optionValue){
            if(optionValue.id) OptionValue.remove({id:optionValue.id});
            $scope.optionType.optionValues.splice($scope.optionType.optionValues.indexOf(optionValue), 1);
        };
        
        $scope.save = function(){
        	$scope.message = '';
        	OptionType.changeOptionTypeValues($scope.optionType, function(result){
        		$scope.message = "Updated";
        	}, function(response){
        		
        	});
        }
    });
