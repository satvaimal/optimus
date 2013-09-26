import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestsServiceDelete:"Generate unit tests for 'delete' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'delete' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestsServiceDelete )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def content = "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateThrownField()
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idAssigned )
    content << generateNullMethod( domainClass.name )
    content << generateInvalidMethod( domainClass.name, idAssigned )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceDeleteTests.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = "import grails.test.mixin.*\n"
    content << "import org.junit.*\n"
    content << "import org.junit.rules.*\n\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceDeleteTests {\n\n"
    content.toString()

}// End of method

String generateThrownField() {

    def content = "${TAB}@Rule\n"
    content << "${TAB}public ExpectedException thrown = "
    content << "ExpectedException.none()\n"
    content << "\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = "${TAB}@Before\n"
    content << "${TAB}void setUp() {\n\n"
    content << "${TAB*2}${className}Mock.mock( 0 ).save("
    content << " failOnError:true )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idAssigned ) {

    def idName = 'id'
    if ( idAssigned ) idName = idAssigned.name
    def content = "${TAB}void testOk() {\n\n"
    content << "${TAB*2}assertEquals \"'count' should be 1\""
    content << ", 1, ${className}.count()\n"
    content << "${TAB*2}def instance = service.get("
    if ( idAssigned ) {
        content << " ${className}Mock.mock( 0 ).${idName} )\n"
    } else {
        content << " 1 )\n"
    }// End of else
    content << "${TAB*2}service.delete( instance )\n"
    content << "${TAB*2}assertEquals \"'count' should be 0\""
    content << ", 0, ${className}.count()\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = "${TAB}void testNull() {\n\n"
    content << "${TAB*2}thrown.expect("
    content << " IllegalArgumentException )\n"
    content << "${TAB*2}thrown.expectMessage( "
    content << "\"Parameter '${classNameLower}' is null\" )\n"
    content << "${TAB*2}service.delete( null )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateInvalidMethod( className, idAssigned ) {

    def idName = idAssigned ? idAssigned.name : 'id'
    def content = "${TAB}void testInvalid() {\n\n"
    content << "${TAB*2}def instance = new ${className}()\n"
    content << "${TAB*2}assertFalse \"'exists'"
    content << " should be false\",\n"
    content << "${TAB*3}${className}.exists( "
    content << "instance.${idName} )\n"
    content << "${TAB*2}service.delete( instance )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
