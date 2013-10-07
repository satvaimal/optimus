import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestsServiceUpdateSpock:"Generate unit tests for 'update' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of Spock 'update' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestsServiceUpdateSpock )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateOkMethod( domainClass.name )
    content << generateNullMethod( domainClass.name )
    content << generateInvalidMethod( domainClass )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceUpdateSpec.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceUpdateSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateOkMethod( className ) {

    def content = '' << "${TAB}def \"test ok\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def instance = ${className}Mock.mock( 0 )\n"
    content << "${TAB*3}service.update( instance )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}${className}.count() == 1\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def \"test ${className} null\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def instance = null\n"
    content << "${TAB*3}service.update( instance )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}IllegalArgumentException e = thrown()\n"
    content << "${TAB*3}e.message == \"Parameter"
    content << " '${classNameLower}' is null\"\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateInvalidMethod( domainClass ) {

    def requiredAttributes = getRequiredAttributes(
        domainClass.constrainedProperties )
    if ( !requiredAttributes ) return ''
    def attr = requiredAttributes[ 0 ]
    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${TAB}def \"test ${className} invalid\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def instance = ${className}Mock.mock( 0 )\n"
    content << "${TAB*3}instance.${attr} = ${attr}\n"
    content << "${TAB*3}service.update( instance )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}IllegalArgumentException e = thrown()\n"
    content << "${TAB*3}e.message == \"Parameter"
    content << " '${classNameLower}' is invalid\"\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}${attr} = null\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
