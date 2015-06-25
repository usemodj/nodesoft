'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('topic', {
                parent: 'site',
                url: '/forum/:forumId/topic',
                data: {
                    roles: [],
                    pageTitle: 'nodesoftApp.topic.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/topic/topics.list.html',
                        controller: 'TopicController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('topic');
                        return $translate.refresh();
                    }]
                }
            })
            .state('topicView', {
                parent: 'site',
                url: '/forum/:forumId/topic/:id',
                data: {
                    roles: [],
                    pageTitle: 'nodesoftApp.topic.view.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/topic/topics.view.html',
                        controller: 'ViewTopicController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('topic');
                        $translatePartialLoader.addPart('post');
                        return $translate.refresh();
                    }]
                }
            });
    });
