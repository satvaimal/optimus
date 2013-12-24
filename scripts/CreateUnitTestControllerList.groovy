import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestControllerList:
    "Generate unit tests for 'list' controller method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'list' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestControllerList )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name )
    content << generateRequestMethodInvalidMethod()
    content << generateGetTemplateMethod()
    content << generateMockMethods( domainClass.name )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ControllerListSpec.groovy"
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
    content << "class ${className}ControllerListSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def setup() {\n"
    content << "${TAB*2}views[ '/${classNameLower}/_list.gsp' ]"
    content << " = getTemplate()\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def \"test ok\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def control = mock${className}Service()\n"
    content << "${TAB*3}request.method = 'GET'\n"
    content << "${TAB*3}def model = controller.list()\n"
    content << "${TAB*2}control.verify()\n\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}response.text == 'OK'\n"
    content << "${TAB*3}response.status == 200\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = '' << "${TAB}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${TAB}def \"test request method invalid\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}request.method = 'POST'\n"
    content << "${TAB*3}controller.list()\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}response.status == 405\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateGetTemplateMethod() {

    def content = '' << "${TAB}private String getTemplate() {\n"
    content << "${TAB*2}'<g:if test=\"\${items && total}\">OK</g:if>"
    content << "<g:else>ERROR</g:else>'\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateMockMethods( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}private GrailsMock mock${className}Service() {\n\n"
    content << "${TAB*2}def control = mockFor( ${className}Service )\n"
    content << "${TAB*2}control.demand.list( 1 ) { Map params ->\n"
    content << "${TAB*3}[ items:[ new ${className}() ], total:1 ] }\n"
    content << "${TAB*2}controller.${classNameLower}Service ="
    content << " control.createMock()\n"
    content << "${TAB*2}control\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
