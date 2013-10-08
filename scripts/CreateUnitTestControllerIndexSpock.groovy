import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestControllerIndexSpock:
    "Generate Spock unit tests for 'index' controller method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of Spock 'index' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestControllerIndexSpock )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateOkMethod( domainClass.name )
    content << generateOkWithParamsMethod( domainClass.name )
    content << generateRequestMethodInvalidMethod()
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ControllerIndexSpec.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Controller)\n"
    content << "class ${className}ControllerIndexSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateOkMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def \"test ok\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}request.method = 'GET'\n"
    content << "${TAB*3}controller.index()\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}response.redirectedUrl =="
    content << " '/${classNameLower}/content'\n"
    content << "${TAB*3}response.status == 302\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkWithParamsMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def \"test ok with params\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}request.method = 'GET'\n"
    content << "${TAB*3}params.name = 'value'\n"
    content << "${TAB*3}controller.index()\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}response.redirectedUrl =="
    content << " '/${classNameLower}/content?name=value'\n"
    content << "${TAB*3}response.status == 302\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = '' << "${TAB}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${TAB}def \"test request method invalid\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}request.method = 'POST'\n"
    content << "${TAB*3}controller.index()\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}response.status == 405\n\n"
    content << "${TAB}}\n\n"

}// End of method
