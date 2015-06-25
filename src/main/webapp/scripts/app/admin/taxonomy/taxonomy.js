'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('admin.taxonomy', {
                parent: 'adminProduct',
                url: '/taxonomy',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.taxonomy.home.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/taxonomy/taxonomys.html',
                        controller: 'TaxonomyController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('taxonomy');
                        $translatePartialLoader.addPart('taxon');
                        return $translate.refresh();
                    }]
                }
            })
            .state('admin.taxonomyDetail', {
                parent: 'adminProduct',
                url: '/taxonomy/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.taxonomy.detail.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/taxonomy/taxons.html',
                        controller: 'TaxonController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('taxonomy');
                        $translatePartialLoader.addPart('taxon');
                        return $translate.refresh();
                    }]
                }
            });
    });
