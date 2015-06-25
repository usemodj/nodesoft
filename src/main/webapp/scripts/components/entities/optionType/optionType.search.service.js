'use strict';

angular.module('nodesoftApp')
    .factory('OptionTypeSearch', function ($resource) {
        return $resource('api/_search/optionTypes/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
