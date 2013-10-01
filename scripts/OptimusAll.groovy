includeTargets << new File( optimusPluginDir,
    'scripts/CreateConfig.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestConstraints.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateServiceClass.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestService.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateLogs.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateControllerClass.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestController.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateViews.groovy' )

target( optimusAll:'Generate all optimus artifacts' ) {
    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createConfig,
        createUnitTestConstraints,
        createServiceClass,
        createUnitTestService,
        createLogs,
        createControllerClass, 
        createUnitTestController,
        createViews )
    def msg = "Finished generation of artifacts"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( optimusAll )
