'use strict';

angular.module('nodesoftApp')
    .controller('TicketController', ['$scope', 'Ticket', 'TicketSearch', 'ParseLinks', 'Upload', function ($scope, Ticket, TicketSearch, ParseLinks, Upload) {
        $scope.tickets = [];
        $scope.page = 1;
        $scope.files = [];
       
        //listen for the file selected event
        $scope.$on("fileSelected", function (event, args) {
          $scope.$apply(function () {
            //add the file object to the scope's files collectionU
            $scope.files.push(args.file);
          });
        });
        
        $scope.loadAll = function() {
            Ticket.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.tickets = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Ticket.get({id: id}, function(result) {
                $scope.ticket = result;
                $('#saveTicketModal').modal('show');
            });
        };

        $scope.save = function () {

            $scope.progress = 0;
            $scope.error = null;
            $scope.ticket.status = 'request';
            $scope.upload = Upload.upload({
              url: 'api/upload/ticket',
              method: 'POST',
              fields : {
            	  ticket: $scope.ticket
              },
              sendFieldsAs: 'json',
              data: {
            	  'ticket': $scope.ticket
              },
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

        $scope.search = function () {
            TicketSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.tickets = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $('#saveTicketModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ticket = {subject: null, status: null, views: null, replies: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    }])
    .controller('ViewTicketController', function ($scope, $state, $stateParams, $modal, Ticket, User, Message, Auth, Upload) {
        $scope.ticket = {};
        $scope.files = [];

        //listen for the file selected event
        $scope.$on("fileSelected", function (event, args) {
          $scope.$apply(function () {
            //add the file object to the scope's files collection
            $scope.files.push(args.file);
          });
        });
        Auth.identity(false, function(account){
        	console.log(account);
        	$scope.user = account;
        });

        
        $scope.load = function (id) {
            Ticket.get({id: id}, function(result) {
              console.log(result);
              $scope.ticket = result;
            });
        };
        $scope.load($stateParams.id);
        
        $scope.replyMessage = function(form){
            $scope.newMessage.ticket_id = $stateParams.id;
            $scope.newMessage.status = 'request';
            $scope.progress = 0;
            $scope.error = null;
            //console.log($scope.files);
            $scope.upload = Upload.upload({
              url: 'api/ticket/reply',
              method: 'POST',
              fields : {
                message : $scope.newMessage
              },
              file: ($scope.files != null)? $scope.files: null,
              fileFormDataName: 'file'
            }).progress(function (evt) {
              // Math.min is to fix IE which reports 200% sometimes
              $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
            }).success(function (data, status, headers, config) {
              //console.log(config);
              //console.log('>>success data')
              console.log(data); //message with assets
              //$scope.data = data;
              $state.go('ticketView',{id: $stateParams.id}, {reload: true});

            }).success(function (data, status, headers, config) {
              $scope.error = data;
            });
          };

          $scope.editMessage = function(message){
              $scope.message = message;
              //console.log(">>message:");console.log($scope.message);
              var modalInstance = $modal.open({
                templateUrl: 'scripts/app/support/tickets.edit.html',
                controller: 'EditTicketController',
                resolve: {
                  message: function(){
                    return $scope.message;
                  }
                }
              });
              modalInstance.result.then(function(editedMessage){
                //console.log(editedMessage);
                updateMessage(editedMessage); //update message with file attachment
              });

              //$state.go('forums.topics.edit', {forum_id: $stateParams.forum_id, id: $stateParams.id});
            };

            // Update topic with file attachment
            var updateMessage = function( message) {
              message.ticket_id = $stateParams.id;
              $scope.progress = 0;
              $scope.error = null;
              //console.log($scope.files);
              $scope.upload = Upload.upload({
                url: 'api/ticket/update_message',
                method: 'POST',
                fields : {
                  message : message
                },
                file: (message.files != null)? message.files: null,
                fileFormDataName: 'file'
              }).progress(function (evt) {
                // Math.min is to fix IE which reports 200% sometimes
                $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
              }).success(function (data, status, headers, config) {
                //console.log(config);
                //console.log('>>success data')
                console.log(data); //message with assets
                //$scope.data = data;
                $state.go('ticketView',{id: $stateParams.id}, {reload: true});
              }).success(function (data, status, headers, config) {
                $scope.error = data;
              });
            };
    })
  .controller('EditTicketController', ['$scope', '$state', 'Asset', '$modalInstance', 'message', function ($scope, $state, Asset, $modalInstance, message) {
    var origin = angular.copy(message);
    $scope.message = message;
    $scope.files = [];

    //listen for the file selected event
    $scope.$on("fileSelected", function (event, args) {
      $scope.$apply(function () {
        //add the file object to the scope's files collection
        $scope.files.push(args.file);
      });
    });

    $scope.save = function(){
      $scope.message.files = $scope.files;
      $modalInstance.close($scope.message);
    };

    $scope.cancel = function(){
      $scope.message.content = origin.content;
      $modalInstance.dismiss('cancel');
    };

    $scope.removeFile = function(file){
      var files = $scope.message.assets;
      if(files){
        Asset.remove({id: file.id}, function(){
          files.splice(files.indexOf(file),1);
        });
      }
    };

  }]);
