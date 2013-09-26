includeTargets << new File( optimusPluginDir,
    'scripts/CreateViewsIndex.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/InstallOptimusTemplates.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateContentTemplate.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateListTemplate.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateFormTemplate.groovy' )

target( createViews:'Generate all views artifacts' ) {
    depends(
        createViewsIndex,
        installOptimusTemplates,
        createContentTemplate,
        createListTemplate,
        createFormTemplate )
    def msg = "Finished generation of views artifacts"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createViews )
