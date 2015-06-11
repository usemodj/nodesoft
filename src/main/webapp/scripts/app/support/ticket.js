'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ticketList', {
                parent: 'site',
                url: '/ticket',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.ticket.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/support/tickets.list.html',
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
            .state('ticketView', {
                parent: 'site',
                url: '/ticket/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'nodesoftApp.ticket.view.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/support/tickets.view.html',
                        controller: 'ViewTicketController'
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
