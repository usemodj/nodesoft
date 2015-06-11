'use strict';

angular.module('nodesoftApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


