<div class="list-group">
  <a class="list-group-item active" href="${createLink(uri: '/')}">Home</a>
  <g:each var="c" in="${grailsApplication.controllerClasses.sort {it.fullName} }">
  <g:remoteLink class="list-group-item" controller="${c.logicalPropertyName}" method="GET" update="content" before="\$(this).find('.loading').show();\$('.list-group-item').removeClass('active');\$(this).addClass('active')" onComplete="\$('.loading').hide();">
    ${c.name}
    <span class="loading">
      <span class="glyphicon glyphicon-refresh spinner"/>
    </span>
  </g:remoteLink>
  </g:each>
</div>
