includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createPropertiesFile:"Generate 'config.properties' file" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def commonDir = '/src/java'
    def targetDir = "${basedir}${commonDir}"
    def filename = 'config.properties'
    def content = new File(
        "${optimusPluginDir}${commonDir}/${filename}" ).text
    createFile( targetDir, filename, content )
    def msg = "Finished generation of '${filename}' file"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createPropertiesFile )
