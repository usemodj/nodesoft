'use strict';

angular.module('nodesoftApp')
    .factory('ArticleSearch', function ($resource) {
        return $resource('api/_search/articles/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
