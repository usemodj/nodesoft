'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ticket', {
                parent: 'entity',
                url: '/ticket',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.ticket.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ticket/tickets.html',
                        controller: 'TicketController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ticket');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ticketDetail', {
                parent: 'entity',
                url: '/ticket/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.ticket.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ticket/ticket-detail.html',
                        controller: 'TicketDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ticket');
                        return $translate.refresh();
                    }]
                }
            });
    });
