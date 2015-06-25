'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('forum', {
                parent: 'entity',
                url: '/forum',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.forum.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/forum/forums.html',
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
            .state('forumDetail', {
                parent: 'entity',
                url: '/forum/:id',
                data: {
                    roles: ['ROLE_USER'],
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
