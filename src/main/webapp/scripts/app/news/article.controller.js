'use strict';

angular.module('nodesoftApp')
    .controller('ArticleController', function ($scope, $state, Principal, Article, User, ArticleSearch, ParseLinks, Upload) {
        $scope.articles = [];
        $scope.page = 1;
        $scope.files = [];
        
        $scope.isInAnyRole = function(roles){
        	return Principal.isInAnyRole(roles);
        },

        //listen for the file selected event
        $scope.$on("fileSelected", function (event, args) {
          $scope.$apply(function () {
            //add the file object to the scope's files collectionU
            $scope.files.push(args.file);
          });
        });

        $scope.loadAll = function() {
            Article.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.articles.push(result[i]);
                }
                console.log($scope.articles.length)
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.articles = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.showUpdate = function (id) {
            Article.get({id: id}, function(result) {
                $scope.article = result;
                $('#saveArticleModal').modal('show');
            });
        };

        $scope.save = function () {
        	var url = 'api/upload/article';
            if ($scope.article.id != null) {
                url = 'api/articles/update_article';
            } 
            $scope.progress = 0;
            $scope.error = null;
            $scope.upload = Upload.upload({
              url: url,
              method: 'POST',
              fields : {
            	  article: $scope.article
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
            Article.get({id: id}, function(result) {
                $scope.article = result;
                $('#deleteArticleConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Article.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteArticleConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            ArticleSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.articles = result;
            }, function(response) {
            	
                if(response.status === 404) {
                    //$scope.loadAll();
                    $scope.loadPage(1);
                }
            });
        };

        $scope.refresh = function () {
            $scope.reset();
            $('#saveArticleModal').modal('hide');
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.article = {subject: null, summary: null, content: null, imgUrl: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    })
    .controller('ViewArticleController', function ($scope, $state, $stateParams, $modal, Principal, Article, User, Upload) {
        $scope.article = {};
        $scope.isInAnyRole = function(roles){
        	return Principal.isInAnyRole(roles);
        },
        
        $scope.load = function (id) {
            Article.get({id: id}, function(result) {
              $scope.article = result;
            });
        };
        $scope.load($stateParams.id);
        
        $scope.delete = function (id) {
            $('#deleteArticleConfirmation').modal('show');
        };

        $scope.confirmDelete = function (id) {
            Article.delete({id: id},
                function () {
	                $('#deleteArticleConfirmation').on('hidden.bs.modal', function(){
	                	$state.go("article", {}, {reload: true});
	                });
	                $('#deleteArticleConfirmation').modal('hide');
                });
       };

        $scope.editArticle = function(){
            //console.log(">>article:");console.log($scope.article);
            var modalInstance = $modal.open({
              templateUrl: 'scripts/app/news/articles.edit.html',
              controller: 'EditArticleController',
              resolve: {
                article: function(){
                  return $scope.article;
                }
              }
            });
            modalInstance.result.then(function(editedArticle){
              //console.log(editedArticle);
              save(editedArticle); //update article with file attachment
            }, function(){
              //console.log('Caneled');
            });

          };

          // Update article with file attachment
          var save = function(article) {
            $scope.progress = 0;
            $scope.error = null;

            var url = 'api/upload/article';
            if ($scope.article.id != null) {
                url = 'api/articles/update_article';
            } 
            $scope.upload = Upload.upload({
              url: url,
              method: 'POST',
              fields : {
                article: article
              },
              file: (article.files != null)? article.files: null,
              fileFormDataName: 'file'
            }).progress(function (evt) {
              // Math.min is to fix IE which reports 200% sometimes
              $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
            }).success(function (data, status, headers, config) {
              //console.log(config);
              //console.log('>>success data')
              //console.log(data); //article with assets
              $state.go('articleView',{id: $stateParams.id}, {reload: true});
            }).error(function (data, status, headers, config){
              console.log(data);
              $scope.error = data;
            });
          };
          
    })
  .controller('EditArticleController', ['$scope', '$state', 'Asset', '$modalInstance', 'article', function ($scope, $state, Asset, $modalInstance, article) {
    var origin = angular.copy(article);
    $scope.article = article;
    $scope.files = [];

    //listen for the file selected event
    $scope.$on("fileSelected", function (event, args) {
      $scope.$apply(function () {
        //add the file object to the scope's files collection
        $scope.files.push(args.file);
      });
    });

    $scope.save = function(){
      $scope.article.files = $scope.files;
      $modalInstance.close($scope.article);
    };
    $scope.cancel = function(){
      $scope.article.subject = origin.subject;
      $scope.article.imgUrl = origin.imgUrl;
      $scope.article.summary = origin.summary;
      $scope.article.content = origin.content;
      $modalInstance.dismiss('cancel');
    };
    
    $scope.removeFile = function(file){
      var files = $scope.article.assets;
      if(files){
        Asset.remove({id: file.id}, function(){
          files.splice(files.indexOf(file),1);
        });
      }
    };
    
    $scope.clear = function () {
        $scope.article = {subject: null, summary: null, content: null, imgUrl: null, id: null};
        $scope.editForm.$setPristine();
        $scope.editForm.$setUntouched();
        $modalInstance.dismiss('cancel');
    };


  }]);

