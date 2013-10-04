includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceListOffsetSpock:"Generate unit tests for 'list' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of Spock 'list-offset' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceListOffsetSpock )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateMethod( 'ok', "'15'", 5 )
    content << generateMethod( 'null', "null", 10 )
    content << generateMethod( 'blank', "''", 10 )
    content << generateMethod( 'invalid', "'A'", 10 )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceListOffsetSpec.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceListOffsetSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = '' << "${TAB}def setup() {\n\n"
    content << "${TAB*2}20.times {\n"
    content << "${TAB*3}${className}Mock.mock( it ).save("
    content << " failOnError:true )\n"
    content << "${TAB*2}}\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateMethod( methodSuffix, offsetValue, equalsValue ) {

    def content = '' << "${TAB}def \"test ${methodSuffix}\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def result = service.list( params )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}result.items.size() == ${equalsValue}\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}params = [ offset:${offsetValue} ]\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
