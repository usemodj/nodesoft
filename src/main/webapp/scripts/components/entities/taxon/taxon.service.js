'use strict';

angular.module('nodesoftApp')
    .factory('Taxon', function ($resource) {
        return $resource('api/taxons/:id', {}, {
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
