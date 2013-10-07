import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceUpdate:"Generate unit tests for 'update' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'update' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceUpdate )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateThrownField()
    content << generateOkMethod( domainClass.name )
    content << generateNullMethod( domainClass.name )
    content << generateInvalidMethod( domainClass )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceUpdateTests.groovy"
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
    content << "class ${className}ServiceUpdateTests {\n\n"
    content.toString()

}// End of method

String generateThrownField() {

    def content = '' << "${TAB}@Rule\n"
    content << "${TAB}public ExpectedException thrown = "
    content << "ExpectedException.none()\n\n"
    content.toString()

}// End of method

String generateOkMethod( className ) {

    def content = '' << "${TAB}void testOk() {\n\n"
    content << "${TAB*2}def instance = ${className}Mock.mock( 0 )\n"
    content << "${TAB*2}assertEquals \"'count' should be 0\""
    content << ", 0, ${className}.count()\n"
    content << "${TAB*2}service.update( instance )\n"
    content << "${TAB*2}assertEquals \"'count' should be 1\""
    content << ", 1, ${className}.count()\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}void test${className}Null() {\n\n"
    content << "${TAB*2}def instance = null\n"
    content << "${TAB*2}thrown.expect("
    content << " IllegalArgumentException )\n"
    content << "${TAB*2}thrown.expectMessage( "
    content << "\"Parameter '${classNameLower}' is null\" )\n"
    content << "${TAB*2}service.update( instance )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateInvalidMethod( domainClass ) {

    def requiredAttributes = getRequiredAttributes(
        domainClass.constrainedProperties )
    if ( !requiredAttributes ) return ''
    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def content = new StringBuilder()
    content << "${TAB}void test${className}Invalid() {\n\n"
    content << "${TAB*2}def instance = ${className}Mock.mock( 0 )\n"
    content << "${TAB*2}instance.${requiredAttributes[0]} = null\n"
    content << "${TAB*2}thrown.expect("
    content << " IllegalArgumentException )\n"
    content << "${TAB*2}thrown.expectMessage( "
    content << "\"Parameter '${classNameLower}' is invalid\" )\n"
    content << "${TAB*2}service.update( instance )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
