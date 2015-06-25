'use strict';

angular.module('nodesoftApp')
    .controller('TopicController', function ($scope, $stateParams, Topic, Forum, User, Post, Asset, TopicSearch, ParseLinks, Upload) {
        $scope.topics = [];
        $scope.page = 1;
        $scope.files = [];
        $scope.forumId = $stateParams.forumId;
        
        //listen for the file selected event
        $scope.$on("fileSelected", function (event, args) {
          $scope.$apply(function () {
            //add the file object to the scope's files collectionU
            $scope.files.push(args.file);
          });
        });

        $scope.loadAll = function() {
            Topic.getForumTopics({forum_id: $scope.forumId, page: $scope.page, per_page: 10}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.topics = result.topics;
                $scope.stickyTopics = result.stickyTopics;
                //console.log(result)
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
        	$scope.files = [];
            Topic.getRoot({id: id}, function(result) {
            	//console.log(result);
                $scope.topic = result;
                $('#saveTopicModal').modal('show');
            });
        };

        $scope.save = function () {
        	var topic = {
        		forumId: $scope.forumId,
        		id: $scope.topic.id,
        		name: $scope.topic.name,
        		locked: $scope.topic.locked,
        		sticky: $scope.topic.sticky,
        		post:{
        			id: $scope.topic.post.id,
        			content: $scope.topic.post.content
        		}
        	};
        	var url = 'api/topics/upload';
            if ($scope.topic.id != null) {
            	url = 'api/topics/update';
            } 
            //console.log($scope.topic);
            
            $scope.progress = 0;
            $scope.error = null;
            $scope.upload = Upload.upload({
              url: url,
              method: 'POST',
              fields : {
            	  topic: topic
              },
              sendFieldsAs: 'json',
              file: ($scope.files != null)? $scope.files: null,
              fileFormDataName: 'file'
            }).progress(function (evt) {
              // Math.min is to fix IE which reports 200% sometimes
              var uploading =  parseInt(100.0 * evt.loaded / evt.total);
              $scope.progress = Math.min(100, uploading);
            }).success(function (data, status, headers, config) {
              //console.log(config);
              //console.log('>>success data')
              //console.log(data);
              $scope.refresh();
              //return $state.go('ticketView',{id: data.id}, {reload: true});
            }).error(function (data, status, headers, config) {
              //console.log(config);
              //console.log('>>success data')
              console.log(data);
              $scope.error = data;
            });
            
        };

        $scope.delete = function (id) {
            Topic.get({id: id}, function(result) {
                $scope.topic = result;
                $('#deleteTopicConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Topic.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteTopicConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            TopicSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.topics = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.removeFile = function(file){
            var files = $scope.topic.post.assets;
            if(files){
              Asset.remove({id: file.id}, function(){
                files.splice(files.indexOf(file),1);
              });
            }
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTopicModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.topic = {name: null, views: null, replies: null, locked: null, sticky: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    })
    .controller('ViewTopicController', function ($scope, $state, $stateParams, Topic, Forum, User, Post, Asset, Upload) {
        $scope.topic = {};
        $scope.newPost = {};
        $scope.post = {};
        $scope.forumId = $stateParams.forumId;
        $scope.topicId = $stateParams.id;
        $scope.files = [];
        //listen for the file selected event
        $scope.$on("fileSelected", function (event, args) {
          $scope.$apply(function () {
            //add the file object to the scope's files collectionU
            $scope.files.push(args.file);
          });
        });
     
        $scope.load = function (id) {
            Topic.get({id: id}, function(result) {
              $scope.topic = result;
              if(!$scope.currentUser || !$scope.isInAnyRole(['ROLE_ADMIN']) && $scope.currentUser.email != $scope.topic.user.email){
            	  $state.go('topic',{forumId: $stateParams.forumId});
              }
              //console.log(result)
            });
        };
        $scope.load($stateParams.id);
        
        $scope.showUpdate = function (post) {
        	$scope.files = [];
            $scope.post = post;
            $('#savePostModal').modal('show');
        };
        $scope.deletePost = function (post) {
            $scope.post = post;
            $('#deletePostConfirmation').modal('show');
        };

        $scope.confirmDeletePost = function (id) {
            Post.delete({id: id},
                function () {
                    $scope.load($stateParams.id);
                    $('#deletePostConfirmation').modal('hide');
                    $scope.clear();
                });
        };
        $scope.refresh = function () {
            $scope.load($stateParams.id);
            $('#savePostModal').modal('hide');
            $scope.clear();
        };

        $scope.save = function () {
        	var post = {
        		forumId: $scope.forumId,
        		topicId: $scope.topicId,
        		id: $scope.post.id,
        		name: $scope.post.name || $scope.newPost.name,
        		content: $scope.post.content || $scope.newPost.content
        		
        	};
        	var url = 'api/topics/post';
            if (post.id != null) {
            	url = 'api/topics/editPost';
            } 
            console.log(post);
            
            $scope.progress = 0;
            $scope.error = null;
            $scope.upload = Upload.upload({
              url: url,
              method: 'POST',
              fields : {
            	  post: post
              },
              sendFieldsAs: 'json',
              file: ($scope.files != null)? $scope.files: null,
              fileFormDataName: 'file'
            }).progress(function (evt) {
              // Math.min is to fix IE which reports 200% sometimes
              var uploading =  parseInt(100.0 * evt.loaded / evt.total);
              $scope.progress = Math.min(100, uploading);
            }).success(function (data, status, headers, config) {
//                $('#savePostModal').on('hidden.bs.modal', function(){
//                	return $state.go('topicView',{forumId: $scope.forumId, id: $scope.topicId}, {reload: true});
//                });
            	$scope.refresh();
             }).error(function (data, status, headers, config) {
              //console.log(config);
              //console.log('>>success data')
              console.log(data);
              $scope.error = data;
            });
            
        };

        $scope.removeFile = function(file){
            var files = $scope.post.assets;
            if(files){
              Asset.remove({id: file.id}, function(){
                files.splice(files.indexOf(file),1);
              });
            }
        };
        
        $scope.clear = function () {
            $scope.post = {};
            $scope.newPost = {};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();

        };

    });

