includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerIndex.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerContent.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerList.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerCreate.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerSave.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerEdit.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerUpdate.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestControllerDelete.groovy' )

target( createUnitTestController:'Generate unit tests for controller class' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createUnitTestControllerIndex,
        createUnitTestControllerContent,
        createUnitTestControllerList,
        createUnitTestControllerCreate,
        createUnitTestControllerSave,
        createUnitTestControllerEdit,
        createUnitTestControllerUpdate,
        createUnitTestControllerDelete )
    def msg = "Finished generation of controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestController )
