import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceGet:"Generate unit tests for 'get' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'get' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceGet )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def idName = idAssigned ? idAssigned.name : 'id'
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateThrownField()
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idName )
    content << generateNullMethod( domainClass.name, idName )
    content << generateNotFoundMethod( domainClass.name, idName )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceGetTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import org.junit.*\n"
    content << "import org.junit.rules.*\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceGetTests {\n\n"
    content.toString()

}// End of method

String generateThrownField() {

    def content = '' << "${TAB}@Rule\n"
    content << "${TAB}public ExpectedException thrown = "
    content << "ExpectedException.none()\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = '' << "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n"
    content << "${TAB*2}${className}Mock.mock( 0 ).save("
    content << " failOnError:true )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 0 ).${idName}" : '1'
    def content = new StringBuilder()
    content << "${TAB}void testOk() {\n\n"
    content << "${TAB*2}def result = service.get( ${id} )\n"
    content << "${TAB*2}assertNotNull \"'result'"
    content << " should not be null\", result\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className, idName ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = new StringBuilder()
    content << "${TAB}void test${idName.capitalize()}Null() {\n\n"
    content << "${TAB*2}thrown.expect("
    content << " IllegalArgumentException )\n"
    content << "${TAB*2}thrown.expectMessage( "
    content << "\"Parameter '${idName}' is null\" )\n"
    content << "${TAB*2}service.get( null )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNotFoundMethod( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 1 ).${idName}" : '2'
    def content = '' << "${TAB}void testNotFound() {\n\n"
    content << "${TAB*2}def result = service.get( ${id} )\n"
    content << "${TAB*2}assertNull \"'result '"
    content << " should be null\", result\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
