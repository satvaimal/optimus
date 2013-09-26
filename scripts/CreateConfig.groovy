includeTargets << new File( optimusPluginDir,
    'scripts/CreatePropertiesFile.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateConfigFile.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateDatasourceFile.groovy' )

target( createConfig:'Generate all config artifacts' ) {
    depends(
        createPropertiesFile,
        createConfigFile,
        createDatasourceFile )
    def msg = "Finished generation of config artifacts"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createConfig )
