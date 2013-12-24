import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestControllerSave:
    "Generate unit tests for 'save' controller method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp,
        createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'save' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestControllerSave )

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
    def content = '' << "${tab()}def setup() {\n"
    content << "${tab()*2}views[ '/${classNameLower}/_form.gsp' ]"
    content << " = getTemplate()\n"
    content << "${tab()}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idName ) {

    def id = idName != 'id' ? "\${${className}Mock.mock( 0 ).${idName}}" : '1'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def \"test ok\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def control = mock${className}Service()\n"
    content << "${tab()*3}request.method = 'POST'\n"
    content << "${tab()*3}setUpParams()\n"
    content << "${tab()*3}controller.save()\n"
    content << "${tab()*3}control.verify()\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}flash.formMessage == 'default.created.message'\n"
    content << "${tab()*3}response.redirectedUrl =="
    content << " \"/${classNameLower}/edit/${id}\"\n"
    content << "${tab()*3}response.status == 302\n\n"
    content << "${tab()}}\n\n"
    content.toString()

}// End of method

String generateParamsInvalidMethod( domainClass ) {

    def requiredAttributes = getRequiredAttributes(
        domainClass.constrainedProperties )
    if ( !requiredAttributes ) return ''
    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def \"test params invalid\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def control = mock${className}Service( false )\n"
    content << "${tab()*3}request.method = 'POST'\n"
    content << "${tab()*3}setUpParams()\n"
    content << "${tab()*3}params.${requiredAttributes[0]} = null\n"
    content << "${tab()*3}controller.save()\n"
    content << "${tab()*3}control.verify()\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}response.text == 'OK'\n"
    content << "${tab()*3}response.status == 400\n\n"
    content << "${tab()}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = '' << "${tab()}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${tab()}def \"test request method invalid\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}request.method = 'GET'\n"
    content << "${tab()*3}controller.save()\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}response.status == 405\n\n"
    content << "${tab()}}\n\n"
    content.toString()

}// End of method

String generateGetTemplateMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}private String getTemplate() {\n"
    content << "${tab()*2}'<g:if test=\"\${${classNameLower}Instance}\">OK</g:if>"
    content << "<g:else>ERROR</g:else>'\n"
    content << "${tab()}}\n\n"
    content.toString()

}// End of method

String generateMockMethods( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 0 ).${idName}" : '1'
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}private GrailsMock"
    content << " mock${className}Service( save = true ) {\n\n"
    content << "${tab()*2}def control = mockFor( ${className}Service )\n"
    content << "${tab()*2}control.demand.create( 1 ) {"
    content << " ${className} instance ->\n"
    content << "${tab()*3}if ( save ) {\n"
    content << "${tab()*4}instance.${idName} = ${id}\n"
    content << "${tab()*4}instance.save( failOnError:true )\n"
    content << "${tab()*3}} else throw new IllegalArgumentException( 'error' )\n"
    content << "${tab()*2}}\n"
    content << "${tab()*2}controller.${classNameLower}Service = "
    content << "control.createMock()\n"
    content << "${tab()*2}control\n\n"
    content << "${tab()}}\n\n"
    content.toString()

}// End of method

String generateSetUpParamsMethod( className ) {

    def content = '' << "${tab()}private void setUpParams() {\n\n"
    content << "${tab()*2}def mock = ${className}Mock.mock( 0 )\n"
    content << "${tab()*2}mock.properties.each{ params.\"\${it.key}\""
    content << " = it.value }\n\n"
    content << "${tab()}}\n\n"
    content.toString()

}// End of method
