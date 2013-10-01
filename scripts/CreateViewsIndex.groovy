includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createViewsIndex:"Generate 'index.gsp' file" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def commonDir = '/grails-app/views'
    def targetDir = "${basedir}${commonDir}"
    [ 'header', 'topMenu', 'menu', 'content' ].each {
        ant.copy( file:"${optimusPluginDir}${commonDir}/_${it}.gsp",
             todir:"${targetDir}/" )
    }// End of closure
    ant.copy( file:"${optimusPluginDir}${commonDir}/index.gsp",
         todir:"${targetDir}/" )
    def msg = "Finished generation of 'index' files"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createViewsIndex )
