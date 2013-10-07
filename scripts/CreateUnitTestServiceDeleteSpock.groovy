import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceDeleteSpock:
    "Generate Spock unit tests for 'delete' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of Spock 'delete' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceDeleteSpock )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idAssigned )
    content << generateNullMethod( domainClass.name )
    content << generateInvalidMethod( domainClass.name, idAssigned )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceDeleteSpec.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports() {

    def content = '' << "import grails.test.mixin.*\n"
    content << "import spock.lang.*\n\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << "@TestFor(${className}Service)\n"
    content << "@Mock(${className})\n"
    content << "class ${className}ServiceDeleteSpec extends Specification {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = '' << "${TAB}def setup() {\n"
    content << "${TAB*2}${className}Mock.mock( 0 ).save("
    content << " failOnError:true )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idAssigned ) {

    def content = '' << ''
    content << "${TAB}def \" test ok\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def instance = service.get( ${idName} )\n"
    content << "${TAB*3}service.delete( instance )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}${className}.count() == 0\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}${idName} ="
    if ( idAssigned ) {
        content << " ${className}Mock.mock( 0 ).${idName}\n"
    } else {
        content << " 1\n"
    }// End of else
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << ''
    content << "${TAB}void \"test ${className} null\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}service.delete( ${classNameLower} )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}IllegalArgumentException e = thrown()\n"
    content << "${TAB*3}e.message == \"Parameter '${classNameLower}'"
    content << " is null\"\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}${classNameLower} = null\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateInvalidMethod( className, idAssigned ) {

    def idName = idAssigned ? idAssigned.name : 'id'
    def content = '' << ''
    content << "${TAB}def \" test invalid\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def instance = new ${className}()\n"
    content << "${TAB*3}service.delete( instance )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}${className}.exists( instance.${idName} ) == false\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
