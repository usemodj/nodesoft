'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('admin.optionType', {
                parent: 'adminProduct',
                url: '/optionType',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.optionType.home.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/optionType/optionTypes.html',
                        controller: 'OptionTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionType');
                        $translatePartialLoader.addPart('optionValue');
                        return $translate.refresh();
                    }]
                }
            })
            .state('admin.optionTypeDetail', {
                parent: 'adminProduct',
                url: '/optionType/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.optionType.detail.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/optionType/optionValues.html',
                        controller: 'EditOptionTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionType');
                        $translatePartialLoader.addPart('optionValue');
                        return $translate.refresh();
                    }]
                }
            });
    });
