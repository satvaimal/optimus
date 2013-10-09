includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerIndexSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerContentSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerListSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerCreateSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerSaveSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerEditSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerUpdateSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerDeleteSpock.groovy' )

target( createUnitTestControllerSpock:
    'Generate Spock unit tests for controller class' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createUnitTestControllerIndexSpock,
        createUnitTestControllerContentSpock,
        createUnitTestControllerListSpock,
        createUnitTestControllerCreateSpock,
        createUnitTestControllerSaveSpock,
        createUnitTestControllerEditSpock,
        createUnitTestControllerUpdateSpock,
        createUnitTestControllerDeleteSpock )
    def msg = "Finished generation of Spock controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestControllerSpock )
