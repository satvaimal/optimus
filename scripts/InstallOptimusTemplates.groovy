includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( installOptimusTemplates:'Generate templates for Optimus views' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def commonDir = '/src/templates/scaffolding'
    def targetDir = "${basedir}${commonDir}"
    if ( !new File( targetDir ).exists() ) {
        ant.mkdir( dir:targetDir )
    }// End of if
    [ '_content.gsp', '_list.gsp', '_form.gsp', 'renderEditor.template' ].each {
        def content = new File(
            "${optimusPluginDir}${commonDir}/${it}" ).text
        createFile( targetDir, it, content )
    }// End of closure
    def msg = "Finished installation of Optimus templates"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( installOptimusTemplates )
