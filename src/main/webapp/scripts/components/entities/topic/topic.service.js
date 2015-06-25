'use strict';

angular.module('nodesoftApp')
    .factory('Topic', function ($resource, $http) {
        var resource = $resource('api/topics/:id', {}, {
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
        	},
        	getRoot: function(data, callback){
        		var cb = callback || angular.noop;
        		$http.get("api/topics/"+data.id+"/root")
        		.success( function(result, status, headers, config){
        			return cb(result, status);
        		})
        		.error( function(result, status, headers, config){
        			return cb(result, status);
        		});
        	},
        	getForumTopics: function(data, callback){
        		var cb = callback || angular.noop;
        		$http.get("api/forum/topics?forum_id="+data.forum_id+"&page="+data.page+ "&per_page="+ data.per_page)
        		.success( function(result, status, headers, config){
        			return cb(result, headers);
        		})
        		.error( function(result, status, headers, config){
        			return cb(result, headers);
        		});
        	}

        }

    });
