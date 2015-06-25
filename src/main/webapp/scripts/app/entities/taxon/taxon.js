'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('taxon', {
                parent: 'entity',
                url: '/taxon',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.taxon.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/taxon/taxons.html',
                        controller: 'TaxonController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('taxon');
                        return $translate.refresh();
                    }]
                }
            })
            .state('taxonDetail', {
                parent: 'entity',
                url: '/taxon/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.taxon.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/taxon/taxon-detail.html',
                        controller: 'TaxonDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('taxon');
                        return $translate.refresh();
                    }]
                }
            });
    });
