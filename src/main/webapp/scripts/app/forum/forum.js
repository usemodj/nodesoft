'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('forum', {
                parent: 'site',
                url: '/forum',
                data: {
                    roles: [],
                    pageTitle: 'nodesoftApp.forum.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/forum/forums.html',
                        controller: 'ForumController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('forum');
                        return $translate.refresh();
                    }]
                }
            })
            .state('forumView', {
                parent: 'site',
                url: '/forum/:id',
                data: {
                    roles: [],
                    pageTitle: 'nodesoftApp.forum.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/forum/forum-detail.html',
                        controller: 'ForumDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('forum');
                        return $translate.refresh();
                    }]
                }
            });
    });
