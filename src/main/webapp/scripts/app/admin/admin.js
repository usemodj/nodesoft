'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('admin', {
                abstract: true,
                parent: 'site',
                url: '/admin',
                views: {
                    'content@': {
                      templateUrl: 'scripts/app/admin/layout.html'
                    }
                  }

            });
    });
