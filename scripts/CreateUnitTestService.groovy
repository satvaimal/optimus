includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceList.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceCreate.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceUpdate.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceGet.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceDelete.groovy' )

target( createUnitTestService:'Generate unit tests for service class' ) {

    depends( checkVersion, configureProxy, bootstrap,
        createUnitTestsServiceList,
        createUnitTestsServiceCreate,
        createUnitTestsServiceUpdate,
        createUnitTestsServiceGet,
        createUnitTestsServiceDelete )
    def msg = "Finished generation of service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestService )
