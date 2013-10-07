includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceListSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceCreateSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceUpdateSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceGetSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceDeleteSpock.groovy' )

target( createUnitTestServiceSpock:'Generate unit tests for service class' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createUnitTestServiceListSpock,
        createUnitTestServiceCreateSpock,
        createUnitTestServiceUpdateSpock,
        createUnitTestServiceGetSpock,
        createUnitTestServiceDeleteSpock )
    def msg = "Finished generation of Spock service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceSpock )
