import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceGetSpock:
    "Generate Spock unit tests for 'get' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of Spock 'get' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceGetSpock )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idAssigned )
    content << generateNullMethod( domainClass.name, idAssigned )
    content << generateNotFoundMethod( domainClass.name, idAssigned )
    content << '}'
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def fileName = "${domainClass.name}ServiceGetSpec.groovy"
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
    content << "class ${className}ServiceGetSpec extends Specification {\n\n"
    content.toString()

}// End of method

String generateSetUpMethod( className ) {

    def content = '' << "${TAB}def setup() {\n"
    content << "${TAB*2}${className}Mock.mock( 1 ).save( failOnError:true )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idAssigned ) {

    def idName = 'id'
    if ( idAssigned ) idName = idAssigned.name
    def content = '' << ''
    content << "${TAB}def \" test ok\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def result = service.get( ${idName} )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}result != null\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}${idName} ="
    if ( idAssigned ) {
        content << " ${className}Mock.mock( 1 ).${idName}\n"
    } else {
        content << " 1\n"
    }// End of else
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className, idAssigned ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def idName = idAssigned ? idAssigned.name : 'id'
    def content = '' << ''
    content << "${TAB}void \"test ${idName.capitalize()} null\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}service.get( ${idName} )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}IllegalArgumentException e = thrown()\n"
    content << "${TAB*3}e.message == \"Parameter '${idName}' is null\"\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}${idName} = null\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method

String generateNotFoundMethod( className, idAssigned ) {

    def idName = 'id'
    if ( idAssigned ) idName = idAssigned.name
    def content = new StringBuilder()
    content << "${TAB}def \" test not found\"() {\n\n"
    content << "${TAB*2}when:\n"
    content << "${TAB*3}def result = service.get( ${idName} )\n"
    content << "${TAB*2}then:\n"
    content << "${TAB*3}result == null\n"
    content << "${TAB*2}where:\n"
    content << "${TAB*3}${idName} ="
    if ( idAssigned ) {
        content << " ${className}Mock.mock( 2 ).${idName}\n"
    } else {
        content << " 2\n"
    }// End of else
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
