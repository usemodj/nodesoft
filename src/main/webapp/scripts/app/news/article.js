'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('article', {
                parent: 'site',
                url: '/article',
                data: {
                    roles: [],
                    pageTitle: 'nodesoftApp.article.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/articles.html',
                        controller: 'ArticleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('article');
                        return $translate.refresh();
                    }]
                }
            })
            .state('articleView', {
                parent: 'site',
                url: '/article/:id',
                data: {
                    roles: [],
                    pageTitle: 'nodesoftApp.article.view.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/news/articles.view.html',
                        controller: 'ViewArticleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('article');
                        return $translate.refresh();
                    }]
                }
            });
    });
