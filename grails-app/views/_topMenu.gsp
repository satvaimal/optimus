<nav class="navbar navbar-default" role="navigation">
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="#"><g:message code="default.app.name" default="AppName"/></a>
  </div>
  <div class="collapse navbar-collapse navbar-ex1-collapse">
    <ul class="nav navbar-nav">
      <li class="item-home active">
        <g:remoteLink controller="home" method="GET" update="content" before="\$(this).find('.loading').show()" onComplete="\$('.loading').hide();\$('.navbar-nav li').removeClass('active');\$('.item-home').addClass('active');">
          <g:message code="default.home.label"/>
          <span class="loading">
            <span class="glyphicon glyphicon-refresh spinner"/>
          </span>
        </g:remoteLink>
      </li>
      <g:each var="c" in="${grailsApplication.controllerClasses.sort {it.fullName} }">
      <g:if test="${c.logicalPropertyName != 'home'}">
      <li class="item-${c.logicalPropertyName}">
        <g:remoteLink controller="${c.logicalPropertyName}" method="GET" update="content" before="\$(this).find('.loading').show()" onComplete="\$('.loading').hide();\$('.navbar-nav li').removeClass('active');\$('.item-${c.logicalPropertyName}').addClass('active');">
          ${c.name}
          <span class="loading">
            <span class="glyphicon glyphicon-refresh spinner"/>
          </span>
        </g:remoteLink>
      </li>
      </g:if>
      </g:each>
    </ul>
  </div>
</nav>
