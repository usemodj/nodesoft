'use strict';

angular.module('nodesoftApp')
    .factory('Variant', function ($resource, $http) {
        var resource = $resource('api/variants/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    if (data.deletedDate != null) data.deletedDate = new Date(data.deletedDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
        
        return {
        	query: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		resource.query(data, function(value, headers){
        			return cb(value, headers);
        		}, function(response){
        			return cb2(response);
        		});
        		
        	},
        	get: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		resource.get(data, function(value, headers){
        			return cb(value, headers);
        		}, function(response){
        			return cb2(response);
        		});
        	},
        	save: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		resource.save(data, function(value, headers){
        			return cb(value, headers);
        		}, function(response){
        			return cb2(response);
        		});
        		
        	},
        	update: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		resource.update(data, function(value, headers){
        			return cb(value, headers);
        		}, function(response){
        			return cb2(response);
        		});
        	},
        	delete: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		resource.delete(data, function(value, headers){
        			return cb(value, headers);
        		}, function(response){
        			return cb2(response);
        		});
        	},
        	updatePosition: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		//console.log(data)
        		$http.get('api/variants/position/' + data.entry)
        		.success( function(result, status, headers, config){
        			return cb(result, status, headers, config);
        		})
        		.error( function(result, status, headers, config){
        			return cb2(result, status, headers, config);
        		});
        	},
        	setActive: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		//console.log(data)
        		$http.get('api/variants/active/'+ data.id)
        		.success( function(result, status, headers, config){
        			return cb(result, status, headers, config);
        		})
        		.error( function(result, status, headers, config){
        			return cb2(result, status, headers, config);
        		});
        	}
        };
    });
