import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceCreate:
    "Generate unit tests for 'create' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'create' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceCreate )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateOkMethod( domainClass.name )
    content << generateNullMethod( domainClass.name )
    content << generateInvalidMethod( domainClass )
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceCreateSpec.groovy"
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
    content << "class ${className}ServiceCreateSpec"
    content << " extends Specification {\n\n"
    content.toString()

}// End of method

String generateOkMethod( className ) {

    def content = '' << "${tab()}def \"test ok\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def instance = ${className}Mock.mock( 0 )\n"
    content << "${tab()*3}service.create( instance )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}${className}.count() == 1\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def \"test ${className} null\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def instance = null\n"
    content << "${tab()*3}service.create( instance )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}IllegalArgumentException e = thrown()\n"
    content << "${tab()*3}e.message == \"Parameter"
    content << " '${classNameLower}' is null\"\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateInvalidMethod( domainClass ) {

    def requiredAttributes = getRequiredAttributes(
        domainClass.constrainedProperties )
    if ( !requiredAttributes ) return ''
    def attr = requiredAttributes[ 0 ]
    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << "${tab()}def \"test ${className} invalid\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def instance = ${className}Mock.mock( 0 )\n"
    content << "${tab()*3}instance.${attr} = ${attr}\n"
    content << "${tab()*3}service.create( instance )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}IllegalArgumentException e = thrown()\n"
    content << "${tab()*3}e.message == \"Parameter"
    content << " '${classNameLower}' is invalid\"\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}${attr} = null\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
