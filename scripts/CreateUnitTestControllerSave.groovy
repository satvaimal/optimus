import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestsControllerSave:"Generate unit tests for 'save' controller method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'save' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestsControllerSave )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def idName = idAssigned ? idAssigned.name : 'id'
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idName )
    content << generateParamsInvalidMethod( domainClass )
    content << generateRequestMethodInvalidMethod()
    content << generateGetTemplateMethod( domainClass.name )
    content << generateMockMethods( domainClass.name, idName )
    content << generateSetUpParamsMethod( domainClass.name )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ControllerSaveTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.GrailsMock\n"
    content << "import grails.test.mixin.*\n"
    content << "import org.junit.*\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Controller)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ControllerSaveTests {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n"
    content << "${TAB*2}views[ '/${classNameLower}/_form.gsp' ]"
    content << " = this.getTemplate()\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idName ) {

    def id = idName != 'id' ? "\${${className}Mock.mock(1).${idName}}" : '1'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}void testOk() {\n\n"
    content << "${TAB*2}def control = this.mock${className}Service()\n"
    content << "${TAB*2}request.method = 'POST'\n"
    content << "${TAB*2}this.setUpParams()\n"
    content << "${TAB*2}controller.save()\n"
    content << "${TAB*2}def expected = 'default.created.message'\n"
    content << "${TAB*2}assertEquals \"'message' should be '\${expected}'\",\n"
    content << "${TAB*3}expected, flash.formMessage\n"
    content << "${TAB*2}expected = \"/${classNameLower}/edit/${id}\"\n"
    content << "${TAB*2}assertEquals \"'redirectedUrl' should be"
    content << " '\${expected}'\",\n"
    content << "${TAB*3}expected, response.redirectedUrl\n"
    content << "${TAB*2}assertEquals \"'status' should be 302\""
    content << ", 302, response.status\n"
    content << "${TAB*2}control.verify()\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateParamsInvalidMethod( domainClass ) {

    def requiredAttributes = getRequiredAttributes(
        domainClass.constrainedProperties )
    if ( !requiredAttributes ) return ''
    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}void testParamsInvalid() {\n\n"
    content << "${TAB*2}def control = this.mock${className}Service( false )\n"
    content << "${TAB*2}request.method = 'POST'\n"
    content << "${TAB*2}this.setUpParams()\n"
    content << "${TAB*2}params.${requiredAttributes[0]} = null\n"
    content << "${TAB*2}controller.save()\n"
    content << "${TAB*2}def expected = 'OK'\n"
    content << "${TAB*2}assertEquals \"'text' should be '\${expected}'\",\n"
    content << "${TAB*3}expected, response.text\n"
    content << "${TAB*2}assertEquals \"'status' should be 400\""
    content << ", 400, response.status\n"
    content << "${TAB*2}control.verify()\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = '' << "${TAB}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${TAB}void testRequestMethodInvalid() {\n\n"
    content << "${TAB*2}request.method = 'GET'\n"
    content << "${TAB*2}controller.save()\n"
    content << "${TAB*2}assertEquals \"'status' should be 405\""
    content << ", 405, response.status\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateGetTemplateMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}private String getTemplate() {\n"
    content << "${TAB*2}'<g:if test=\"\${${classNameLower}Instance}\">OK</g:if>"
    content << "<g:else>ERROR</g:else>'\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateMockMethods( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock(1).${idName}" : '1'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}private GrailsMock"
    content << " mock${className}Service( save = true ) {\n\n"
    content << "${TAB*2}def control = mockFor( ${className}Service )\n"
    content << "${TAB*2}control.demand.create( 1 ) {"
    content << " ${className} instance ->\n"
    content << "${TAB*3}if ( save ) {\n"
    content << "${TAB*4}instance.${idName} = ${id}\n"
    content << "${TAB*4}instance.save( failOnError:true )\n"
    content << "${TAB*3}} else throw new IllegalArgumentException( 'error' )\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}controller.${classNameLower}Service = "
    content << "control.createMock()\n"
    content << "${TAB*2}control\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateSetUpParamsMethod( className ) {

    def content = '' << "${TAB}private void setUpParams() {\n\n"
    content << "${TAB*2}def mock = ${className}Mock.mock( 1 )\n"
    content << "${TAB*2}mock.properties.each{ params.\"\${it.key}\""
    content << " = it.value }\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
