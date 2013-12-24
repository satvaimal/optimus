import grails.util.Environment
import groovy.util.ConfigObject
import groovy.util.ConfigSlurper

class OptimusGrailsPlugin {

  def version = "0.4"
  def grailsVersion = "2.3 > *"
  def title = "Optimus Plugin"
  def author = "Alejandro Garc\u00EDa Granados"
  def authorEmail = "alejandro.garcia.granados@gmail.com"
  def description = 'Grails Optimus Plugin'
  def documentation = "http://satvaimal.github.io/optimus/"
  def license = "APACHE"
  def issueManagement = [system: "GITHUB", url: "https://github.com/satvaimal/optimus/issues"]
  def scm = [url: "https://github.com/satvaimal/optimus"]

  def doWithSpring = {
    mergeConfig( application )
  }// End of closure

  def onConfigChange = { event ->
    this.mergeConfig( application )
  }// End of closure

  private void mergeConfig( application ) {

    def currentConfig = application.config.grails.optimus
    def slurper = new ConfigSlurper( Environment.current.name )
    def secondaryConfig = slurper.parse(
      application.classLoader.loadClass( 'OptimusConfig' ) )
    def config = new ConfigObject()
    config.putAll( secondaryConfig.optimus.merge( currentConfig ) )
    application.config.grails.optimus = config

  }// End of method

}// End of class
