'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('admin.variant', {
                parent: 'adminProduct',
                url: '/product/:productId/variant',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.variant.home.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/variant/variants.html',
                        controller: 'VariantController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('variant');
                        return $translate.refresh();
                    }]
                }
            });
    });
