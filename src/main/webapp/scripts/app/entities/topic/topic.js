'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('topic', {
                parent: 'entity',
                url: '/topic',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.topic.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/topic/topics.html',
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
            .state('topicDetail', {
                parent: 'entity',
                url: '/topic/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.topic.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/topic/topic-detail.html',
                        controller: 'TopicDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('topic');
                        return $translate.refresh();
                    }]
                }
            });
    });
