import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestsControllerContent:"Generate unit tests for 'content' controller method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'content' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestsControllerContent )

void generate( domainClass ) {

    def content = "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name )
    content << generateRequestMethodInvalidMethod()
    content << generateGetTemplateMethod()
    content << generateMockMethods( domainClass.name )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ControllerContentTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = "import grails.test.GrailsMock\n"
    content << "import grails.test.mixin.*\n"
    content << "import org.junit.*\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = "@TestFor(${className}Controller)\n"
    content << "class ${className}ControllerContentTests {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n"
    content << "${TAB*2}views[ '/${classNameLower}/_content.gsp' ]"
    content << " = this.getTemplate()\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = "${TAB}void testOk() {\n\n"
    content << "${TAB*2}def control = this.mock${className}Service()\n"
    content << "${TAB*2}request.method = 'GET'\n"
    content << "${TAB*2}def model = controller.content()\n"
    content << "${TAB*2}def expected = 'OK'\n"
    content << "${TAB*2}assertEquals \"'text' should be '\${expected}'\",\n"
    content << "${TAB*3}expected, response.text\n"
    content << "${TAB*2}assertEquals \"'status' should be 200\""
    content << ", 200, response.status\n"
    content << "${TAB*2}control.verify()\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = "${TAB}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${TAB}void testRequestMethodInvalid() {\n\n"
    content << "${TAB*2}request.method = 'POST'\n"
    content << "${TAB*2}controller.content()\n"
    content << "${TAB*2}assertEquals \"'status' should be 405\""
    content << ", 405, response.status\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateGetTemplateMethod() {

    def content = "${TAB}private String getTemplate() {\n"
    content << "${TAB*2}'<g:if test=\"\${items && total}\">OK</g:if>"
    content << "<g:else>ERROR</g:else>'\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateMockMethods( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = "${TAB}private GrailsMock mock${className}Service() {\n\n"
    content << "${TAB*2}def control = mockFor( ${className}Service )\n"
    content << "${TAB*2}control.demand.list( 1 ) { Map params ->\n"
    content << "${TAB*3}[ items:[ new ${className}() ], total:1 ] }\n"
    content << "${TAB*2}controller.${classNameLower}Service ="
    content << " control.createMock()\n"
    content << "${TAB*2}control\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
