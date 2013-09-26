includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceListOffset:"Generate unit tests for 'list' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'list-offset' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceListOffset )

void generate( domainClass ) {

    def content = "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateMethod( 'Ok', "'15'", 5 )
    content << generateMethod( 'Null', "null", 10 )
    content << generateMethod( 'Blank', "''", 10 )
    content << generateMethod( 'Invalid', "'A'", 10 )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceListOffsetTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = "import grails.test.mixin.*\n"
    content << "import org.junit.*\n\n"

}// End of method

String generateClassDeclaration( className ) {

    def content = "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceListOffsetTests {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n\n"
    content << "${TAB*2}20.times {\n"
    content << "${TAB*3}${className}Mock.mock( it ).save("
    content << " failOnError:true )\n"
    content << "${TAB*2}}\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateMethod( methodSuffix, offsetValue, equalsValue ) {

    def content = "${TAB}void test${methodSuffix}() {\n\n"
    content << "${TAB*2}def params = [ offset:${offsetValue} ]\n"
    content << "${TAB*2}def result = service.list( params )\n"
    content << "${TAB*2}assertNotNull \"'result'"
    content << " should not be null\", result\n"
    content << "${TAB*2}def items = result.items\n"
    content << "${TAB*2}assertNotNull \"'items'"
    content << " should not be null\", items\n"
    content << "${TAB*2}assertEquals \"'size'"
    content << " should be ${equalsValue}\", ${equalsValue}, items.size()\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
