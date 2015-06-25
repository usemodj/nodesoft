'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('optionValue', {
                parent: 'entity',
                url: '/optionValue',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.optionValue.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/optionValue/optionValues.html',
                        controller: 'OptionValueController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionValue');
                        return $translate.refresh();
                    }]
                }
            })
            .state('optionValueDetail', {
                parent: 'entity',
                url: '/optionValue/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.optionValue.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/optionValue/optionValue-detail.html',
                        controller: 'OptionValueDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionValue');
                        return $translate.refresh();
                    }]
                }
            });
    });
