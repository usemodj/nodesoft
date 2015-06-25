'use strict';

angular.module('nodesoftApp')
    .factory('OptionValue', function ($resource) {
        return $resource('api/optionValues/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
