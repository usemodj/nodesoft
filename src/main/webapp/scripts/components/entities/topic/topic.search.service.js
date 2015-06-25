'use strict';

angular.module('nodesoftApp')
    .factory('TopicSearch', function ($resource) {
        return $resource('api/_search/topics/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
