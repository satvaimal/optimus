includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createViewsIndex:"Generate 'index.gsp' file" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def commonDir = '/grails-app/views'
    def targetDir = "${basedir}${commonDir}"
    [ '_header', '_topMenu', '_menu', 'index' ].each {
        def content = new File(
            "${optimusPluginDir}${commonDir}/${it}.gsp" ).text
        createFile( targetDir, "${it}.gsp", content )
    }// End of closure
    def msg = "Finished generation of 'index' files"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createViewsIndex )
