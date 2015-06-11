'use strict';

angular.module('nodesoftApp')
    .factory('AssetSearch', function ($resource) {
        return $resource('api/_search/assets/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
