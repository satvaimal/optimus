<div class="list-group">
  <a class="list-group-item active" href="${createLink(uri: '/')}">Home</a>
  <g:each var="c" in="${grailsApplication.controllerClasses.sort {it.fullName} }">
  <g:remoteLink class="list-group-item" controller="${c.logicalPropertyName}" method="GET" update="content">${c.name}</g:remoteLink>
  </g:each>
</div>
