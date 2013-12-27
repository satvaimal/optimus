import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createUnitTestControllerIndex:
    "Generate unit tests for 'index' controller method" ) {

    depends( checkVersion, configureProxy, packageApp, loadApp, configureApp )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'index' controller unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestControllerIndex )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateOkMethod( domainClass.name )
    content << generateOkWithParamsMethod( domainClass.name )
    content << generateRequestMethodInvalidMethod()
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def filename = "${domainClass.name}ControllerIndexSpec.groovy"
    createFile( directory, filename, content.toString() )

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
    def content = '' << "${tab()}def \"test ok\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}request.method = 'GET'\n"
    content << "${tab()*3}controller.index()\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}response.redirectedUrl =="
    content << " '/${classNameLower}/content'\n"
    content << "${tab()*3}response.status == 302\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateOkWithParamsMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def \"test ok with params\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}request.method = 'GET'\n"
    content << "${tab()*3}params.name = 'value'\n"
    content << "${tab()*3}controller.index()\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}response.redirectedUrl =="
    content << " '/${classNameLower}/content?name=value'\n"
    content << "${tab()*3}response.status == 302\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateRequestMethodInvalidMethod() {

    def content = '' << "${tab()}@Ignore( 'See http://jira.grails.org/browse/"
    content << "GRAILS-8426' )\n"
    content << "${tab()}def \"test request method invalid\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}request.method = 'POST'\n"
    content << "${tab()*3}controller.index()\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}response.status == 405\n\n"
    content << "${tab()}}${comment('method')}\n\n"

}// End of method
