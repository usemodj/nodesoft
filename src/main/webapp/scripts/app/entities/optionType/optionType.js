'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('optionType', {
                parent: 'entity',
                url: '/optionType',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.optionType.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/optionType/optionTypes.html',
                        controller: 'OptionTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionType');
                        return $translate.refresh();
                    }]
                }
            })
            .state('optionTypeDetail', {
                parent: 'entity',
                url: '/optionType/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.optionType.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/optionType/optionType-detail.html',
                        controller: 'OptionTypeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionType');
                        return $translate.refresh();
                    }]
                }
            });
    });
