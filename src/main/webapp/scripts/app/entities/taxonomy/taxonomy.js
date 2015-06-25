'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('taxonomy', {
                parent: 'entity',
                url: '/taxonomy',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.taxonomy.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/taxonomy/taxonomys.html',
                        controller: 'TaxonomyController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('taxonomy');
                        return $translate.refresh();
                    }]
                }
            })
            .state('taxonomyDetail', {
                parent: 'entity',
                url: '/taxonomy/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.taxonomy.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/taxonomy/taxonomy-detail.html',
                        controller: 'TaxonomyDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('taxonomy');
                        return $translate.refresh();
                    }]
                }
            });
    });
