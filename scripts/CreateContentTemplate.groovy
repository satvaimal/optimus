includeTargets << new File( optimusPluginDir,
    'scripts/InstallOptimusTemplates.groovy' )

target( createContentTemplate:"Generate templates for 'content' view" ) {

    depends( installOptimusTemplates )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { this.generate( it ) }
    def msg = "Finished generation of 'content' templates"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createContentTemplate )

void generate( domainClass ) {

    def DefaultGrailsTemplateGenerator = classLoader.loadClass(
        'org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator')
    def templateGenerator = DefaultGrailsTemplateGenerator.newInstance(
        classLoader )
    templateGenerator.grailsApplication = grailsApp
    templateGenerator.pluginManager = pluginManager
    def viewsDir = new File(
        "${basedir}/grails-app/views/${domainClass.propertyName}" )
    if ( !viewsDir.exists() ) viewsDir.mkdirs()
    templateGenerator.generateView( domainClass, '_content',
        viewsDir.absolutePath )

}// End of method


