includeTargets << new File( optimusPluginDir,
    'scripts/CreateConfig.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateAll.groovy' )
target( optimusAll:'Generate all optimus artifacts' ) {
    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createConfig,
        createAll )
    def msg = "Finished generation of artifacts"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( optimusAll )
