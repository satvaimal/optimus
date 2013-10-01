includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createConfigFile:"Generate 'Config.groovy' file" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    generate( domainClassList[ 0 ] )
    def msg = "Finished generation of 'Config.groovy' file"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createConfigFile )

void generate( domainClass ) {

    def pckg = domainClass.packageName
    new File(basedir, "grails-app/log/" ).mkdirs()
    def content = new File(optimusPluginDir,
        "grails-app/utils/OptimusConfig.txt"
        ).text.replaceAll( '%%pckg%%', pckg  )
    new File(basedir, "grails-app/conf/Config.groovy" ).text = content

}// End of method
