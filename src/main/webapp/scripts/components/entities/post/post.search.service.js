'use strict';

angular.module('nodesoftApp')
    .factory('PostSearch', function ($resource) {
        return $resource('api/_search/posts/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
