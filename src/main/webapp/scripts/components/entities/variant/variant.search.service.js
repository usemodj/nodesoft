'use strict';

angular.module('nodesoftApp')
    .factory('VariantSearch', function ($resource) {
        return $resource('api/_search/variants/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
