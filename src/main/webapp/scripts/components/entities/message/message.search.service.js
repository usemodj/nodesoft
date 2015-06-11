'use strict';

angular.module('nodesoftApp')
    .factory('MessageSearch', function ($resource) {
        return $resource('api/_search/messages/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
