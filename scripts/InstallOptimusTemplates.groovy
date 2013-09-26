includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( installOptimusTemplates:'Generate templates for Optimus views' ) {

    depends( checkVersion, configureProxy, bootstrap )
    def commonDir = '/src/templates/scaffolding'
    def targetDir = "${basedir}${commonDir}"
    if ( !new File( targetDir ).exists() ) {
        ant.mkdir( dir:targetDir )
    }// End of if
    [ '_content.gsp', '_list.gsp', '_form.gsp', 'renderEditor.template' ].each {
        ant.copy( file:"${optimusPluginDir}${commonDir}/${it}",
             todir:"${targetDir}/" )
    }// End of closure
    def msg = "Finished installation of Optimus templates"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( installOptimusTemplates )
