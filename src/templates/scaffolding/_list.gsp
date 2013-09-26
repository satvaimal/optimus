<g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
<div class="panel panel-default">
  <div class="panel-heading">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
  </div>
  <div class="panel-body">
    <g:if test="\${flash.listMessage}">
    <div class="alert alert-info alert-dismissable" role="status"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>\${flash.listMessage}</div>
    </g:if>
    <div class="table-responsive">
      <table class="table table-striped">
        <thead>
          <tr>
            <%  excludedProps = grails.persistence.Event.allEvents.toList() << 'id' << 'version'
                allowedNames = domainClass.persistentProperties*.name << 'dateCreated' << 'lastUpdated'
                props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && it.type != null && !Collection.isAssignableFrom(it.type) }
                Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                props.eachWithIndex { p, i ->
                    if (i < 3) {
                        if (p.isAssociation()) { %>
            <th><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}" /></th>
            <%          } else { %>
            <g:sortableColumn property="${p.name}" title="\${message(code: '${domainClass.propertyName}.${p.name}.label', default: '${p.naturalName}')}" />
            <%  }   }   } %>
            <th><g:message code="default.options.label" default="Options" /></th>
          </tr>
        </thead>
        <tbody>
        <g:each in="\${items}" status="i" var="${propertyName}">
          <tr>
            <%  props.eachWithIndex { p, i ->
                    if (i < 3) {
                        if (p.type == Boolean || p.type == boolean) { %>
            <td><g:formatBoolean boolean="\${${propertyName}.${p.name}}" /></td>
            <%          } else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) { %>
            <td><g:formatDate date="\${${propertyName}.${p.name}}" /></td>
            <%          } else { %>
            <td>\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</td>
            <%  }   }   } %>
            <td>
              <g:remoteLink action="edit" id="\${${propertyName}.id}" update="form" method="GET"><span class="label label-success"><span class="glyphicon glyphicon-eye-open"></span></span></g:remoteLink>
              <g:remoteLink action="delete" id="\${${propertyName}.id}" update="content" before="if(confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}') == false) return false"><span class="label label-danger"><span class="glyphicon glyphicon-remove"></span></span></g:remoteLink>
            </td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <util:remotePaginate total="\${total}" action="list" update="list" method="GET"/>
  </div>
</div>
