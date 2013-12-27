import grails.util.GrailsUtil

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
    new File( basedir, 'grails-app/log/' ).mkdirs()
    def content = new File(optimusPluginDir,
        "grails-app/utils/Config-${getVersion()}.txt"
        ).text.replaceAll( '%%pckg%%', "${pckg}.aop"  )
    createFile( "${basedir}/grails-app/conf", 'Config.groovy', content )

}// End of method

def getVersion() {

    def grailsVersion = new BigDecimal( GrailsUtil.grailsVersion[ 0..2 ] )
    def version = '2.0.0'
    if ( grailsVersion >= 2.1 && grailsVersion < 2.2 ) version = '2.1.0'
    else if ( grailsVersion >= 2.2 && grailsVersion < 2.3 ) version = '2.2.0'
    else if ( grailsVersion >= 2.3 ) version = '2.3.0'
    version

}// End of method
