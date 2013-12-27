import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceDelete:
    "Generate  unit tests for 'delete' service method" ) {

    depends( createMock )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'delete' service unit tests"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createUnitTestServiceDelete )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def content = '' << "package ${domainClass.packageName}\n\n"
    content << generateImports()
    content << generateClassDeclaration( domainClass.name )
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idAssigned )
    content << generateNullMethod( domainClass.name )
    content << generateInvalidMethod( domainClass.name, idAssigned )
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def filename = "${domainClass.name}ServiceDeleteSpec.groovy"
    createFile( directory, filename, content.toString() )

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

    def content = '' << "${tab()}def setup() {\n"
    content << "${tab()*2}${className}Mock.mock( 0 ).save("
    content << " failOnError:true )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idAssigned ) {

    def idName = idAssigned ? idAssigned.name : 'id'
    def content = '' << ''
    content << "${tab()}def \" test ok\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def instance = service.get( ${idName} )\n"
    content << "${tab()*3}service.delete( instance )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}${className}.count() == 0\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}${idName} ="
    if ( idAssigned ) {
        content << " ${className}Mock.mock( 0 ).${idName}\n"
    } else {
        content << " 1\n"
    }// End of else
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << ''
    content << "${tab()}void \"test ${className} null\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}service.delete( ${classNameLower} )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}IllegalArgumentException e = thrown()\n"
    content << "${tab()*3}e.message == \"Parameter '${classNameLower}'"
    content << " is null\"\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}${classNameLower} = null\n"
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateInvalidMethod( className, idAssigned ) {

    def idName = idAssigned ? idAssigned.name : 'id'
    def content = '' << ''
    content << "${tab()}def \" test invalid\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def instance = new ${className}()\n"
    content << "${tab()*3}service.delete( instance )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}${className}.exists( instance.${idName} ) == false\n"
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
