'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('variant', {
                parent: 'entity',
                url: '/variant',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.variant.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/variant/variants.html',
                        controller: 'VariantController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('variant');
                        return $translate.refresh();
                    }]
                }
            })
            .state('variantDetail', {
                parent: 'entity',
                url: '/variant/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.variant.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/variant/variant-detail.html',
                        controller: 'VariantDetailController'
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
