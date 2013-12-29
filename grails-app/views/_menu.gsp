<div class="list-group">
  <g:remoteLink class="list-group-item item-home active" controller="home" method="GET" update="content" before="\$(this).find('.loading').show()" onComplete="\$('.loading').hide();\$('.list-group-item').removeClass('active');\$('.item-home').addClass('active');">
    <g:message code="default.home.label"/>
    <span class="loading">
      <span class="glyphicon glyphicon-refresh spinner"/>
    </span>
  </g:remoteLink>
  <g:each var="c" in="${grailsApplication.controllerClasses.sort {it.fullName} }">
  <g:if test="${c.logicalPropertyName != 'home'}">
  <g:remoteLink class="list-group-item item-${c.logicalPropertyName}" controller="${c.logicalPropertyName}" method="GET" update="content" before="\$(this).find('.loading').show()" onComplete="\$('.loading').hide();\$('.list-group-item').removeClass('active');\$('.item-${c.logicalPropertyName}').addClass('active');">
    ${c.name}
    <span class="loading">
      <span class="glyphicon glyphicon-refresh spinner"/>
    </span>
  </g:remoteLink>
  </g:if>
  </g:each>
</div>
