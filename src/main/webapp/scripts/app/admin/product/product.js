'use strict';

angular.module('nodesoftApp')
    .config(function ($stateProvider) {
        $stateProvider
        	.state('adminProduct',{
        		abstract: true,
        		parent: 'admin',
        		views: {
        			'content@admin':{
        				templateUrl:'scripts/app/admin/product/layout.html'
        			}
        		}
        	})
            .state('admin.product', {
                parent: 'adminProduct',
                url: '/product',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.product.home.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/product/products.html',
                        controller: 'AdminProductController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('product');
                        $translatePartialLoader.addPart('variant');
                        return $translate.refresh();
                    }]
                }
            })
            .state('admin.productEdit', {
                parent: 'adminProduct',
                url: '/product/:id',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.product.detail.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/product/products.edit.html',
                        controller: 'EditProductController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('product');
                        $translatePartialLoader.addPart('variant');
                        return $translate.refresh();
                    }]
                }
            })
            .state('admin.productClone', {
                parent: 'adminProduct',
                url: '/product/:id/clone',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'nodesoftApp.product.detail.title'
                },
                views: {
                    'content@adminProduct': {
                        templateUrl: 'scripts/app/admin/product/products.edit.html',
                        controller: 'CloneProductController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('product');
                        $translatePartialLoader.addPart('variant');
                        return $translate.refresh();
                    }]
                }
            });

    });
