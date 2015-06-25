mvn spring-boot:run

yo jhipster:entity foo

-----

 
 bower install  angular-relative-date --save

 bower install ng-file-upload --save
 
## angular-markdown-filter
 
 $ bower install angular-markdown-filter --save
 
It's recomended to also use ngSanitize if you plan to bind the output using ngBindHtml to pervent XSS.

$ bower install angular-sanitize --save

<script src="bower_components/showdown/compressed/showdown.js"></script>
<script src="bower_components/angular-markdown-filter/markdown.js"></script>
<!-- Optional: -->
<script src="bower_components/angular-sanitize/angular-sanitize.js"></script>
angular.module('myApp', [
  'ngSanitize', // Optional
  'markdown'
]);


## bootstrap - AngularJS directives specific to Bootstrap
bower install angular-bootstrap --save
app.js:
angular.module('myModule', ['ui.bootstrap']);


## fontawsome

bower install fontawesome --save

## bootstrap-markdown editor

bower install bootstrap-markdown --save

make markdown-editor.directive.js


## jquery-ui-bootstrap

bower install jqueryuibootstrap --save

## jquery-ui

bower install jquery-ui --save

## angular-ui/ui-select2 - An AngularJS wrapper for select2
An AngularJS wrapper for select2

Install:
 $ bower install angular-ui-select2 --save

app.js:
 var myAppModule = angular.module('MyApp', ['ui.select2']);
 
## ui-sortable

bower install angular-ui-sortable --sav

app.js:
 var myAppModule = angular.module('MyApp', ['ui.sortable']);
 
## ui-tree

bower install angular-ui-tree

<link rel="stylesheet" href="bower_components/angular-ui-tree/dist/angular-ui-tree.min.css">
<script type="text/javascript" src="bower_components/angular-ui-tree/dist/angular-ui-tree.js"></script>

app.js:
  var myAppModule = angular.module('MyApp', ['ui.tree'])
