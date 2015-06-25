'use strict';

angular.module('nodesoftApp')
    .factory('OptionType', function ($resource, $http) {
        var resource = $resource('api/optionTypes/:id', {}, {
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
        		$http.get('api/optionTypes/position/' + data.entry)
        		.success( function(result, status, headers, config){
        			return cb(result, status, headers, config);
        		})
        		.error( function(result, status, headers, config){
        			return cb2(result, status, headers, config);
        		});
        	},
        	changeOptionTypeValues: function(data, success, error){
        		var cb = success || angular.noop;
        		var cb2 = error || angular.noop;
        		//console.log(data)
        		$http.post('api/optionTypes/changeValues', data)
        		.success( function(result, status, headers, config){
        			return cb(result, status, headers, config);
        		})
        		.error( function(result, status, headers, config){
        			return cb2(result, status, headers, config);
        		});
        	}
        };
    });
