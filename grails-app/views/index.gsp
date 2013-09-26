<!DOCTYPE html>
<html>
  <head>
    <title><g:message code="app.name" default="Grails App Name"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${resource(plugin:'optimus', dir: 'css', file: 'bootstrap.min.css')}" type="text/css" media="screen">
    <link rel="stylesheet" href="${resource(plugin:'optimus', dir: 'css', file: 'main.css')}" type="text/css">
    <!--[if lt IE 9]>
      <script src="../../assets/js/html5shiv.js"></script>
      <script src="../../assets/js/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
    <div>
      <g:render template="/header"/>
    </div>
    <div class="visible-xs">
      <g:render template="/topMenu"/>
    </div>
    <div class="col-sm-3 col-lg-2 hidden-xs">
      <g:render template="/menu"/>
    </div>
    <div id="content" class="col-sm-9 col-lg-10">
      <g:render template="/content"/>
    </div>
    <script src="//code.jquery.com/jquery.js"></script>
    <script src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>
  </body>
</html>
