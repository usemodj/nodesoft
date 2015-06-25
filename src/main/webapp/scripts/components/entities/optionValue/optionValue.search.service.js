'use strict';

angular.module('nodesoftApp')
    .factory('OptionValueSearch', function ($resource) {
        return $resource('api/_search/optionValues/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
