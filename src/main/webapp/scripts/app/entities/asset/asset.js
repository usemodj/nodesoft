'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('asset', {
                parent: 'entity',
                url: '/asset',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.asset.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/asset/assets.html',
                        controller: 'AssetController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('asset');
                        return $translate.refresh();
                    }]
                }
            })
            .state('assetDetail', {
                parent: 'entity',
                url: '/asset/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.asset.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/asset/asset-detail.html',
                        controller: 'AssetDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('asset');
                        return $translate.refresh();
                    }]
                }
            });
    });
