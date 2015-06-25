'use strict';

angular.module('nodesoftApp')
    .factory('TaxonomySearch', function ($resource) {
        return $resource('api/_search/taxonomys/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
