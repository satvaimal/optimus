import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createHome:'Generate Home artifacts' ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    createController()
    createViews()
    def msg = "Finished generation of 'Home' artifacts"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createHome )

private void createController() {

    def targetDir = "${basedir}/grails-app/controllers"
    def filename = 'HomeController.groovy'
    def content = new File(
        "${optimusPluginDir}/grails-app/utils/${filename}" ).text
    createFile( targetDir, filename, content )

}// End of method

private void createViews() {

    def commonDir = '/grails-app/views/home'
    new File( basedir, commonDir ).mkdirs()
    def targetDir = "${basedir}${commonDir}"
    def filename = '_index.gsp'
    def content = new File(
        "${optimusPluginDir}${commonDir}/${filename}" ).text
    createFile( targetDir, filename, content )

}// End of method
