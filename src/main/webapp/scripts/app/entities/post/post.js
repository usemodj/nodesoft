'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('post', {
                parent: 'entity',
                url: '/post',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.post.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/post/posts.html',
                        controller: 'PostController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('post');
                        return $translate.refresh();
                    }]
                }
            })
            .state('postDetail', {
                parent: 'entity',
                url: '/post/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.post.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/post/post-detail.html',
                        controller: 'PostDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('post');
                        return $translate.refresh();
                    }]
                }
            });
    });
