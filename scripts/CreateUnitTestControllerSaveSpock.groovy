import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestControllerSaveSpock:
    "Generate Spock unit tests for 'save' controller method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of Spock 'save' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestControllerSaveSpock )

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
    def fileName = "${domainClass.name}ControllerSaveSpec.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.GrailsMock\n"
    content << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Controller)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ControllerSaveSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def setup() {\n"
    content << "${TAB*2}views[ '/${classNameLower}/_form.gsp' ]"
    content << " = this.getTemplate()\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idName ) {

    def id = idName != 'id' ? "\${${className}Mock.mock(1).${idName}}" : '1'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def \"test ok\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def control = this.mock${className}Service()\n"
    content << "${TAB*3}request.method = 'POST'\n"
    content << "${TAB*3}setUpParams()\n"
    content << "${TAB*3}controller.save()\n"
    content << "${TAB*3}control.verify()\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}flash.formMessage == 'default.created.message'\n"
    content << "${TAB*3}response.redirectedUrl =="
    content << " \"/${classNameLower}/edit/${id}\"\n"
    content << "${TAB*3}response.status == 302\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateParamsInvalidMethod( domainClass ) {

    def requiredAttributes = getRequiredAttributes(
        domainClass.constrainedProperties )
    if ( !requiredAttributes ) return ''
    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def \"test params invalid\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def control = this.mock${className}Service( false )\n"
    content << "${TAB*3}request.method = 'POST'\n"
    content << "${TAB*3}this.setUpParams()\n"
    content << "${TAB*3}params.${requiredAttributes[0]} = null\n"
    content << "${TAB*3}controller.save()\n"
    content << "${TAB*3}control.verify()\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}response.text == 'OK'\n"
    content << "${TAB*3}response.status == 400\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = '' << "${TAB}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${TAB}def \"test request method invalid\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}request.method = 'GET'\n"
    content << "${TAB*3}controller.save()\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}response.status == 405\n\n"
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
