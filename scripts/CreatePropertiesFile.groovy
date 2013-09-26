includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createPropertiesFile:"Generate 'config.properties' file" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def commonDir = '/src/java'
    def targetDir = "${basedir}${commonDir}"
    ant.copy( file:"${optimusPluginDir}${commonDir}/config.properties",
         todir:"${targetDir}/" )
    def msg = "Finished generation of 'config.properties' file"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createPropertiesFile )
