import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestsControllerIndex:"Generate unit tests for 'index' controller method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'index' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestsControllerIndex )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateOkMethod( domainClass.name )
    content << generateOkWithParamsMethod( domainClass.name )
    content << generateRequestMethodInvalidMethod()
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ControllerIndexTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import org.junit.*\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Controller)\n"
    content << "class ${className}ControllerIndexTests {\n\n"
    content.toString()

}// End of method

String generateOkMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}void testOk() {\n\n"
    content << "${TAB*2}request.method = 'GET'\n"
    content << "${TAB*2}controller.index()\n"
    content << "${TAB*2}def expected = '/${classNameLower}/content'\n"
    content << "${TAB*2}assertEquals \"'redirectedUrl' should be"
    content << " '\${expected}'\",\n"
    content << "${TAB*3}expected, response.redirectedUrl\n"
    content << "${TAB*2}assertEquals \"'status' should be 302\""
    content << ", 302, response.status\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkWithParamsMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}void testOkWithParams() {\n\n"
    content << "${TAB*2}request.method = 'GET'\n"
    content << "${TAB*2}params.name = 'value'\n"
    content << "${TAB*2}controller.index()\n"
    content << "${TAB*2}def expected = '/${classNameLower}/content"
    content << "?name=value'\n"
    content << "${TAB*2}assertEquals \"'redirectedUrl' should be"
    content << " '\${expected}'\",\n"
    content << "${TAB*3}expected, response.redirectedUrl\n"
    content << "${TAB*2}assertEquals \"'status' should be 302\""
    content << ", 302, response.status\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = '' << "${TAB}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${TAB}void testRequestMethodInvalid() {\n\n"
    content << "${TAB*2}request.method = 'POST'\n"
    content << "${TAB*2}controller.index()\n"
    content << "${TAB*2}assertEquals \"'status' should be 405\""
    content << ", 405, response.status\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
