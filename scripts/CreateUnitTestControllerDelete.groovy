import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestsControllerDelete:"Generate unit tests for 'delete' controller method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'delete' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestsControllerDelete )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def idName = idAssigned ? idAssigned.name : 'id'
    def content = "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idName )
    content << generateIdNullMethod()
    content << generateNotFoundMethod( domainClass.name, idName )
    content << generateRequestMethodInvalidMethod( domainClass.name, idName )
    content << generateMockMethods( domainClass.name, idAssigned )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ControllerDeleteTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = "import javax.servlet.http.HttpServletRequest\n"
    content << "import grails.test.GrailsMock\n"
    content << "import grails.test.mixin.*\n"
    content << "import org.junit.*\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = "@TestFor(${className}Controller)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ControllerDeleteTests {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n\n"
    content << "${TAB*2}${className}Mock.mock( 1 ).save("
    content << " failOnError:true )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 1 ).${idName}" : '1'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = "${TAB}void testOk() {\n\n"
    content << "${TAB*2}def control = this.mock${className}Service()\n"
    content << "${TAB*2}request.method = 'POST'\n"
    content << "${TAB*2}controller.delete( ${id} )\n"
    content << "${TAB*2}def expected = 'default.deleted.message'\n"
    content << "${TAB*2}assertEquals \"'message' should be '\${expected}'\",\n"
    content << "${TAB*3}expected, flash.listMessage\n"
    content << "${TAB*2}expected = '/${classNameLower}/content'\n"
    content << "${TAB*2}assertEquals \"'redirectedUrl' should be"
    content << " '\${expected}'\",\n"
    content << "${TAB*3}expected, response.redirectedUrl\n"
    content << "${TAB*2}assertEquals \"'status' should be 302\""
    content << ", 302, response.status\n"
    content << "${TAB*2}control.verify()\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateIdNullMethod() {

    def content = "${TAB}void testIdNull() {\n\n"
    content << "${TAB*2}def control = this.mock"
    content << "${CRACKING_SERVICE.capitalize()}Service()\n"
    content << "${TAB*2}request.method = 'GET'\n"
    content << "${TAB*2}controller.delete( null )\n"
    content << "${TAB*2}def expected = '/logout'\n"
    content << "${TAB*2}assertEquals \"'redirectedUrl' should be"
    content << " '\${expected}'\",\n"
    content << "${TAB*3}expected, response.redirectedUrl\n"
    content << "${TAB*2}assertEquals \"'status' should be 302\""
    content << ", 302, response.status\n"
    content << "${TAB*2}control.verify()\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateNotFoundMethod( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 2 ).${idName}" : '2'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = "${TAB}void testNotFound() {\n\n"
    content << "${TAB*2}def control = this.mock${className}Service( false )\n"
    content << "${TAB*2}def control2 = this.mock"
    content << "${CRACKING_SERVICE.capitalize()}Service()\n"
    content << "${TAB*2}request.method = 'GET'\n"
    content << "${TAB*2}controller.delete( ${id} )\n"
    content << "${TAB*2}def expected = '/logout'\n"
    content << "${TAB*2}assertEquals \"'redirectedUrl' should be"
    content << " '\${expected}'\",\n"
    content << "${TAB*3}expected, response.redirectedUrl\n"
    content << "${TAB*2}assertEquals \"'status' should be 302\""
    content << ", 302, response.status\n"
    content << "${TAB*2}control.verify()\n"
    content << "${TAB*2}control2.verify()\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 1 ).${idName}" : '1'
    def content = "${TAB}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${TAB}void testRequestMethodInvalid() {\n\n"
    content << "${TAB*2}request.method = 'GET'\n"
    content << "${TAB*2}controller.delete( ${id} )\n"
    content << "${TAB*2}assertEquals \"'status' should be 405\""
    content << ", 405, response.status\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateMockMethods( className, idAssigned ) {

    def content = ''
    content << generateMockServiceMethod( className, idAssigned )
    content << generateCrackingServiceMethod()
    content.toString()

}// End of method

String generateMockServiceMethod( className, idAssigned ) {

    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = "${TAB}private GrailsMock"
    content << " mock${className}Service( delete = true ) {\n\n"
    content << "${TAB*2}def control = mockFor( ${className}Service )\n"
    content << "${TAB*2}control.demand.get( 1 ) { ${idType} id ->\n"
    content << "${TAB*3}${className}.findBy${idName.capitalize()}( id )\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}if ( delete ) {\n"
    content << "${TAB*3}control.demand.delete( 1 ) { ${className} instance ->\n"
    content << "${TAB*4}instance.delete()\n"
    content << "${TAB*3}}\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}controller.${classNameLower}Service = "
    content << "control.createMock()\n"
    content << "${TAB*2}control\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateCrackingServiceMethod() {

    def content = "${TAB}private GrailsMock"
    content << " mock${CRACKING_SERVICE.capitalize()}Service() {\n\n"
    content << "${TAB*2}def control = mockFor("
    content << " ${CRACKING_SERVICE.capitalize()}Service )\n"
    content << "${TAB*2}control.demand.notify( 1 ) {"
    content << " HttpServletRequest request, Map params -> }\n"
    content << "${TAB*2}controller.${CRACKING_SERVICE}Service = "
    content << "control.createMock()\n"
    content << "${TAB*2}control\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
