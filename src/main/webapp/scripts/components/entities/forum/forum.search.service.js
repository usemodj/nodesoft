'use strict';

angular.module('nodesoftApp')
    .factory('ForumSearch', function ($resource) {
        return $resource('api/_search/forums/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
