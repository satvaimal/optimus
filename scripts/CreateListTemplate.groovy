includeTargets << new File( optimusPluginDir,
    'scripts/InstallOptimusTemplates.groovy' )

target( createListTemplate:"Generate templates for 'list' view" ) {

    depends( installOptimusTemplates )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { this.generate( it ) }
    def msg = "Finished generation of 'list' templates"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createListTemplate )

void generate( domainClass ) {

    def DefaultGrailsTemplateGenerator = classLoader.loadClass(
        'org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator')
    def templateGenerator = DefaultGrailsTemplateGenerator.newInstance( classLoader )
    templateGenerator.grailsApplication = grailsApp
    templateGenerator.pluginManager = pluginManager
    def viewsDir = new File(basedir,
        "grails-app/views/${domainClass.propertyName}" )
    if ( !viewsDir.exists() ) viewsDir.mkdirs()
    templateGenerator.generateView( domainClass, '_list',
        viewsDir.absolutePath )

}// End of method
