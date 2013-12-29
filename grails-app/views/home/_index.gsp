<div class="col-md-12">
  <div class="panel panel-default">
    <div class="panel-heading">
      <h1>Welcome to Grails!</h1>
    </div>
    <div class="panel-body">
      Congratulations, you have successfully started your first Grails application! At the moment this is the default page, feel free to modify it to either redirect to a controller or display whatever content you may choose. There is a list of controllers that are currently deployed in this application, click on each to execute its default action.
    </div>
  </div>
</div>
<div class="col-md-6">
  <div class="panel panel-default">
    <div class="panel-heading">
      <h1>Installed Plugins</h1>
    </div>
    <table class="table table-striped">
      <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
      <tr>
        <td>${plugin.name}</td>
        <td><span class="badge">${plugin.version}</span></td>
      </tr>
      </g:each>
    </table>
  </div>
</div>
<div class="col-md-6">
  <div class="panel panel-default">
    <div class="panel-heading">
      <h1>Application Status</h1>
    </div>
    <table class="table table-striped">
      <tr>
        <td>App version:</td>
        <td><span class="badge"><g:meta name="app.version"/></span></td>
      </tr>
      <tr>
        <td>Grails version:</td>
        <td><span class="badge"><g:meta name="app.grails.version"/></span></td>
      </tr>
      <tr>
        <td>Groovy version:</td>
        <td><span class="badge">${groovy.lang.GroovySystem.getVersion()}</span></td>
      </tr>
      <tr>
        <td>JVM version:</td>
        <td><span class="badge">${System.getProperty('java.version')}</span></td>
      </tr>
      <tr>
        <td>Reloading active:</td>
        <td><span class="badge">${grails.util.Environment.reloadingAgentEnabled}</span></td>
      </tr>
      <tr>
        <td>Controllers:</td>
        <td><span class="badge">${grailsApplication.controllerClasses.size()}</span></td>
      </tr>
      <tr>
        <td>Domains:</td>
        <td><span class="badge">${grailsApplication.domainClasses.size()}</span></td>
      </tr>
      <tr>
        <td>Services:</td>
        <td><span class="badge">${grailsApplication.serviceClasses.size()}</span></td>
      </tr>
      <tr>
        <td>Tag Libraries:</td>
        <td><span class="badge">${grailsApplication.tagLibClasses.size()}</span></td>
      </tr>
    </table>
  </div>
</div>
