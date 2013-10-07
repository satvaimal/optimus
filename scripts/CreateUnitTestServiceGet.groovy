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
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateThrownField()
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idAssigned )
    content << generateNullMethod( domainClass.name, idAssigned )
    content << generateNotFoundMethod( domainClass.name, idAssigned )
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
    content << "${TAB}void setUp() {\n\n"
    content << "${TAB*2}${className}Mock.mock( 1 ).save("
    content << " failOnError:true )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idAssigned ) {

    def idName = 'id'
    if ( idAssigned ) idName = idAssigned.name
    def content = new StringBuilder()
    content << "${TAB}void testOk() {\n\n"
    content << "${TAB*2}def result = service.get("
    if ( idAssigned ) {
        content << " ${className}Mock.mock( 1 ).${idName} )\n"
    } else {
        content << " 1 )\n"
    }// End of else
    content << "${TAB*2}assertNotNull \"'result'"
    content << " should not be null\", result\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className, idAssigned ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def idName = idAssigned ? idAssigned.name : 'id'
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

String generateNotFoundMethod( className, idAssigned ) {

    def idName = 'id'
    if ( idAssigned ) idName = idAssigned.name
    def content = '' << "${TAB}void testNotFound() {\n\n"
    content << "${TAB*2}def result = service.get("
    if ( idAssigned ) {
        content << " ${className}Mock.mock( 2 ).${idName} )\n"
    } else {
        content << " 2 )\n"
    }// End of else
    content << "${TAB*2}assertNull \"'result '"
    content << " should be null\", result\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
