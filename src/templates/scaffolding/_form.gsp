<g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
<div class="panel panel-default">
  <div class="panel-heading">
    <g:if test="\${edit}">
    <h1><g:message code="default.show.label" args="[entityName]" /></h1>
    </g:if>
    <g:else>
    <h1><g:message code="default.create.label" args="[entityName]" /></h1>
    </g:else>
  </div>
  <div class="panel-body">
    <g:if test="\${flash.formMessage}">
    <div class="alert alert-info alert-dismissable" role="status"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>\${flash.formMessage}</div>
    </g:if>
    <form role="form">
  <% excludedProps = grails.persistence.Event.allEvents.toList() << 'version' << 'dateCreated' << 'lastUpdated'
     persistentPropNames = domainClass.persistentProperties*.name

     boolean hasHibernate = pluginManager?.hasGrailsPlugin('hibernate') || pluginManager?.hasGrailsPlugin('hibernate4') 
     if (hasHibernate) { 
         def GrailsDomainBinder = getClass().classLoader.loadClass('org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder') 
         def identity = GrailsDomainBinder.newInstance().getMapping(domainClass)?.identity
         if (identity && identity instanceof org.codehaus.groovy.grails.orm.hibernate.cfg.CompositeIdentity == false && identity?.generator == 'assigned') { 
             persistentPropNames << domainClass.identifier.name 
         } 
     } 
     props = domainClass.properties.findAll { persistentPropNames.contains(it.name) && !excludedProps.contains(it.name) }
     Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
     for (p in props) {
         if (p.embedded) {
             def embeddedPropNames = p.component.persistentProperties*.name
             def embeddedProps = p.component.properties.findAll { embeddedPropNames.contains(it.name) && !excludedProps.contains(it.name) }
             Collections.sort(embeddedProps, comparator.constructors[0].newInstance([p.component] as Object[]))
   %><fieldset class="embedded"><legend><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}" /></legend><%
             for (ep in p.component.properties) {
                 renderFieldForProperty(ep, p.component, "${p.name}.")
             }
   %></fieldset><%
          } else {
              renderFieldForProperty(p, domainClass)
          }
     }

private renderFieldForProperty(p, owningClass, prefix = "") {
    boolean hasHibernate = pluginManager?.hasGrailsPlugin('hibernate')
    boolean display = true
    boolean required = false
    if (hasHibernate) {
        cp = owningClass.constrainedProperties[p.name]
        display = (cp ? cp.display : true)
        required = (cp ? !(cp.propertyType in [boolean, Boolean]) && !cp.nullable && (cp.propertyType != String || !cp.blank) : false)
    }
    if (display) { %>
      <div class="form-group \${hasErrors(bean:${propertyName}, field:'${p.name}','has-error' )}">
        <label for="${prefix}${p.name}" class="control-label">
          <g:message code="${domainClass.propertyName}.${prefix}${p.name}.label" default="${p.naturalName}" />
          <% if (required) { %><span class="required-indicator">*</span><% } %>
        </label>
        ${renderEditor(p)}
        <g:hasErrors bean="\${${propertyName}}" field="${p.name}">
          <g:eachError bean="\${${propertyName}}" field="${p.name}">
          <span class="help-block"><g:message error="\${it}"/></span>
          </g:eachError>
        </g:hasErrors>
      </div>
  <%  }   } %>
      <g:if test="\${edit}">
      <g:hiddenField name="id" value="\${${propertyName}?.id}" />
      <g:hiddenField name="version" value="\${${propertyName}?.version}" />
      <g:submitToRemote class="btn btn-primary" url="[action: 'update']" update="[success:'form', failure:'form']" name="update" value="\${message(code: 'default.button.update.label', default: 'Update')}" before="\\\$('form').find('.loading').show()" onComplete="\\\$('.loading').hide();" onSuccess="\${remoteFunction(action:'list', update:'list', method:'GET')}"/>
      <g:field class="btn btn-default" type="reset" name="reset" value="\${message(code: 'default.button.reset.label', default: 'Reset')}"/>
      <g:remoteLink class="btn btn-success" action="create" update="form" method="GET" before="\\\$('form').find('.loading').show()" onComplete="\\\$('.loading').hide();"><g:message code="default.button.new.label" default="New"/></g:remoteLink>
      </g:if>
      <g:else>
      <g:submitToRemote class="btn btn-primary" url="[action: 'save']" update="[success:'form', failure:'form']" name="create" value="\${message(code: 'default.button.create.label', default: 'Create')}" before="\\\$('form').find('.loading').show()" onComplete="\\\$('.loading').hide();" onSuccess="\${remoteFunction(action:'list', update:'list', method:'GET')}"/>
      <g:field class="btn btn-default" type="reset" name="reset" value="\${message(code: 'default.button.reset.label', default: 'Reset')}"/>
      </g:else>
      <span class="loading">
        <span class="glyphicon glyphicon-refresh spinner"/>
      </span>
    </form>
  </div>
</div>
