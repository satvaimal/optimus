import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/CreateMock.groovy' )

target( createUnitTestServiceGet:
    "Generate  unit tests for 'get' service method" ) {

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
    content << generateSetUpMethod( domainClass.name )
    content << generateOkMethod( domainClass.name, idName )
    content << generateNullMethod( domainClass.name, idName )
    content << generateNotFoundMethod( domainClass.name, idName )
    content << "}${comment('class')}"
    def directory = generateDirectory( "test/unit", domainClass.packageName )
    def filename = "${domainClass.name}ServiceGetSpec.groovy"
    createFile( directory, filename, content.toString() )

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

    def content = '' << "${tab()}def setup() {\n"
    content << "${tab()*2}${className}Mock.mock( 0 ).save( failOnError:true )\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateOkMethod( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 0 ).${idName}" : '1'
    def content = '' << ''
    content << "${tab()}def \" test ok\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def result = service.get( ${idName} )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}result != null\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}${idName} = ${id}\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateNullMethod( className, idName ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def content = '' << ''
    content << "${tab()}void \"test ${idName.capitalize()} null\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}service.get( ${idName} )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}IllegalArgumentException e = thrown()\n"
    content << "${tab()*3}e.message == \"Parameter '${idName}' is null\"\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}${idName} = null\n"
    content << "\n${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method

String generateNotFoundMethod( className, idName ) {

    def id = idName != 'id' ? "${className}Mock.mock( 1 ).${idName}" : '2'
    def content = new StringBuilder()
    content << "${tab()}def \" test not found\"() {\n\n"
    content << "${tab()*2}when:\n"
    content << "${tab()*3}def result = service.get( ${idName} )\n"
    content << "${tab()*2}then:\n"
    content << "${tab()*3}result == null\n"
    content << "${tab()*2}where:\n"
    content << "${tab()*3}${idName} = ${id}\n\n"
    content << "${tab()}}${comment('method')}\n\n"
    content.toString()

}// End of method
