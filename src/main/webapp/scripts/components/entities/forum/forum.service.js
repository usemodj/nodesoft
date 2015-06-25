'use strict';

angular.module('nodesoftApp')
    .factory('Forum', function ($resource, $http) {
        var resource = $resource('api/forums/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
        
        return {
        	query: function(data, callback){
        		var cb = callback || angular.noop;
        		resource.query(data, function(results, headers){
        			return cb(results, headers);
        		});
        	},
        	get: function(data, callback){
        		var cb = callback || angular.noop;
        		resource.get(data, function(result){
        			return cb(result);
        		});
        	},
        	save: function(data, callback){
        		var cb = callback || angular.noop;
        		resource.save(data, function(result){
        			return cb(result);
        		});
        		
        	},
        	update: function(data, callback){
        		var cb = callback || angular.noop;
        		resource.update(data, function(result){
        			return cb(result);
        		});
        	},
        	delete: function(data, callback){
        		var cb = callback || angular.noop;
        		resource.delete(data, function(result){
        			return cb(result);
        		});
        	}
        }
    });
