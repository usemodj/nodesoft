'use strict';

angular.module('nodesoftApp')
    .factory('TaxonSearch', function ($resource) {
        return $resource('api/_search/taxons/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
