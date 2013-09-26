includeTargets << new File( optimusPluginDir,
    'scripts/CreateServiceListLog.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateServiceCreateLog.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateServiceUpdateLog.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateServiceGetLog.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateServiceDeleteLog.groovy' )

target( createServiceLogs:'Generate logs for service class' ) {

    depends( checkVersion, configureProxy, bootstrap,
        createServiceListLog,
        createServiceCreateLog,
        createServiceUpdateLog,
        createServiceGetLog,
        createServiceDeleteLog )

}// End of closure

setDefaultTarget( createServiceLogs )

