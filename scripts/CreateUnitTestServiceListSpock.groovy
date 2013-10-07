import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceListMaxSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceListOffsetSpock.groovy' )
includeTargets << new File( optimusPluginDir,
    'scripts/CreateUnitTestServiceListSortOrderSpock.groovy' )

target( createUnitTestServiceListSpock:"Generate unit tests for 'list' service method" ) {

    depends( createUnitTestServiceListMaxSpock,
        createUnitTestServiceListOffsetSpock,
        createUnitTestServiceListSortOrderSpock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of Spock 'list' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceListSpock )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateOkMethod()
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceListSpec.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceListSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateOkMethod() {

    def content = '' << "${TAB}def \"test ok\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def result = service.list( params )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}result.items != null\n"
    content << "${TAB*3}result.total != null\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}params = [:]\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
