includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createDatasourceFile:"Generate 'DataSource.groovy' file" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def content = new File(optimusPluginDir, "grails-app/utils/OptimusDataSource.txt").text
    createFile( "${basedir}/grails-app/conf", 'DataSource.groovy', content )
    def msg = "Finished generation of 'DataSource.groovy' file"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createDatasourceFile )
